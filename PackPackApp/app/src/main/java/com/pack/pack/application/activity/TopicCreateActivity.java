package com.pack.pack.application.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.pack.pack.application.AppController;
import com.pack.pack.application.R;
import com.pack.pack.application.data.util.AbstractNetworkTask;
import com.pack.pack.application.data.util.FileUtil;
import com.pack.pack.application.data.util.IAsyncTaskStatusListener;
import com.pack.pack.application.topic.activity.model.ParcelableTopic;
import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIBuilder;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.client.api.MultipartRequestProgressListener;
import com.pack.pack.model.web.JTopic;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.pack.pack.application.AppController.IMAGE_PICK_REQUSET_CODE;

/**
 *
 * @author Saurav
 *
 */
public class TopicCreateActivity extends AppCompatActivity implements IAsyncTaskStatusListener {

    private EditText topic_create_name;
    private EditText topic_create_description;
    private EditText topic_create_category;
    private ImageView topic_create_wallpaper;
    private TextView wallpaper_select;

    private TextView topic_create_txtPercentage;
    private ProgressBar topic_create_progressBar;

    private Button topic_create_submit;

    private File mediaFile;

    private static final String LOG_TAG = "TopicCreateActivity";

    public static final String RESULT_KEY = "topic";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_create);
        
        topic_create_name = (EditText) findViewById(R.id.topic_create_name);
        topic_create_description = (EditText) findViewById(R.id.topic_create_description);
        topic_create_category = (EditText) findViewById(R.id.topic_create_category);
        topic_create_wallpaper = (ImageView) findViewById(R.id.topic_create_wallpaper);
        wallpaper_select = (TextView) findViewById(R.id.wallpaper_select);

        SpannableString spannableString = new SpannableString("Select an image, as wallpaper of your topic");
        spannableString.setSpan(new UnderlineSpan(), 0, spannableString.length(), 0);
        wallpaper_select.setText(spannableString);

        topic_create_txtPercentage = (TextView) findViewById(R.id.topic_create_txtPercentage);
        topic_create_progressBar = (ProgressBar) findViewById(R.id.topic_create_progressBar);
        topic_create_submit = (Button) findViewById(R.id.topic_create_submit);

        wallpaper_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Wallpaper"),
                        IMAGE_PICK_REQUSET_CODE);
            }
        });

        topic_create_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doCreateTopic();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == IMAGE_PICK_REQUSET_CODE) {
            if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                Uri uri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    topic_create_wallpaper.setVisibility(View.VISIBLE);
                    topic_create_wallpaper.setImageBitmap(bitmap);

                    /*String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                            Locale.getDefault()).format(new Date());
                    File tempFile = new File(this.getFilesDir().getAbsolutePath(), timeStamp);
                    tempFile.createNewFile();*/

                    mediaFile = new File(FileUtil.getPath(this, uri));
                } catch (IOException e) {
                    Log.d(LOG_TAG, "Failed Reading wallpaper (preview) :: " + e.getMessage());
                }
            } else {
                Toast.makeText(this, "You have cancelled wallpaper selection",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onPreStart() {
    }

    @Override
    public void onSuccess(Object data) {
        Toast.makeText(this, "Successfully created new topic",
                Toast.LENGTH_LONG).show();
        JTopic jTopic = (JTopic) data;
        ParcelableTopic pTopic = new ParcelableTopic(jTopic);
        Intent newIntent = new Intent();
        newIntent.putExtra(RESULT_KEY, pTopic);
        setResult(RESULT_OK, newIntent);
        finish();
    }

    @Override
    public void onFailure(String errorMsg) {
        Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
        Intent newIntent = new Intent();
        newIntent.putExtra(RESULT_KEY, errorMsg);
        setResult(RESULT_CANCELED, newIntent);
        finish();
    }

    @Override
    public void onPostComplete() {
    }

    private void doCreateTopic() {
        JTopic jTopic = null;
        String topicName = topic_create_name.getText().toString();
        String topicDescription = topic_create_description.getText().toString();
        String topicCategory = topic_create_category.getText().toString();
        TaskData data = new TaskData(topicName, topicDescription, topicCategory);
        new TopicCreateTask(this).execute(data);
    }

    private class TopicCreateTask extends AbstractNetworkTask<TaskData, Integer, JTopic> {

        private String errorMsg;

        public TopicCreateTask(IAsyncTaskStatusListener listener) {
            addListener(listener);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            topic_create_progressBar.setVisibility(View.VISIBLE);
            topic_create_progressBar.setProgress(0);
            topic_create_txtPercentage.setVisibility(View.VISIBLE);
            topic_create_txtPercentage.setText("0%");
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            topic_create_progressBar.setVisibility(View.VISIBLE);
            topic_create_progressBar.setProgress(progress[0]);
            topic_create_txtPercentage.setVisibility(View.VISIBLE);
            topic_create_txtPercentage.setText(String.valueOf(progress[0]) + "%");
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
                errorMsg = "Failed creating new topic";
                Log.i(LOG_TAG, "Failed creating new topic :: " + e.getMessage());
            }
            return jTopic;
        }

        @Override
        protected COMMAND command() {
            return COMMAND.CREATE_NEW_TOPIC;
        }

        @Override
        protected Map<String, Object> prepareApiParams(TaskData data) {
            Map<String, Object> apiParams = new HashMap<String, Object>();
            String topicName = data.getTopicName();
            String topicDescription = data.getTopicDescription();
            String topicCategory = data.getTopicCategory();
            String ownerId = AppController.getInstance().getUserId();
            apiParams.put(APIConstants.Topic.NAME, topicName);
            apiParams.put(APIConstants.Topic.DESCRIPTION, topicDescription);
            apiParams.put(APIConstants.Topic.CATEGORY, topicCategory);
            apiParams.put(APIConstants.Topic.OWNER_ID, ownerId);
            apiParams.put(APIConstants.Topic.WALLPAPER, mediaFile);
            return apiParams;
        }

        @Override
        protected void onPostExecute(JTopic jTopic) {
            super.onPostExecute(jTopic);
            topic_create_progressBar.setProgress(100);
            topic_create_txtPercentage.setText("100%");
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

    private class TaskData {

        private String topicName;
        private String topicDescription;
        private String topicCategory;

        TaskData(String topicName, String topicDescription, String topicCategory) {
            setTopicName(topicName);
            setTopicDescription(topicDescription);
            setTopicCategory(topicCategory);
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

        String getTopicCategory() {
            return topicCategory;
        }

        void setTopicCategory(String topicCategory) {
            this.topicCategory = topicCategory;
        }
    }
}
