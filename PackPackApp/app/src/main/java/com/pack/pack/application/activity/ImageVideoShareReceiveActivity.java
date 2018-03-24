package com.pack.pack.application.activity;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.pack.pack.application.Constants;
import com.pack.pack.application.R;
/*import com.pack.pack.application.data.cache.ImageVideoShareDataHolder;
import com.pack.pack.application.data.cache.PackAttachmentsCache;*/
import java.util.UUID;

import static com.pack.pack.application.AppController.TOPIC_ID_KEY;

public class ImageVideoShareReceiveActivity extends AppCompatActivity {

    private ListView share_receive_list;

    /*private TopicThumbnailViewAdapter adapter;

    private Pagination<JTopic> page;*/

    private Uri imageUri;

    private String sharedText;

    private static final String LOG_TAG = "SharedImageUpload";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_video_share_receive);

        imageUri = (Uri) getIntent().getParcelableExtra(Constants.SHARED_IMAGE_URI_KEY);
        sharedText = getIntent().getStringExtra(Constants.SHARED_TEXT_OR_URL_KEY);

        share_receive_list = (ListView) findViewById(R.id.share_receive_list);
       /* adapter = new TopicThumbnailViewAdapter(this, new ArrayList<JTopic>());
        share_receive_list.setAdapter(adapter);
        share_receive_list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                int count = share_receive_list.getCount();
                if (scrollState == SCROLL_STATE_IDLE) {
                    if (share_receive_list.getLastVisiblePosition() > count - 3) {
                        new LoadAllFamilyAndSocietyTask().execute(AppController.getInstance().getUserId());
                    }
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });*/
        share_receive_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /*JTopic topic = adapter.getTopics().get(position);
                if(imageUri != null) {
                    uploadSharedImage(topic);
                } else if(sharedText != null) {
                    uploadSharedText(topic);
                } else {
                    Toast.makeText(ImageVideoShareReceiveActivity.this, "Unable to share...", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(ImageVideoShareReceiveActivity.this, LandingPageActivity.class);
                    finish();
                    startActivity(intent);
                }*/
            }
        });

        /*new LoadAllFamilyAndSocietyTask().execute(AppController.getInstance().getUserId());*/
    }

    private void showProgressDialog() {

    }

    private void hideProgressDialog() {

    }

   /* private void uploadSharedText(JTopic topic) {
        sharedText = sharedText.trim();
        if(sharedText.startsWith("http://") || sharedText.startsWith("https://")) {
            uploadExternalLink(topic);
        } else {
            uploadSharedTextMsg(topic);
        }
    }

    private void uploadSharedTextMsg(final JTopic topic) {
        IAsyncTaskStatusListener uploadTextMsgListener = new IAsyncTaskStatusListener() {
            @Override
            public void onPreStart(String taskID) {
            }

            private void handleFailure() {
                Toast.makeText(ImageVideoShareReceiveActivity.this, "Failed to share...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(ImageVideoShareReceiveActivity.this, LandingPageActivity.class);
                ImageVideoShareReceiveActivity.this.finish();
                startActivity(intent);
            }

            @Override
            public void onSuccess(String taskID, Object data) {
                if(data != null && (data instanceof JPackAttachment)) {
                    ParcelableTopic parcel = new ParcelableTopic(topic);
                    if(ApiConstants.FAMILY.equalsIgnoreCase(topic.getCategory())) {
                        Intent intent = new Intent(ImageVideoShareReceiveActivity.this, MyFamilyActivity.class);
                        intent.putExtra(AppController.TOPIC_PARCELABLE_KEY, parcel);
                        ImageVideoShareReceiveActivity.this.finish();
                        startActivity(intent);
                    } else if(ApiConstants.SOCIETY.equalsIgnoreCase(topic.getCategory())) {
                        Intent intent = new Intent(ImageVideoShareReceiveActivity.this, MySocietyActivity.class);
                        intent.putExtra(AppController.TOPIC_PARCELABLE_KEY, parcel);
                        ImageVideoShareReceiveActivity.this.finish();
                        startActivity(intent);
                    }
                } else {
                    handleFailure();
                }
            }

            @Override
            public void onFailure(String taskID, String errorMsg) {

            }

            @Override
            public void onPostComplete(String taskID) {
            }
        };

        new UploadTextMessageTask(ImageVideoShareReceiveActivity.this, topic, uploadTextMsgListener).execute(sharedText);
    }

    private void uploadExternalLink(final JTopic topic) {
        IAsyncTaskStatusListener listener = new IAsyncTaskStatusListener() {
            @Override
            public void onPreStart(String taskID) {
            }

            @Override
            public void onSuccess(String taskID, Object data) {
                if(data != null) {
                    JRssFeed sharedFeedForUpload = (JRssFeed) data;
                    sharedFeedForUpload.setOgUrl(sharedText);
                    uploadSharedFeed(topic, sharedFeedForUpload);
                } else {
                    handleFailure();
                }
            }

            private void handleFailure() {
                Toast.makeText(ImageVideoShareReceiveActivity.this, "Failed to share...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(ImageVideoShareReceiveActivity.this, LandingPageActivity.class);
                ImageVideoShareReceiveActivity.this.finish();
                startActivity(intent);
            }

            @Override
            public void onFailure(String taskID, String errorMsg) {
                handleFailure();
            }

            @Override
            public void onPostComplete(String taskID) {
            }
        };
        new ReadCopiedLink(ImageVideoShareReceiveActivity.this, listener).execute(sharedText);
    }*/

   /* private void uploadSharedFeed(final JTopic topic, JRssFeed sharedFeedForUpload) {
        ExternalLinkAttchmentData uploadData = new ExternalLinkAttchmentData();
        uploadData.setTopicId(topic.getId());
        uploadData.setUserId(AppController.getInstance().getUserId());
        uploadData.setTitle(sharedFeedForUpload.getOgTitle());
        uploadData.setDescription(sharedFeedForUpload.getOgDescription());
        uploadData.setAttachmentUrl(sharedFeedForUpload.getOgUrl());
        uploadData.setAttachmentThumbnailUrl(sharedFeedForUpload.getOgImage());

        IAsyncTaskStatusListener uploadLinkListener = new IAsyncTaskStatusListener() {
            @Override
            public void onPreStart(String taskID) {
            }

            private void handleFailure() {
                Toast.makeText(ImageVideoShareReceiveActivity.this, "Failed to share...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(ImageVideoShareReceiveActivity.this, LandingPageActivity.class);
                ImageVideoShareReceiveActivity.this.finish();
                startActivity(intent);
            }

            @Override
            public void onSuccess(String taskID, Object data) {
                if(data != null && (data instanceof JPackAttachment)) {
                    ParcelableTopic parcel = new ParcelableTopic(topic);
                    if(ApiConstants.FAMILY.equalsIgnoreCase(topic.getCategory())) {
                        Intent intent = new Intent(ImageVideoShareReceiveActivity.this, MyFamilyActivity.class);
                        intent.putExtra(AppController.TOPIC_PARCELABLE_KEY, parcel);
                        ImageVideoShareReceiveActivity.this.finish();
                        startActivity(intent);
                    } else if(ApiConstants.SOCIETY.equalsIgnoreCase(topic.getCategory())) {
                        Intent intent = new Intent(ImageVideoShareReceiveActivity.this, MySocietyActivity.class);
                        intent.putExtra(AppController.TOPIC_PARCELABLE_KEY, parcel);
                        ImageVideoShareReceiveActivity.this.finish();
                        startActivity(intent);
                    }
                } else {
                    handleFailure();
                }
            }

            @Override
            public void onFailure(String taskID, String errorMsg) {

            }

            @Override
            public void onPostComplete(String taskID) {
            }
        };

        new UploadExternalLink(ImageVideoShareReceiveActivity.this, uploadLinkListener).execute(uploadData);
    }*/

   /* private void uploadSharedImage(JTopic topic) {
        try {
            Uri uri = imageUri;
            if(!FileUtil.checkUploadSize(this, uri)) {
                Toast.makeText(ImageVideoShareReceiveActivity.this, "Selected file larger than 20MB", Toast.LENGTH_LONG).show();
                return;
            }
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                    getContentResolver(), uri);
            startUploadActivity(bitmap, topic);

        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            Toast.makeText(getApplicationContext(),
                    "Failed to upload image", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private void startUploadActivity(Bitmap bitmap, JTopic topic) {
        Intent intent = new Intent(this, UploadActivity2.class);
        String sharedImageId = UUID.randomUUID().toString();
        PackAttachmentsCache.open(this).addSelectedAttachmentPhoto(sharedImageId, bitmap);
        intent.putExtra(UploadActivity2.SHARED_IMAGE_ID, sharedImageId);
        intent.putExtra(TOPIC_ID_KEY, topic.getId());

        ImageVideoShareDataHolder.getInstance().addTopic(topic);
        startActivityForResult(intent, Constants.SHARED_IMAGE_UPLOAD_REQUEST_CODE);
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*switch (requestCode) {
            case Constants.SHARED_IMAGE_UPLOAD_REQUEST_CODE:
                JPackAttachment attachment = null;
                try {
                    String json = data.getStringExtra(Constants.ATTACHMENT_UNDER_UPLOAD);

                    if(json != null && !json.trim().isEmpty()) {
                        attachment = JSONUtil.deserialize(json, JPackAttachment.class, true);
                    }
                    if(attachment != null) {
                        String topicId = data.getStringExtra(UploadImageAttachmentService.TOPIC_ID);
                        new UploadTask(attachment, topicId).execute();
                    }
                } catch (PackPackException e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                }
                finish();
                break;
        }*/
    }

    /*private class UploadTask extends AsyncTask<Void, Void, Void> {

        private JPackAttachment attachment;

        private String topicId;

        UploadTask(JPackAttachment attachment, String topicId) {
            this.attachment = attachment;
            this.topicId = topicId;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            invokeUploadService(topicId, attachment);
            return null;
        }

        private void invokeUploadService(String topicId, JPackAttachment attachment) {
            if(PackAttachmentType.IMAGE.name().equals(attachment.getMimeType())) {
                Intent service = new Intent(ImageVideoShareReceiveActivity.this, UploadImageAttachmentService.class);
                service.putExtra(UploadImageAttachmentService.ATTACHMENT_TITLE, attachment.getTitle());
                service.putExtra(UploadImageAttachmentService.ATTACHMENT_DESCRIPTION, attachment.getDescription());
                service.putExtra(UploadImageAttachmentService.TOPIC_ID, topicId);
                service.putExtra(UploadImageAttachmentService.ATTACHMENT_ID, attachment.getId());
                service.putExtra(UploadImageAttachmentService.ATTACHMENT_IS_TOPIC_SHARED_FEED, true);
                ImageVideoShareReceiveActivity.this.startService(service);

                JTopic topic = ImageVideoShareDataHolder.getInstance().getTopic(topicId);
                if(topic != null) {
                    ParcelableTopic parcel = new ParcelableTopic(topic);
                    ImageVideoShareDataHolder.getInstance().clear(topicId);
                    if(ApiConstants.FAMILY.equalsIgnoreCase(topic.getCategory())) {
                        Intent intent = new Intent(ImageVideoShareReceiveActivity.this, MyFamilyActivity.class);
                        intent.putExtra(AppController.TOPIC_PARCELABLE_KEY, parcel);
                        startActivity(intent);
                    } else if(ApiConstants.SOCIETY.equalsIgnoreCase(topic.getCategory())) {
                        Intent intent = new Intent(ImageVideoShareReceiveActivity.this, MySocietyActivity.class);
                        intent.putExtra(AppController.TOPIC_PARCELABLE_KEY, parcel);
                        startActivity(intent);
                    }
                } else {
                    Intent intent = new Intent(ImageVideoShareReceiveActivity.this, LandingPageActivity.class);
                    startActivity(intent);
                }
                ImageVideoShareReceiveActivity.this.finish();
            }
        }
    }

    private class LoadAllFamilyAndSocietyTask extends AbstractNetworkTask<String, Integer, Pagination<JTopic>> {

        private String errorMsg;

        public LoadAllFamilyAndSocietyTask() {
            super(false, false, ImageVideoShareReceiveActivity.this, false);
        }


        @Override
        protected String getFailureMessage() {
            return errorMsg;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
            AppController.getInstance().waitForLoginSuccess();;
        }

        @Override
        protected String getPaginationContainerId() {
            return AppController.getInstance().getUserId();
        }

        @Override
        protected String getPaginationContainerClassName() {
            return UserInfo.class.getName() + ":TOPIC";
        }

        @Override
        protected String getContainerIdForObjectStore() {
            return AppController.getInstance().getUserId();
        }

        @Override
        protected Pagination<JTopic> doRetrieveFromDB(SQLiteDatabase readable, String inputObject) {
            Pagination<JTopic> page = null;
            List<JTopic> result = DBUtil.loadAllJsonModelByContainerId(readable,
                    AppController.getInstance().getUserId(), JTopic.class);
            String userId = inputObject;//AppController.getInstance().getUserId();
            if(result != null && !result.isEmpty()) {
                page = new Pagination<JTopic>();
                PaginationInfo paginationInfo = DBUtil.loadPaginationInfo(
                        readable, userId, getPaginationContainerClassName());
                if(paginationInfo != null) {
                    page.setNextLink(paginationInfo.getNextLink());
                    page.setPreviousLink(paginationInfo.getPreviousLink());
                }
                page.setResult(result);
            }
            return page;
        }

        @Override
        protected void onPostExecute(Pagination<JTopic> jTopicPagination) {
            super.onPostExecute(jTopicPagination);
            if(jTopicPagination != null) {
                List<JTopic> topics = jTopicPagination.getResult();
                if(topics != null) {
                    adapter.setTopics(topics);
                    adapter.notifyDataSetChanged();
                }
            }
            hideProgressDialog();
        }

        @Override
        protected COMMAND command() {
            return COMMAND.GET_USER_FOLLOWED_TOPIC_LIST;
        }

        @Override
        protected Pagination<JTopic> executeApi(API api) throws Exception {
            try {
                showProgressDialog();
                page = (Pagination<JTopic>) api.execute();
            } catch (Exception e) {
                errorMsg = e.getMessage();
            } finally {
                //hideProgressDialog();
            }
            return page;
        }

        @Override
        protected Map<String, Object> prepareApiParams(String inputObject) {
            Map<String, Object> apiParams = new HashMap<String, Object>();
            String userId = inputObject;//AppController.getInstance().getUserId();
            apiParams.put(APIConstants.User.ID, userId);
            String nextLink = page != null ? page.getNextLink() : "";
            String categoryName = ApiConstants.SOCIETY + ":" + ApiConstants.FAMILY;
            apiParams.put(APIConstants.PageInfo.PAGE_LINK, nextLink);
            apiParams.put(APIConstants.Topic.CATEGORY, categoryName);
            return apiParams;
        }
    }

    private class UploadTextMessageTask extends AbstractNetworkTask<String, Integer, JPackAttachment> {

        private String errorMsg;

        private JTopic topic;

        UploadTextMessageTask(Context context, JTopic topic, IAsyncTaskStatusListener listener) {
            super(false, false, false,context, false, false);
            this.topic = topic;
            addListener(listener);
        }

        @Override
        protected COMMAND command() {
            return COMMAND.ADD_SHARED_TEXT_MSG_TO_TOPIC;
        }

        @Override
        protected String getFailureMessage() {
            return errorMsg;
        }

        @Override
        protected JPackAttachment executeApi(API api) throws Exception {
            JPackAttachment attachment = null;
            try {
                attachment = (JPackAttachment) api.execute();
            } catch (Exception e) {
                errorMsg = "Failed to upload new attachment (Shared Text Message)";
            }
            return attachment;
        }

        @Override
        protected String getContainerIdForObjectStore() {
            return null;
        }

        @Override
        protected Map<String, Object> prepareApiParams(String inputObject) {
            Map<String, Object> apiParams = new HashMap<String, Object>();
            apiParams.put(APIConstants.Topic.ID, topic.getId());
            apiParams.put(APIConstants.User.ID, AppController.getInstance().getUserId());
            apiParams.put(APIConstants.Attachment.TITLE, " ");
            apiParams.put(APIConstants.Attachment.DESCRIPTION, inputObject);
            return apiParams;
        }
    }

    private class UploadExternalLink extends AbstractNetworkTask<ExternalLinkAttchmentData, Integer, JPackAttachment> {

        private String errorMsg;

        UploadExternalLink(Context context, IAsyncTaskStatusListener listener) {
            super(false, false, false,context, false, false);
            addListener(listener);
        }

        @Override
        protected COMMAND command() {
            return COMMAND.ADD_SHARED_EXTERNAL_LINK_TO_TOPIC;
        }

        @Override
        protected String getFailureMessage() {
            return errorMsg;
        }

        @Override
        protected JPackAttachment executeApi(API api) throws Exception {
            JPackAttachment attachment = null;
            try {
                attachment = (JPackAttachment) api.execute();
            } catch (Exception e) {
                errorMsg = "Failed to upload new attachment (Shared External Link)";
            }
            return attachment;
        }

        @Override
        protected String getContainerIdForObjectStore() {
            return null;
        }

        @Override
        protected Map<String, Object> prepareApiParams(ExternalLinkAttchmentData inputObject) {
            Map<String, Object> apiParams = new HashMap<String, Object>();
            apiParams.put(APIConstants.Topic.ID, inputObject.getTopicId());
            apiParams.put(APIConstants.User.ID, inputObject.getUserId());
            apiParams.put(APIConstants.Attachment.TITLE, inputObject.getTitle());
            apiParams.put(APIConstants.Attachment.DESCRIPTION, inputObject.getDescription());
            apiParams.put(APIConstants.Attachment.ATTACHMENT_URL, inputObject.getAttachmentUrl());
            apiParams.put(APIConstants.Attachment.ATTACHMENT_THUMBNAIL_URL, inputObject.getAttachmentThumbnailUrl());
            return apiParams;
        }
    }

    private class ExternalLinkAttchmentData {

        private String topicId;

        private String packId;

        private String userId;

        private String title;

        private String description;

        private String attachmentUrl;

        private String attachmentThumbnailUrl;

        public String getTopicId() {
            return topicId;
        }

        public void setTopicId(String topicId) {
            this.topicId = topicId;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getAttachmentUrl() {
            return attachmentUrl;
        }

        public void setAttachmentUrl(String attachmentUrl) {
            this.attachmentUrl = attachmentUrl;
        }

        public String getAttachmentThumbnailUrl() {
            return attachmentThumbnailUrl;
        }

        public void setAttachmentThumbnailUrl(String attachmentThumbnailUrl) {
            this.attachmentThumbnailUrl = attachmentThumbnailUrl;
        }
    }*/
}
