package in.squill.squilloffice;

import android.content.IntentFilter;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.pack.pack.application.R;
import com.pack.pack.application.service.SyncService;
import com.pack.pack.application.service.events.NetworkStatusListener;

/**
 * Created by Saurav on 15-03-2017.
 */
public abstract class AbstractAppCompatActivity extends AppCompatActivity implements NetworkBasedActivity {

    private NetworkStatusListener broadcastListener;

    private Snackbar err;

    @Override
    protected void onResume() {
        super.onResume();
        if(broadcastListener == null) {
            broadcastListener = new NetworkStatusListener(this);
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(
                broadcastListener, new IntentFilter(
                        SyncService.CHECK_INTERNET));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(broadcastListener != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastListener);
        }
    }

    @Override
    public void onNetworkConnect() {
        if(this.err != null && this.err.isShown()) {
            this.err.dismiss();
        }
    }

    @Override
    public void onNetworkDisconnect() {
        View mainLayout = findViewById(R.id.mainLayout);
        if(mainLayout != null && (this.err == null || !this.err.isShown())) {
            this.err = Snackbar.make(mainLayout, "No Internet Connection",
                    Snackbar.LENGTH_INDEFINITE);
            View errView = this.err.getView();
            errView.setBackgroundColor(Color.RED);
            this.err.show();
        }
    }
}
