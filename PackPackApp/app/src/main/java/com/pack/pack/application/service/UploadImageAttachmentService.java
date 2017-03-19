package com.pack.pack.application.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.pack.pack.application.AppController;
import com.pack.pack.application.R;
import com.pack.pack.application.activity.UploadActivity;
import com.pack.pack.application.data.cache.PackAttachmentsCache;
import com.pack.pack.application.data.util.ApiConstants;
import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIBuilder;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.ByteBody;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.model.web.JPackAttachment;

import org.apache.http.entity.mime.content.ContentBody;

import java.io.ByteArrayOutputStream;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Saurav on 23-02-2017.
 */
public class UploadImageAttachmentService extends Service {

    public static final String PACK_ID = "PACK_ID";
    public static final String TOPIC_ID = "TOPIC_ID";
    public static final String ATTACHMENT_TITLE = "ATTACHMENT_TITLE";
    public static final String ATTACHMENT_DESCRIPTION = "ATTACHMENT_DESCRIPTION";
    public static final String ATTACHMENT_ID = "ATTACHMENT_ID";

    private static final String LOG_TAG = "UploadImageService";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent == null) {
            return START_REDELIVER_INTENT;
        }
        String packId = intent.getStringExtra(PACK_ID);
        String topicId = intent.getStringExtra(TOPIC_ID);
        String attachmentTitle = intent.getStringExtra(ATTACHMENT_TITLE);
        String attachmentDescription = intent.getStringExtra(ATTACHMENT_DESCRIPTION);

        //String selectedInputVideoFilePath = intent.getStringExtra(SELECTED_INPUT_VIDEO_FILE);

        String attachmentId = intent.getStringExtra(ATTACHMENT_ID);

        JPackAttachment attachment = new JPackAttachment();
        attachment.setId(attachmentId);
        attachment.setUploadProgress(true);
        attachment.setTitle(attachmentTitle);
        attachment.setDescription(attachmentDescription);

        PackAttachmentsCache.open(this).addUploadInProgressAttachment(attachment, packId);

        upload(attachmentId, packId, topicId, attachmentTitle, attachmentDescription);

        return START_REDELIVER_INTENT;
    }

    private void showNotification(final ExecutorStatus status) {
        if(status == null) {
            return;
        }
        final int NOTIFICATION_ID = 1338;
        final NotificationManager notificationManager =
                (NotificationManager) getSystemService(
                        Context.NOTIFICATION_SERVICE);
        final NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.logo)
                        .setContentTitle("Uploading")
                        .setContentText("Photo Upload is in progress");
        new Thread(new Runnable() {
            @Override
            public void run() {
                notificationBuilder.setProgress(100, 10, true);
                notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
                while(!status.isComplete()) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Log.d(LOG_TAG, e.getMessage(), e);
                    }
                }
                notificationBuilder.setProgress(100, 100, true);
                notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
            }
        }).start();
    }

    private void broadcastStatus(String packId, String oldAttachmentId, String newAttachmentId, boolean success) {
        Intent broadcast = new Intent("UPLOAD_ATTACHMENT");
        broadcast.putExtra(UploadResult.ATTACHMENT_OLD_ID, oldAttachmentId);
        broadcast.putExtra(UploadResult.PACK_ID, packId);
        if(success) {
            broadcast.putExtra(UploadResult.STATUS, UploadResult.OK_STATUS);
            broadcast.putExtra(UploadResult.ATTACHMENT_NEW_ID, newAttachmentId);
        } else {
            broadcast.putExtra(UploadResult.STATUS, UploadResult.ERROR_STATUS);
        }

        LocalBroadcastManager.getInstance(UploadImageAttachmentService.this).sendBroadcast(broadcast);
    }

    private void upload(String attachmentId, String packId, String topicId, String attachmentTitle, String attachmentDescription) {
        ExecutorsPool.INSTANCE.submit(new ExecutorTask(attachmentId, packId, topicId, attachmentTitle, attachmentDescription, new ExecutorStatus()));
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private class ExecutorTask implements Runnable {

        private String attachmentId;
        private String packId;
        private String topicId;
        private String attachmentTitle;
        private String attachmentDescription;

        private ExecutorStatus status;

        ExecutorTask(String attachmentId, String packId, String topicId, String attachmentTitle,
                     String attachmentDescription, ExecutorStatus status) {
            this.attachmentId = attachmentId;
            this.packId = packId;
            this.topicId = topicId;
            this.attachmentTitle = attachmentTitle;
            this.attachmentDescription = attachmentDescription;
            this.status = status;
        }

        @Override
        public void run() {
            boolean success = true;
            showNotification(status);
            String newAttachmentId = null;
            Bitmap mediaBitmap = PackAttachmentsCache.open(UploadImageAttachmentService.this)
                    .getSelectedAttachmentPhoto(attachmentId);
            if(mediaBitmap != null) {
                try {
                    ByteArrayOutputStream baOS = new ByteArrayOutputStream();
                    mediaBitmap.compress(Bitmap.CompressFormat.JPEG, 60, baOS);
                    byte[] bytes = baOS.toByteArray();
                    ByteBody byteBody = new ByteBody();
                    byteBody.setBytes(bytes);


                    COMMAND command = COMMAND.ADD_IMAGE_TO_PACK;
                    API api = APIBuilder.create(ApiConstants.BASE_URL).setAction(command)
                            .setOauthToken(AppController.getInstance().getoAuthToken())
                            .addApiParam(APIConstants.User.ID, AppController.getInstance().getUserId())
                            .addApiParam(APIConstants.Pack.ID, packId)
                            .addApiParam(APIConstants.Topic.ID, topicId)
                            .addApiParam(APIConstants.Attachment.FILE_ATTACHMENT, byteBody)
                            .addApiParam(APIConstants.Attachment.TITLE, attachmentTitle)
                            .addApiParam(APIConstants.Attachment.DESCRIPTION, attachmentDescription)
                            .build();
                    JPackAttachment result = (JPackAttachment)api.execute(null);
                    if(result == null) {
                        success = false;
                    } else {
                        newAttachmentId = result.getId();
                        if(newAttachmentId == null) {
                            success = false;
                        } else {
                            success = true;
                            PackAttachmentsCache.open(UploadImageAttachmentService.this)
                                    .successfullyUploadedAttachment(result, packId, attachmentId);
                        }
                    }
                } catch (Exception e) {
                    Log.d(LOG_TAG, e.getMessage(), e);
                    success = false;
                } finally {
                    //AppCache.INSTANCE.removeSelectedAttachmentPhoto(attachmentId);
                }
                status.setSuccess(success);
                status.setComplete(true);
                broadcastStatus(packId, attachmentId, newAttachmentId, success);
            }
        }
    }

    private class ExecutorStatus {

        private boolean complete = false;

        private boolean success = false;

        public boolean isComplete() {
            return complete;
        }

        public void setComplete(boolean complete) {
            this.complete = complete;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }
    }
}
