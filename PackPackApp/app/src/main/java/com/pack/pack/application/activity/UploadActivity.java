package com.pack.pack.application.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.pack.pack.application.AppController;
import com.pack.pack.application.R;
import com.pack.pack.application.data.util.ApiConstants;
import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIBuilder;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.ByteBody;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.client.api.MultipartRequestProgressListener;
import com.pack.pack.model.web.JPackAttachment;

import org.apache.http.entity.mime.content.ContentBody;

import java.io.ByteArrayOutputStream;
import java.io.File;

import static com.pack.pack.application.AppController.TOPIC_ID_KEY;
import static com.pack.pack.application.AppController.UPLOAD_ENTITY_ID_KEY;
import static com.pack.pack.application.AppController.UPLOAD_ENTITY_TYPE_KEY;
import static com.pack.pack.application.AppController.UPLOAD_FILE_IS_PHOTO;
import static com.pack.pack.application.AppController.UPLOAD_FILE_PATH;
import static com.pack.pack.application.AppController.UPLOAD_FILE_BITMAP;
import static com.pack.pack.application.AppController.UPLOAD_ATTACHMENT_TITLE;
import static com.pack.pack.application.AppController.UPLOAD_ATTACHMENT_DESCRIPTION;


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

    private Bitmap mediaBitmap;

    private String title;

    private String description;

    private boolean isPhotoUpload = true;

    private TextView upload_txtPercentage;
    private ImageView upload_imgPreview;
    private VideoView upload_videoPreview;
    private EditText upload_title;
    private EditText upload_description;
    private AppCompatButton upload_submit;
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
        //mediaBitmap = getIntent().getParcelableExtra(UPLOAD_FILE_BITMAP);
        mediaBitmap = AppController.getInstance().getSelectedBitmapPhoto();
        isPhotoUpload = getIntent().getBooleanExtra(UPLOAD_FILE_IS_PHOTO, true);
        title = getIntent().getStringExtra(UPLOAD_ATTACHMENT_TITLE) + "";
        description = getIntent().getStringExtra(UPLOAD_ATTACHMENT_DESCRIPTION) + "";

        upload_txtPercentage = (TextView) findViewById(R.id.upload_txtPercentage);
        upload_submit = (AppCompatButton) findViewById(R.id.upload_submit);
        upload_progressBar = (ProgressBar) findViewById(R.id.upload_progressBar);
        upload_imgPreview = (ImageView) findViewById(R.id.upload_imgPreview);
        upload_videoPreview = (VideoView) findViewById(R.id.upload_videoPreview);
        upload_title = (EditText) findViewById(R.id.upload_title);
        upload_description = (EditText) findViewById(R.id.upload_description);

        if (mediaFilePath != null || mediaBitmap != null) {
            previewMedia();
        } else {
            Toast.makeText(getApplicationContext(),
                    "Sorry, file path is missing!", Toast.LENGTH_LONG).show();
        }

        upload_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                title = upload_title.getText() + "";
                description = upload_description.getText() + "";
                if(title.length() < 5) {
                    Toast.makeText(UploadActivity.this, "Title should be of minimum 5 characters long.",
                            Toast.LENGTH_LONG).show();
                    return;
                } else if(description.length() < 50) {
                    Toast.makeText(UploadActivity.this, "Description should be of minimum 50 characters long.",
                            Toast.LENGTH_LONG).show();
                    return;
                }
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

            if(mediaFilePath != null) {
                final Bitmap bitmap = BitmapFactory.decodeFile(mediaFilePath, options);
                upload_imgPreview.setImageBitmap(bitmap);
            } else if(mediaBitmap != null) {
                upload_imgPreview.setImageBitmap(mediaBitmap);
            }
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
                    if(isPhotoUpload) {
                        command = COMMAND.ADD_IMAGE_TO_PACK;
                    } else {
                        command = COMMAND.ADD_VIDEO_TO_PACK;
                    }
                    API api = null;
                    APIBuilder apiBuilder = APIBuilder.create(ApiConstants.BASE_URL).setAction(command)
                            .setOauthToken(AppController.getInstance().getoAuthToken())
                            .addApiParam(APIConstants.User.ID, AppController.getInstance().getUserId())
                            .addApiParam(APIConstants.Pack.ID, uploadEntityId)
                            .addApiParam(APIConstants.Topic.ID, topicId);

                    if(AppController.getInstance().getSelectedGalleryVideo() != null) {
                        ContentBody contentBody = AppController.getInstance().getSelectedGalleryVideo();
                        api = apiBuilder.addApiParam(APIConstants.Attachment.FILE_ATTACHMENT, contentBody)
                                .addApiParam(APIConstants.Attachment.TITLE, title)
                                .addApiParam(APIConstants.Attachment.DESCRIPTION, description)
                                .build();
                    } else if(mediaFilePath != null) {
                        File file = new File(mediaFilePath);
                        api = apiBuilder.addApiParam(APIConstants.Attachment.FILE_ATTACHMENT, file)
                                .addApiParam(APIConstants.Attachment.TITLE, title)
                                .addApiParam(APIConstants.Attachment.DESCRIPTION, description)
                                .build();
                    } else if(mediaBitmap != null) {
                        ByteArrayOutputStream baOS = new ByteArrayOutputStream();
                        mediaBitmap.compress(Bitmap.CompressFormat.JPEG, 75, baOS);
                        byte[] bytes = baOS.toByteArray();
                        ByteBody byteBody = new ByteBody();
                        byteBody.setBytes(bytes);
                        api = apiBuilder.addApiParam(APIConstants.Attachment.FILE_ATTACHMENT, byteBody)
                                .addApiParam(APIConstants.Attachment.TITLE, title)
                                .addApiParam(APIConstants.Attachment.DESCRIPTION, description)
                                .build();
                    }
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
            } finally {
                AppController.getInstance().setSelectedBitmapPhoto(null);
                AppController.getInstance().setSelectedGalleryVideo(null);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            upload_progressBar.setProgress(100);
            upload_txtPercentage.setText("100%");
            setResult(RESULT_OK, getIntent());
            finish();
        }
    }
}
