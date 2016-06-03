package com.pack.pack.application.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import com.pack.pack.application.AppController;
import com.pack.pack.application.R;
import com.pack.pack.application.adapters.PackAttachmentsAdapter;
import com.pack.pack.application.topic.activity.model.ParcelablePack;
import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIBuilder;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.model.web.JPack;
import com.pack.pack.model.web.JPackAttachment;
import com.pack.pack.model.web.Pagination;

import java.util.ArrayList;
import java.util.List;

public class PackDetailActivity extends AppCompatActivity {

    private PackAttachmentsAdapter adapter;

    private ProgressDialog progressDialog;

    private ScrollablePackDetail currentScrollableObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pack_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        TextView activity_pack_title = (TextView) findViewById(R.id.activity_pack_title);
        TextView activity_pack_story = (TextView) findViewById(R.id.activity_pack_story);
        ParcelablePack pack = (ParcelablePack) getIntent().getParcelableExtra(AppController.PACK_PARCELABLE_KEY);
        activity_pack_title.setText(pack.getTitle());
        activity_pack_story.setText(pack.getStory());

        ListView activity_pack_attachments = (ListView) findViewById(R.id.activity_pack_attachments);
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

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    AppController.APP_EXTERNAL_STORAGE_WRITE_REQUEST_CODE);
        }

        new LoadPackDetailTask().execute(currentScrollableObject);
    }

    private class ScrollablePackDetail {
        public String packId = null;
        public String topicId = null;
        public String previousLink = null;
        public String nextLink = null;
        public boolean scrollUp = false;
    }

    private class LoadPackDetailTask extends AsyncTask<ScrollablePackDetail, Integer, Pagination<JPackAttachment>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected Pagination<JPackAttachment> doInBackground(ScrollablePackDetail... objects) {
            Pagination<JPackAttachment> page = null;
            if(objects == null || objects.length == 0)
                return page;
            try {
                ScrollablePackDetail obj = objects[0];
                String packId = obj.packId;
                String topicId = obj.topicId;
                String oAuthToken = AppController.getInstance().getoAuthToken();
                String userId = AppController.getInstance().getUserId();
                API api = APIBuilder.create().setAction(COMMAND.GET_ALL_ATTACHMENTS_IN_PACK)
                        .setOauthToken(oAuthToken)
                        .addApiParam(APIConstants.User.ID, userId)
                        .addApiParam(APIConstants.Pack.ID, packId)
                        .addApiParam(APIConstants.Topic.ID, topicId)
                        .addApiParam(APIConstants.PageInfo.PAGE_LINK,
                                obj.scrollUp ? obj.previousLink : obj.nextLink)
                        .build();
                page = (Pagination<JPackAttachment>) api.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return page;
        }

        @Override
        protected void onPostExecute(Pagination<JPackAttachment> page) {
            super.onPostExecute(page);
            if(page != null) {
                adapter.setAttachments(page.getResult());
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
                    AppController.enableShareOption();
                    finish();
                    startActivity(getIntent());
                }
                else {
                    AppController.disableShareOption();
                }
        }
    }
}
