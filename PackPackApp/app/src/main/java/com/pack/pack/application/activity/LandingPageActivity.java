package com.pack.pack.application.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

//import com.google.firebase.messaging.FirebaseMessaging;
//import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessaging;
import com.pack.pack.application.AppController;
import com.pack.pack.application.Constants;
import com.pack.pack.application.R;
import com.pack.pack.application.adapters.LandingPageGridAdapter;
import com.pack.pack.application.data.cache.PreferenceManager;
import com.pack.pack.application.data.util.ApiConstants;

public class LandingPageActivity extends AppCompatActivity {

    private GridView landing_page_grid;

    private static String[] texts = new String[] {
            "Refreshments",
            "News",
            "My Society",
            "My Family",
            "Visionaries",
            "Settings"
    };
    private static int[] imageIds = new int[] {
            R.drawable.broadcast,
            R.drawable.news_entry,
            R.drawable.smart_society,
            R.drawable.my_family,
            R.drawable.art_culture,
            R.drawable.app_settings
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);

        PreferenceManager preferenceManager = new PreferenceManager(getApplicationContext());
        //if(!preferenceManager.isFirstTimeLogin()) {
            FirebaseMessaging.getInstance().subscribeToTopic(Constants.GLOBAL_NOTIFICATION_TOPIC);
        //}

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    AppController.CAMERA_ACCESS_REQUEST_CODE);
        } else {
            AppController.getInstance().cameraPermissionGranted();
        }

        if(!AppController.getInstance().isCameraPermissionGranted()
                && ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    AppController.APP_EXTERNAL_STORAGE_READ_REQUEST_CODE);
        } else {

        }

        landing_page_grid = (GridView) findViewById(R.id.landing_page_grid);
        LandingPageGridAdapter adapter = new LandingPageGridAdapter(this, texts, imageIds);
        landing_page_grid.setAdapter(adapter);
        landing_page_grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) { // Open broadcast
                    Intent intent = new Intent(LandingPageActivity.this, BroadcastActivity.class);
                    startActivity(intent);
                } else if (position == 1) { // Open news
                    Intent intent = new Intent(LandingPageActivity.this, NewsActivity.class);
                    startActivity(intent);
                } else if (position == 2) { // Open My Society
                    Intent intent = new Intent(LandingPageActivity.this, GenericTopicListActivity.class);
                    intent.putExtra(GenericTopicListActivity.CATEGORY_TYPE, ApiConstants.SOCIETY);
                    startActivity(intent);
                } else if (position == 3) { // Open My Family
                    Intent intent = new Intent(LandingPageActivity.this, GenericTopicListActivity.class);
                    intent.putExtra(GenericTopicListActivity.CATEGORY_TYPE, ApiConstants.FAMILY);
                    startActivity(intent);
                } else if (position == 4) { // Open visions of artists
                    Intent intent = new Intent(LandingPageActivity.this, GenericTopicListActivity.class);
                    intent.putExtra(GenericTopicListActivity.CATEGORY_TYPE, ApiConstants.OTHERS);
                    startActivity(intent);
                } else if (position == 5) { // Open App Settings
                    Intent intent = new Intent(LandingPageActivity.this, SettingsActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case AppController.CAMERA_ACCESS_REQUEST_CODE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    AppController.getInstance().cameraPermissionGranted();
                    /*if(Build.VERSION.SDK_INT >= 11) {
                        recreate();
                    } else {
                        finish();
                        startActivity(getIntent());
                    }*/
                } else {
                    AppController.getInstance().cameraPermisionDenied();
                }
                break;
            case AppController.APP_EXTERNAL_STORAGE_READ_REQUEST_CODE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    AppController.getInstance().externalReadGranted();
                    /*if(Build.VERSION.SDK_INT >= 11) {
                        recreate();
                    } else {
                        finish();
                        startActivity(getIntent());
                    }*/
                } else {
                    AppController.getInstance().externalReadDenied();
                }
                break;
        }
    }
}
