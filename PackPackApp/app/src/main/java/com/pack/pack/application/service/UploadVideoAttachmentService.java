package com.pack.pack.application.service;

import android.app.Notification;
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
import com.pack.pack.application.data.cache.PackAttachmentsCache;
import com.pack.pack.application.data.util.ApiConstants;
import com.pack.pack.application.data.util.CompressionStatusListener;
import com.pack.pack.application.data.util.ImageUtil;
import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIBuilder;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.model.web.JPackAttachment;

import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.InputStreamBody;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.UUID;

/**
 * Created by Saurav on 23-02-2017.
 */
public class UploadVideoAttachmentService extends Service {

    public static final String SELECTED_INPUT_VIDEO_FILE = "SELECTED_INPUT_VIDEO_FILE";
    public static final String PACK_ID = "PACK_ID";
    public static final String TOPIC_ID = "TOPIC_ID";
    public static final String ATTACHMENT_TITLE = "ATTACHMENT_TITLE";
    public static final String ATTACHMENT_DESCRIPTION = "ATTACHMENT_DESCRIPTION";
    public static final String ATTACHMENT_ID = "ATTACHMENT_ID";

    private static final String LOG_TAG = "UploadVideoService";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String packId = intent.getStringExtra(PACK_ID);
        String topicId = intent.getStringExtra(TOPIC_ID);
        String attachmentTitle = intent.getStringExtra(ATTACHMENT_TITLE);
        String attachmentDescription = intent.getStringExtra(ATTACHMENT_DESCRIPTION);

        String selectedInputVideoFilePath = intent.getStringExtra(SELECTED_INPUT_VIDEO_FILE);

        String attachmentId = intent.getStringExtra(ATTACHMENT_ID);

        JPackAttachment attachment = new JPackAttachment();
        attachment.setId(attachmentId);
        attachment.setUploadProgress(true);
        attachment.setTitle(attachmentTitle);
        attachment.setDescription(attachmentDescription);

        PackAttachmentsCache.open(this).addUploadInProgressAttachment(attachment, packId);

        upload(selectedInputVideoFilePath, attachmentId, packId, topicId, attachmentTitle, attachmentDescription);

        return START_REDELIVER_INTENT;
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

    private void notifyTargetIntent() {
        /*NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.logo)
                        .setContentTitle("Uploading")
                        .setContentText("Upload is in progress");

        int NOTIFICATION_ID = 1337;

        Intent targetIntent = new Intent(this, UploadActivity.class);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder.setContentIntent(contentIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());*/
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

        LocalBroadcastManager.getInstance(UploadVideoAttachmentService.this).sendBroadcast(broadcast);
    }

    private void upload(String selectedInputVideoFilePath, String attachmentId, String packId, String topicId, String attachmentTitle, String attachmentDescription) {
        try {
            final File originalFile = new File(selectedInputVideoFilePath);

            File selectedVideoFileDir = UploadVideoAttachmentService.this.getCacheDir();
            final File selectedVideoFile = File.createTempFile(ApiConstants.APP_NAME, ".mp4", selectedVideoFileDir);

            CompressionStatusListenerImpl compressionStatusListener = new CompressionStatusListenerImpl(
                    selectedVideoFile, originalFile, attachmentId, packId, topicId, attachmentTitle, attachmentDescription);
            ImageUtil.compressVideo(UploadVideoAttachmentService.this, selectedInputVideoFilePath,
                    selectedVideoFile.getAbsolutePath(), compressionStatusListener);
        } catch (Exception e) {
            Log.d(LOG_TAG, e.getMessage(), e);
        }
    }

    private class CompressionStatusListenerImpl implements CompressionStatusListener {

        private File originalFile;

        private File selectedVideoFile;

        private boolean isDone = false;

        private String attachmentId;

        private String packId;

        private String topicId;

        private String attachmentTitle;

        private String attachmentDescription;

        CompressionStatusListenerImpl(File selectedVideoFile, File originalFile,
                                      String attachmentId, String packId, String topicId,
                                      String attachmentTitle, String attachmentDescription) {
            this.selectedVideoFile = selectedVideoFile;
            this.originalFile = originalFile;
            this.attachmentId = attachmentId;
            this.packId = packId;
            this.topicId = topicId;
            this.attachmentTitle = attachmentTitle;
            this.attachmentDescription = attachmentDescription;
        }

        @Override
        public void onSuccess(String message) {
            try {
                InputStream inputStream = new FileInputStream(selectedVideoFile);
                ContentBody contentBody = new InputStreamBody(inputStream, UUID.randomUUID().toString() + ".mp4");
                PackAttachmentsCache.open(UploadVideoAttachmentService.this).addSelectedAttachmentVideo(attachmentId, contentBody);

                ExecutorsPool.INSTANCE.submit(new ExecutorTask(attachmentId, packId, topicId, attachmentTitle, attachmentDescription, true));
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                isDone = true;
            }
        }

        @Override
        public void onFailure(String message) {
            try {
                InputStream inputStream = new FileInputStream(originalFile);
                ContentBody contentBody = new InputStreamBody(inputStream, UUID.randomUUID().toString() + ".mp4");
                PackAttachmentsCache.open(UploadVideoAttachmentService.this).addSelectedAttachmentVideo(attachmentId, contentBody);

                ExecutorsPool.INSTANCE.submit(new ExecutorTask(attachmentId, packId, topicId, attachmentTitle, attachmentDescription, false));
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                isDone = true;
            }
        }

        @Override
        public void onFinish() {
            isDone = true;
        }

        boolean isDone() {
            return isDone;
        }
    }

    private class ExecutorTask implements Runnable {

        private String attachmentId;
        private String packId;
        private String topicId;
        private String attachmentTitle;
        private String attachmentDescription;
        private boolean isCompressed;

        ExecutorTask(String attachmentId, String packId, String topicId, String attachmentTitle, String attachmentDescription, boolean isCompressed) {
            this.attachmentId = attachmentId;
            this.packId = packId;
            this.topicId = topicId;
            this.attachmentTitle = attachmentTitle;
            this.attachmentDescription = attachmentDescription;
            this.isCompressed = isCompressed;
        }

        @Override
        public void run() {
            invokeUploadApi(attachmentId, packId, topicId, attachmentTitle, attachmentDescription);
        }

        private void invokeUploadApi(String attachmentId, String packId, String topicId,
                                     String attachmentTitle, String attachmentDescription) {
            boolean success = true;
            String newAttachmentId = null;
            ContentBody contentBody = PackAttachmentsCache.open(UploadVideoAttachmentService.this).getSelectedAttachmentVideo(attachmentId);
            if(contentBody != null) {
                try {
                    COMMAND command = COMMAND.ADD_VIDEO_TO_PACK;
                    API api = APIBuilder.create(ApiConstants.BASE_URL).setAction(command)
                            .setOauthToken(AppController.getInstance().getoAuthToken())
                            .addApiParam(APIConstants.User.ID, AppController.getInstance().getUserId())
                            .addApiParam(APIConstants.Pack.ID, packId)
                            .addApiParam(APIConstants.Topic.ID, topicId)
                            .addApiParam(APIConstants.Attachment.FILE_ATTACHMENT, contentBody)
                            .addApiParam(APIConstants.Attachment.TITLE, attachmentTitle)
                            .addApiParam(APIConstants.Attachment.DESCRIPTION, attachmentDescription)
                            .addApiParam(APIConstants.Attachment.IS_COMPRESSED, isCompressed)
                            .build();
                    JPackAttachment result = (JPackAttachment) api.execute(null);
                    if(result == null) {
                        success = false;
                    } else {
                        success = true;
                        newAttachmentId = result.getId();
                        PackAttachmentsCache.open(UploadVideoAttachmentService.this).successfullyUploadedAttachment(result, packId, attachmentId);
                    }
                } catch (Exception e) {
                    Log.d(LOG_TAG, e.getMessage(), e);
                    success = false;
                } finally {
                    //PackAttachmentsCache.INSTANCE.removeSelectedAttachmentVideo(attachmentId);
                }
                broadcastStatus(packId, attachmentId, newAttachmentId, success);
                //notifyTargetIntent();
            }
        }
    }
}
