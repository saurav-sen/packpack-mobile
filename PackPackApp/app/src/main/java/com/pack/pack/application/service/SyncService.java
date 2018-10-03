package com.pack.pack.application.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.pack.pack.application.AppController;
import com.pack.pack.application.db.DBUtil;
import com.pack.pack.application.db.HttpImage;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SyncService extends Service {

    public static final String CHECK_INTERNET = "CHECK_INTERNET";

    public static final String CONNECTION_STATUS = "CONNECTION_STATUS";

    public static final String CONNECTION_STATUS_CONNECTED = "CONNECTED";
    public static final String CONNECTION_STATUS_NOT_CONNECTED = "NOT_CONNECTED";

    private static final String LOG_TAG = "SyncService";

    private Timer timer = new Timer();

    public SyncService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        timer.scheduleAtFixedRate(new CheckNetworkTask(), 0, 10 * 1000);
        timer.scheduleAtFixedRate(new LruBitmapCacheCleanupTask(SyncService.this),
                0, 1 * 60 * 60 * 1000); // Hourly Scheduled Task
        return START_NOT_STICKY;
    }

    private class CheckNetworkTask extends TimerTask {

        @Override
        public void run() {
            try {
                broadcastStatus(NetworkUtil.checkConnectivity(SyncService.this));
            } catch (Exception e) {
                Log.e(LOG_TAG, e.getMessage(), e);
            }
        }
    }

    private class LruBitmapCacheCleanupTask extends TimerTask {

        private Context context;

        LruBitmapCacheCleanupTask(Context context) {
            this.context = context;
        }

        @Override
        public void run() {
            try {
                List<HttpImage> httpImages = DBUtil.getAllHttpImageInfos(context);
                if(httpImages == null || httpImages.isEmpty()) {
                    AppController.getInstance().getLruBitmapCache().flushIfSizeExceeds();
                    return;
                }

                List<HttpImage> toDelete = new ArrayList<>();
                for(HttpImage httpImage : httpImages) {
                    String url = httpImage.getUrl();
                    if(url == null || url.trim().isEmpty())
                        continue;
                    String timestamp = httpImage.getTimestamp();
                    if(timestamp == null || timestamp.trim().isEmpty())
                        continue;
                    long t0 = Long.parseLong(timestamp.trim());
                    long t1 = System.currentTimeMillis();
                    int diff = (int)((((t1 - t0)/1000)/60)/60);
                    if(diff >= 12) { // 12 Hour ago cached image (Less likely to be used, force remove from cache to minimize memory usage)
                        try {
                            AppController.getInstance().getLruBitmapCache().evict(url);
                        } catch (Exception e) {
                            Log.e(LOG_TAG, e.getMessage(), e);
                        }
                        toDelete.add(httpImage);
                    }
                }
                DBUtil.deleteHttpImageInfos(toDelete, context);
                AppController.getInstance().getLruBitmapCache().flushIfSizeExceeds();
            } catch (Exception e) {
                Log.e(LOG_TAG, e.getMessage(), e);
            }
        }
    }

    private void broadcastStatus(boolean isConnected) {
        Intent intent = new Intent(CHECK_INTERNET);
        if(isConnected) {
            intent.putExtra(CONNECTION_STATUS, CONNECTION_STATUS_CONNECTED);
        } else {
            intent.putExtra(CONNECTION_STATUS, CONNECTION_STATUS_NOT_CONNECTED);
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
