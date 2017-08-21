package com.pack.pack.application.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.pack.pack.application.Constants;
import com.pack.pack.application.R;
import com.pack.pack.application.data.cache.PackAttachmentsCache;
import com.pack.pack.application.data.util.ApiConstants;
import com.pack.pack.application.data.util.ImageUtil;
import com.pack.pack.application.service.UploadImageAttachmentService;
import com.pack.pack.application.service.UploadVideoAttachmentService;
import com.pack.pack.common.util.JSONUtil;
import com.pack.pack.model.web.JPackAttachment;
import com.pack.pack.model.web.PackAttachmentType;
import com.pack.pack.services.exception.PackPackException;

import static com.pack.pack.application.AppController.TOPIC_ID_KEY;
import static com.pack.pack.application.AppController.UPLOAD_FILE_IS_PHOTO;
import static com.pack.pack.application.AppController.UPLOAD_FILE_PATH;

public class UploadActivity2 extends AppCompatActivity {

    private String topicId;

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

    private String sharedImageId;

    public static final String SHARED_IMAGE_ID = "SHARED_IMAGE_ID";

    private static final String LOG_TAG = "UploadActivity2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload2);

        topicId = getIntent().getStringExtra(TOPIC_ID_KEY);
        mediaFilePath = getIntent().getStringExtra(UPLOAD_FILE_PATH);
        sharedImageId = getIntent().getStringExtra(SHARED_IMAGE_ID);

        //mediaBitmap = getIntent().getParcelableExtra(UPLOAD_FILE_BITMAP);
        mediaBitmap = PackAttachmentsCache.open(this).getSelectedAttachmentPhoto(sharedImageId);
        if(mediaBitmap != null) {
            mediaBitmap = ImageUtil.downscaleBitmap(mediaBitmap, 1200, 900, false);
            PackAttachmentsCache.open(this).addSelectedAttachmentPhoto(sharedImageId, mediaBitmap);
        }
        isPhotoUpload = getIntent().getBooleanExtra(UPLOAD_FILE_IS_PHOTO, true);

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
                int descNoOfWords = description.split(" ").length;
                if(title.length() < 5) {
                    Toast.makeText(UploadActivity2.this, "Title should be of minimum 5 characters long.",
                            Toast.LENGTH_LONG).show();
                    return;
                } else if(description.length() < ApiConstants.MIN_ATTACHMENT_DESC_FIELD_LENGTH) {
                    Toast.makeText(UploadActivity2.this, "Description should be of minimum " + ApiConstants.MIN_ATTACHMENT_DESC_FIELD_LENGTH + " characters long.",
                            Toast.LENGTH_LONG).show();
                    return;
                } else if(descNoOfWords > ApiConstants.MAX_ATTACHMENT_DESC_FIELD_LENGTH) {
                    Toast.makeText(UploadActivity2.this, "Description is too long, max words " + ApiConstants.MAX_ATTACHMENT_DESC_FIELD_LENGTH,
                            Toast.LENGTH_LONG).show();
                    return;
                }
                upload_submit.setEnabled(false);
                try {
                    String selectedInputVideoFilePath = mediaFilePath;
                    putAttachmentUploadInBackground(sharedImageId, topicId, selectedInputVideoFilePath);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void putAttachmentUploadInBackground(String id, String topicId, String selectedInputVideoFilePath) {
        String attachmentType = null;
        try {
            if(isPhotoUpload) {
                attachmentType = PackAttachmentType.IMAGE.name();
                getIntent().putExtra(UploadImageAttachmentService.TOPIC_ID, topicId);
            } else {
                attachmentType = PackAttachmentType.VIDEO.name();
                getIntent().putExtra(UploadVideoAttachmentService.SELECTED_INPUT_VIDEO_FILE, selectedInputVideoFilePath);
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
            getIntent().putExtra(Constants.ATTACHMENT_UNDER_UPLOAD_IS_PHOTO, isPhotoUpload);
            getIntent().putExtra(Constants.ATTACHMENT_UNDER_UPLOAD, json);
        } catch (PackPackException e) {
            Log.d(LOG_TAG, e.getMessage(), e);
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
}
