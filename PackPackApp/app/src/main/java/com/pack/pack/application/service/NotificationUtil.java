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
import com.pack.pack.application.Constants;
import com.pack.pack.application.R;
import com.pack.pack.application.activity.IntroMainActivity;
import com.pack.pack.application.activity.NotificationViewerActivity;
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

    public static void showCustomNotificationMessage(String msgType, String ogTitle, String ogImage, Bitmap ogImageBitmap,
                                                     String ogUrl, String shareableUrl, String summary, Context context) {
        if(ogTitle == null || ogTitle.trim().isEmpty()) { // No message to display then no notification.
            return;
        }
        RemoteViews contentView = new RemoteViews(AppController.PACKAGE_NAME, R.layout.custom_notification);
        contentView.setImageViewResource(R.id.notification_image, R.drawable.squill_notification);
        contentView.setTextViewText(R.id.notification_title, ogTitle);

        boolean sound = true;
        //Intent intent = new Intent(context, NotificationViewerActivity.class);
        Intent intent = new Intent(context, IntroMainActivity.class);
        intent.putExtra(Constants.MSG_TYPE, Constants.NOTIFICATION_DATA_MSG_TYPE);
        intent.putExtra(Constants.OG_TITLE, ogTitle);
        intent.putExtra(Constants.OG_IMAGE, ogImage);
        intent.putExtra(Constants.OG_URL, ogUrl);
        intent.putExtra(Constants.SUMMARY_TEXT, summary);
        if(summary != null && !summary.trim().isEmpty()) {
            intent.setAction(String.valueOf(System.currentTimeMillis()));
        } else {
            shareableUrl = ogUrl;
            sound = false;
        }
        intent.putExtra(Constants.SHAREABLE_URL, shareableUrl);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        final int NOTIFICATION_ID = new Random().nextInt();//Math.abs(feedMsg.getKey())%10000;
        final NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(
                        Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder notificationBuilder = null;
        if(ogImageBitmap != null) {
            notificationBuilder =
                    new NotificationCompat.Builder(context, "squill_news_01")
                            //.setContent(contentView)
                            .setSmallIcon(R.drawable.squill_notification)
                            /*.setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                                    R.drawable.squill_notification))*/
                            .setLargeIcon(ogImageBitmap)
                            .setContentTitle("SQUILL")
                            .setContentText(ogTitle)
                            /*.setStyle(new NotificationCompat.BigPictureStyle()
                                    .bigPicture(ogImageBitmap)
                                    .setBigContentTitle(summary))*/
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(ogTitle))
                            .setContentInfo(ogTitle)
                            .setContentIntent(pendingIntent)
                            .setShowWhen(true)
                            .setAutoCancel(true)
                            .setPriority(NotificationCompat.PRIORITY_MAX);
            if(sound) {
                notificationBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
            }
        } else {
            notificationBuilder =
                    new NotificationCompat.Builder(context, "squill_news_01")
                            //.setContent(contentView)
                            .setSmallIcon(R.drawable.squill_notification)
                            .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                                    R.drawable.squill_notification))
                            .setContentTitle("SQUILL")
                            .setContentText(ogTitle)
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(ogTitle))
                            .setShowWhen(true)
                            .setAutoCancel(true)
                            .setContentIntent(pendingIntent)
                            .setPriority(NotificationCompat.PRIORITY_MAX);
            if(sound) {
                notificationBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
            }
        }
        notificationManager.notify(APP_NAME, NOTIFICATION_ID, notificationBuilder.build());
    }

    public static void showNotificationMessage(Context context, String title, String message) {
        if(message == null || message.trim().isEmpty()) { // No message to display then no notification.
            return;
        }

        Intent intent = new Intent(context, SplashActivity.class);
        intent.setAction(String.valueOf(System.currentTimeMillis()));
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
                        .setShowWhen(true)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setPriority(NotificationCompat.PRIORITY_MAX);
        notificationManager.notify(APP_NAME, NOTIFICATION_ID, notificationBuilder.build());
    }
}
