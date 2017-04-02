package com.pack.pack.application.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import com.pack.pack.application.AppController;
import com.pack.pack.application.Constants;
import com.pack.pack.application.R;
import com.pack.pack.application.data.util.ApiConstants;
import com.pack.pack.application.data.util.CreateDiscussionTask;
import com.pack.pack.application.data.util.IAsyncTaskStatusListener;
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

public class DiscussionCreateActivity extends AbstractActivity implements RTFListener, IAsyncTaskStatusListener {

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
        CreateDiscussionTask.CreateInfo obj = new CreateDiscussionTask.CreateInfo();
        obj.content = rtfText;
        obj.isReply = getIntent().getBooleanExtra(Constants.DISCUSSION_IS_REPLY, false);
        obj.entityId = getIntent().getStringExtra(Constants.DISCUSSION_ENTITY_ID);
        obj.entityType = getIntent().getStringExtra(Constants.DISCUSSION_ENTITY_TYPE);
        new CreateDiscussionTask(this).execute(obj);
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

    @Override
    public void onPreStart(String taskID) {
        showProgressDialog();
    }

    @Override
    public void onFailure(String taskID, String errorMsg) {
        Toast.makeText(this, "Failed to create discussion", Toast.LENGTH_SHORT);
    }

    @Override
    public void onPostComplete(String taskID) {
        hideProgressDialog();
    }

    @Override
    public void onSuccess(String taskID, Object data) {
        if(data != null && (data instanceof JDiscussion)) {
            done((JDiscussion)data);
        }
        Toast.makeText(this, "Successfully created discussion", Toast.LENGTH_SHORT);
    }
}