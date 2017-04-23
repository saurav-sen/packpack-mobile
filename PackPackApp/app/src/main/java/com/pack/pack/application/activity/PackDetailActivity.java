package com.pack.pack.application.activity;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
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
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pack.pack.application.AppController;
import com.pack.pack.application.Constants;
import com.pack.pack.application.R;
import com.pack.pack.application.adapters.PackAttachmentsAdapter;
import com.pack.pack.application.data.cache.InMemory;
import com.pack.pack.application.data.cache.PackAttachmentsCache;
import com.pack.pack.application.data.util.AbstractNetworkTask;
import com.pack.pack.application.data.util.IAsyncTaskStatusListener;
import com.pack.pack.application.db.DBUtil;
import com.pack.pack.application.db.DbObject;
import com.pack.pack.application.db.PaginationInfo;
import com.pack.pack.application.image.loader.DownloadImageTask;
import com.pack.pack.application.service.UploadImageAttachmentService;
import com.pack.pack.application.service.UploadResult;
import com.pack.pack.application.service.UploadVideoAttachmentService;
import com.pack.pack.application.topic.activity.model.ParcelablePack;
import com.pack.pack.application.topic.activity.model.ParcelableTopic;
import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIBuilder;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.common.util.JSONUtil;
import com.pack.pack.model.web.EntityType;
import com.pack.pack.model.web.JPack;
import com.pack.pack.model.web.JPackAttachment;
import com.pack.pack.model.web.JRssFeed;
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

public class PackDetailActivity extends AbstractAppCompatActivity {

    private PackAttachmentsAdapter adapter;

    private ProgressDialog progressDialog;

    private ScrollablePackDetail currentScrollableObject;

    private ParcelablePack pack;

    private TextView activity_pack_title;

    private TextView activity_pack_story;

    private ImageButton activity_pack_see_more;

    private ListView activity_pack_attachments;

    private FloatingActionButton fab_edit;

    private FloatingActionButton fab_upload;

    private FloatingActionButton fab_copy_link;

    private static final String LOG_TAG = "PackDetailActivity";

    private boolean expanded = false;

    private int descriptionLineCount = 0;

    private String shortStory;
    private String longStory;

    private boolean expandable = true;

    private static final int DELETE_MENU_ITEM = 1;

    private Animation fab_open;
    private Animation fab_close;
    private Animation rotate_forward;
    private Animation rotate_backward;

    private boolean showFab = false;

    private JRssFeed selectedFeedForUpload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pack_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        pack = (ParcelablePack) getIntent().getParcelableExtra(AppController.PACK_PARCELABLE_KEY);
        ParcelableTopic topic = InMemory.INSTANCE.get(pack.getParentTopicId());

        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_backward);

        fab_edit = (FloatingActionButton) findViewById(R.id.fab_edit);
        fab_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(showFab){
                    fab_edit.startAnimation(rotate_backward);
                    fab_upload.startAnimation(fab_close);
                    fab_copy_link.startAnimation(fab_close);
                    fab_upload.setClickable(false);
                    fab_copy_link.setClickable(false);
                    showFab = false;
                } else {
                    fab_edit.startAnimation(rotate_forward);
                    fab_upload.startAnimation(fab_open);
                    fab_copy_link.startAnimation(fab_open);
                    fab_upload.setClickable(true);
                    fab_copy_link.setClickable(true);
                    showFab = true;
                }
            }
        });

        fab_upload = (FloatingActionButton) findViewById(R.id.fab_upload);
        fab_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PackDetailActivity.this, ImageVideoCaptureActivity.class);
                intent.putExtra(TOPIC_ID_KEY, pack.getParentTopicId());
                intent.putExtra(UPLOAD_ENTITY_ID_KEY, pack.getId());
                intent.putExtra(UPLOAD_ENTITY_TYPE_KEY, JPackAttachment.class.getName());
                startActivityForResult(intent, Constants.IMAGE_VIDEO_CAPTURE_REQUEST_CODE);
            }
        });

        fab_copy_link = (FloatingActionButton) findViewById(R.id.fab_copy_link);
        fab_copy_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedFeedForUpload = null;
                final Dialog copyLinkDialog = new Dialog(PackDetailActivity.this);
                copyLinkDialog.setContentView(R.layout.copy_link);
                copyLinkDialog.setTitle("Share External Link");

                final EditText copy_link_url = (EditText) copyLinkDialog.findViewById(R.id.copy_link_url);
                Button copy_link_ok = (Button) copyLinkDialog.findViewById(R.id.copy_link_ok);

                final RelativeLayout feed_display = (RelativeLayout) copyLinkDialog.findViewById(R.id.feed_display);
                final ImageView feed_image = (ImageView) copyLinkDialog.findViewById(R.id.feed_image);
                final TextView feed_title = (TextView) copyLinkDialog.findViewById(R.id.feed_title);
                //final TextView feed_description = (TextView) copyLinkDialog.findViewById(R.id.feed_description);

                final Button copy_link_done = (Button) copyLinkDialog.findViewById(R.id.copy_link_done);

                final IAsyncTaskStatusListener testLinkListener = new IAsyncTaskStatusListener() {
                    @Override
                    public void onPreStart(String taskID) {
                    }

                    @Override
                    public void onSuccess(String taskID, Object data) {
                        if(data != null) {
                            feed_display.setVisibility(View.VISIBLE);
                            copy_link_done.setVisibility(View.VISIBLE);
                            selectedFeedForUpload = (JRssFeed) data;
                            feed_display.setTag(selectedFeedForUpload.getOgUrl());
                        }
                        if(selectedFeedForUpload != null) {
                            new DownloadImageTask(feed_image, 200, 200, PackDetailActivity.this).execute(selectedFeedForUpload.getOgImage());
                            feed_title.setText(selectedFeedForUpload.getOgTitle());
                            //feed_description.setText(selectedFeedForUpload.getOgDescription());
                        }
                    }

                    @Override
                    public void onFailure(String taskID, String errorMsg) {
                        feed_display.setVisibility(View.GONE);
                        copy_link_done.setVisibility(View.GONE);
                        feed_display.setTag(null);
                    }

                    @Override
                    public void onPostComplete(String taskID) {

                    }
                };

                copy_link_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String url = copy_link_url.getText() != null ? copy_link_url.getText().toString() : null;
                        if(url != null && !url.trim().isEmpty()) {
                            new ReadCopiedLink(PackDetailActivity.this, testLinkListener).execute(url);
                        }
                    }
                });

                final IAsyncTaskStatusListener uploadLinkListener = new IAsyncTaskStatusListener() {
                    @Override
                    public void onPreStart(String taskID) {

                    }

                    @Override
                    public void onSuccess(String taskID, Object data) {
                        copyLinkDialog.dismiss();
                        if(data != null && (data instanceof JPackAttachment)) {
                            JPackAttachment attachment = (JPackAttachment) data;
                            adapter.getAttachments().add(attachment);
                            adapter.notifyDataSetChanged();
                        } else {
                            Snackbar.make(activity_pack_title, "Failed to upload link", Snackbar.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(String taskID, String errorMsg) {
                        copyLinkDialog.dismiss();
                        Snackbar.make(activity_pack_title, "Failed to upload link", Snackbar.LENGTH_LONG).show();
                    }

                    @Override
                    public void onPostComplete(String taskID) {

                    }
                };

                copy_link_done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(selectedFeedForUpload != null) {
                            ExternalLinkAttchmentData uploadData = new ExternalLinkAttchmentData();
                            uploadData.setTopicId(pack.getParentTopicId());
                            uploadData.setPackId(pack.getId());
                            uploadData.setUserId(AppController.getInstance().getUserId());
                            uploadData.setTitle(selectedFeedForUpload.getOgTitle());
                            uploadData.setDescription(selectedFeedForUpload.getOgDescription());
                            uploadData.setAttachmentUrl(selectedFeedForUpload.getOgUrl());
                            uploadData.setAttachmentThumbnailUrl(selectedFeedForUpload.getOgImage());

                            new UploadExternalLink(PackDetailActivity.this, uploadLinkListener).execute(uploadData);
                        }
                        selectedFeedForUpload = null;
                    }
                });

                copyLinkDialog.show();
            }
        });

        if(topic != null && !topic.isFollowing()) {
            fab_edit.setVisibility(View.GONE);
        } else {
            fab_edit.setVisibility(View.VISIBLE);
        }

        activity_pack_title = (TextView) findViewById(R.id.activity_pack_title);
        activity_pack_story = (TextView) findViewById(R.id.activity_pack_story);
        activity_pack_title.setText(pack.getTitle());

        activity_pack_see_more = (ImageButton) findViewById(R.id.activity_pack_see_more);

        activity_pack_story.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (expandable && descriptionLineCount > 3) {
                    activity_pack_see_more.setVisibility(View.VISIBLE);
                    ObjectAnimator animation = ObjectAnimator.ofInt(activity_pack_story, "maxLines", 3);
                    animation.setDuration(0).start();
                    expandable = false;
                }
            }
        });

        activity_pack_see_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!expanded) {
                    expanded = true;
                    ObjectAnimator animation = ObjectAnimator.ofInt(activity_pack_story, "maxLines", descriptionLineCount);
                    animation.setDuration(100).start();
                    activity_pack_story.setText(longStory);
                    activity_pack_see_more.setImageDrawable(ContextCompat.getDrawable(PackDetailActivity.this, R.drawable.ic_expand_less));
                } else {
                    expanded = false;
                    activity_pack_story.setText(shortStory);
                    ObjectAnimator animation = ObjectAnimator.ofInt(activity_pack_story, "maxLines", 3);
                    animation.setDuration(100).start();
                    activity_pack_see_more.setImageDrawable(ContextCompat.getDrawable(PackDetailActivity.this,R.drawable.ic_expand_more));
                }
            }
        });

        longStory = pack.getStory();

        String[] split = longStory.split("[\n|\r]");
        descriptionLineCount = split.length;

        if(descriptionLineCount > 3) {
            //shortStory = new StringBuilder().append(split[0]).append("\n").append(split[1]).toString();
            StringBuilder str = new StringBuilder();
            int lineCount = 0;
            for(int i=0; lineCount < 2 && i<descriptionLineCount; i++) {
                String s = split[i];
                if(s.trim().length() > 0) {
                    if(lineCount > 0) {
                        str.append("\n");
                    }
                    lineCount++;
                }
                str.append(s);
            }
            shortStory = str.toString();
        } else {
            shortStory = longStory;
            activity_pack_see_more.setVisibility(View.GONE);
        }

        activity_pack_story.setText(shortStory);

        activity_pack_attachments = (ListView) findViewById(R.id.activity_pack_attachments);
        adapter = new PackAttachmentsAdapter(this, new ArrayList<JPackAttachment>(10), topic, pack);
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
                        /*adapter.notifyDataSetChanged();
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
                        }*/
                        if(PackAttachmentType.IMAGE.name().equals(attachment.getMimeType())) {
                            String packId = data.getStringExtra(UploadImageAttachmentService.PACK_ID);
                            String topicId = data.getStringExtra(UploadImageAttachmentService.TOPIC_ID);
                            adapter.scheduleAttachmentUpload(packId, topicId, attachment, null);
                        } else if(PackAttachmentType.VIDEO.name().equals(attachment.getMimeType())) {
                            String packId = data.getStringExtra(UploadVideoAttachmentService.PACK_ID);
                            String topicId = data.getStringExtra(UploadVideoAttachmentService.TOPIC_ID);
                            String selectedInputVideoFilePath = data.getStringExtra(UploadVideoAttachmentService.SELECTED_INPUT_VIDEO_FILE);
                            adapter.scheduleAttachmentUpload(packId, topicId, attachment, selectedInputVideoFilePath);
                        }
                        adapter.notifyDataSetChanged();
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
            super(true, true, PackDetailActivity.this, false);
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
                List<JPackAttachment> uploadInProgressAttachments = PackAttachmentsCache
                        .open(PackDetailActivity.this).getUploadInProgressAttachments(
                                getInputObject().packId);
                if(uploadInProgressAttachments != null) {
                    attachments.addAll(uploadInProgressAttachments);
                }
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
            String packId = intent.getStringExtra(UploadResult.PACK_ID);
            String oldAttachmentId = intent.getStringExtra(UploadResult.ATTACHMENT_OLD_ID);
            String newAttachmentId = intent.getStringExtra(UploadResult.ATTACHMENT_NEW_ID);
            String status = intent.getStringExtra(UploadResult.STATUS);
            if(UploadResult.OK_STATUS.equals(status)) {
                List<JPackAttachment> successfullyUploadedAttachments = PackAttachmentsCache.open(context).getSuccessfullyUploadedAttachments(packId);
                Map<String, JPackAttachment> successfullyUploadedAttachmentsMap = new HashMap<String, JPackAttachment>();
                for(JPackAttachment successfullyUploadedAttachment : successfullyUploadedAttachments) {
                    successfullyUploadedAttachmentsMap.put(successfullyUploadedAttachment.getId(), successfullyUploadedAttachment);
                }
                Map<String, String> inProgressVssuccessfulUploadAttachmentsMap = PackAttachmentsCache.open(context).getSuccessfulUploadVsInProgressAttachmentsMap();
                adapter.onUploadSuccess(packId, inProgressVssuccessfulUploadAttachmentsMap, successfullyUploadedAttachmentsMap);
                //adapter.notifyDataSetChanged();
            } else {
                adapter.onUploadError(oldAttachmentId);
                //adapter.notifyDataSetChanged();
            }
        }
    };

    private class ReadCopiedLink extends AbstractNetworkTask<String, Integer, JRssFeed> {

        private String errorMsg;

        ReadCopiedLink(Context context, IAsyncTaskStatusListener listener) {
            super(false, false, false,context, false, true);
            addListener(listener);
        }

        @Override
        protected COMMAND command() {
            return COMMAND.CRAWL_FEED;
        }

        @Override
        protected String getFailureMessage() {
            return errorMsg;
        }

        @Override
        protected JRssFeed executeApi(API api) throws Exception {
            JRssFeed feed = null;
            try {
                feed = (JRssFeed) api.execute();
            } catch (Exception e) {
                errorMsg = "Failed reading from external link";
            }
            return feed;
        }

        @Override
        protected String getContainerIdForObjectStore() {
            return null;
        }

        @Override
        protected Map<String, Object> prepareApiParams(String inputObject) {
            Map<String, Object> apiParams = new HashMap<String, Object>();
            apiParams.put(APIConstants.ExternalResource.RESOURCE_URL, inputObject);
            return apiParams;
        }
    }

    private class UploadExternalLink extends AbstractNetworkTask<ExternalLinkAttchmentData, Integer, JPackAttachment> {

        private String errorMsg;

        UploadExternalLink(Context context, IAsyncTaskStatusListener listener) {
            super(false, false, false,context, true, true);
            addListener(listener);
        }

        @Override
        protected COMMAND command() {
            return COMMAND.ADD_VIDEO_TO_PACK_EXTERNAL_LINK;
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
                errorMsg = "Failed reading to upload new attachment";
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
            apiParams.put(APIConstants.Pack.ID, inputObject.getPackId());
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

        public String getPackId() {
            return packId;
        }

        public void setPackId(String packId) {
            this.packId = packId;
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
    }
}
