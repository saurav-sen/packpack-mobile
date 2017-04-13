package com.pack.pack.application.service;

import android.app.Service;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import java.util.Timer;
import java.util.TimerTask;

public class CheckNetworkService extends Service {

    public static final String CHECK_INTERNET = "CHECK_INTERNET";

    public static final String CONNECTION_STATUS = "CONNECTION_STATUS";

    public static final String CONNECTION_STATUS_CONNECTED = "CONNECTED";
    public static final String CONNECTION_STATUS_NOT_CONNECTED = "NOT_CONNECTED";

    private Timer timer = new Timer();

    public CheckNetworkService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        timer.scheduleAtFixedRate(new CheckNetworkTask(), 0, 10 * 1000);
        return START_STICKY;
    }

    private class CheckNetworkTask extends TimerTask {

        @Override
        public void run() {
            broadcastStatus(NetworkUtil.checkConnectivity(CheckNetworkService.this));
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
