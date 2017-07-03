package com.pack.pack.application.activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;

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
import com.pack.pack.application.data.util.AbstractNetworkTask;
import com.pack.pack.application.data.util.IAsyncTaskStatusListener;
import com.pack.pack.application.image.loader.DownloadImageTask;
import com.pack.pack.application.topic.activity.model.ParcelableTopic;
import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.model.web.EntityType;
import com.pack.pack.model.web.JTopic;
import com.pack.pack.model.web.JUser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.pack.pack.application.AppController.EDIT_TOPIC_REQUSET_CODE;

import io.branch.invite.SimpleInviteBuilder;
import io.branch.referral.Branch;

/**
 *
 * @author Saurav
 *
 */
public class TopicDetailActivity extends AbstractAppCompatActivity implements OnMapReadyCallback {

    private ParcelableTopic topic;

    private static final String MAP_FRAGMENT_TAG = "map";

    private boolean gMapAvailable = false;

    private GoogleMap gMap;

    //private ImageButton invitePeople;

    private static final String LOG_TAG = "TopicDetailActivity";

    private TextView topic_name_text;

    private TextView topic_description_text;

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
       /*MenuItem item1 = menu.findItem(R.id.enter_forum);
       if(item1 != null) {
           item1.setVisible(true);
       }*/
       invalidateOptionsMenu();
       return true;
    }

   @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            /*case R.id.enter_forum:
                Intent intent = new Intent(TopicDetailActivity.this, DiscussionViewActivity.class);
                intent.putExtra(Constants.DISCUSSION_ENTITY_ID, topic.getTopicId());
                intent.putExtra(Constants.DISCUSSION_ENTITY_TYPE, EntityType.TOPIC.name());
                startActivity(intent);
                break;*/
            case R.id.app_settings:
                Intent intent_0 = new Intent(TopicDetailActivity.this, SettingsActivity.class);
                startActivity(intent_0);
                break;
        }
        return true;
    }

    private boolean readContactsGranted = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.topic_detail_toolbar);
        //setActionBar(toolbar);
        setSupportActionBar(toolbar);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    AppController.READ_CONTACTS_REQUEST_CODE);
        } else {
            readContactsGranted = false;
        }

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        topic = (ParcelableTopic) getIntent().getParcelableExtra(AppController.TOPIC_PARCELABLE_KEY);

        FloatingActionButton topic_edit_fab = (FloatingActionButton) findViewById(R.id.topic_edit_fab);
        String userId = AppController.getInstance().getUserId();
        if(userId.equals(topic.getOwnerId())) {
            topic_edit_fab.setVisibility(View.VISIBLE);
            topic_edit_fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(TopicDetailActivity.this, TopicEditActivity.class);
                    intent.putExtra(TopicEditActivity.TOPIC_INPUT, topic);
                    startActivityForResult(intent, EDIT_TOPIC_REQUSET_CODE);
                }
            });
        } else {
            topic_edit_fab.setVisibility(View.GONE);
        }

        topic_name_text = (TextView) findViewById(R.id.topic_name_text);
        topic_name_text.setText((topic.getTopicName() + "").trim());

        String longStory = topic.getDescription() + "";

        String[] split = longStory.split("[\n|\r]");
        int longStoryLineCount = split.length;

        topic_description_text = (TextView) findViewById(R.id.topic_description_text);
        topic_description_text.setMinLines(longStoryLineCount);

        if(longStoryLineCount > 3) {
            StringBuilder str = new StringBuilder();
            for(int i=0; i<longStoryLineCount; i++) {
                String s = split[i];
                str.append(s);
                if(s.trim().length() > 0) {
                    str.append("\n");
                }
            }
            longStory = str.toString();
        }

        topic_description_text.setText(longStory);

        ImageView topic_wallpaper_img = (ImageView) findViewById(R.id.topic_wallpaper_img);

        ProgressBar topic_detail_loading_progress = (ProgressBar) findViewById(R.id.topic_detail_loading_progress);
        topic_detail_loading_progress.setVisibility(View.VISIBLE);

       /* Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int imageWidth = (int)(size.x * 0.7f);
        int imageHeight = (int)(size.y * 0.5f);*/
        new DownloadImageTask(topic_wallpaper_img, this, topic_detail_loading_progress).execute(topic.getWallpaperUrl());

        ImageButton enterTopic = (ImageButton) findViewById(R.id.enter_topic_detail);
        enterTopic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openTopic(topic);
            }
        });

        final ImageButton followTopic = (ImageButton) findViewById(R.id.follow_not_follow_topic);
        if(!topic.isFollowing()) {
            followTopic.setImageResource(R.drawable.follow_topic);
        } else {
            followTopic.setImageResource(R.drawable.neglect_topic);
        }

        String topicOwnerId = topic.getOwnerId();

        if(userId.equals(topicOwnerId)) {
            followTopic.setAlpha(0.4f);
            followTopic.setClickable(false);
        } else {
            followTopic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FollowTopicTask task = new FollowTopicTask(TopicDetailActivity.this, topic);
                    task.addListener(new FollowTopicTaskListener(task.getTaskID(), followTopic, topic.isFollowing()));
                    task.execute(topic.getTopicId());
                }
            });
        }

        /*invitePeople = (ImageButton) findViewById(R.id.invite_people);
        invitePeople.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // https://github.com/BranchMetrics/Branch-Android-Invite-SDK
                new SimpleInviteBuilder(TopicDetailActivity.this, "Inviting userID", "Inviting user Name").showInviteDialog();
            }
        });*/

        /*ImageButton promoteTopic = (ImageButton) findViewById(R.id.promote_topic);
        promoteTopic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });*/

        ImageButton shareTopic = (ImageButton) findViewById(R.id.share_topic);
        shareTopic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareTopic(view);
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

    protected void openTopic(ParcelableTopic topic) {
        Intent intent = new Intent(TopicDetailActivity.this, InsideTopicActivity.class);
        intent.putExtra(AppController.TOPIC_PARCELABLE_KEY, topic);
        startActivity(intent);
    }

    protected void shareTopic(View view) {
        File file = createScreenCast(view);
        shareImage(file);
    }

    private void shareImage(File file) {
        if(file == null || !file.exists()) {
            return;
        }

        Uri uri = Uri.fromFile(file);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/*");

        intent.putExtra(Intent.EXTRA_SUBJECT, "");
        intent.putExtra(Intent.EXTRA_TEXT, "Checkout my vision " + topic.getTopicName() + " @ SQUILL @ " + AppController.getInstance().getApkUrl());
        intent.putExtra(Intent.EXTRA_STREAM, uri);

        startActivity(Intent.createChooser(intent, "Share My Vision"));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == EDIT_TOPIC_REQUSET_CODE) {
            if(resultCode == RESULT_OK && data != null) {
                ParcelableTopic pTopic = (ParcelableTopic) data.getParcelableExtra(TopicEditActivity.RESULT_KEY);
                topic = pTopic;
                topic_name_text.setText(topic.getTopicName());
                topic_description_text.setText(topic.getDescription());
            } else if(resultCode == RESULT_CANCELED) {
                String msg = "Failed to update";
                if(data != null) {
                    String str = data.getStringExtra(TopicEditActivity.RESULT_KEY);
                    if(str != null) {
                        msg = str;
                    }
                }
                Toast.makeText(TopicDetailActivity.this, msg, Toast.LENGTH_LONG).show();
            }
        }
    }

    private File createScreenCast(View view) {
        File file = null;
        View rootView = getWindow().getDecorView().findViewById(R.id.mainLayout);
        View screenView = rootView.getRootView();
        screenView.setDrawingCacheEnabled(true);
        Bitmap screenCast = Bitmap.createBitmap(screenView.getDrawingCache());
        screenView.setDrawingCacheEnabled(false);
        //String dirPath = TopicDetailActivity.this.getCacheDir().getAbsolutePath() + File.separator + "screencasts";
        String dirPath = Environment.getExternalStorageDirectory() + File.separator + "screencasts";
        File dir = new File(dirPath);
        if(!dir.exists()) {
            dir.mkdir();
        }
        String fileName = UUID.randomUUID().toString() + ".jpg";
        file = new File(dirPath + File.separator + fileName);
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            screenCast.compress(Bitmap.CompressFormat.JPEG, 85, outputStream);
            outputStream.flush();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Failed To Save Screencast", e);
            Snackbar.make(view, "Failed To Share", Snackbar.LENGTH_LONG);
        } finally {
            try {
                if(outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                Log.e(LOG_TAG, "Failed Closing Output Stream", e);
            }
        }
        return file;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case AppController.READ_CONTACTS_REQUEST_CODE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    readContactsGranted = true;
                    AppController.getInstance().initializeBranchIO();
                } else {
                    readContactsGranted = false;
                }
                /*if(invitePeople != null) {
                    invitePeople.setEnabled(readContactsGranted);
                }*/
                break;
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

    private class FollowTopicTask extends AbstractNetworkTask<String, Integer, Void> {

        private ParcelableTopic topic;

        public FollowTopicTask(Context context, ParcelableTopic topic) {
            super(false, false, false, context, true, true);
            this.topic = topic;
        }

        @Override
        protected String getContainerIdForObjectStore() {
            return null;
        }

        @Override
        protected Void executeApi(API api) throws Exception {
            api.execute();
            return null;
        }

        @Override
        protected COMMAND command() {
            if(!topic.isFollowing()) {
                return COMMAND.FOLLOW_TOPIC;
            }
            return COMMAND.NEGLECT_TOPIC;
        }

        @Override
        protected void fireOnSuccess(Object data) {
            topic.setIsFollowing(!topic.isFollowing());
            super.fireOnSuccess(data);
        }

        @Override
        protected Map<String, Object> prepareApiParams(String inputObject) {
            JUser user = AppController.getInstance().getUser();
            Map<String, Object> apiParams = new HashMap<String, Object>();
            apiParams.put(APIConstants.User.ID, user.getId());
            apiParams.put(APIConstants.Topic.ID, inputObject);
            return apiParams;
        }

        @Override
        protected String getFailureMessage() {
            return "Failed following topic command";
        }
    }

    private class FollowTopicTaskListener implements IAsyncTaskStatusListener {

        private String taskID;

        private ImageButton imageButton;

        private boolean isFollow;

        FollowTopicTaskListener(String taskID, ImageButton imageButton, boolean isFollow) {
            this.taskID = taskID;
            this.imageButton = imageButton;
            this.isFollow = isFollow;
        }

        @Override
        public void onPreStart(String taskID) {

        }

        @Override
        public void onSuccess(String taskID, Object data) {
            TopicDetailActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (isFollow) {
                        imageButton.setImageResource(R.drawable.follow_topic);
                    } else {
                        imageButton.setImageResource(R.drawable.neglect_topic);
                    }
                }
            });
        }

        @Override
        public void onFailure(String taskID, String errorMsg) {

        }

        @Override
        public void onPostComplete(String taskID) {

        }
    }
}
