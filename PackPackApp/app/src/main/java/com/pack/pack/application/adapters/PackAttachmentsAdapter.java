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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pack.pack.application.AppController;
import com.pack.pack.application.R;
import com.pack.pack.application.activity.FullscreenAttachmentViewActivity;
import com.pack.pack.application.activity.PackAttachmentCommentsActivity;
import com.pack.pack.application.data.cache.AppCache;
import com.pack.pack.application.data.util.AbstractNetworkTask;
import com.pack.pack.application.data.util.DateTimeUtil;
import com.pack.pack.application.db.DBUtil;
import com.pack.pack.application.image.loader.DownloadImageTask;
import com.pack.pack.application.view.CircleImageView;
import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.model.web.JPackAttachment;
import com.pack.pack.model.web.JUser;
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

    private Map<String, AttachmentUnderUploadDetails> attachmentIdVsAttachmentDetails = new HashMap<String, AttachmentUnderUploadDetails>();

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

        final CircleImageView user_profile_picture = (CircleImageView) convertView.findViewById(R.id.user_profile_picture);
        final TextView user_name = (TextView) convertView.findViewById(R.id.user_name);
        final TextView attachment_create_time = (TextView) convertView.findViewById(R.id.attachment_create_time);

        JPackAttachment attachment = getItem(position);
        if(attachment != null) {
            JUser creator = attachment.getCreator();
            if(creator != null) {
                user_name.setText(creator.getName());
                attachment_create_time.setText(DateTimeUtil.sentencify(attachment.getCreationTime()));
                if(creator.getProfilePictureUrl() != null
                        && !creator.getProfilePictureUrl().trim().isEmpty()) {
                    new DownloadImageTask(user_profile_picture, getContext()).execute(creator.getProfilePictureUrl());
                } else {
                    user_profile_picture.setImageResource(R.drawable.default_profile_picture_big);
                }
            }
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
                Intent commentsIntent = new Intent((Activity) getContext(), PackAttachmentCommentsActivity.class);
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

        final RelativeLayout upload_in_progress_overlay = (RelativeLayout) convertView.findViewById(R.id.upload_in_progress_overlay);

        if(attachment == null) {
            attachment = getItem(position);
        }

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

            if(attachment.isUploadProgress()) {
                upload_in_progress_overlay.setVisibility(View.VISIBLE);
                AttachmentUnderUploadDetails attachmentUnderUploadDetails = new AttachmentUnderUploadDetails();
                attachmentUnderUploadDetails.setAttachment(attachment);
                attachmentUnderUploadDetails.setImageView(pack_attachment_img);
                attachmentUnderUploadDetails.setOverlayProgress(upload_in_progress_overlay);

                attachmentIdVsAttachmentDetails.put(attachment.getId(), attachmentUnderUploadDetails);
            } else {
                upload_in_progress_overlay.setVisibility(View.GONE);
            }

            if(url != null) {
                new DownloadImageTask(pack_attachment_img, 700, 600, PackAttachmentsAdapter.this.getContext(), pack_loading_progress)
                        .execute(url);
            }
        }
        return convertView;
    }

    private void replace(JPackAttachment oldAttachment, JPackAttachment newAttachment) {
        oldAttachment.setAttachmentUrl(newAttachment.getAttachmentUrl());
        oldAttachment.setId(newAttachment.getId());
        oldAttachment.setUploadProgress(false);
        oldAttachment.setAttachmentType(newAttachment.getAttachmentType());
        oldAttachment.setViews(newAttachment.getViews());
        oldAttachment.setMimeType(newAttachment.getMimeType());
        oldAttachment.setLikes(newAttachment.getLikes());
        oldAttachment.setDescription(newAttachment.getDescription());
        oldAttachment.setAttachmentThumbnailUrl(newAttachment.getAttachmentThumbnailUrl());
        oldAttachment.setAvgRating(newAttachment.getAvgRating());
        oldAttachment.setCreationTime(newAttachment.getCreationTime());
        oldAttachment.setCreator(newAttachment.getCreator());
        oldAttachment.setTitle(newAttachment.getTitle());
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

    public void onUploadError(String oldAttachmentId) {
        AttachmentUnderUploadDetails attachmentUnderUploadDetails = attachmentIdVsAttachmentDetails.get(oldAttachmentId);
        attachmentUnderUploadDetails.setUploadSuccess(false);
        attachmentUnderUploadDetails.getAttachment().setUploadProgress(false);
        notifyDataSetChanged();
    }

    public void onUploadSuccess(String oldAttachmentId, JPackAttachment newAttachment) {
        AttachmentUnderUploadDetails attachmentUnderUploadDetails = attachmentIdVsAttachmentDetails.get(oldAttachmentId);
        attachmentUnderUploadDetails.setUploadSuccess(true);
        replace(attachmentUnderUploadDetails.getAttachment(), newAttachment);
        attachmentUnderUploadDetails.getAttachment().setUploadProgress(false);
        AppCache.INSTANCE.removeFromCacheOfSuccessfullyUploadedAttachment(newAttachment);
        notifyDataSetChanged();
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

    private class AttachmentUnderUploadDetails {

        private JPackAttachment attachment;

        public JPackAttachment getAttachment() {
            return attachment;
        }

        public void setAttachment(JPackAttachment attachment) {
            this.attachment = attachment;
        }

        public RelativeLayout getOverlayProgress() {
            return overlayProgress;
        }

        public void setOverlayProgress(RelativeLayout overlayProgress) {
            this.overlayProgress = overlayProgress;
        }

        public ImageView getImageView() {
            return imageView;
        }

        public void setImageView(ImageView imageView) {
            this.imageView = imageView;
        }

        private ImageView imageView;

        private RelativeLayout overlayProgress;

        public boolean isUploadSuccess() {
            return uploadSuccess;
        }

        public void setUploadSuccess(boolean uploadSuccess) {
            this.uploadSuccess = uploadSuccess;
        }

        private boolean uploadSuccess;
    }
}
