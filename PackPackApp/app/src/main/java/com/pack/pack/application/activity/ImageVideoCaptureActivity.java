package com.pack.pack.application.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.pack.pack.application.AppController;
import com.pack.pack.application.Constants;
import com.pack.pack.application.R;
import com.pack.pack.application.data.cache.PackAttachmentsCache;
import com.pack.pack.application.data.util.ApiConstants;
import com.pack.pack.application.data.util.FileUtil;
import com.pack.pack.application.data.util.ImageUtil;
import com.pack.pack.application.service.UploadImageAttachmentService;
import com.pack.pack.application.service.UploadVideoAttachmentService;
import com.pack.pack.application.topic.activity.model.UploadAttachmentData;
import com.pack.pack.application.view.CameraPreview;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import static com.pack.pack.application.AppController.MEDIA_TYPE_IMAGE;
import static com.pack.pack.application.AppController.MEDIA_TYPE_VIDEO;
import static com.pack.pack.application.AppController.CAMERA_CAPTURE_PHOTO_REQUEST_CODE;
import static com.pack.pack.application.AppController.CAMERA_RECORD_VIDEO_REQUEST_CODE;
import static com.pack.pack.application.AppController.UPLOAD_ENTITY_ID_KEY;
import static com.pack.pack.application.AppController.UPLOAD_ENTITY_TYPE_KEY;
import static com.pack.pack.application.AppController.UPLOAD_FILE_PATH;
import static com.pack.pack.application.AppController.UPLOAD_FILE_IS_PHOTO;
import static com.pack.pack.application.AppController.TOPIC_ID_KEY;
import static com.pack.pack.application.AppController.GALLERY_SELECT_PHOTO_REQUEST_CODE;
import static com.pack.pack.application.AppController.GALLERY_SELECT_VIDEO_REQUEST_CODE;

/**
 *
 * @author Saurav
 *
 */
public class ImageVideoCaptureActivity extends AbstractActivity {

    private CameraPreview cameraPreview;

    private FrameLayout fLayout;

   // private GridView galley_items;

    private static final String LOG_TAG = "ImageVideoCapture";

    private static final String MEDIA_FILE_URI_KEY = "media_file_uri";

    private Uri mediaFileUri;

    private String uploadEntityId;

    private String uploadEntityType;

    private String topicId;

    //private List<String> galleryImages = Collections.emptyList();

    private Button btnUploadSelectPhoto;

    private Button btnUploadSelectVideo;

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

        if(!AppController.getInstance().isCameraPermissionGranted()
                && ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    AppController.APP_EXTERNAL_STORAGE_READ_REQUEST_CODE);
        } /*else {
            galleryImages = getAllGalleryImagesPath();
        }*/

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

        //initializeGalleryGridView();

        btnUploadSelectPhoto = (Button) findViewById(R.id.btnUploadSelectPhoto);
        btnUploadSelectPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectPhotoFromGallery();
            }
        });
        btnUploadSelectVideo = (Button) findViewById(R.id.btnUploadSelectVideo);
        btnUploadSelectVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectVideoFromGallery();
            }
        });
    }

    private void selectPhotoFromGallery() {
        copyUploadData();
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Photo"), GALLERY_SELECT_PHOTO_REQUEST_CODE);
    }

    private void selectVideoFromGallery() {
        copyUploadData();
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Video"), GALLERY_SELECT_VIDEO_REQUEST_CODE);
    }

    private class ViewHolder {
        ImageView imageView;
    }

   /*@Override
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
    }*/

    private boolean checkUploadSize(Uri mediaFileUri) {
        /*Cursor cursor = getContentResolver().query(mediaFileUri, null, null, null, null);
        int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
        long sizeInMb = cursor.getLong(sizeIndex)/ (1024*1024);
        if(sizeInMb > ApiConstants.UPLOAD_SIZE_LIMIT_IN_MB) {
            return false;
        }*/
        /*try {
            File file = new File(mediaFileUri.getPath());
            if(!file.exists()) {
                Log.e(LOG_TAG, "Error calculating file size. File handle not resolved correctly.");
                return true;
            }
            long sizeInMb = file.length()/(1024*1024);
            if(sizeInMb > ApiConstants.UPLOAD_SIZE_LIMIT_IN_MB) {
                return false;
            }
            return true;
        } catch (Throwable e) {
            Log.e(LOG_TAG, "Error calculating file size. " + e.getMessage(), e);
            return true;
        }*/
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CAMERA_CAPTURE_PHOTO_REQUEST_CODE:
            {
                if(resultCode == RESULT_OK) {
                    if(!checkUploadSize(mediaFileUri)) {
                        Toast.makeText(ImageVideoCaptureActivity.this, "Selected file larger than 20MB", Toast.LENGTH_LONG).show();
                        return;
                    }
                    /*mediaFileUri = AppController.getInstance().getUploadAttachmentData().getMediaFileUri();
                    startUploadActivity(mediaFileUri.getPath(), true);*/
                    try {
                        /*Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                                getContentResolver(), data.getData());*/
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                                getContentResolver(), mediaFileUri);
                        startUploadActivity(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    AppController.getInstance().setUploadAttachmentData(null);
                    Toast.makeText(getApplicationContext(),
                            "You have cancelled photo capture", Toast.LENGTH_SHORT)
                            .show();
                }
            }
            break;
            case CAMERA_RECORD_VIDEO_REQUEST_CODE:
            {
                if(resultCode == RESULT_OK) {
                   /* mediaFileUri = AppController.getInstance().getUploadAttachmentData().getMediaFileUri();
                    startUploadActivity(mediaFileUri.getPath(), false);*/

                    if(!checkUploadSize(mediaFileUri)) {
                        Toast.makeText(ImageVideoCaptureActivity.this, "Selected file larger than 20MB", Toast.LENGTH_LONG).show();
                        return;
                    }

                    try {
                        //mediaFileUri = AppController.getInstance().getUploadAttachmentData().getMediaFileUri();
                        final String fileName = FileUtil.getFileNameFromUri(mediaFileUri, getApplicationContext());
                        String selectedInputVideoFilePath = FileUtil.getPath(this, mediaFileUri);
                        startUploadActivity(selectedInputVideoFilePath, false);

                        /*File file = new File(selectedInputVideoFilePath);

                        File selectedVideoFileDir = this.getCacheDir();
                        final File selectedVideoFile = File.createTempFile(ApiConstants.APP_NAME, ".mp4", selectedVideoFileDir);

                        ImageUtil.compressVideo(this, selectedInputVideoFilePath, selectedVideoFile.getAbsolutePath(), new CompressionStatusListener() {
                            @Override
                            public void onSuccess(String message) {
                                try {
                                    InputStream inputStream = new FileInputStream(selectedVideoFile);
                                    ContentBody contentBody = new InputStreamBody(inputStream, fileName);
                                    AppController.getInstance().setSelectedGalleryVideo(contentBody);
                                    AppController.getInstance().getUploadAttachmentData().setMediaFileUri(mediaFileUri);
                                    String selectedVideoUriPath = FileUtil.getPath(getApplicationContext(), mediaFileUri);
                                    startUploadActivity(selectedVideoUriPath, false);
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(String message) {
                                mediaFileUri = AppController.getInstance().getUploadAttachmentData().getMediaFileUri();
                                startUploadActivity(mediaFileUri.getPath(), false);
                            }

                            @Override
                            public void onFinish() {

                            }
                        });*/
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    AppController.getInstance().setUploadAttachmentData(null);
                    Toast.makeText(getApplicationContext(),
                            "You have cancelled video recording", Toast.LENGTH_SHORT)
                            .show();
                }
            }
            break;
            case GALLERY_SELECT_PHOTO_REQUEST_CODE:
                if(resultCode == RESULT_OK) {
                    try {
                        Uri uri = data.getData();
                        if(!checkUploadSize(uri)) {
                            Toast.makeText(ImageVideoCaptureActivity.this, "Selected file larger than 20MB", Toast.LENGTH_LONG).show();
                            return;
                        }
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                                getContentResolver(), uri);
                        startUploadActivity(bitmap);
                       /* if(!ApiConstants.IS_PRODUCTION_ENV) {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                                    getContentResolver(), data.getData());
                            startUploadActivity(bitmap);
                        } else {
                            Uri selectedPhotoUri = data.getData();
                            String path = FileUtil.getPath(this, selectedPhotoUri);
                            AppController.getInstance().getUploadAttachmentData().setMediaFileUri(selectedPhotoUri);
                            startUploadActivity(path, true);
                        }*/
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    AppController.getInstance().setUploadAttachmentData(null);
                    Toast.makeText(getApplicationContext(),
                            "You have cancelled photo selection", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            case GALLERY_SELECT_VIDEO_REQUEST_CODE:
                if(resultCode == RESULT_OK) {
                    /*mediaFileUri = data.getData();
                    startUploadActivity(mediaFileUri.getPath(), false);*/
                    /*Uri selectedVideoUri = data.getData();
                    String selectedVideoUriPath = getGalleryVideoPath(selectedVideoUri);*/
                    final Uri selectedVideoUri = data.getData();
                    try {
                        if(!checkUploadSize(selectedVideoUri)) {
                            Toast.makeText(ImageVideoCaptureActivity.this, "Selected file larger than 20MB", Toast.LENGTH_LONG).show();
                            return;
                        }
                        //final String fileName = FileUtil.getFileNameFromUri(selectedVideoUri, getApplicationContext());
                        String selectedInputVideoFilePath = FileUtil.getPath(this, selectedVideoUri);
                        startUploadActivity(selectedInputVideoFilePath, false);

                        /*File file = new File(selectedInputVideoFilePath);

                        File selectedVideoFileDir = this.getCacheDir();
                        final File selectedVideoFile = File.createTempFile(ApiConstants.APP_NAME, ".mp4", selectedVideoFileDir);

                        ImageUtil.compressVideo(this, selectedInputVideoFilePath, selectedVideoFile.getAbsolutePath(), new CompressionStatusListener() {
                            @Override
                            public void onSuccess(String message) {
                                try {
                                    InputStream inputStream = new FileInputStream(selectedVideoFile);
                                    ContentBody contentBody = new InputStreamBody(inputStream, fileName);
                                    AppController.getInstance().setSelectedGalleryVideo(contentBody);
                                    AppController.getInstance().getUploadAttachmentData().setMediaFileUri(selectedVideoUri);
                                    String selectedVideoUriPath = FileUtil.getPath(getApplicationContext(), selectedVideoUri);
                                    startUploadActivity(selectedVideoUriPath, false);
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(String message) {
                                mediaFileUri = selectedVideoUri;
                                startUploadActivity(mediaFileUri.getPath(), false);
                            }

                            @Override
                            public void onFinish() {

                            }
                        });*/
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    AppController.getInstance().setUploadAttachmentData(null);
                    Toast.makeText(getApplicationContext(),
                            "You have cancelled video selection", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            case Constants.PACK_ATTACHMENT_UPLOAD_REQUEST_CODE:
               /* String json = data.getStringExtra(UploadActivity.ATTACHMENT_UNDER_UPLOAD);
                boolean isPhotoUpload = data.getBooleanExtra(UploadActivity.ATTACHMENT_UNDER_UPLOAD_IS_PHOTO, false);
                if(isPhotoUpload) {
                    String packId = getIntent().getStringExtra(UploadImageAttachmentService.PACK_ID);
                    String topicId = getIntent().getStringExtra(UploadImageAttachmentService.TOPIC_ID);

                    getIntent().putExtra(UploadImageAttachmentService.PACK_ID, packId);
                    getIntent().putExtra(UploadImageAttachmentService.TOPIC_ID, topicId);
                } else {
                    String selectedInputVideoFilePath = getIntent().getStringExtra(UploadVideoAttachmentService.SELECTED_INPUT_VIDEO_FILE);
                    String packId = getIntent().getStringExtra(UploadVideoAttachmentService.PACK_ID);
                    String topicId = getIntent().getStringExtra(UploadVideoAttachmentService.TOPIC_ID);

                    getIntent().putExtra(UploadVideoAttachmentService.SELECTED_INPUT_VIDEO_FILE, selectedInputVideoFilePath);
                    getIntent().putExtra(UploadVideoAttachmentService.PACK_ID, packId);
                    getIntent().putExtra(UploadVideoAttachmentService.TOPIC_ID, topicId);
                }
                getIntent().putExtra(UploadActivity.ATTACHMENT_UNDER_UPLOAD, json);
                setResult(RESULT_OK, getIntent());*/
                setResult(RESULT_OK, data);
                finish();
        }
    }

    private void startUploadActivity(String filePath, boolean isPhoto) {
        Intent intent = new Intent(this, UploadActivity.class);
        String newAttachmentId = UUID.randomUUID().toString();
        intent.putExtra(UploadActivity.ATTACHMENT_ID, newAttachmentId);
        UploadAttachmentData uploadAttachmentData = AppController.getInstance().getUploadAttachmentData();
        intent.putExtra(UPLOAD_FILE_PATH, filePath);
        intent.putExtra(TOPIC_ID_KEY, uploadAttachmentData.getTopicId());
        intent.putExtra(UPLOAD_FILE_IS_PHOTO, isPhoto);
        intent.putExtra(UPLOAD_ENTITY_ID_KEY, uploadAttachmentData.getUploadEntityId());
        intent.putExtra(UPLOAD_ENTITY_TYPE_KEY, uploadAttachmentData.getUploadEntityType());
        startActivityForResult(intent, Constants.PACK_ATTACHMENT_UPLOAD_REQUEST_CODE);
    }

    private void startUploadActivity(Bitmap bitmap) {
        Intent intent = new Intent(this, UploadActivity.class);
        //intent.putExtra(UPLOAD_FILE_BITMAP, bitmap);
        String newAttachmentId = UUID.randomUUID().toString();
        PackAttachmentsCache.open(this).addSelectedAttachmentPhoto(newAttachmentId, bitmap);

        UploadAttachmentData uploadAttachmentData = AppController.getInstance().getUploadAttachmentData();

        intent.putExtra(UploadActivity.ATTACHMENT_ID, newAttachmentId);
        intent.putExtra(TOPIC_ID_KEY, uploadAttachmentData.getTopicId());
        intent.putExtra(UPLOAD_FILE_IS_PHOTO, true);
        intent.putExtra(UPLOAD_ENTITY_ID_KEY, uploadAttachmentData.getUploadEntityId());
        intent.putExtra(UPLOAD_ENTITY_TYPE_KEY, uploadAttachmentData.getUploadEntityType());
        startActivityForResult(intent, Constants.PACK_ATTACHMENT_UPLOAD_REQUEST_CODE);
    }

    /*private void startUploadActivity(Bitmap bitmap) {
        Intent intent = new Intent(this, UploadActivity.class);
        //intent.putExtra(UPLOAD_FILE_BITMAP, bitmap);
        AppController.getInstance().setSelectedBitmapPhoto(bitmap);
        intent.putExtra(TOPIC_ID_KEY, topicId);
        intent.putExtra(UPLOAD_FILE_IS_PHOTO, true);
        intent.putExtra(UPLOAD_ENTITY_ID_KEY, uploadEntityId);
        intent.putExtra(UPLOAD_ENTITY_TYPE_KEY, uploadEntityType);
        startActivityForResult(intent, Constants.PACK_ATTACHMENT_UPLOAD_REQUEST_CODE);
    }*/

    private void copyUploadData() {
        UploadAttachmentData uploadAttachmentData = new UploadAttachmentData(mediaFileUri, uploadEntityId, uploadEntityType, topicId);
        AppController.getInstance().setUploadAttachmentData(uploadAttachmentData);
    }

    private void capturePhoto() {
        copyUploadData();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mediaFileUri = ImageUtil.getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        //AppController.getInstance().getUploadAttachmentData().setMediaFileUri(mediaFileUri);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mediaFileUri);
        startActivityForResult(intent, CAMERA_CAPTURE_PHOTO_REQUEST_CODE);
    }

    private void recordVideo() {
        copyUploadData();
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        mediaFileUri = ImageUtil.getOutputMediaFileUri(MEDIA_TYPE_VIDEO);
        //AppController.getInstance().getUploadAttachmentData().setMediaFileUri(mediaFileUri);
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
                } else {
                    AppController.getInstance().cameraPermisionDenied();
                }
                break;
            case AppController.APP_EXTERNAL_STORAGE_READ_REQUEST_CODE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    AppController.getInstance().externalReadGranted();
                    finish();
                    startActivity(getIntent());
                } else {
                    AppController.getInstance().externalReadDenied();
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(AppController.getInstance().getUploadAttachmentData() != null) {
            return;
        }
        if(!AppController.getInstance().isCameraPermissionGranted())
            return;
        cameraPreview = new CameraPreview(this);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(fLayout.getWidth(), 200);
       // cameraPreview.setLayoutParams(layoutParams);
        fLayout.addView(cameraPreview);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(AppController.getInstance().getUploadAttachmentData() != null) {
            return;
        }
        if(cameraPreview != null) {
            cameraPreview.stop();
        }
    }
}