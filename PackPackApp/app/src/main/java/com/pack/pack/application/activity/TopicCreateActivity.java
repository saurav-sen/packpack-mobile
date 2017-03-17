package com.pack.pack.application.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.pack.pack.application.AppController;
import com.pack.pack.application.R;
import com.pack.pack.application.data.util.AbstractNetworkTask;
import com.pack.pack.application.data.util.ApiConstants;
import com.pack.pack.application.data.util.FileUtil;
import com.pack.pack.application.data.util.IAsyncTaskStatusListener;
import com.pack.pack.application.data.util.ImageUtil;
import com.pack.pack.application.topic.activity.model.ParcelableTopic;
import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIBuilder;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.client.api.MultipartRequestProgressListener;
import com.pack.pack.model.web.JCategory;
import com.pack.pack.model.web.JTopic;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.pack.pack.application.AppController.IMAGE_PICK_REQUSET_CODE;

/**
 *
 * @author Saurav
 *
 */
public class TopicCreateActivity extends AbstractAppCompatActivity implements IAsyncTaskStatusListener {

    private EditText topic_create_name;
    private EditText topic_create_description;
    private EditText topic_create_localityAddr;
    private EditText topic_create_city;
    private EditText topic_create_country;
    private AutoCompleteTextView topic_create_category;
    private ImageView topic_create_wallpaper;
    private Button wallpaper_select;

    private TextView topic_create_txtPercentage;
    private ProgressBar topic_create_progressBar;

    private AppCompatButton topic_create_submit;

    private File mediaFile;

    private static final String LOG_TAG = "TopicCreateActivity";

    public static final String RESULT_KEY = "topic";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_create);
        
        topic_create_name = (EditText) findViewById(R.id.topic_create_name);
        topic_create_description = (EditText) findViewById(R.id.topic_create_description);
        topic_create_category = (AutoCompleteTextView) findViewById(R.id.topic_create_category);
        topic_create_localityAddr = (EditText) findViewById(R.id.topic_create_localityAddr);
        topic_create_city = (EditText) findViewById(R.id.topic_create_city);
        topic_create_country = (EditText) findViewById(R.id.topic_create_country);
        topic_create_wallpaper = (ImageView) findViewById(R.id.topic_create_wallpaper);
        wallpaper_select = (Button) findViewById(R.id.wallpaper_select);

        topic_create_category.setThreshold(1);
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(this,
                android.support.v7.appcompat.R.layout.select_dialog_item_material,
                AppController.getInstance().getFollowedCategories());
        topic_create_category.setAdapter(categoryAdapter);

        /*SpannableString spannableString = new SpannableString("Select an image, as wallpaper of your topic");
        spannableString.setSpan(new UnderlineSpan(), 0, spannableString.length(), 0);
        wallpaper_select.setText(spannableString);*/

        topic_create_txtPercentage = (TextView) findViewById(R.id.topic_create_txtPercentage);
        topic_create_progressBar = (ProgressBar) findViewById(R.id.topic_create_progressBar);
        topic_create_submit = (AppCompatButton) findViewById(R.id.topic_create_submit);

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
                    if(bitmap == null) {
                        return;
                    }
                    bitmap = ImageUtil.downscaleBitmap(bitmap, 1200, 900);
                    topic_create_wallpaper.setVisibility(View.VISIBLE);
                    topic_create_wallpaper.setImageBitmap(bitmap);

                    /*String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                            Locale.getDefault()).format(new Date());
                    File tempFile = new File(this.getFilesDir().getAbsolutePath(), timeStamp);
                    tempFile.createNewFile();*/
                    String path = FileUtil.getPath(this, uri);
                    if(path == null) {
                        Toast.makeText(this, "Problem reading the file",
                                Toast.LENGTH_LONG).show();
                    } else {
                        mediaFile = new File(path);
                    }
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
        wallpaper_select.setEnabled(true);
        topic_create_submit.setEnabled(true);
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
        String locality = topic_create_localityAddr.getText().toString();
        String city = topic_create_city.getText().toString();
        String country = topic_create_country.getText().toString();
        if(topicName.length() < 5) {
            Toast.makeText(TopicCreateActivity.this, "Name should be of minimum 5 characters long.",
                    Toast.LENGTH_LONG).show();
            return;
        } else if(topicDescription.length() < ApiConstants.MIN_DESC_FIELD_LENGTH) {
            Toast.makeText(TopicCreateActivity.this, "Description should be of minimum 50 characters long.",
                    Toast.LENGTH_LONG).show();
            return;
        } else if(!validateTopicCategory(topicCategory)) {
            Toast.makeText(TopicCreateActivity.this, "Not a valid category name.",
                    Toast.LENGTH_LONG).show();
            return;
        } else if(locality == null || locality.trim().isEmpty()) {
            Toast.makeText(TopicCreateActivity.this, "Locality can't be empty.",
                    Toast.LENGTH_LONG).show();
            return;
        } else if(city == null || city.trim().isEmpty()) {
            Toast.makeText(TopicCreateActivity.this, "City can't be empty.",
                    Toast.LENGTH_LONG).show();
            return;
        } else if(country == null || country.trim().isEmpty()) {
            Toast.makeText(TopicCreateActivity.this, "Country can't be empty.",
                    Toast.LENGTH_LONG).show();
            return;
        } else if(mediaFile == null) {
            Toast.makeText(TopicCreateActivity.this, "Please select topic wallpaper",
                    Toast.LENGTH_LONG).show();
            return;
        }
        wallpaper_select.setEnabled(false);
        topic_create_submit.setEnabled(false);
        TaskData data = new TaskData(topicName, topicDescription, topicCategory, locality, city, country);
        new TopicCreateTask(this).execute(data);
    }

    private boolean validateTopicCategory(String topicCategory) {
        if(topicCategory == null || topicCategory.trim().isEmpty())
            return false;
        List<JCategory> categories = AppController.getInstance().getSupportedCategories().getCategories();
        for(JCategory category : categories) {
            if(category.getLabel().equalsIgnoreCase(topicCategory)) {
                return true;
            }
        }
        return false;
    }

    private class TopicCreateTask extends AbstractNetworkTask<TaskData, Integer, JTopic> {

        private String errorMsg;

        public TopicCreateTask(IAsyncTaskStatusListener listener) {
            super(false, true, TopicCreateActivity.this);
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
        protected String getContainerIdForObjectStore() {
            return null;
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
            String localityAddr = data.getLocality();
            String city = data.getCity();
            String country = data.getCountry();
            apiParams.put(APIConstants.Topic.NAME, topicName);
            apiParams.put(APIConstants.Topic.DESCRIPTION, topicDescription);
            apiParams.put(APIConstants.Topic.CATEGORY, topicCategory);
            apiParams.put(APIConstants.Topic.OWNER_ID, ownerId);
            apiParams.put(APIConstants.Topic.LOCALITY_ADDRESS, localityAddr);
            apiParams.put(APIConstants.Topic.CITY, city);
            apiParams.put(APIConstants.Topic.COUNTRY, country);
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

        private String city;

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        private String country;

        private String locality;

        public String getLocality() {
            return locality;
        }

        public void setLocality(String locality) {
            this.locality = locality;
        }

        TaskData(String topicName, String topicDescription, String topicCategory, String locality, String city, String country) {
            setTopicName(topicName);
            setTopicDescription(topicDescription);
            setTopicCategory(topicCategory);
            setLocality(locality);
            setCity(city);
            setCountry(country);
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
