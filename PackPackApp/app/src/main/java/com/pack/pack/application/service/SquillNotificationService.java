package com.pack.pack.application.service;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

/*import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;*/
import com.pack.pack.application.Constants;
import com.pack.pack.application.R;
import com.pack.pack.common.util.JSONUtil;
import com.pack.pack.model.web.notification.FeedMsg;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

/**
 * Created by Saurav on 10-07-2017.
 */
public class SquillNotificationService {}
/*public class SquillNotificationService extends FirebaseMessagingService {

    private static final String LOG_TAG = "FCM Service";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        try {
            if(remoteMessage.getNotification() != null) {
                String msgBody = remoteMessage.getNotification().getBody();
                Log.d(LOG_TAG, "From: " + remoteMessage.getFrom());
                Log.d(LOG_TAG, "Notification Message Body: " + msgBody);
                handleNotificationMessage(msgBody);
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
            *//*String json = new String(msgBody);
            FeedMsg msg = JSONUtil.deserialize(json, FeedMsg.class, true);
            showNotification(msg);*//*
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
        NotificationUtil.showNotificationMessage(message, message, getApplicationContext());
    }

    private void handleDataMessage(JSONObject json) {
        try {
            JSONObject data = json.getJSONObject("data");
            String title = data.getString("title");

            if(!NotificationUtil.isApplicationRunningInBackgroud(getApplicationContext())) {
                Intent intent = new Intent(Constants.PUSH_DATA_MSG);
                intent.putExtra("msg", title);
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
            }
            NotificationUtil.showNotificationMessage(title, title, getApplicationContext());
        } catch (JSONException e) {
            Log.d(LOG_TAG, e.getMessage(), e);
        }
    }

    *//*private void showNotification(FeedMsg feedMsg) {
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
    }*//*
}*/
