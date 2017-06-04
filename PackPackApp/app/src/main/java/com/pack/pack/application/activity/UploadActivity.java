package com.pack.pack.application.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
import com.pack.pack.application.data.cache.PackAttachmentsCache;
import com.pack.pack.application.data.util.ApiConstants;
import com.pack.pack.application.data.util.CompressionStatusListener;
import com.pack.pack.application.data.util.FileUtil;
import com.pack.pack.application.data.util.ImageUtil;
import com.pack.pack.application.service.UploadImageAttachmentService;
import com.pack.pack.application.service.UploadVideoAttachmentService;
import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIBuilder;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.ByteBody;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.client.api.MultipartRequestProgressListener;
import com.pack.pack.common.util.JSONUtil;
import com.pack.pack.model.web.JPackAttachment;
import com.pack.pack.model.web.PackAttachmentType;
import com.pack.pack.services.exception.PackPackException;

import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.InputStreamBody;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.UUID;
import java.util.zip.GZIPInputStream;

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
public class UploadActivity extends AbstractActivity {

    public static final String ATTACHMENT_UNDER_UPLOAD = "ATTACHMENT_UNDER_UPLOAD";
    public static final String ATTACHMENT_UNDER_UPLOAD_IS_PHOTO = "ATTACHMENT_UNDER_UPLOAD_IS_PHOTO";

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

    private String newAttachmentId;

    public static final String ATTACHMENT_ID = "ATTACHMENT_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        AppController.getInstance().setUploadAttachmentData(null);

        topicId = getIntent().getStringExtra(TOPIC_ID_KEY);
        uploadEntityId = getIntent().getStringExtra(UPLOAD_ENTITY_ID_KEY);
        uploadEntityType = getIntent().getStringExtra(UPLOAD_ENTITY_TYPE_KEY);
        mediaFilePath = getIntent().getStringExtra(UPLOAD_FILE_PATH);
        newAttachmentId = getIntent().getStringExtra(ATTACHMENT_ID);

        //mediaBitmap = getIntent().getParcelableExtra(UPLOAD_FILE_BITMAP);
        mediaBitmap = PackAttachmentsCache.open(this).getSelectedAttachmentPhoto(newAttachmentId);
        if(mediaBitmap != null) {
            mediaBitmap = ImageUtil.downscaleBitmap(mediaBitmap, 1200, 900, false);
            PackAttachmentsCache.open(this).addSelectedAttachmentPhoto(newAttachmentId, mediaBitmap);
        }
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
                } else if(description.length() < ApiConstants.MIN_ATTACHMENT_DESC_FIELD_LENGTH) {
                    Toast.makeText(UploadActivity.this, "Description should be of minimum 5 characters long.",
                            Toast.LENGTH_LONG).show();
                    return;
                } else if(description.length() > ApiConstants.MAX_ATTACHMENT_DESC_FIELD_LENGTH) {
                    Toast.makeText(UploadActivity.this, "Description is too long, max allowed 200.",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                upload_submit.setEnabled(false);
                if(!isPhotoUpload) {
                    try {
                        String selectedInputVideoFilePath = mediaFilePath;

                        putAttachmentUploadInBackground(newAttachmentId, uploadEntityId, topicId, selectedInputVideoFilePath);

                        /*CompressionStatusListenerImpl compressionStatusListener = new CompressionStatusListenerImpl(selectedVideoFile, file);
                        ImageUtil.compressVideo(UploadActivity.this, selectedInputVideoFilePath, selectedVideoFile.getAbsolutePath(), compressionStatusListener);*/

                        //String[] command = new String[]{"/system/bin/ls -li /data/user/0/com.pack.pack.application/files/ffmpeg"};
                        /*String[] command = new String[]{"ffmpeg -h"};
                        Process process = Runtime.getRuntime().exec(command);
                        {
                            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                            int read;

                            String output = "";

                            String line;
                            while ((line = reader.readLine()) != null) {
                                output.concat(line + "\n");
                                Log.w("myApp", "[[output]]:" + line);
                            }
                            reader.close();
                        }

                        {
                            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                            int read;

                            String output = "";

                            String line;
                            while ((line = reader.readLine()) != null) {
                                output.concat(line + "\n");
                                Log.w("myApp", "[[Error]]:" + line);
                            }
                            reader.close();
                        }

                        compressionStatusListener = new CompressionStatusListenerImpl(selectedVideoFile, file);
                        ImageUtil.compressVideo(UploadActivity.this, selectedInputVideoFilePath, selectedVideoFile.getAbsolutePath(), compressionStatusListener);*/

                       // process.waitFor();

                        /*while(!compressionStatusListener.isDone()) {

                        }*/
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {

                    //new UploadJob(id, true).execute();
                    putAttachmentUploadInBackground(newAttachmentId, uploadEntityId, topicId, null);
                }
            }
        });
    }

    private void putAttachmentUploadInBackground(String id, String uploadEntityId, String topicId, String selectedInputVideoFilePath) {
        if(JPackAttachment.class.getName().equals(uploadEntityType)) {
            String attachmentType = null;
            try {
                if(isPhotoUpload) {
                    attachmentType = PackAttachmentType.IMAGE.name();
                    getIntent().putExtra(UploadImageAttachmentService.PACK_ID, uploadEntityId);
                    getIntent().putExtra(UploadImageAttachmentService.TOPIC_ID, topicId);
                } else {
                    attachmentType = PackAttachmentType.VIDEO.name();
                    getIntent().putExtra(UploadVideoAttachmentService.SELECTED_INPUT_VIDEO_FILE, selectedInputVideoFilePath);
                    getIntent().putExtra(UploadVideoAttachmentService.PACK_ID, uploadEntityId);
                    getIntent().putExtra(UploadVideoAttachmentService.TOPIC_ID, topicId);
                }
                JPackAttachment attachment = new JPackAttachment();
                attachment.setId(id);
                attachment.setTitle(title);
                attachment.setDescription(description);
                attachment.setLikes(0);
                attachment.setViews(0);
                attachment.setUploadProgress(true);
                attachment.setAttachmentType(attachmentType);
                attachment.setMimeType(attachmentType);
                String json = JSONUtil.serialize(attachment);
                getIntent().putExtra(ATTACHMENT_UNDER_UPLOAD_IS_PHOTO, isPhotoUpload);
                getIntent().putExtra(ATTACHMENT_UNDER_UPLOAD, json);
            } catch (PackPackException e) {
                Log.d(LOG_TAG, e.getMessage(), e);
            }
        }
        setResult(RESULT_OK, getIntent());
        finish();
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
                Bitmap bitmap = BitmapFactory.decodeFile(mediaFilePath, options);
                bitmap = ImageUtil.downscaleBitmap(bitmap, 1200, 900);
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

    /*private class UploadJob extends AsyncTask<Void, Integer, Object> implements Job {

        JobChangeListener stateChangeListener;
        boolean waitForListenerToAttach;

        private Object signal = new Object();

        private String id;

        private boolean failed = false;

        UploadJob(String id, boolean waitForListenerToAttach) {
            this.waitForListenerToAttach = waitForListenerToAttach;
            this.id = id;
        }

        @Override
        public String getId() {
            return this.id;
        }

        @Override
        public void setStateChangeListener(JobChangeListener stateChangeListener) {
            this.stateChangeListener = stateChangeListener;
            if(!waitForListenerToAttach) {
                return;
            }
            if(this.stateChangeListener != null) {
                signal.notifyAll();
            }
        }

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
        protected Object doInBackground(Void... voids) {
            Object result = null;
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
                        mediaBitmap.compress(Bitmap.CompressFormat.JPEG, 60, baOS);
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

                    if (waitForListenerToAttach) {
                        JobRegistry.INSTANCE.register(this);
                        synchronized (this) {
                            if (stateChangeListener == null) {
                                signal.wait();
                            }
                        }
                    }

                    result = api.execute(null);
                }
            } catch (Exception e) {
                Log.d(LOG_TAG, "Failed to upload attachment: " + e.getMessage());
                failed = true;
                if(stateChangeListener != null) {
                    stateChangeListener.onError(id, "Failed to upload attachment");
                }
            } finally {
                AppController.getInstance().setSelectedBitmapPhoto(null);
                AppController.getInstance().setSelectedGalleryVideo(null);
            }
            return result;
        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);
            upload_progressBar.setProgress(100);
            upload_txtPercentage.setText("100%");
            if(stateChangeListener != null) {
                if(failed) {
                    stateChangeListener.onError(id, "Failed to upload attachment");
                } else {
                    stateChangeListener.onSuccess(id, result);
                }
            }
            if(!JPackAttachment.class.getName().equals(uploadEntityType)) {
                setResult(RESULT_OK, getIntent());
                finish();
            }
           *//* if(JPackAttachment.class.getName().equals(uploadEntityType)) {
                try {
                    JPackAttachment attachment = new JPackAttachment();
                    attachment.setTitle(title);
                    attachment.setDescription(description);
                    attachment.setLikes(0);
                    attachment.setViews(0);
                    attachment.setAttachmentType(attachmentType);
                    attachment.setMimeType(attachmentType);
                    String json = JSONUtil.serialize(attachment);
                    getIntent().putExtra(ATTACHMENT_UNDER_UPLOAD, json);
                } catch (PackPackException e) {
                    Log.d(LOG_TAG, e.getMessage(), e);
                }
            }
            setResult(RESULT_OK, getIntent());
            finish();*//*
        }
    }*/
}
