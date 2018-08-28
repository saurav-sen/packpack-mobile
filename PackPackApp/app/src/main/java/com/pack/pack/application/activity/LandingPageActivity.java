package com.pack.pack.application.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

public class LandingPageActivity extends AppCompatActivity {

    private GridView landing_page_grid;

    private static String[] texts = new String[] {
            "Jsut Like That",
            "News",
            "Sports",
            "Science & Technology",
            "Articles",
            "Bookmarks"
    };
    private static int[] imageIds = new int[] {
            R.drawable.broadcast,
            R.drawable.news_entry,
            R.drawable.sports_icon,
            R.drawable.science_icon,
            R.drawable.article_icon,
            R.drawable.bookmark
    };

    public static final String MESSAGE_IF_ANY = "message_if_any";

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
        } /*else {

        }*/

        landing_page_grid = (GridView) findViewById(R.id.landing_page_grid);
        LandingPageGridAdapter adapter = new LandingPageGridAdapter(this, texts, imageIds);
        landing_page_grid.setAdapter(adapter);
        landing_page_grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) { // Open broadcast (Just like that)
                    Intent intent = new Intent(LandingPageActivity.this, BroadcastActivity.class);
                    startActivity(intent);
                } else if (position == 1) { // Open news (News)
                    Intent intent = new Intent(LandingPageActivity.this, NewsActivity.class);
                    startActivity(intent);
                } else if (position == 2) { // Open Sports News (Sports)
                    Intent intent = new Intent(LandingPageActivity.this, SportsActivity.class);
                    startActivity(intent);
                } else if (position == 3) { // Open Science & Technology News (Science & Technology)
                    Intent intent = new Intent(LandingPageActivity.this, ScienceNewsActivity.class);
                    startActivity(intent);
                } else if (position == 4) { // Open Articles/Editorials (Articles)
                    Intent intent = new Intent(LandingPageActivity.this, ArticlesActivity.class);
                    startActivity(intent);
                } else if (position == 5) { // Open Bookmarks
                    Intent intent = new Intent(LandingPageActivity.this, SettingsActivity.class);
                    startActivity(intent);
                }
            }
        });

        AppController.getInstance().setLandingPageActive(true);

        String messageToDisplay = getIntent().getStringExtra(MESSAGE_IF_ANY);
        if(messageToDisplay != null && !messageToDisplay.trim().isEmpty()) {
            Snackbar.make(landing_page_grid, messageToDisplay, Snackbar.LENGTH_LONG);
        }
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
        }
        return super.onOptionsItemSelected(item);
    }
}
