package com.pack.pack.application.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.pack.pack.application.AppController;
import com.pack.pack.application.Constants;
import com.pack.pack.application.R;
import com.pack.pack.application.data.util.AbstractNetworkTask;
import com.pack.pack.application.data.util.IAsyncTaskStatusListener;
import com.pack.pack.application.topic.activity.model.ParcelablePack;
import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.model.web.JPack;

import java.util.HashMap;
import java.util.Map;

public class CreatePackActivity extends AppCompatActivity implements IAsyncTaskStatusListener {

    private String topicId;

    private EditText pack_title;

    private EditText pack_description;

    private AppCompatButton pack_create_submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_pack);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.topicId = getIntent().getStringExtra(AppController.TOPIC_ID_KEY);

        pack_title = (EditText) findViewById(R.id.pack_title);
        pack_description = (EditText) findViewById(R.id.pack_description);
        pack_create_submit = (AppCompatButton) findViewById(R.id.pack_create_submit);
        pack_create_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PackCreateInfo info = new PackCreateInfo();
                info.title = pack_title.getText().toString();
                info.story = pack_description.getText().toString();
                info.userId = AppController.getInstance().getUserId();
                info.topicId = CreatePackActivity.this.topicId;
                doCreatePack(info);
            }
        });
    }

    @Override
    public void onSuccess(Object data) {
        JPack pack = (JPack) data;
        ParcelablePack parcelablePack = new ParcelablePack(pack);
        Intent resultant = new Intent();
        resultant.putExtra(AppController.PACK_PARCELABLE_KEY, parcelablePack);
        setResult(RESULT_OK, resultant);
        finish();
    }

    @Override
    public void onFailure(String errorMsg) {
        Intent resultant = new Intent();
        resultant.putExtra(Constants.ERROR_MSG, errorMsg);
        setResult(RESULT_CANCELED, resultant);
        finish();
    }

    @Override
    public void onPreStart() {
    }

    @Override
    public void onPostComplete() {
    }

    private class PackCreateInfo {
        String story;
        String title;
        String topicId;
        String userId;

        private PackCreateInfo(){}
    }

    private void doCreatePack(PackCreateInfo info) {
        String title = info.title + "";
        String story = info.story + "";
        if(title.length() < 5) {
            Toast.makeText(CreatePackActivity.this, "Title should be of minimum 5 characters long.",
                    Toast.LENGTH_LONG).show();
            return;
        } else if(story.length() < 50) {
            Toast.makeText(CreatePackActivity.this, "Story should be of minimum 50 characters long.",
                    Toast.LENGTH_LONG).show();
            return;
        }
        new CreateTopicTasK(this, this).execute(info);
    }

    private class CreateTopicTasK extends AbstractNetworkTask<PackCreateInfo, Integer, JPack> {

        private String containerId;

        private String errorMsg;

        CreateTopicTasK(Context context, IAsyncTaskStatusListener listener) {
            super(true, true, context);
            addListener(listener);
        }

        @Override
        protected COMMAND command() {
            return COMMAND.CREATE_NEW_PACK;
        }

        @Override
        protected String getFailureMessage() {
            return errorMsg;
        }

        @Override
        protected JPack executeApi(API api) throws Exception {
            JPack pack = null;
            try {
                pack = (JPack) api.execute();
            } catch (Exception e) {
                errorMsg = "Failed creating new pack";
            }
            return pack;
        }

        @Override
        protected String getContainerIdForObjectStore() {
            return containerId;
        }

        @Override
        protected Map<String, Object> prepareApiParams(PackCreateInfo inputObject) {
            Map<String, Object> apiParams = new HashMap<String, Object>();
            apiParams.put(APIConstants.Pack.STORY, inputObject.story);
            apiParams.put(APIConstants.Pack.TITLE, inputObject.title);
            apiParams.put(APIConstants.User.ID, inputObject.userId);
            apiParams.put(APIConstants.Topic.ID, inputObject.topicId);
            containerId = inputObject.topicId;
            return apiParams;
        }
    }
}
