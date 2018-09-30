package com.pack.pack.application.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.pack.pack.application.Constants;
import com.pack.pack.application.R;
import com.pack.pack.application.activity.fragments.BookmarkFragment;
import com.pack.pack.application.activity.fragments.DiscoverFragment;
import com.pack.pack.application.activity.fragments.ArticlesFragment;
import com.pack.pack.application.activity.fragments.SportsFragment;
import com.pack.pack.application.activity.fragments.TrendingFragment;
import com.pack.pack.application.data.util.BottomNavigationViewHelper;

public class LandingPageActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private Fragment trendingFragment;
    private Fragment sportsFragment;
    private Fragment scienceFragment;
    private Fragment funFragment;
    private Fragment specialFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_navigation);

        trendingFragment = new TrendingFragment();
        loadFragment(trendingFragment);

        FirebaseMessaging.getInstance().subscribeToTopic(Constants.GLOBAL_NOTIFICATION_TOPIC);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        BottomNavigationViewHelper.disableShiftMode(navigation);
        navigation.setOnNavigationItemSelectedListener(this);
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
        Fragment fragment = null;

        switch (item.getItemId()) {
            case R.id.navigation_tending:
                if(trendingFragment == null) {
                    trendingFragment = new TrendingFragment();
                }
                fragment = trendingFragment;
                break;

            case R.id.navigation_sports:
                if(sportsFragment == null) {
                    sportsFragment = new SportsFragment();
                }
                fragment = sportsFragment;
                break;

            case R.id.navigation_article:
                if(scienceFragment == null) {
                    scienceFragment = new ArticlesFragment();
                }
                fragment = scienceFragment;
                break;

            case R.id.navigation_discover:
                if(funFragment == null) {
                    funFragment = new DiscoverFragment();
                }
                fragment = funFragment;
                break;

            case R.id.navigation_bookmark:
                if(specialFragment == null) {
                    specialFragment = new BookmarkFragment();
                }
                fragment = specialFragment;
                break;
        }

        return loadFragment(fragment);
    }
}
