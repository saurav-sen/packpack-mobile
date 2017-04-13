package com.pack.pack.application.service;

import android.app.Service;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.pack.pack.application.data.cache.InMemory;
import com.pack.pack.application.data.util.ApiConstants;
import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIBuilder;
import com.pack.pack.client.api.COMMAND;

import java.util.Timer;
import java.util.TimerTask;

public class SquillNTPService extends Service {

    private static final String LOG_TAG = "SquillNTPService";

    private Timer timer = new Timer();

    public SquillNTPService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int oneMinute = 60 * 60 * 1000;
        timer.scheduleAtFixedRate(new NTPTask(), 0, 5 * oneMinute);
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class NTPTask extends TimerTask {

        @Override
        public void run() {
            try {
                if(!NetworkUtil.checkConnectivity(SquillNTPService.this)) {
                    return;
                }
                API api = APIBuilder.create(ApiConstants.BASE_URL)
                        .setAction(COMMAND.SYNC_TIME).build();
                long serverCurrentTimeInMillis = (long) api.execute();
                InMemory.INSTANCE.setServerCurrentTimeInMillis(serverCurrentTimeInMillis);
            } catch (Exception e) {
                Log.d(LOG_TAG, e.getMessage(), e);
            }
        }
    }
}
