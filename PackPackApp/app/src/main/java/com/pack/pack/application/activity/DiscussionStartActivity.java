package com.pack.pack.application.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.pack.pack.application.Constants;
import com.pack.pack.application.R;
import com.pack.pack.application.data.util.CreateDiscussionTask;
import com.pack.pack.application.data.util.IAsyncTaskStatusListener;
import com.pack.pack.application.topic.activity.model.ParcelableDiscussion;
import com.pack.pack.model.web.JDiscussion;

/**
 * Created by Saurav on 21-10-2016.
 */
public class DiscussionStartActivity extends Activity implements IAsyncTaskStatusListener {

    private EditText discussion_title0;

    private EditText discussion_description0;

    private Button discussion_start_submit0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_discussion);

        discussion_title0 = (EditText) findViewById(R.id.discussion_title0);
        discussion_description0 = (EditText) findViewById(R.id.discussion_description0);
        discussion_start_submit0 = (Button) findViewById(R.id.discussion_start_submit0);
        discussion_start_submit0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateDiscussionTask.CreateInfo obj = new CreateDiscussionTask.CreateInfo();
                obj.title = discussion_title0.getText().toString();
                obj.content = discussion_description0.getText().toString();
                obj.isReply = getIntent().getBooleanExtra(Constants.DISCUSSION_IS_REPLY, false);
                obj.entityId = getIntent().getStringExtra(Constants.DISCUSSION_ENTITY_ID);
                obj.entityType = getIntent().getStringExtra(Constants.DISCUSSION_ENTITY_TYPE);
                new CreateDiscussionTask(DiscussionStartActivity.this).execute(obj);
            }
        });
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
    public void onPreStart() {
        showProgressDialog();
    }

    @Override
    public void onFailure(String errorMsg) {
        Toast.makeText(this, "Failed to create discussion", Toast.LENGTH_SHORT);
    }

    @Override
    public void onPostComplete() {
        hideProgressDialog();
    }

    @Override
    public void onSuccess(Object data) {
        if(data != null && (data instanceof JDiscussion)) {
            done((JDiscussion)data);
        }
        Toast.makeText(this, "Successfully created discussion", Toast.LENGTH_SHORT);
    }
}
