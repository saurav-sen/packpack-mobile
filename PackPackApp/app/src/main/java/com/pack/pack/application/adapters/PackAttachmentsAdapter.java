package com.pack.pack.application.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;

import com.pack.pack.application.AppController;
import com.pack.pack.application.R;
import com.pack.pack.application.activity.FullscreenAttachmentViewActivity;
import com.pack.pack.application.activity.PackAttachmentCommentsActivity;
import com.pack.pack.application.image.loader.DownloadImageTask;
import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIBuilder;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.model.web.JPackAttachment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Saurav on 22-05-2016.
 */
public class PackAttachmentsAdapter extends ArrayAdapter<JPackAttachment> {

    private LayoutInflater inflater;
    private Activity activity;

    private List<JPackAttachment> attachments;

    public PackAttachmentsAdapter(Activity activity, List<JPackAttachment> attachments) {
        super(activity, R.layout.activity_pack_detail_item, attachments);
        this.activity = activity;
        this.attachments = attachments;
    }

    public List<JPackAttachment> getAttachments() {
        if(attachments == null) {
            attachments = new ArrayList<JPackAttachment>(10);
        }
        return attachments;
    }

    public void setAttachments(List<JPackAttachment> attachments) {
        this.attachments = attachments;
        AppController.getInstance().cachePackAttachments(attachments);
    }

    @Override
    public int getCount() {
        return attachments != null ? attachments.size() : 0;
    }

    @Override
    public JPackAttachment getItem(int position) {
        if(position >= attachments.size())
            return null;
        return attachments.get(position);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(inflater == null) {
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if(convertView == null) {
            convertView = inflater.inflate(R.layout.activity_pack_detail_item, null);
        }
        ImageView pack_attachment_img = (ImageView) convertView.findViewById(R.id.pack_attachment_img);
        pack_attachment_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), FullscreenAttachmentViewActivity.class);
                intent.putExtra("index", position);
                getContext().startActivity(intent);
            }
        });

        Button pack_attachment_like = (Button) convertView.findViewById(R.id.pack_attachment_like);
        pack_attachment_like.setText("Like");
        pack_attachment_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JPackAttachment attachment = getItem(position);
                if(attachment != null) {
                    addLikeToPackAttachment(attachment.getId());
                }
            }
        });

        Button pack_attachment_comment = (Button) convertView.findViewById(R.id.pack_attachment_comment);
        pack_attachment_comment.setText("Comment");
        pack_attachment_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JPackAttachment attachment = getItem(position);
                Intent commentsIntent = new Intent((Activity)getContext(), PackAttachmentCommentsActivity.class);
                commentsIntent.putExtra(AppController.PACK_ATTACHMENT_ID_KEY, attachment.getId());
                getContext().startActivity(commentsIntent);
            }
        });

        Button pack_attachment_share = (Button) convertView.findViewById(R.id.pack_attachment_share);
        pack_attachment_share.setText("Share");
        pack_attachment_share.setEnabled(AppController.getInstance().isEnableShareOption());
        pack_attachment_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               shareImage(position);
            }
        });

        JPackAttachment attachment = getItem(position);
        if(attachment != null) {
            new DownloadImageTask(pack_attachment_img, 700, 500).execute(attachment.getAttachmentUrl());
        }
        return convertView;
    }

    private void addLikeToPackAttachment(String id) {
        new AddLikeTask().execute(id);
    }

    private void shareImage(int position) {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/*");
        JPackAttachment attachment = getItem(position);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Bitmap bitmap = AppController.getInstance().getLruBitmapCache().getBitmap(attachment.getAttachmentUrl());
        bitmap.compress(Bitmap.CompressFormat.JPEG, 60, byteArrayOutputStream);
        String fileName = attachment.getTitle();
        if(fileName == null) {
            fileName = UUID.randomUUID().toString();
        }
        File shareFile = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS) +
                File.separator + fileName + ".jpeg");
       // File shareFile = null;
        FileOutputStream fileOutputStream = null;
        try {
            boolean bool = shareFile.createNewFile();
            if(!bool) {
                return;
            }
            fileOutputStream = new FileOutputStream(shareFile);
            fileOutputStream.write(byteArrayOutputStream.toByteArray());
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(shareFile != null) {
            Uri fileUri = Uri.fromFile(shareFile);
            share.putExtra(Intent.EXTRA_STREAM, fileUri);
            share.putExtra(Intent.EXTRA_SUBJECT, attachment.getTitle());

            getContext().startActivity(share);
        }
    }

    private class AddLikeTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... IDs) {
            if(IDs == null || IDs.length == 0)
                return null;
            try {
                String ID = IDs[0];
                API api = APIBuilder.create().setAction(COMMAND.ADD_LIKE_TO_PACK_ATTACHMENT)
                        .setOauthToken(AppController.getInstance().getoAuthToken())
                        .addApiParam(APIConstants.PackAttachment.ID, ID)
                        .addApiParam(APIConstants.User.ID, AppController.getInstance().getUserId())
                        .build();
                api.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
