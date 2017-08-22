package com.pack.pack.application.activity;

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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.pack.pack.application.AppController;
import com.pack.pack.application.Constants;
import com.pack.pack.application.R;
import com.pack.pack.application.adapters.TopicThumbnailViewAdapter;
import com.pack.pack.application.data.cache.PackAttachmentsCache;
import com.pack.pack.application.data.util.AbstractNetworkTask;
import com.pack.pack.application.data.util.ApiConstants;
import com.pack.pack.application.data.util.FileUtil;
import com.pack.pack.application.db.DBUtil;
import com.pack.pack.application.db.PaginationInfo;
import com.pack.pack.application.db.UserInfo;
import com.pack.pack.application.service.UploadImageAttachmentService;
import com.pack.pack.application.service.UploadVideoAttachmentService;
import com.pack.pack.application.topic.activity.model.UploadAttachmentData;
import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.common.util.JSONUtil;
import com.pack.pack.model.web.JPackAttachment;
import com.pack.pack.model.web.JTopic;
import com.pack.pack.model.web.PackAttachmentType;
import com.pack.pack.model.web.Pagination;
import com.pack.pack.services.exception.PackPackException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.pack.pack.application.AppController.CAMERA_CAPTURE_PHOTO_REQUEST_CODE;
import static com.pack.pack.application.AppController.TOPIC_ID_KEY;
import static com.pack.pack.application.AppController.UPLOAD_ENTITY_ID_KEY;
import static com.pack.pack.application.AppController.UPLOAD_ENTITY_TYPE_KEY;
import static com.pack.pack.application.AppController.UPLOAD_FILE_IS_PHOTO;

public class ImageVideoShareReceiveActivity extends AppCompatActivity {

    private ListView share_receive_list;

    private TopicThumbnailViewAdapter adapter;

    private Pagination<JTopic> page;

    private Uri imageUri;

    private static final String LOG_TAG = "SharedImageUpload";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_video_share_receive);

        imageUri = (Uri) getIntent().getParcelableExtra(Constants.SHARED_IMAGE_URI_KEY);

        share_receive_list = (ListView) findViewById(R.id.share_receive_list);
        adapter = new TopicThumbnailViewAdapter(this, new ArrayList<JTopic>());
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
        });
        share_receive_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                JTopic topic = adapter.getTopics().get(position);
                uploadSharedImage(topic);
            }
        });

        new LoadAllFamilyAndSocietyTask().execute(AppController.getInstance().getUserId());
    }

    private void showProgressDialog() {

    }

    private void hideProgressDialog() {

    }

    private void uploadSharedImage(JTopic topic) {
        try {
            Uri uri = imageUri;
            if(!FileUtil.checkUploadSize(this, uri)) {
                Toast.makeText(ImageVideoShareReceiveActivity.this, "Selected file larger than 20MB", Toast.LENGTH_LONG).show();
                return;
            }
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                    getContentResolver(), uri);
            startUploadActivity(bitmap, topic);
           /* if(!ApiConstants.IS_PRODUCTION_ENV) {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                        getContentResolver(), data.getData());
                startUploadActivity(bitmap);
            } else {
                Uri selectedPhotoUri = data.getData();
                String path = FileUtil.getPath(this, selectedPhotoUri);
                AppController.getInstance().getUploadAttachmentData().setMediaFileUri(selectedPhotoUri);
                startUploadActivity(path, true);
            }*/
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

        startActivityForResult(intent, Constants.SHARED_IMAGE_UPLOAD_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
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
                //setResult(RESULT_OK, data);
                finish();
                break;
        }
    }

    private class UploadTask extends AsyncTask<Void, Void, Void> {

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
            }
        }
    }

    private class LoadAllFamilyAndSocietyTask extends AbstractNetworkTask<String, Integer, Pagination<JTopic>> {

        private String errorMsg;

        public LoadAllFamilyAndSocietyTask() {
            super(false, false, ImageVideoShareReceiveActivity.this, false);
        }

        /*@Override
        protected Pagination<JTopic> doInBackground(Void... inputObjects) {
            *//*if(xes == null || xes.length == 0)
                return null;*//*
            //setInputObject(null);
            Pagination<JTopic> page = null;
            page = doRetrieveFromDB(getSquillDbHelper().getReadableDatabase(), getInputObject());
            if(page == null) {
                page = doExecuteInBackground(null);
            }
            return page;
        }*/

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
}
