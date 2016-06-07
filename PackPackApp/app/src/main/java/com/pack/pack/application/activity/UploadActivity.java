package com.pack.pack.application.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.pack.pack.application.AppController;
import com.pack.pack.application.R;
import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIBuilder;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.client.api.MultipartRequestProgressListener;
import com.pack.pack.model.web.JPackAttachment;

import java.io.File;

import static com.pack.pack.application.AppController.TOPIC_ID_KEY;
import static com.pack.pack.application.AppController.UPLOAD_ENTITY_ID_KEY;
import static com.pack.pack.application.AppController.UPLOAD_ENTITY_TYPE_KEY;
import static com.pack.pack.application.AppController.UPLOAD_FILE_IS_PHOTO;
import static com.pack.pack.application.AppController.UPLOAD_FILE_PATH;

/**
 *
 * @author Saurav
 *
 */
public class UploadActivity extends Activity {

    private String topicId;

    private String uploadEntityId;

    private String uploadEntityType;

    private String mediaFilePath;

    private boolean isPhotoUpload = true;

    private TextView upload_txtPercentage;
    private ImageView upload_imgPreview;
    private VideoView upload_videoPreview;
    private Button upload_submit;
    private ProgressBar upload_progressBar;

    private static final String LOG_TAG = "UploadActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        topicId = getIntent().getStringExtra(TOPIC_ID_KEY);
        uploadEntityId = getIntent().getStringExtra(UPLOAD_ENTITY_ID_KEY);
        uploadEntityType = getIntent().getStringExtra(UPLOAD_ENTITY_TYPE_KEY);
        mediaFilePath = getIntent().getStringExtra(UPLOAD_FILE_PATH);
        isPhotoUpload = getIntent().getBooleanExtra(UPLOAD_FILE_IS_PHOTO, true);

        upload_txtPercentage = (TextView) findViewById(R.id.upload_txtPercentage);
        upload_submit = (Button) findViewById(R.id.upload_submit);
        upload_progressBar = (ProgressBar) findViewById(R.id.upload_progressBar);
        upload_imgPreview = (ImageView) findViewById(R.id.upload_imgPreview);
        upload_videoPreview = (VideoView) findViewById(R.id.upload_videoPreview);

        if (mediaFilePath != null) {
            previewMedia();
        } else {
            Toast.makeText(getApplicationContext(),
                    "Sorry, file path is missing!", Toast.LENGTH_LONG).show();
        }

        upload_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new UploadJob().execute();
            }
        });
    }

    private void previewMedia() {
        if (isPhotoUpload) {
            upload_imgPreview.setVisibility(View.VISIBLE);
            upload_videoPreview.setVisibility(View.GONE);
            BitmapFactory.Options options = new BitmapFactory.Options();

            // down sizing image as it throws OutOfMemory Exception for larger
            // images
            options.inSampleSize = 8;

            final Bitmap bitmap = BitmapFactory.decodeFile(mediaFilePath, options);
            upload_imgPreview.setImageBitmap(bitmap);
        } else {
            upload_imgPreview.setVisibility(View.GONE);
            upload_videoPreview.setVisibility(View.VISIBLE);
            upload_videoPreview.setVideoPath(mediaFilePath);
            upload_videoPreview.start();
        }
    }

    private class UploadJob extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            upload_progressBar.setProgress(0);
            upload_txtPercentage.setText("0%");
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            upload_progressBar.setVisibility(View.VISIBLE);
            upload_progressBar.setProgress(progress[0]);
            upload_txtPercentage.setText(String.valueOf(progress[0]) + "%");
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                COMMAND command = null;
                if(JPackAttachment.class.getName().equals(uploadEntityType)) {
                    File file = new File(mediaFilePath);
                    if(isPhotoUpload) {
                        command = COMMAND.ADD_IMAGE_TO_PACK;
                    } else {
                        command = COMMAND.ADD_VIDEO_TO_PACK;
                    }
                    API api = APIBuilder.create().setAction(command)
                            .setOauthToken(AppController.getInstance().getoAuthToken())
                            .addApiParam(APIConstants.User.ID, AppController.getInstance().getUserId())
                            .addApiParam(APIConstants.Pack.ID, uploadEntityId)
                            .addApiParam(APIConstants.Topic.ID, topicId)
                            .addApiParam(APIConstants.Attachment.FILE_ATTACHMENT, file)
                            .build();
                    MultipartRequestProgressListener listener = new MultipartRequestProgressListener() {
                        @Override
                        public void countTransferProgress(long progress, long total) {
                            int percentage = (int)((progress/total)*100);
                            publishProgress(percentage);
                        }
                    };
                    api.execute(listener);
                }
            } catch (Exception e) {
                Log.d(LOG_TAG, "Failed to upload attachment: " + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            upload_progressBar.setProgress(100);
            upload_txtPercentage.setText("100%");
        }
    }
}
