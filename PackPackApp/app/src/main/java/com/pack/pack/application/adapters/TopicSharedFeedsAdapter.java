package com.pack.pack.application.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.pack.pack.application.AppController;
import com.pack.pack.application.Constants;
import com.pack.pack.application.R;
import com.pack.pack.application.activity.AttachmentStoryEditActivity;
import com.pack.pack.application.activity.AttachmentStoryReaderActivity;
import com.pack.pack.application.activity.FullScreenPlayVideoActivity;
import com.pack.pack.application.activity.FullScreenWebViewActivity;
import com.pack.pack.application.activity.FullscreenAttachmentViewActivity;
import com.pack.pack.application.activity.PackAttachmentCommentsActivity;
import com.pack.pack.application.data.cache.InMemory;
import com.pack.pack.application.data.cache.PackAttachmentsCache;
import com.pack.pack.application.data.util.AbstractNetworkTask;
import com.pack.pack.application.data.util.ApiConstants;
import com.pack.pack.application.data.util.DateTimeUtil;
import com.pack.pack.application.data.util.IAsyncTaskStatusListener;
import com.pack.pack.application.data.util.UserUtil;
import com.pack.pack.application.db.DBUtil;
import com.pack.pack.application.image.loader.DownloadImageTask;
import com.pack.pack.application.service.UploadImageAttachmentService;
import com.pack.pack.application.service.UploadVideoAttachmentService;
import com.pack.pack.application.topic.activity.model.ParcelablePack;
import com.pack.pack.application.topic.activity.model.ParcelableTopic;
import com.pack.pack.application.view.CircleImageView;
import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.model.web.JPackAttachment;
import com.pack.pack.model.web.JUser;
import com.pack.pack.model.web.PackAttachmentType;
import com.pack.pack.model.web.PromoteStatus;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;

/**
 * Created by Saurav on 23-08-2017.
 */
public class TopicSharedFeedsAdapter  extends ArrayAdapter<JPackAttachment> {

    private LayoutInflater inflater;
    private Activity activity;

    private List<JPackAttachment> attachments;

    private Map<String, AttachmentUnderUploadDetails> attachmentIdVsAttachmentDetails = new HashMap<String, AttachmentUnderUploadDetails>();

    private Queue<AttachmentUploadTaskData> scheduledUploads = new LinkedList<AttachmentUploadTaskData>();

    private boolean uploadTaskRunning = false;

    private ParcelableTopic topic;

    private View view;

    private static final String LOG_TAG = "TopicFeedsAdapter";

    public TopicSharedFeedsAdapter(Activity activity, List<JPackAttachment> attachments, ParcelableTopic topic) {
        super(activity, R.layout.topic_shared_feed_items, attachments);
        this.activity = activity;
        this.attachments = attachments;
        this.topic = topic;
    }

    public List<JPackAttachment> getAttachments() {
        if(attachments == null) {
            attachments = new ArrayList<JPackAttachment>(10);
        }
        return attachments;
    }

    public void scheduleAttachmentUpload(String topicId, JPackAttachment attachment, String inputVideoFilePath) {
        AttachmentUploadTaskData attachmentUploadTaskData = new AttachmentUploadTaskData(topicId, attachment, inputVideoFilePath);
        scheduledUploads.offer(attachmentUploadTaskData);
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
            convertView = inflater.inflate(R.layout.topic_shared_feed_items, null);
        }
        if(view == null) {
            this.view = convertView;
        }

        final TextView pack_attachment_title = (TextView) convertView.findViewById(R.id.pack_attachment_title);
        final TextView pack_attachment_description = (TextView) convertView.findViewById(R.id.pack_attachment_description);

        final CircleImageView user_profile_picture = (CircleImageView) convertView.findViewById(R.id.user_profile_picture);
        final TextView user_name = (TextView) convertView.findViewById(R.id.user_name);
        final TextView attachment_create_time = (TextView) convertView.findViewById(R.id.attachment_create_time);

        /*final ImageButton edit_story = (ImageButton) convertView.findViewById(R.id.edit_story);
        final ImageButton read_story = (ImageButton) convertView.findViewById(R.id.read_story);

        edit_story.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JPackAttachment attachment = (JPackAttachment)getItem(position);
                Intent intent = new Intent(activity, AttachmentStoryEditActivity.class);
                intent.putExtra(Constants.ATTACHMENT_ID, attachment.getId());
                activity.startActivity(intent);
                //activity.startActivityForResult(intent, Constants.ATTACHMENT_LONG_STORY_EDIT_REQUEST_CODE);
            }
        });

        read_story.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JPackAttachment attachment = (JPackAttachment)getItem(position);
                Intent intent = new Intent(activity, AttachmentStoryReaderActivity.class);
                intent.putExtra(Constants.ATTACHMENT_ID, attachment.getId());
                activity.startActivity(intent);
            }
        });*/

        /*final ImageButton  deleteAttachment = (ImageButton) convertView.findViewById(R.id.deleteAttachment);
        if(AppController.getInstance().getUserId().equals(topic.getOwnerId())) {
            deleteAttachment.setVisibility(View.VISIBLE);
            deleteAttachment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    JPackAttachment attachment = (JPackAttachment)getItem(position);
                    AttachmentToDelete attachmentToDelete = new AttachmentToDelete(pack.getParentTopicId(),
                            pack.getId(), attachment, position);
                    new DeleteAttachment(getContext()).execute(attachmentToDelete);
                }
            });
        } else {
            deleteAttachment.setVisibility(View.GONE);
        }*/

        JPackAttachment attachment = getItem(position);
        if(attachment != null) {
            JUser creator = attachment.getCreator();
            if(creator != null) {
                /*if(AppController.getInstance().getUserId().equals(creator.getId())) {
                    edit_story.setVisibility(View.VISIBLE);
                }*/
                user_name.setText(UserUtil.resolveUserDisplayName(creator));
                long t1 = attachment.getCreationTime();
                long t2 = InMemory.INSTANCE.getServerCurrentTimeInMillis();
                attachment_create_time.setText(DateTimeUtil.sentencify(t1, t2));
                user_profile_picture.setImageResource(R.drawable.default_profile_picture_big);
                if(creator.getProfilePictureUrl() != null
                        && !creator.getProfilePictureUrl().trim().isEmpty()) {
                    new DownloadImageTask(user_profile_picture, getContext()).execute(creator.getProfilePictureUrl());
                }
            }
            /*if(attachment.getStoryId() != null) {
                read_story.setVisibility(View.VISIBLE);
            }*/
        }

        ImageView pack_attachment_img = (ImageView) convertView.findViewById(R.id.pack_attachment_img);
        pack_attachment_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JPackAttachment attachment = getItem(position);
                if(PackAttachmentType.IMAGE.name().equalsIgnoreCase(attachment.getAttachmentType())) {
                    if(attachment.isExternalLink()) {
                        Intent intent = new Intent(getContext(), FullScreenWebViewActivity.class);
                        intent.putExtra(FullScreenWebViewActivity.WEB_LINK, attachment.getAttachmentUrl());
                        getContext().startActivity(intent);
                    } else {
                        Intent intent = new Intent(getContext(), FullscreenAttachmentViewActivity.class);
                        intent.putExtra("index", position);
                        getContext().startActivity(intent);
                    }
                } else {
                    playVideo(attachment);
                }
            }
        });

        final ImageView pack_attachment_video_play = (ImageView) convertView.findViewById(R.id.pack_attachment_video_play);
        pack_attachment_video_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent intent = new Intent(getContext(), FullscreenAttachmentViewActivity.class);
                intent.putExtra("index", position);
                getContext().startActivity(intent);*/
                JPackAttachment attachment = getItem(position);
                playVideo(attachment);
            }
        });

        final ProgressBar pack_loading_progress = (ProgressBar) convertView.findViewById(R.id.pack_loading_progress);

        /*final Button pack_attachment_like = (Button) convertView.findViewById(R.id.pack_attachment_like);
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
        });*/

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
                //shareImage(position);
                shareJPackAttachment(position);
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
                if(PackAttachmentType.IMAGE.name().equals(attachment.getMimeType())) {
                    Bitmap bitmap = PackAttachmentsCache.open(getContext()).getSelectedAttachmentPhoto(attachment.getId());
                    if(bitmap != null) {
                        pack_attachment_img.setImageBitmap(bitmap);
                    }
                }

                attachmentIdVsAttachmentDetails.put(attachment.getId(), attachmentUnderUploadDetails);
                if(!uploadTaskRunning) {
                    uploadTaskRunning = true;
                    new UploadTask().execute();
                }
            } else {
                upload_in_progress_overlay.setVisibility(View.GONE);
            }

            if(url != null) {
                boolean isIncludeOauthToken = false;
                if(url.contains(ApiConstants.BASE_URL)) {
                    isIncludeOauthToken = true;

                }
                new DownloadImageTask(pack_attachment_img, 900, 700, TopicSharedFeedsAdapter.this.getContext(), pack_loading_progress, isIncludeOauthToken)
                        .execute(url);
            }
        }
        return convertView;
    }

    private void playVideo(JPackAttachment attachment) {
        boolean isExternalLink = attachment.isExternalLink();

        if (isExternalLink) {
            String VIDEO_ID = attachment.getExtraMetaData().get("YOUTUBE_VIDEO_ID");
            if ((VIDEO_ID != null && !VIDEO_ID.isEmpty())) {
                Intent intent = YouTubeStandalonePlayer.createVideoIntent(activity, ApiConstants.YOUTUBE_API_KEY, VIDEO_ID);
                activity.startActivity(intent);
            } else {
                String videoURL = attachment.getAttachmentUrl();
                if (videoURL.contains("youtube")) {
                    String[] split = attachment.getAttachmentUrl().split("v=");
                    if (split.length > 1) {
                        VIDEO_ID = split[1];
                    }
                }
                if ((VIDEO_ID != null && !VIDEO_ID.isEmpty())) {
                    Intent intent = YouTubeStandalonePlayer.createVideoIntent(activity, ApiConstants.YOUTUBE_API_KEY, VIDEO_ID);
                    activity.startActivity(intent);
                } else {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(attachment.getAttachmentUrl()));
                    activity.startActivity(intent);
                }
            }
        } else {
            Intent intent = new Intent(activity, FullScreenPlayVideoActivity.class);
            intent.putExtra(FullScreenPlayVideoActivity.VIDEO_URL, attachment.getAttachmentUrl());
            activity.startActivity(intent);
        }
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

    private void shareJPackAttachment(int position) {
        JPackAttachment attachment = getItem(position);
        PromoteAttachmentTask task = new PromoteAttachmentTask(getContext());
        task.addListener(new IAsyncTaskStatusListener() {
            @Override
            public void onPreStart(String taskID) {
            }

            @Override
            public void onSuccess(String taskID, Object data) {
                String publicUrl = (String) data;
                shareUrl(publicUrl);
            }

            @Override
            public void onFailure(String taskID, String errorMsg) {
                Snackbar.make(view, errorMsg, Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onPostComplete(String taskID) {
            }
        });
        task.execute(attachment);
        /*String userId = AppController.getInstance().getUserId();
        API api = APIBuilder.create(ApiConstants.PUBLIC_ENDPOINT_BASE_URL)
                .setAction(COMMAND.PROMOTE_PACK_ATTACHMENT)
                .addApiParam(APIConstants.PackAttachment.ID, attachment.getId())
                .addApiParam(APIConstants.User.ID, userId)
                .build();*/
    }

    private class PromoteAttachmentTask extends AbstractNetworkTask<JPackAttachment, Integer, String> {

        public PromoteAttachmentTask(Context context) {
            super(false, false, false, context, false, true);
        }

        private String errorMsg;

        @Override
        protected String getContainerIdForObjectStore() {
            return null;
        }

        @Override
        protected String getBaseUrl() {
            return ApiConstants.PUBLIC_ENDPOINT_BASE_URL;
        }

        @Override
        protected COMMAND command() {
            return COMMAND.PROMOTE_PACK_ATTACHMENT;
        }

        @Override
        protected Map<String, Object> prepareApiParams(JPackAttachment inputObject) {
            String userId = AppController.getInstance().getUserId();
            Map<String, Object> apiParams = new HashMap<String, Object>();
            apiParams.put(APIConstants.PackAttachment.ID, inputObject.getId());
            apiParams.put(APIConstants.User.ID, userId);
            return apiParams;
        }

        @Override
        protected String executeApi(API api) throws Exception {
            PromoteStatus status = null;
            try {
                status = (PromoteStatus) api.execute();
                if(status != null) {
                    return status.getPublicUrl();
                }
                return null;
            } catch (Exception e) {
                errorMsg = "Failed generating publicly accessible URL";
                throw e;
            }
        }

        @Override
        protected String getFailureMessage() {
            return errorMsg;
        }
    }

    private void shareUrl(String url) {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

        share.putExtra(Intent.EXTRA_TEXT, url);

        //getContext().startActivity(share);

        getContext().startActivity(Intent.createChooser(share, "Share From SQUILL"));
    }

    private void shareImage(int position) {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/*");
        JPackAttachment attachment = getItem(position);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        if("VIDEO".equalsIgnoreCase(attachment.getMimeType())) {

        }
        Bitmap bitmap = AppController.getInstance().getLruBitmapCache().getBitmap(attachment.getAttachmentUrl());
        if(bitmap != null) {
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

                if(attachment.getMimeType().equals("VIDEO")) {
                    getContext().startActivity(Intent.createChooser(share, "Share Video"));
                } else {
                    getContext().startActivity(Intent.createChooser(share, "Share Photograps"));
                }
            }
        }
    }

    public void onUploadError(String oldAttachmentId) {
        AttachmentUnderUploadDetails attachmentUnderUploadDetails = attachmentIdVsAttachmentDetails.get(oldAttachmentId);
        attachmentUnderUploadDetails.setUploadSuccess(false);
        attachmentUnderUploadDetails.getAttachment().setUploadProgress(false);
        notifyDataSetChanged();
    }

    public void onUploadSuccess(String packId, Map<String, String> oldVsNewAttachmentsMap, Map<String, JPackAttachment> successfullyUploadedAttachmentsMap) {
        Iterator<String> itr = oldVsNewAttachmentsMap.keySet().iterator();
        while(itr.hasNext()) {
            String oldAttachmentId = itr.next();
            String newAttachmentId = oldVsNewAttachmentsMap.get(oldAttachmentId);
            JPackAttachment newAttachment = successfullyUploadedAttachmentsMap.get(newAttachmentId);
            if(newAttachment == null) {
                continue;
            }
            AttachmentUnderUploadDetails attachmentUnderUploadDetails = attachmentIdVsAttachmentDetails.get(oldAttachmentId);
            if(attachmentUnderUploadDetails == null) {
                continue;
            }
            attachmentUnderUploadDetails.setUploadSuccess(true);
            replace(attachmentUnderUploadDetails.getAttachment(), newAttachment);
            attachmentUnderUploadDetails.getAttachment().setUploadProgress(false);
            PackAttachmentsCache.open(getContext()).removeFromCacheOfSuccessfullyUploadedAttachment(packId, newAttachment);
            //itr.remove();
        }
        notifyDataSetChanged();
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

    private class AttachmentUploadTaskData {

        AttachmentUploadTaskData(String topicId, JPackAttachment attachment, String inputVideoFilePath) {
            this.topicId = topicId;
            this.attachment = attachment;
            this.inputVideoFilePath = inputVideoFilePath;
        }

        private String topicId;

        private JPackAttachment attachment;

        private String inputVideoFilePath;

        public String getInputVideoFilePath() {
            return inputVideoFilePath;
        }

        public void setInputVideoFilePath(String inputVideoFilePath) {
            this.inputVideoFilePath = inputVideoFilePath;
        }

        public String getTopicId() {
            return topicId;
        }

        public void setTopicId(String topicId) {
            this.topicId = topicId;
        }

        public JPackAttachment getAttachment() {
            return attachment;
        }

        public void setAttachment(JPackAttachment attachment) {
            this.attachment = attachment;
        }
    }

    private class UploadTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                uploadTaskRunning = true;
                AttachmentUploadTaskData attachmentUploadTaskData = null;
                while((attachmentUploadTaskData = scheduledUploads.poll()) != null) {
                    invokeUploadService(attachmentUploadTaskData.getTopicId(),
                            attachmentUploadTaskData.getAttachment(), attachmentUploadTaskData.getInputVideoFilePath());
                }
            } finally {
                uploadTaskRunning = false;
            }
            return null;
        }

        private void invokeUploadService(String topicId, JPackAttachment attachment, String selectedInputVideoFilePath) {
            if(PackAttachmentType.IMAGE.name().equals(attachment.getMimeType())) {
                Intent service = new Intent(getContext(), UploadImageAttachmentService.class);
                service.putExtra(UploadImageAttachmentService.ATTACHMENT_TITLE, attachment.getTitle());
                service.putExtra(UploadImageAttachmentService.ATTACHMENT_DESCRIPTION, attachment.getDescription());
                service.putExtra(UploadImageAttachmentService.TOPIC_ID, topicId);
                service.putExtra(UploadImageAttachmentService.ATTACHMENT_ID, attachment.getId());
                service.putExtra(UploadImageAttachmentService.ATTACHMENT_IS_TOPIC_SHARED_FEED, true);
                getContext().startService(service);
            } else if(PackAttachmentType.VIDEO.name().equals(attachment.getMimeType())) {
                Intent service = new Intent(getContext(), UploadVideoAttachmentService.class);
                service.putExtra(UploadVideoAttachmentService.SELECTED_INPUT_VIDEO_FILE, selectedInputVideoFilePath);
                service.putExtra(UploadVideoAttachmentService.ATTACHMENT_TITLE,  attachment.getTitle());
                service.putExtra(UploadVideoAttachmentService.ATTACHMENT_DESCRIPTION, attachment.getDescription());
                service.putExtra(UploadVideoAttachmentService.TOPIC_ID, topicId);
                service.putExtra(UploadVideoAttachmentService.ATTACHMENT_ID, attachment.getId());
                service.putExtra(UploadImageAttachmentService.ATTACHMENT_IS_TOPIC_SHARED_FEED, true);
                getContext().startService(service);
            }
        }
    }

    private class AttachmentToDelete {

        AttachmentToDelete(String topicId, String packId, JPackAttachment attachment, int selectedItemPosition) {
            setTopicId(topicId);
            setPackId(packId);
            setAttachment(attachment);
            setSelectedItemPosition(selectedItemPosition);
        }

        private int selectedItemPosition;

        public int getSelectedItemPosition() {
            return selectedItemPosition;
        }

        public void setSelectedItemPosition(int selectedItemPosition) {
            this.selectedItemPosition = selectedItemPosition;
        }

        private String topicId;

        public String getTopicId() {
            return topicId;
        }

        public void setTopicId(String topicId) {
            this.topicId = topicId;
        }

        private String packId;

        public String getPackId() {
            return packId;
        }

        public void setPackId(String packId) {
            this.packId = packId;
        }

        public JPackAttachment getAttachment() {
            return attachment;
        }

        public void setAttachment(JPackAttachment attachment) {
            this.attachment = attachment;
        }

        private JPackAttachment attachment;
    }
}
