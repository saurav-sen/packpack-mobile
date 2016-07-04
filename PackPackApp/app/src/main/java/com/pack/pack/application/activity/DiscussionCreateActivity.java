package com.pack.pack.application.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.pack.pack.application.AppController;
import com.pack.pack.application.Constants;
import com.pack.pack.application.R;
import com.pack.pack.application.topic.activity.model.ParcelableDiscussion;
import com.pack.pack.application.view.RTFEditor;
import com.pack.pack.application.view.RTFListener;
import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIBuilder;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.model.web.EntityType;
import com.pack.pack.model.web.JDiscussion;

import org.apache.commons.lang3.StringEscapeUtils;

public class DiscussionCreateActivity extends Activity implements RTFListener {

    private RTFEditor editor;
    private String rtfText;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_discussion);

        editor = (RTFEditor) findViewById(R.id.discussion_editor);
        editor.setOnSaveListener(this);
    }

    @Override
    public void onSave(String rtfText) {
        rtfText = StringEscapeUtils.escapeHtml4(rtfText);
        CreateInfo obj = new CreateInfo();
        obj.content = rtfText;
        obj.isReply = getIntent().getBooleanExtra(Constants.DISCUSSION_IS_REPLY, false);
        obj.entityId = getIntent().getStringExtra(Constants.DISCUSSION_ENTITY_ID);
        obj.entityType = getIntent().getStringExtra(Constants.DISCUSSION_ENTITY_TYPE);
        new CreateDiscussionTask().execute(obj);
    }

    public void done(JDiscussion discussion) {
        Intent intent = getIntent();
        intent.putExtra(Constants.PARCELLABLE_DISCUSSION_KEY, new ParcelableDiscussion(discussion));
        this.setResult(RESULT_OK, intent);
        finish();
    }

    private void showProgressDialog() {
        /*DiscussionCreateActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog = new ProgressDialog(DiscussionCreateActivity.this);
                progressDialog.setMessage("...");
                progressDialog.show();
            }
        });*/
    }

    private void hideProgressDialog() {
        /*DiscussionCreateActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                    progressDialog = null;
                }
            }
        });*/
    }

    private class CreateInfo {
        public String entityId;
        public String entityType;
        public String title = "";
        public String content = "";
        public boolean isReply;
    }

    private class CreateDiscussionTask extends AsyncTask<CreateInfo, Integer, JDiscussion> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected JDiscussion doInBackground(CreateInfo... createInfos) {
            if(createInfos == null || createInfos.length == 0)
                return null;
            JDiscussion discussion = null;
            try {
                CreateInfo createInfo = createInfos[0];
                String oAuthToken = AppController.getInstance().getoAuthToken();
                String userId = AppController.getInstance().getUserId();
                COMMAND command = null;
                String parentIdKey = null;
                if(EntityType.TOPIC.name().equalsIgnoreCase(createInfo.entityType)) {
                    command = COMMAND.START_DISCUSSION_ON_TOPIC;
                    parentIdKey = APIConstants.Topic.ID;
                } else if(EntityType.PACK.name().equalsIgnoreCase(createInfo.entityType)) {
                    command = COMMAND.START_DISCUSSION_ON_PACK;
                    parentIdKey = APIConstants.Pack.ID;
                }

                API api = APIBuilder.create().setOauthToken(oAuthToken)
                        .setAction(command)
                        .addApiParam(APIConstants.User.ID, userId)
                        .addApiParam(parentIdKey, createInfo.entityId)
                        .addApiParam(APIConstants.Discussion.TITLE, createInfo.title)
                        .addApiParam(APIConstants.Discussion.CONTENT, createInfo.content)
                        .build();
                discussion = (JDiscussion) api.execute();
            } catch (Exception e) {
               // e.printStackTrace();
            }
            return discussion;
        }

        @Override
        protected void onPostExecute(JDiscussion jDiscussion) {
            super.onPostExecute(jDiscussion);
            hideProgressDialog();
            done(jDiscussion);
        }
    }
}