package com.pack.pack.application.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.pack.pack.application.AppController;
import com.pack.pack.application.R;
import com.pack.pack.application.data.util.AbstractNetworkTask;
import com.pack.pack.application.data.util.ApiConstants;
import com.pack.pack.application.data.util.IAsyncTaskStatusListener;
import com.pack.pack.application.topic.activity.model.ParcelableTopic;
import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.ByteBody;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.client.api.MultipartRequestProgressListener;
import com.pack.pack.model.web.JCategories;
import com.pack.pack.model.web.JCategory;
import com.pack.pack.model.web.JTopic;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TopicEditActivity extends AbstractAppCompatActivity implements IAsyncTaskStatusListener {

    public static final String TOPIC_INPUT = "topicInput";

    public static final String RESULT_KEY = "topic";

    private static final String LOG_TAG = "TopicEditActivity";
    
    private ParcelableTopic topic;
    
    private EditText topic_edit_name;
    private EditText topic_edit_description;
    
    private TextView topic_edit_txtPercentage;
    private ProgressBar topic_edit_progressBar;
    
    private AppCompatButton topic_edit_submit;
    private AppCompatButton topic_edit_cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_edit);

        topic = (ParcelableTopic) getIntent().getParcelableExtra(TOPIC_INPUT);
        
        topic_edit_name = (EditText) findViewById(R.id.topic_edit_name);
        topic_edit_description = (EditText) findViewById(R.id.topic_edit_description);

        topic_edit_name.setText(topic.getTopicName());
        topic_edit_description.setText(topic.getDescription());

        topic_edit_txtPercentage = (TextView) findViewById(R.id.topic_edit_txtPercentage);
        topic_edit_progressBar = (ProgressBar) findViewById(R.id.topic_edit_progressBar);
        topic_edit_submit = (AppCompatButton) findViewById(R.id.topic_edit_submit);
        topic_edit_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doEditTopic();
            }
        });

        topic_edit_cancel = (AppCompatButton) findViewById(R.id.topic_edit_cancel);
        topic_edit_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent();
                newIntent.putExtra(RESULT_KEY, "You have cancelled edit.");
                setResult(RESULT_CANCELED, newIntent);
                finish();
            }
        });
    }

    @Override
    public void onFailure(String taskID, String errorMsg) {
        Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
        Intent newIntent = new Intent();
        newIntent.putExtra(RESULT_KEY, errorMsg);
        setResult(RESULT_CANCELED, newIntent);
        finish();
    }

    @Override
    public void onPreStart(String taskID) {
    }

    @Override
    public void onPostComplete(String taskID) {
    }

    @Override
    public void onSuccess(String taskID, Object data) {
        Toast.makeText(this, "Successfully updated vision",
                Toast.LENGTH_LONG).show();
        JTopic jTopic = (JTopic) data;
        ParcelableTopic pTopic = new ParcelableTopic(jTopic);
        Intent newIntent = new Intent();
        newIntent.putExtra(RESULT_KEY, pTopic);
        setResult(RESULT_OK, newIntent);
        finish();
    }

    private void doEditTopic() {
        JTopic jTopic = null;
        String topicId = topic.getTopicId();
        String topicName = topic_edit_name.getText().toString();
        String topicDescription = topic_edit_description.getText().toString();
        
        int descNoOfWords = topicDescription.split(" ").length;
        if(topicName.length() < 5) {
            Toast.makeText(TopicEditActivity.this, "Name should be of minimum 5 characters long.",
                    Toast.LENGTH_LONG).show();
            return;
        } else if(topicDescription.length() < ApiConstants.MIN_VISION_DESC_FIELD_LENGTH) {
            Toast.makeText(TopicEditActivity.this, "Description should be of minimum " + ApiConstants.MIN_VISION_DESC_FIELD_LENGTH + " characters long.",
                    Toast.LENGTH_LONG).show();
            return;
        } else if(descNoOfWords > ApiConstants.MAX_VISION_DESC_FIELD_LENGTH) {
            Toast.makeText(TopicEditActivity.this, "Description is too long, max words " + ApiConstants.MAX_VISION_DESC_FIELD_LENGTH,
                    Toast.LENGTH_LONG).show();
            return;
        }
        topic_edit_submit.setEnabled(false);
        TaskData data = new TaskData(topicId, topicName, topicDescription);
        new TopicEditTask(this).execute(data);
    }

    private class TaskData {

        private String topicId;
        private String topicName;
        private String topicDescription;

        TaskData(String topicId, String topicName, String topicDescription) {
            setTopicId(topicId);
            setTopicName(topicName);
            setTopicDescription(topicDescription);
        }

        String getTopicId() {
            return topicId;
        }

        void setTopicId(String topicId) {
            this.topicId = topicId;
        }

        String getTopicName() {
            return topicName;
        }

        void setTopicName(String topicName) {
            this.topicName = topicName;
        }

        String getTopicDescription() {
            return topicDescription;
        }

        void setTopicDescription(String topicDescription) {
            this.topicDescription = topicDescription;
        }
    }

    private class TopicEditTask extends AbstractNetworkTask<TaskData, Integer, JTopic> {

        private String errorMsg;

        public TopicEditTask(IAsyncTaskStatusListener listener) {
            super(false, true, TopicEditActivity.this, true);
            addListener(listener);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            topic_edit_progressBar.setVisibility(View.VISIBLE);
            topic_edit_progressBar.setProgress(0);
            topic_edit_txtPercentage.setVisibility(View.VISIBLE);
            topic_edit_txtPercentage.setText("0%");
        }

        @Override
        protected String getContainerIdForObjectStore() {
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            topic_edit_progressBar.setVisibility(View.VISIBLE);
            topic_edit_progressBar.setProgress(progress[0]);
            topic_edit_txtPercentage.setVisibility(View.VISIBLE);
            topic_edit_txtPercentage.setText(String.valueOf(progress[0]) + "%");
        }

        @Override
        protected JTopic executeApi(API api) throws Exception {
            JTopic jTopic = null;
            try {
                MultipartRequestProgressListener listener = new MultipartRequestProgressListener() {
                    @Override
                    public void countTransferProgress(long progress, long total) {
                        int percentage = (int)((progress/total)*100);
                        publishProgress(percentage);
                    }
                };
                jTopic = (JTopic) api.execute(listener);
            } catch (Exception e) {
                errorMsg = "Failed updating Vision details";
                Log.i(LOG_TAG, "Failed updating Vision details :: " + e.getMessage());
            }
            return jTopic;
        }

        @Override
        protected COMMAND command() {
            return COMMAND.EDIT_EXISTING_TOPIC;
        }

        @Override
        protected Map<String, Object> prepareApiParams(TaskData data) {
            Map<String, Object> apiParams = new HashMap<String, Object>();
            String topicId = data.getTopicId();
            String topicName = data.getTopicName();
            String topicDescription = data.getTopicDescription();
            String ownerId = AppController.getInstance().getUserId();
            apiParams.put(APIConstants.Topic.ID, topicId);
            apiParams.put(APIConstants.Topic.NAME, topicName);
            apiParams.put(APIConstants.Topic.DESCRIPTION, topicDescription);
            apiParams.put(APIConstants.Topic.OWNER_ID, ownerId);
            return apiParams;
        }

        @Override
        protected void onPostExecute(JTopic jTopic) {
            super.onPostExecute(jTopic);
            topic_edit_progressBar.setProgress(100);
            topic_edit_txtPercentage.setText("100%");
        }

        @Override
        protected Object getSuccessResult(JTopic result) {
            return result;
        }

        @Override
        protected boolean isSuccess(JTopic result) {
            return result != null;
        }

        @Override
        protected String getFailureMessage() {
            return errorMsg;
        }
    }
}
