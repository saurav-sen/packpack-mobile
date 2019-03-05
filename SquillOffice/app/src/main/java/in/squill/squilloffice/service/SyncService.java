package in.squill.squilloffice.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import in.squill.squilloffice.cz.fhucho.android.util.SimpleDiskCacheInitializer;

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
                3 * 60 * 60 * 1000, 4 * 60 * 60 * 1000); // Quarterly Scheduled Task
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
                SimpleDiskCacheInitializer.enforceMaxSizeLimit(context);
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
