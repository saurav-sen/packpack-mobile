package com.pack.pack.application;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.pack.pack.application.Constants;
import com.pack.pack.application.R;
import com.pack.pack.application.activity.SplashActivity;
import com.pack.pack.application.async.FeedReceiveCallback;
import com.pack.pack.application.async.FeedsDownloadUtil;
import com.pack.pack.application.data.cache.PreferenceManager;
import com.pack.pack.application.data.util.DownloadFeedImageTask;
import com.pack.pack.application.data.util.IAsyncTaskStatusListener;
import com.pack.pack.application.service.NotificationUtil;
import com.pack.pack.common.util.JSONUtil;
import com.pack.pack.model.web.notification.FeedMsg;
import com.pack.pack.model.web.notification.FeedMsgType;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.Random;

/**
 * Created by Saurav on 10-07-2017.
 */
public class SquillNotificationService extends FirebaseMessagingService {

    private static final String LOG_TAG = "FCM Service";

    Notification.InboxStyle inboxStyle;
    private PreferenceManager preferenceManager;

    @Override
    public void onCreate() {
        super.onCreate();
        preferenceManager = new PreferenceManager(this);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        try {
            if(remoteMessage.getData() != null && !remoteMessage.getData().isEmpty()) {
                handleDataMessage(remoteMessage.getData());
            } else if(remoteMessage.getNotification() != null) {
                String msgBody = "C:" + remoteMessage.getNotification().getBody();
                Log.d(LOG_TAG, "From: " + remoteMessage.getFrom());
                Log.d(LOG_TAG, "Notification Message Body: " + msgBody);

                FeedReceiveCallback callback = new FeedReceiveCallback() {
                    @Override
                    public void handleTaskComplete() {
                        // Do nothing
                    }
                };
                FeedsDownloadUtil.downloadLatestFeedsFromOrigin(SquillNotificationService.this, callback, true);
                //NotificationUtil.showCustomNotificationMessage(msgBody, this, false);
            } /*else */
        } catch (Exception e) {
            Log.d(LOG_TAG, e.getMessage(), e);
        }
    }

    private void handleDataMessage(Map<String, String> json) {
        String ogTitle = json.get(Constants.OG_TITLE);
        String ogImage = json.get(Constants.OG_IMAGE);
        String ogUrl = json.get(Constants.OG_URL); // id
        String msgType = json.get(Constants.MSG_TYPE);
        String shareableUrl = json.get(Constants.SHAREABLE_URL);
        String summary = json.get(Constants.SUMMARY_TEXT);

        int notificationID = new Random().nextInt();
            /*if(sequenceId != null) {
                notificationID = Integer.parseInt(sequenceId.trim());
            }*/

            /*if(!NotificationUtil.isApplicationRunningInBackgroud(getApplicationContext())) {
                Intent intent = new Intent(Constants.PUSH_DATA_MSG);
                intent.putExtra("title", title);
                intent.putExtra("msgType", msgType);
                intent.putExtra("key", key);
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
            }*/

        if(ogImage != null && !ogImage.trim().isEmpty()) {
            new DownloadFeedImageTask(null, 850, 600, SquillNotificationService.this, null)
                    .addListener(new OgImageLoadListener(msgType, ogTitle, ogImage, ogUrl, shareableUrl, summary))
                    .execute(ogImage);
        } else {
            NotificationUtil.showCustomNotificationMessage(msgType, ogTitle, ogImage, null,
                    ogUrl, shareableUrl, summary, SquillNotificationService.this);
        }
    }

    private class OgImageLoadListener implements IAsyncTaskStatusListener {

        private String msgType;
        private String ogTitle;
        private String ogImage;
        private String ogUrl;
        private String shareableUrl;
        private String summary;

        private OgImageLoadListener(String msgType, String ogTitle, String ogImage,
                                    String ogUrl, String shareableUrl, String summary) {
            this.msgType = msgType;
            this.ogTitle = ogTitle;
            this.ogImage = ogImage;
            this.ogUrl = ogUrl;
            this.shareableUrl = shareableUrl;
            this.summary = summary;
        }

        @Override
        public void onPreStart(String taskID) {}

        @Override
        public void onSuccess(String taskID, Object data) {
            if(data != null && (data instanceof Bitmap)) {
                NotificationUtil.showCustomNotificationMessage(msgType, ogTitle, ogImage,
                        (Bitmap)data, ogUrl, shareableUrl, summary,
                        SquillNotificationService.this);
            } else {
                onFailure(taskID, null);
            }
        }

        @Override
        public void onFailure(String taskID, String errorMsg) {
            NotificationUtil.showCustomNotificationMessage(msgType, ogTitle, ogImage, null, ogUrl, shareableUrl, summary, SquillNotificationService.this);
        }

        @Override
        public void onPostComplete(String taskID) {}
    }
}
