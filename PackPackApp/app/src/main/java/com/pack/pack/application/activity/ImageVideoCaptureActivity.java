package com.pack.pack.application.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.pack.pack.application.AppController;
import com.pack.pack.application.R;
import com.pack.pack.application.data.util.ImageUtil;
import com.pack.pack.application.view.CameraPreview;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.pack.pack.application.AppController.APP_NAME;
import static com.pack.pack.application.AppController.MEDIA_TYPE_IMAGE;
import static com.pack.pack.application.AppController.MEDIA_TYPE_VIDEO;
import static com.pack.pack.application.AppController.CAMERA_CAPTURE_PHOTO_REQUEST_CODE;
import static com.pack.pack.application.AppController.CAMERA_RECORD_VIDEO_REQUEST_CODE;
import static com.pack.pack.application.AppController.UPLOAD_ENTITY_ID_KEY;
import static com.pack.pack.application.AppController.UPLOAD_ENTITY_TYPE_KEY;
import static com.pack.pack.application.AppController.UPLOAD_FILE_PATH;
import static com.pack.pack.application.AppController.UPLOAD_FILE_IS_PHOTO;
import static com.pack.pack.application.AppController.TOPIC_ID_KEY;

/**
 *
 * @author Saurav
 *
 */
public class ImageVideoCaptureActivity extends Activity {

    private CameraPreview cameraPreview;

    private FrameLayout fLayout;

    private static final String LOG_TAG = "ImageVideoCapture";

    private static final String MEDIA_FILE_URI_KEY = "media_file_uri";

    private Uri mediaFileUri;

    private String uploadEntityId;

    private String uploadEntityType;

    private String topicId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_video_capture);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    AppController.CAMERA_ACCESS_REQUEST_CODE);
        } else {
            AppController.getInstance().cameraPermissionGranted();
        }

        topicId = getIntent().getStringExtra(TOPIC_ID_KEY);
        uploadEntityId = getIntent().getStringExtra(UPLOAD_ENTITY_ID_KEY);
        uploadEntityType = getIntent().getStringExtra(UPLOAD_ENTITY_TYPE_KEY);

        fLayout = (FrameLayout) findViewById(R.id.camera_preview);
        FloatingActionButton capture_photo = (FloatingActionButton) findViewById(R.id.capture_photo);
        FloatingActionButton record_video = (FloatingActionButton) findViewById(R.id.record_video);

        capture_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                capturePhoto();
            }
        });
        record_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recordVideo();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(MEDIA_FILE_URI_KEY, mediaFileUri);
        outState.putCharSequence(TOPIC_ID_KEY, topicId);
        outState.putCharSequence(UPLOAD_ENTITY_ID_KEY, uploadEntityId);
        outState.putCharSequence(UPLOAD_ENTITY_TYPE_KEY, uploadEntityType);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mediaFileUri = savedInstanceState.getParcelable(MEDIA_FILE_URI_KEY);
        topicId = savedInstanceState.getString(TOPIC_ID_KEY);
        uploadEntityId = savedInstanceState.getString(UPLOAD_ENTITY_ID_KEY);
        uploadEntityType = savedInstanceState.getString(UPLOAD_ENTITY_TYPE_KEY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CAMERA_CAPTURE_PHOTO_REQUEST_CODE:
            {
                if(resultCode == RESULT_OK) {
                    startUploadActivity(true);
                } else {
                    Toast.makeText(getApplicationContext(),
                            "You have cancelled photo capture", Toast.LENGTH_SHORT)
                            .show();
                }
            }
            break;
            case CAMERA_RECORD_VIDEO_REQUEST_CODE:
            {
                if(resultCode == RESULT_OK) {
                    startUploadActivity(false);
                } else {
                    Toast.makeText(getApplicationContext(),
                            "You have cancelled video recording", Toast.LENGTH_SHORT)
                            .show();
                }
            }
            break;
        }
    }

    private void startUploadActivity(boolean isPhoto) {
        Intent intent = new Intent(this, UploadActivity.class);
        intent.putExtra(UPLOAD_FILE_PATH, mediaFileUri.getPath());
        intent.putExtra(TOPIC_ID_KEY, topicId);
        intent.putExtra(UPLOAD_FILE_IS_PHOTO, isPhoto);
        intent.putExtra(UPLOAD_ENTITY_ID_KEY, uploadEntityId);
        intent.putExtra(UPLOAD_ENTITY_TYPE_KEY, uploadEntityType);
        startActivity(intent);
    }

    private void capturePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mediaFileUri = ImageUtil.getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mediaFileUri);
        startActivityForResult(intent, CAMERA_CAPTURE_PHOTO_REQUEST_CODE);
    }

    private void recordVideo() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        mediaFileUri = ImageUtil.getOutputMediaFileUri(MEDIA_TYPE_VIDEO);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mediaFileUri);
        startActivityForResult(intent, CAMERA_RECORD_VIDEO_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case AppController.CAMERA_ACCESS_REQUEST_CODE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    AppController.getInstance().cameraPermissionGranted();
                    finish();
                    startActivity(getIntent());
                }
                else {
                    AppController.getInstance().cameraPermisionDenied();
                }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!AppController.getInstance().isCameraPermissionGranted())
            return;
        cameraPreview = new CameraPreview(this);
        fLayout.addView(cameraPreview);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(cameraPreview != null) {
            cameraPreview.stop();
        }
    }
}
