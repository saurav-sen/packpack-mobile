package com.pack.pack.application.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pack.pack.application.AppController;
import com.pack.pack.application.R;
import com.pack.pack.application.activity.FullscreenAttachmentViewActivity;
import com.pack.pack.application.activity.PackAttachmentCommentsActivity;
import com.pack.pack.application.data.util.AbstractNetworkTask;
import com.pack.pack.application.db.DBUtil;
import com.pack.pack.application.image.loader.DownloadImageTask;
import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.model.web.JPackAttachment;
import com.pack.pack.model.web.PackAttachmentType;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        final TextView pack_attachment_title = (TextView) convertView.findViewById(R.id.pack_attachment_title);
        final TextView pack_attachment_description = (TextView) convertView.findViewById(R.id.pack_attachment_description);
        ImageView pack_attachment_img = (ImageView) convertView.findViewById(R.id.pack_attachment_img);
        pack_attachment_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), FullscreenAttachmentViewActivity.class);
                intent.putExtra("index", position);
                getContext().startActivity(intent);
            }
        });

        final ImageView pack_attachment_video_play = (ImageView) convertView.findViewById(R.id.pack_attachment_video_play);
        pack_attachment_video_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), FullscreenAttachmentViewActivity.class);
                intent.putExtra("index", position);
                getContext().startActivity(intent);
            }
        });

        final ProgressBar pack_loading_progress = (ProgressBar) convertView.findViewById(R.id.pack_loading_progress);

        final Button pack_attachment_like = (Button) convertView.findViewById(R.id.pack_attachment_like);
        pack_attachment_like.setTag("NotLiked");
        pack_attachment_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JPackAttachment attachment = getItem(position);
                if(attachment != null) {
                    String tag = (String) pack_attachment_like.getTag();
                    if(tag != null && "NotLiked".equals(tag)) {
                        pack_attachment_like.setTag("Liked");
                        pack_attachment_like.setCompoundDrawables(activity.getResources().getDrawable(
                                R.drawable.like_after), null, null, null);
                        addLikeToPackAttachment(attachment.getId());
                    } else if(tag != null && "Liked".equals(tag)) {
                        pack_attachment_like.setTag("NotLiked");
                        pack_attachment_like.setCompoundDrawables(activity.getResources().getDrawable(
                                R.drawable.like_before), null, null, null);
                    }
                }
            }
        });

        Button pack_attachment_comment = (Button) convertView.findViewById(R.id.pack_attachment_comment);
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
        pack_attachment_share.setEnabled(AppController.getInstance().isEnableShareOption());
        pack_attachment_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               shareImage(position);
            }
        });

        JPackAttachment attachment = getItem(position);
        if(attachment != null) {
            pack_attachment_title.setText(attachment.getTitle() + "");
            pack_attachment_description.setText(attachment.getDescription() + "");
            String url = null;
            if(PackAttachmentType.VIDEO.name().equals(attachment.getMimeType())) {
                url = attachment.getAttachmentThumbnailUrl();
                pack_attachment_img.setVisibility(View.GONE);
                pack_loading_progress.setVisibility(View.GONE);
                pack_attachment_video_play.setVisibility(View.VISIBLE);
            } else if(PackAttachmentType.IMAGE.name().equals(attachment.getMimeType())) {
                url = attachment.getAttachmentUrl();
                pack_attachment_video_play.setVisibility(View.GONE);
                pack_loading_progress.setVisibility(View.GONE);
                pack_attachment_img.setVisibility(View.VISIBLE);
            }
            if(url != null) {
                new DownloadImageTask(pack_attachment_img, 700, 600, PackAttachmentsAdapter.this.getContext(), pack_loading_progress)
                        .execute(url);
            }
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

    private class AddLikeTask extends AbstractNetworkTask<String, Void, Void> {

        public AddLikeTask() {
            super(false, false, true, PackAttachmentsAdapter.this.activity);
        }

        @Override
        protected Void executeApi(API api) throws Exception {
            api.execute();
            return null;
        }

        @Override
        protected String getContainerIdForObjectStore() {
            return getInputObject();
        }

        @Override
        protected COMMAND command() {
            return COMMAND.ADD_LIKE_TO_PACK_ATTACHMENT;
        }

        @Override
        protected Map<String, Object> prepareApiParams(String s) {
            Map<String, Object> apiParams = new HashMap<String, Object>();
            apiParams.put(APIConstants.PackAttachment.ID, s);
            apiParams.put(APIConstants.User.ID, AppController.getInstance().getUserId());
            return apiParams;
        }

        @Override
        protected String getFailureMessage() {
            return null;
        }

        @Override
        protected void doUpdateExistingInDB(SQLiteDatabase writable, String inputObject, Void outputObject) {
            if(inputObject == null)
                return;
            JPackAttachment attachment = DBUtil.loadJsonModelByEntityId(writable, inputObject.trim(),
                    JPackAttachment.class);
            attachment.setLikes(attachment.getLikes() + 1);
            storeResultsInDb(attachment);
        }
    }
}
