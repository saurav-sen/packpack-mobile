package com.pack.pack.application.service.events;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.pack.pack.application.activity.NetworkBasedActivity;
import com.pack.pack.application.service.SyncService;

/**
 * Created by Saurav on 15-03-2017.
 */
public class NetworkStatusListener extends BroadcastReceiver {

    private NetworkBasedActivity parentActivity;

    public NetworkStatusListener(NetworkBasedActivity parentActivity) {
        this.parentActivity = parentActivity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String status = intent.getStringExtra(SyncService.CONNECTION_STATUS);
        if(SyncService.CONNECTION_STATUS_NOT_CONNECTED.equals(status)) {
            onNetworkDisconnect(context);
        } else {
            onNetworkConnect(context);
        }
    }

    private void onNetworkConnect(Context context) {
        if(parentActivity != null) {
            parentActivity.onNetworkConnect();
        }
    }

    private void onNetworkDisconnect(Context context) {
        if(parentActivity != null) {
            parentActivity.onNetworkDisconnect();
        }
    }
}
