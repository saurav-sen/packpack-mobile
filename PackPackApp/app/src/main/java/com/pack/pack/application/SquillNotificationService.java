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
import com.pack.pack.application.data.cache.PreferenceManager;
import com.pack.pack.application.service.NotificationUtil;
import com.pack.pack.common.util.JSONUtil;
import com.pack.pack.model.web.notification.FeedMsg;
import com.pack.pack.model.web.notification.FeedMsgType;

import org.json.JSONException;
import org.json.JSONObject;

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
            if(remoteMessage.getNotification() != null) {
                String msgBody = "C:" + remoteMessage.getNotification().getBody();
                Log.d(LOG_TAG, "From: " + remoteMessage.getFrom());
                Log.d(LOG_TAG, "Notification Message Body: " + msgBody);
                NotificationUtil.showCustomNotificationMessage(msgBody, this, false);
            } else if(remoteMessage.getData() != null && !remoteMessage.getData().isEmpty()) {
                try {
                    JSONObject json = new JSONObject(remoteMessage.getData().toString());
                    Log.d(LOG_TAG, "From: " + remoteMessage.getFrom());
                    Log.d(LOG_TAG, "Notification Message Body: " + json.toString());
                    handleDataMessage(json);
                } catch (JSONException e) {
                    Log.d(LOG_TAG, e.getMessage(), e);
                }
            }
        } catch (Exception e) {
            Log.d(LOG_TAG, e.getMessage(), e);
        }
    }

    private void handleDataMessage(JSONObject json) {
        try {
            String title = json.getString("title");
            String msgType = json.getString("msgType");
            String key = json.getString("key"); // id
            String sequenceId = json.getString("sequenceId");

            int notificationID = new Random().nextInt();
            if(sequenceId != null) {
                notificationID = Integer.parseInt(sequenceId.trim());
            }

            if(!NotificationUtil.isApplicationRunningInBackgroud(getApplicationContext())) {
                Intent intent = new Intent(Constants.PUSH_DATA_MSG);
                intent.putExtra("title", title);
                intent.putExtra("msgType", msgType);
                intent.putExtra("key", key);
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
            }
        } catch (JSONException e) {
            Log.d(LOG_TAG, e.getMessage(), e);
        }
    }
}
