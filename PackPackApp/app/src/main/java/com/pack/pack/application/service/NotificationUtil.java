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
import android.widget.RemoteViews;

import com.pack.pack.application.AppController;
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

    public static boolean isApplicationRunningInForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfos = activityManager.getRunningAppProcesses();
            for(ActivityManager.RunningAppProcessInfo runningAppProcessInfo : runningAppProcessInfos) {
                if(runningAppProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for(String pkg : runningAppProcessInfo.pkgList) {
                        if(pkg.equals(context.getPackageName())) {
                            return true;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> runningTasks = activityManager.getRunningTasks(1);
            if(runningTasks == null || runningTasks.isEmpty()) {
                return false;
            }
            ComponentName componentName = runningTasks.get(0).topActivity;
            if(componentName.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return true;
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

    public static void showCustomNotificationMessage(String message, Context context, boolean isDataMessage) {
        if(message == null || message.trim().isEmpty()) { // No message to display then no notification.
            return;
        }
        RemoteViews contentView = new RemoteViews(AppController.PACKAGE_NAME, R.layout.custom_notification);
        contentView.setImageViewResource(R.id.notification_image, R.drawable.squill_notification);
        contentView.setTextViewText(R.id.notification_title, message);

        Intent intent = new Intent(context, SplashActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        final int NOTIFICATION_ID = new Random().nextInt();//Math.abs(feedMsg.getKey())%10000;
        final NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(
                        Context.NOTIFICATION_SERVICE);
        final NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context)
                        .setContent(contentView)
                        .setSmallIcon(R.drawable.squill_notification)
                        .setContentIntent(pendingIntent)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setPriority(NotificationCompat.PRIORITY_MAX);
        notificationManager.notify(APP_NAME, NOTIFICATION_ID, notificationBuilder.build());
    }

    public static void showNotificationMessage(Context context, String title, String message) {
        if(message == null || message.trim().isEmpty()) { // No message to display then no notification.
            return;
        }

        Intent intent = new Intent(context, SplashActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        final int NOTIFICATION_ID = new Random().nextInt();//Math.abs(feedMsg.getKey())%10000;
        final NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(
                        Context.NOTIFICATION_SERVICE);
        final NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.squill_notification))
                        .setContentIntent(pendingIntent)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setPriority(NotificationCompat.PRIORITY_MAX);
        notificationManager.notify(APP_NAME, NOTIFICATION_ID, notificationBuilder.build());
    }
}
