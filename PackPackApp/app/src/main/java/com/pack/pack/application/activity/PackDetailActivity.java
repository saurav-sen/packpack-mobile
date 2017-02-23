package com.pack.pack.application.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.pack.pack.application.AppController;
import com.pack.pack.application.Constants;
import com.pack.pack.application.R;
import com.pack.pack.application.adapters.PackAttachmentsAdapter;
import com.pack.pack.application.data.cache.AppCache;
import com.pack.pack.application.data.util.AbstractNetworkTask;
import com.pack.pack.application.db.DBUtil;
import com.pack.pack.application.db.DbObject;
import com.pack.pack.application.db.PaginationInfo;
import com.pack.pack.application.service.UploadImageAttachmentService;
import com.pack.pack.application.service.UploadResult;
import com.pack.pack.application.service.UploadVideoAttachmentService;
import com.pack.pack.application.topic.activity.model.ParcelablePack;
import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIBuilder;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.common.util.JSONUtil;
import com.pack.pack.model.web.EntityType;
import com.pack.pack.model.web.JPack;
import com.pack.pack.model.web.JPackAttachment;
import com.pack.pack.model.web.PackAttachmentType;
import com.pack.pack.model.web.Pagination;
import com.pack.pack.services.exception.PackPackException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.pack.pack.application.AppController.TOPIC_ID_KEY;
import static com.pack.pack.application.AppController.UPLOAD_ENTITY_ID_KEY;
import static com.pack.pack.application.AppController.UPLOAD_ENTITY_TYPE_KEY;

public class PackDetailActivity extends AppCompatActivity {

    private PackAttachmentsAdapter adapter;

    private ProgressDialog progressDialog;

    private ScrollablePackDetail currentScrollableObject;

    private ParcelablePack pack;

    private TextView activity_pack_title;

    private ListView activity_pack_attachments;

    private FloatingActionButton fab;

    private static final String LOG_TAG = "PackDetailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pack_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        pack = (ParcelablePack) getIntent().getParcelableExtra(AppController.PACK_PARCELABLE_KEY);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PackDetailActivity.this, ImageVideoCaptureActivity.class);
                intent.putExtra(TOPIC_ID_KEY, pack.getParentTopicId());
                intent.putExtra(UPLOAD_ENTITY_ID_KEY, pack.getId());
                intent.putExtra(UPLOAD_ENTITY_TYPE_KEY, JPackAttachment.class.getName());
                startActivityForResult(intent, Constants.IMAGE_VIDEO_CAPTURE_REQUEST_CODE);
            }
        });

        activity_pack_title = (TextView) findViewById(R.id.activity_pack_title);
        TextView activity_pack_story = (TextView) findViewById(R.id.activity_pack_story);
        activity_pack_title.setText(pack.getTitle());
        activity_pack_story.setText(pack.getStory());

        activity_pack_attachments = (ListView) findViewById(R.id.activity_pack_attachments);
        adapter = new PackAttachmentsAdapter(this, new ArrayList<JPackAttachment>(10));
        activity_pack_attachments.setAdapter(adapter);
        activity_pack_attachments.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });

        currentScrollableObject = new ScrollablePackDetail();
        currentScrollableObject.packId = pack.getId();
        currentScrollableObject.topicId = pack.getParentTopicId();
        currentScrollableObject.scrollUp = false;
        currentScrollableObject.nextLink = "FIRST_PAGE";

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    AppController.APP_EXTERNAL_STORAGE_WRITE_REQUEST_CODE);
        }

        new LoadPackDetailTask().execute(currentScrollableObject);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_menu, menu);
        MenuItem item0 = menu.findItem(R.id.app_settings);
        if(item0 != null) {
            item0.setVisible(true);
        }
        MenuItem item1 = menu.findItem(R.id.enter_forum);
        if(item1 != null) {
            item1.setVisible(false);
        }
        invalidateOptionsMenu();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.app_settings:
                Intent intent = new Intent(PackDetailActivity.this, SettingsActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(currentScrollableObject.nextLink != null) {
            pack.setNextLink(currentScrollableObject.nextLink);
        }
        if(currentScrollableObject.previousLink != null) {
            pack.setPreviousLink(currentScrollableObject.previousLink);
        }
        outState.putParcelable(AppController.PACK_PARCELABLE_KEY, pack);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        pack = (ParcelablePack) savedInstanceState.getParcelable(AppController.PACK_PARCELABLE_KEY);
        currentScrollableObject = new ScrollablePackDetail();
        currentScrollableObject.packId = pack.getId();
        currentScrollableObject.topicId = pack.getParentTopicId();
        currentScrollableObject.scrollUp = false;
        currentScrollableObject.nextLink = pack.getNextLink();
        currentScrollableObject.previousLink = pack.getPreviousLink();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Constants.IMAGE_VIDEO_CAPTURE_REQUEST_CODE) {
            if(resultCode == RESULT_OK) {
                //currentScrollableObject.nextLink = "FIRST_PAGE"; // TODO -- This is to get going for demo purpose
                //new LoadPackDetailTask().execute(currentScrollableObject);
                try {
                    String json = data.getStringExtra(UploadActivity.ATTACHMENT_UNDER_UPLOAD);

                    if(json != null && !json.trim().isEmpty()) {
                        JPackAttachment attachment = JSONUtil.deserialize(json, JPackAttachment.class, true);
                        adapter.getAttachments().add(attachment);
                        adapter.notifyDataSetChanged();
                        if(PackAttachmentType.IMAGE.name().equals(attachment.getMimeType())) {
                            String packId = data.getStringExtra(UploadImageAttachmentService.PACK_ID);
                            String topicId = data.getStringExtra(UploadImageAttachmentService.TOPIC_ID);
                            Intent service = new Intent(PackDetailActivity.this, UploadImageAttachmentService.class);
                            service.putExtra(UploadImageAttachmentService.ATTACHMENT_TITLE, attachment.getTitle());
                            service.putExtra(UploadImageAttachmentService.ATTACHMENT_DESCRIPTION, attachment.getDescription());
                            service.putExtra(UploadImageAttachmentService.PACK_ID, packId);
                            service.putExtra(UploadImageAttachmentService.TOPIC_ID, topicId);
                            service.putExtra(UploadImageAttachmentService.ATTACHMENT_ID, attachment.getId());
                            startService(service);
                        } else if(PackAttachmentType.VIDEO.name().equals(attachment.getMimeType())) {
                            String packId = data.getStringExtra(UploadVideoAttachmentService.PACK_ID);
                            String topicId = data.getStringExtra(UploadVideoAttachmentService.TOPIC_ID);
                            String selectedInputVideoFilePath = data.getStringExtra(UploadVideoAttachmentService.SELECTED_INPUT_VIDEO_FILE);
                            Intent service = new Intent(PackDetailActivity.this, UploadVideoAttachmentService.class);
                            service.putExtra(UploadVideoAttachmentService.SELECTED_INPUT_VIDEO_FILE, selectedInputVideoFilePath);
                            service.putExtra(UploadVideoAttachmentService.ATTACHMENT_TITLE,  attachment.getTitle());
                            service.putExtra(UploadVideoAttachmentService.ATTACHMENT_DESCRIPTION, attachment.getDescription());
                            service.putExtra(UploadVideoAttachmentService.PACK_ID, packId);
                            service.putExtra(UploadVideoAttachmentService.TOPIC_ID, topicId);
                            service.putExtra(UploadVideoAttachmentService.ATTACHMENT_ID, attachment.getId());
                            startService(service);
                        }
                    }
                } catch (PackPackException e) {
                    Log.d(LOG_TAG, e.getMessage(), e);
                }
            } else if(resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "You have cancelled upload", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppController.getInstance().getPackAttachments().clear();
    }

    private class ScrollablePackDetail {
        public String packId = null;
        public String topicId = null;
        public String previousLink = null;
        public String nextLink = null;
        public boolean scrollUp = false;
    }

    private class LoadPackDetailTask extends AbstractNetworkTask<ScrollablePackDetail, Integer, Pagination<JPackAttachment>> {

        private String errorMsg;

        public LoadPackDetailTask() {
            super(true, true, PackDetailActivity.this);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected String getContainerIdForObjectStore() {
            return getInputObject().packId;
        }

        @Override
        protected Pagination<JPackAttachment> doRetrieveFromDB(SQLiteDatabase readable, ScrollablePackDetail inputObject) {
            Pagination<JPackAttachment> page = null;
            List<JPackAttachment> attachments = DBUtil.loadAllAttachmentInfo(readable, inputObject.packId);
            //List<JPackAttachment> attachments = DBUtil.loadAllJsonModelByContainerId(readable, inputObject.packId, JPackAttachment.class);
            if(attachments != null && !attachments.isEmpty()) {
                PaginationInfo paginationInfo = DBUtil.loadPaginationInfo(readable, inputObject.packId);
                page = new Pagination<JPackAttachment>();
                page.setResult(attachments);
                if(paginationInfo != null) {
                    page.setNextLink(paginationInfo.getNextLink());
                    page.setPreviousLink(paginationInfo.getPreviousLink());
                }
            }
            return page;
        }

        @Override
        protected Map<String, Object> prepareApiParams(ScrollablePackDetail inputObject) {
            Map<String, Object> apiParams = new HashMap<String, Object>();
            String packId = inputObject.packId;
            String topicId = inputObject.topicId;
            String userId = AppController.getInstance().getUserId();
            apiParams.put(APIConstants.User.ID, userId);
            apiParams.put(APIConstants.Pack.ID, packId);
            apiParams.put(APIConstants.Topic.ID, topicId);
            apiParams.put(APIConstants.PageInfo.PAGE_LINK,
                    inputObject.scrollUp ? inputObject.previousLink
                            : inputObject.nextLink);
            return apiParams;
        }

        @Override
        protected String getPaginationContainerClassName() {
            return JPack.class.getName();
        }

        @Override
        protected String getPaginationContainerId() {
            return getInputObject().packId;
        }

        @Override
        protected COMMAND command() {
            return COMMAND.GET_ALL_ATTACHMENTS_IN_PACK;
        }

        @Override
        protected Pagination<JPackAttachment> executeApi(API api) throws Exception {
            Pagination<JPackAttachment> page = null;
            try {
                page = (Pagination<JPackAttachment>) api.execute();
            } catch (Exception e) {
                errorMsg = e.getMessage();
            }
            return page;
        }

        @Override
        protected String getFailureMessage() {
            return errorMsg;
        }

        @Override
        protected void onPostExecute(Pagination<JPackAttachment> page) {
            super.onPostExecute(page);
            if(page != null) {
                List<JPackAttachment> attachments = page.getResult();
                AppController.getInstance().getPackAttachments().clear();
                AppController.getInstance().getPackAttachments().addAll(attachments);
                adapter.setAttachments(attachments);
                adapter.notifyDataSetChanged();
                if(currentScrollableObject != null) {
                    currentScrollableObject.nextLink = page.getNextLink();
                    currentScrollableObject.previousLink = page.getPreviousLink();
                }
            }
            hideProgressDialog();
        }

        private void showProgressDialog() {
            PackDetailActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog = new ProgressDialog(PackDetailActivity.this);
                    progressDialog.setMessage("Loading...");
                    progressDialog.show();
                }
            });
        }

        private void hideProgressDialog() {
            PackDetailActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case AppController.APP_EXTERNAL_STORAGE_WRITE_REQUEST_CODE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    AppController.getInstance().enableShareOption();
                    finish();
                    startActivity(getIntent());
                }
                else {
                    AppController.getInstance().disableShareOption();
                }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter("UPLOAD_ATTACHMENT"));
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String oldAttachmentId = intent.getStringExtra(UploadResult.ATTACHMENT_OLD_ID);
            String newAttachmentId = intent.getStringExtra(UploadResult.ATTACHMENT_NEW_ID);
            String status = intent.getStringExtra(UploadResult.STATUS);
            if(UploadResult.OK_STATUS.equals(status)) {
                JPackAttachment newAttachment = AppCache.INSTANCE.getSuccessfullyUploadedAttachment(newAttachmentId);
                adapter.onUploadSuccess(oldAttachmentId, newAttachment);
                //adapter.notifyDataSetChanged();
            } else {
                adapter.onUploadError(oldAttachmentId);
                //adapter.notifyDataSetChanged();
            }
        }
    };
}
