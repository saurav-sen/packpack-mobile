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

    private void showDataNotificationMessage(int notificationId, String msgType, String title, String message) {
        String count = preferenceManager.getNotificationCount(notificationId);
        String msgSuffix =  title;
        if(FeedMsgType.NEWS.name().equals(msgType)) {
            msgSuffix = "news and recent happenings";
        } else if(FeedMsgType.SQUILL_TEAM.name().equals(msgType)) {
            msgSuffix = "refreshment items";
        }
        if (count != null) {
            int value = Integer.parseInt(count);
            value++;
            count = String.valueOf(value);
            inboxStyle = new Notification.InboxStyle();
            inboxStyle.setBigContentTitle("You have " + value + " new " + msgSuffix);
        } else {
            count = String.valueOf(1);
            inboxStyle = new Notification.InboxStyle();
        }
        preferenceManager.setNotificationCount(notificationId, count);
        NotificationUtil.showNotificationMessage(inboxStyle, title, message, this, true, notificationId);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        try {
            if(remoteMessage.getNotification() != null) {
                String msgBody = remoteMessage.getNotification().getBody();
                Log.d(LOG_TAG, "From: " + remoteMessage.getFrom());
                Log.d(LOG_TAG, "Notification Message Body: " + msgBody);
                //handleNotificationMessage(msgBody);
                //NotificationUtil.showNotificationMessage(msgBody, msgBody, this, false);
                NotificationUtil.showCustomNotificationMessage(msgBody, this, false);
            } else if(remoteMessage.getData() != null && !remoteMessage.getData().isEmpty()) {
                //NotificationUtil.showNotificationMessage("Yes", "", getApplicationContext(), false);
                try {
                    JSONObject json = new JSONObject(remoteMessage.getData().toString());
                    Log.d(LOG_TAG, "From: " + remoteMessage.getFrom());
                    Log.d(LOG_TAG, "Notification Message Body: " + json.toString());
                    handleDataMessage(json);
                } catch (JSONException e) {
                    Log.d(LOG_TAG, e.getMessage(), e);
                }
            }
            /*String json = new String(msgBody);
            FeedMsg msg = JSONUtil.deserialize(json, FeedMsg.class, true);
            showNotification(msg);*/
        } catch (Exception e) {
            Log.d(LOG_TAG, e.getMessage(), e);
        }
    }

    private void handleNotificationMessage(String message) {
        if(!NotificationUtil.isApplicationRunningInBackgroud(getApplicationContext())) {
            Intent intent = new Intent(Constants.PUSH_NOTIFICATION_MSG);
            intent.putExtra("msg", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
        NotificationUtil.showNotificationMessage(message, message, getApplicationContext(), false);
    }

    private void handleDataMessage(JSONObject json) {
        try {
            //JSONObject data = json.getJSONObject("data");
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
            String message = "";
            /*if(FeedMsgType.NEWS.name().equals(msgType)) {
                message = "News";
            } else if(FeedMsgType.SQUILL_TEAM.name().equals(msgType)) {
                message = "New Refreshment Item";
            }*/
            //showDataNotificationMessage(notificationID, msgType, title, message);
            //NotificationUtil.showNotificationMessage(title, message, getApplicationContext(), true);
        } catch (JSONException e) {
            Log.d(LOG_TAG, e.getMessage(), e);
        }
    }

    /*private void showNotification(FeedMsg feedMsg) {
        String message =  feedMsg.getTitle();
        if(message == null) {
            return;
        }
        if(message == null) {
            message = "";
        }
        final int NOTIFICATION_ID = new Random().nextInt();//Math.abs(feedMsg.getKey())%10000;
        final NotificationManager notificationManager =
                (NotificationManager) getSystemService(
                        Context.NOTIFICATION_SERVICE);
        final NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(SquillNotificationService.this)
                        .setSmallIcon(R.drawable.logo)
                        .setContentTitle(feedMsg.getTitle())
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setContentText(message);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        notificationBuilder.setSound(alarmSound);

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
        notificationBuilder.setLargeIcon(largeIcon);

        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }*/
}
