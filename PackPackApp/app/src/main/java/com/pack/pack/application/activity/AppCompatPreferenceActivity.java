package com.pack.pack.application.activity;

import android.Manifest;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.pack.pack.application.R;
import com.pack.pack.application.service.SyncService;
import com.pack.pack.application.service.events.NetworkStatusListener;

import java.security.Permission;

/**
 * Created by Saurav on 03-09-2016.
 */
public abstract class AppCompatPreferenceActivity extends PreferenceActivity implements NetworkBasedActivity {

    private AppCompatDelegate mDelegate;

    private NetworkStatusListener broadcastListener;

    private Snackbar err;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getDelegate().installViewFactory();
        getDelegate().onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getDelegate().onPostCreate(savedInstanceState);
    }

    public ActionBar getSupportActionBar() {
        return getDelegate().getSupportActionBar();
    }

    public void setSupportActionBar(@Nullable Toolbar toolbar) {
        getDelegate().setSupportActionBar(toolbar);
    }

    @Override
    public MenuInflater getMenuInflater() {
        return getDelegate().getMenuInflater();
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        getDelegate().setContentView(layoutResID);
    }

    @Override
    public void setContentView(View view) {
        getDelegate().setContentView(view);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        getDelegate().setContentView(view, params);
    }

    @Override
    public void addContentView(View view, ViewGroup.LayoutParams params) {
        getDelegate().addContentView(view, params);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        getDelegate().onPostResume();
    }

    @Override
    protected void onTitleChanged(CharSequence title, int color) {
        super.onTitleChanged(title, color);
        getDelegate().setTitle(title);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        getDelegate().onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        getDelegate().onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getDelegate().onDestroy();
    }

    public void invalidateOptionsMenu() {
        getDelegate().invalidateOptionsMenu();
    }

    private AppCompatDelegate getDelegate() {
        if (mDelegate == null) {
            mDelegate = AppCompatDelegate.create(this, null);
        }
        return mDelegate;
    }

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