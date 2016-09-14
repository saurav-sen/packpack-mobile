package com.pack.pack.application.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.pack.pack.application.AppController;
import com.pack.pack.application.Constants;
import com.pack.pack.application.R;
import com.pack.pack.application.image.loader.DownloadImageTask;
import com.pack.pack.application.topic.activity.model.ParcelableTopic;
import com.pack.pack.model.web.EntityType;

/**
 *
 * @author Saurav
 *
 */
public class TopicDetailActivity extends AppCompatActivity implements OnMapReadyCallback {

    private ParcelableTopic topic;

    private static final String MAP_FRAGMENT_TAG = "map";

    private boolean gMapAvailable = false;

    private GoogleMap gMap;

   @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /*Toolbar toolbar = (Toolbar) findViewById(R.id.topic_detail_toolbar);
        toolbar.inflateMenu(R.menu.inside_topic);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return onOptionsItemSelected(item);
            }
        });*/
       getMenuInflater().inflate(R.menu.app_menu, menu);
       MenuItem item0 = menu.findItem(R.id.app_settings);
       if(item0 != null) {
           item0.setVisible(true);
       }
       MenuItem item1 = menu.findItem(R.id.enter_forum);
       if(item1 != null) {
           item1.setVisible(true);
       }
       invalidateOptionsMenu();
       return true;
    }

   @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.enter_forum:
                Intent intent = new Intent(TopicDetailActivity.this, DiscussionViewActivity.class);
                intent.putExtra(Constants.DISCUSSION_ENTITY_ID, topic.getTopicId());
                intent.putExtra(Constants.DISCUSSION_ENTITY_TYPE, EntityType.TOPIC.name());
                startActivity(intent);
                break;
            case R.id.app_settings:
                Intent intent_0 = new Intent(TopicDetailActivity.this, SettingsActivity.class);
                startActivity(intent_0);
                break;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.topic_detail_toolbar);
        //setActionBar(toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        topic = (ParcelableTopic) getIntent().getParcelableExtra(AppController.TOPIC_PARCELABLE_KEY);

        TextView topic_name_text = (TextView) findViewById(R.id.topic_name_text);
        topic_name_text.setText((topic.getTopicName() + "").trim());

        TextView topic_description_text = (TextView) findViewById(R.id.topic_description_text);
        topic_description_text.setText((topic.getDescription() + "").trim());

        ImageView topic_wallpaper_img = (ImageView) findViewById(R.id.topic_wallpaper_img);

        ProgressBar topic_detail_loading_progress = (ProgressBar) findViewById(R.id.topic_detail_loading_progress);
        topic_detail_loading_progress.setVisibility(View.VISIBLE);

        new DownloadImageTask(topic_wallpaper_img, this, topic_detail_loading_progress).execute(topic.getWallpaperUrl());

        ImageButton enterTopic = (ImageButton) findViewById(R.id.enter_topic_detail);
        enterTopic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TopicDetailActivity.this, InsideTopicActivity.class);
                intent.putExtra(AppController.TOPIC_PARCELABLE_KEY, topic);
                startActivity(intent);
            }
        });

        ImageButton followTopic = (ImageButton) findViewById(R.id.follow_not_follow_topic);
        followTopic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

       /* SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentByTag(MAP_FRAGMENT_TAG);
        if(mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(android.R.id.content, mapFragment, MAP_FRAGMENT_TAG);
            fragmentTransaction.commit();
        }
        mapFragment.getMapAsync(this);*/
        gMapAvailable = isMapAvailable();
        if(gMapAvailable) {
            initializeGMap();
        }
    }

    private void initializeGMap() {
        if(gMap != null)
            return;
        MapFragment fragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        fragment.getMapAsync(this);
    }

    private boolean isMapAvailable() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int code = googleApiAvailability.isGooglePlayServicesAvailable(this);
        boolean bool = (code == ConnectionResult.SUCCESS);
        if(!bool && googleApiAvailability.isUserResolvableError(code)) {
            googleApiAvailability.getErrorDialog(this, code, 1010).show();
        }
        return bool;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        if(gMap != null)
            return;
        if(topic == null)
            return;
        gMap = map;
        gMap.getUiSettings().setZoomControlsEnabled(true);
        gMap.getUiSettings().setZoomGesturesEnabled(true);
        LatLng latLng = new LatLng(topic.getLatitude(), topic.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 8);
        gMap.moveCamera(cameraUpdate);
        gMap.addMarker(new MarkerOptions().position(latLng).anchor(0.5f, 0.5f)).setTitle(topic.getAddress());
    }
}
