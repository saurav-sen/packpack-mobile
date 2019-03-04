package com.pack.pack.application.activity;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.pack.pack.application.AppController;
import com.pack.pack.application.Constants;
import com.pack.pack.application.R;
import com.pack.pack.application.activity.fragments.BaseFragment;
import com.pack.pack.application.activity.fragments.BookmarkFragment;
import com.pack.pack.application.activity.fragments.VideosFragment;
import com.pack.pack.application.activity.fragments.DiscoverFragment;
import com.pack.pack.application.activity.fragments.TrendingFragment;
import com.pack.pack.application.data.util.BottomNavigationViewHelper;
import com.pack.pack.application.service.NetworkUtil;

public class LandingPageActivity extends AbstractAppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private BaseFragment trendingFragment;
    private BaseFragment discoverFragment;
    private BaseFragment videosFragment;
    private Fragment specialFragment;

    private Fragment activeFragment;

    private boolean networkConnected = true;

    private static final int REQUEST_CAMERA_PERMISSION_CODE = 55;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_navigation);

        networkConnected = NetworkUtil.checkConnectivity(LandingPageActivity.this);

        trendingFragment = new TrendingFragment();
        loadFragment(trendingFragment);

        FirebaseMessaging.getInstance().subscribeToTopic(Constants.GLOBAL_NOTIFICATION_TOPIC);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        BottomNavigationViewHelper.disableShiftMode(navigation);
        navigation.setOnNavigationItemSelectedListener(this);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION_CODE);
        } else {
            AppController.getInstance().cameraPermissionGranted();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        boolean enableCamera = false;
        if(REQUEST_CAMERA_PERMISSION_CODE == requestCode) {
            for (int i = 0, len = permissions.length; i < len; i++) {
                String permission = permissions[i];
                if (permission == Manifest.permission.CAMERA && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    enableCamera = true;
                }
            }
        }
        if(enableCamera) {
            AppController.getInstance().cameraPermissionGranted();
        } else {
            AppController.getInstance().cameraPermissionGranted();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.app_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(R.id.app_settings == item.getItemId()) {
            Intent intent = new Intent(LandingPageActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if(R.id.app_feedback == item.getItemId()) {
            Uri uri = Uri.parse("market://details?id=" + getPackageName());
            Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
            try {
                startActivity(myAppLinkToMarket);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this, " unable to find market app", Toast.LENGTH_LONG).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_tending:
                if(trendingFragment == null) {
                    trendingFragment = new TrendingFragment();
                }
                activeFragment = trendingFragment;
                break;

            /*case R.id.navigation_sports:
                if(sportsFragment == null) {
                    sportsFragment = new SportsFragment();
                }
                activeFragment = sportsFragment;
                break;*/

            case R.id.navigation_article:
                if(discoverFragment == null) {
                    discoverFragment = new DiscoverFragment();
                }
                activeFragment = discoverFragment;
                break;

            case R.id.navigation_discover:
                if(videosFragment == null) {
                    videosFragment = new VideosFragment();
                }
                activeFragment = videosFragment;
                break;

            case R.id.navigation_bookmark:
                if(specialFragment == null) {
                    specialFragment = new BookmarkFragment();
                }
                activeFragment = specialFragment;
                break;
        }

        return loadFragment(activeFragment);
    }

    private Snackbar err;

    private void fireNetworkStateChange(boolean isPrevConnected, boolean isNowConnected) {
        if(activeFragment != null && (activeFragment instanceof BaseFragment)) {
            ((BaseFragment)activeFragment).onNetworkStateChange(isPrevConnected, isNowConnected);
        }
    }

    @Override
    public void onNetworkConnect() {
        if(this.err != null && this.err.isShown()) {
            this.err.dismiss();
        }
        if(networkConnected)
            return;
        networkConnected = true;
        fireNetworkStateChange(false, true);
    }

    @Override
    public void onNetworkDisconnect() {
        if(!networkConnected)
            return;
        networkConnected = false;
        View mainLayout = findViewById(R.id.mainLayout);
        if(mainLayout != null && (this.err == null || !this.err.isShown())) {
            this.err = Snackbar.make(mainLayout, "Internet Connection Lost",
                    Snackbar.LENGTH_LONG);
            View errView = this.err.getView();
            CoordinatorLayout.LayoutParams params =(CoordinatorLayout.LayoutParams)errView.getLayoutParams();
            params.gravity = Gravity.TOP;
            errView.setLayoutParams(params);
            errView.setBackgroundColor(Color.RED);
            this.err.show();
        }
        fireNetworkStateChange(true, false);
    }
}
