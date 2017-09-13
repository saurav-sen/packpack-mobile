package com.pack.pack.application.service;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.pack.pack.application.R;
import com.pack.pack.application.activity.SplashActivity;
import com.pack.pack.model.web.notification.FeedMsg;

import java.util.List;
import java.util.Random;

/**
 * Created by Saurav on 11-07-2017.
 */
public final class NotificationUtil {

    public static final String APP_NAME = "SQUILL";

    private NotificationUtil() {
    }

    public static boolean isApplicationRunningInBackgroud(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfos = activityManager.getRunningAppProcesses();
            for(ActivityManager.RunningAppProcessInfo runningAppProcessInfo : runningAppProcessInfos) {
                if(runningAppProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for(String pkg : runningAppProcessInfo.pkgList) {
                        if(pkg.equals(context.getPackageName())) {
                            return false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> runningTasks = activityManager.getRunningTasks(1);
            if(runningTasks == null || runningTasks.isEmpty()) {
                return true;
            }
            ComponentName componentName = runningTasks.get(0).topActivity;
            if(componentName.getPackageName().equals(context.getPackageName())) {
                return false;
            }
        }
        return true;
    }

    public static void showNotificationMessage(Notification.InboxStyle inboxStyle, String title, String message, Context context, boolean isDataMessage, int notificationID) {
        if(message == null) {
            message = "";
        }

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.logo);

        final int NOTIFICATION_ID = notificationID;
        final NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(
                        Context.NOTIFICATION_SERVICE);
        Notification.Builder notificationBuilder = new Notification.Builder(context);
        notificationBuilder.setSmallIcon(R.drawable.logo)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setAutoCancel(true)
                        .setSound(alarmSound)
                        .setLargeIcon(largeIcon);

        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());

        //inboxStyle.setBigContentTitle(bigTitle);
        //inboxStyle.addLine("hi events " + value);

        if(isDataMessage) {
            Intent intent = new Intent(context, SplashActivity.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
            notificationBuilder.setDeleteIntent(pendingIntent);
            notificationBuilder.setStyle(inboxStyle);
            notificationManager.notify(APP_NAME, NOTIFICATION_ID, notificationBuilder.build());
        }
    }

    public static void showNotificationMessage(String title, String message, Context context, boolean isDataMessage) {
        if(message == null) {
            message = "";
        }
        final int NOTIFICATION_ID = new Random().nextInt();//Math.abs(feedMsg.getKey())%10000;
        final NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(
                        Context.NOTIFICATION_SERVICE);
        final NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.logo)
                        .setContentTitle(title)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setContentText(message);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        notificationBuilder.setSound(alarmSound);

        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.logo);
        notificationBuilder.setLargeIcon(largeIcon);

        if(isDataMessage) {
            Intent intent = new Intent(context, SplashActivity.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
            notificationBuilder.setDeleteIntent(pendingIntent);
            notificationManager.notify(APP_NAME, NOTIFICATION_ID, notificationBuilder.build());
        }
    }


    /*private void showNotification(FeedMsg feedMsg, Context context) {
        String message =  feedMsg.getTitle();
        if(message == null) {
            return;
        }
        if(message == null) {
            message = "";
        }
        final int NOTIFICATION_ID = new Random().nextInt();//Math.abs(feedMsg.getKey())%10000;
        final NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(
                        Context.NOTIFICATION_SERVICE);
        final NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.logo)
                        .setContentTitle(feedMsg.getTitle())
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setContentText(message);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        notificationBuilder.setSound(alarmSound);

        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.logo);
        notificationBuilder.setLargeIcon(largeIcon);

        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }*/
}
