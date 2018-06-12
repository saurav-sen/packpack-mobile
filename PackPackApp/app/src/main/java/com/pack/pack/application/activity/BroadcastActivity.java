package com.pack.pack.application.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;

import com.pack.pack.application.AppController;
import com.pack.pack.application.Constants;
import com.pack.pack.application.R;
import com.pack.pack.application.adapters.HomeActivityAdapter;
import com.pack.pack.application.data.util.IAsyncTaskStatusListener;
import com.pack.pack.application.data.util.RefreshmentFeedTask;
import com.squill.feed.web.model.JRssFeed;
import com.pack.pack.model.web.Pagination;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.pack.pack.application.AppController.CREATE_TOPIC_REQUSET_CODE;

public class BroadcastActivity extends AbstractAppCompatActivity {

    //private ViewPager pager;

    //private Toolbar toolbar;

    private HomeActivityAdapter adapter;

    private ListView squill_feeds;

    private String nextLink;

    private String prevLink;

    private ProgressDialog progressDialog;

    //private int pageCurrentItemIndex;

    //public static final String PAGE_CURRENT_INDEX = "pageCurrentItemIndex";
    //public static final String RECREATE = "RECREATE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broadcast);

        /*toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        squill_feeds = (ListView) findViewById(R.id.squill_feeds);
        List<JRssFeed> feeds = new LinkedList<JRssFeed>();
        adapter = new HomeActivityAdapter(this, feeds);
        squill_feeds.setAdapter(adapter);
        squill_feeds.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                int count = squill_feeds.getCount();
                if (scrollState == SCROLL_STATE_IDLE) {
                    if (squill_feeds.getLastVisiblePosition() > count - 1 && !Constants.END_OF_PAGE.equals(nextLink)) {
                        loadRssFeeds(nextLink, false);
                    }
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });

        loadRssFeeds(!Constants.END_OF_PAGE.equals(nextLink) ? nextLink : prevLink, true);
        //new RSSFeedTask(BroadcastActivity.this).execute(!"END_OF_PAGE".equals(nextLink) ? nextLink : prevLink);

        /*FloatingActionButton FAB = (FloatingActionButton) findViewById(R.id.broadcast_create);
        FAB.setVisibility(View.GONE);
        FAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               *//* Intent intent = new Intent(BroadcastActivity.this, TopicCreateActivity.class);
                startActivityForResult(intent, CREATE_TOPIC_REQUSET_CODE);*//*
            }
        });*/

        /*List<String> list = AppController.getInstance().getFollowedCategories();
        TabType[] values = TabType.values();
        Object __OBJECT = new Object();
        Map<String, Object> map = new HashMap<String, Object>();
        for(String l : list) {
            map.put(l, __OBJECT);
        }

        boolean recreate = getIntent().getBooleanExtra(RECREATE, false);

        List<TabType> types = new ArrayList<TabType>();
        for(TabType value : values) {
            if(!value.isEnabled()) {
                continue;
            }
            if((!value.isBroadcastTab())) {
                continue;
            }
            value.setRecreate(recreate);
            types.add(value);
        }

        pager = (ViewPager)findViewById(R.id.broadcast_pager);
        pager.setAdapter(new MainActivityAdapter(getSupportFragmentManager(), types.toArray(new TabType[types.size()])));
        pager.setOffscreenPageLimit(2);
        int itemIndex = getIntent().getIntExtra(PAGE_CURRENT_INDEX, -1);
        if(itemIndex >= 0 && itemIndex < list.size()) {
            pageCurrentItemIndex = itemIndex;
            pager.setCurrentItem(pageCurrentItemIndex);
        }

        TabLayout tabLayout = (TabLayout) findViewById(R.id.broadcast_tabs);
        tabLayout.setSelectedTabIndicatorColor(Color.WHITE);
        tabLayout.setupWithViewPager(pager);

        int i=0;
        for(TabType value : types) {
            tabLayout.getTabAt(i).setIcon(value.getIcon());
            tabLayout.getTabAt(i).setText(value.getDisplayName());
            i++;
        }*/

        /*if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
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

        }*/

        //startNotificationReader();
    }

    /*private void startNotificationReader() {
        Intent intent = new Intent(this, NotificationReaderService.class);
        startService(intent);
    }*/

    /*@Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        pageCurrentItemIndex = pager.getCurrentItem();
        outState.putInt(PAGE_CURRENT_INDEX, pageCurrentItemIndex);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        pageCurrentItemIndex = savedInstanceState.getInt(PAGE_CURRENT_INDEX);
    }*/

    /*@Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case AppController.CAMERA_ACCESS_REQUEST_CODE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    AppController.getInstance().cameraPermissionGranted();
                    *//*if(Build.VERSION.SDK_INT >= 11) {
                        recreate();
                    } else {
                        finish();
                        startActivity(getIntent());
                    }*//*
                } else {
                    AppController.getInstance().cameraPermisionDenied();
                }
                break;
            case AppController.APP_EXTERNAL_STORAGE_READ_REQUEST_CODE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    AppController.getInstance().externalReadGranted();
                    *//*if(Build.VERSION.SDK_INT >= 11) {
                        recreate();
                    } else {
                        finish();
                        startActivity(getIntent());
                    }*//*
                } else {
                    AppController.getInstance().externalReadDenied();
                }
                break;
        }
    }*/

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CREATE_TOPIC_REQUSET_CODE) {
            if(resultCode == RESULT_OK && data != null) {
                ParcelableTopic pTopic = (ParcelableTopic) data.getParcelableExtra(TopicCreateActivity.RESULT_KEY);
                if(Build.VERSION.SDK_INT >= 11) {
                    getIntent().putExtra(PAGE_CURRENT_INDEX, pageCurrentItemIndex);
                    getIntent().putExtra(RECREATE, true);
                    recreate();
                } else {
                    finish();
                    getIntent().putExtra(PAGE_CURRENT_INDEX, pageCurrentItemIndex);
                    getIntent().putExtra(RECREATE, true);
                    startActivity(getIntent());
                }
            }
            else {
                Toast.makeText(BroadcastActivity.this, "Sorry!! Failed creating new vision",
                        Toast.LENGTH_LONG).show();;
            }
        }
    }*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideProgressDialog();
    }

    private void showProgressDialog() {
        if(progressDialog == null) {
            progressDialog = new ProgressDialog(BroadcastActivity.this);
            progressDialog.setMessage("Loading...");
            progressDialog.show();
        }
    }

    private void hideProgressDialog() {
        if(progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    private void loadRssFeeds(String pageLink, boolean loadOfflineData) {
        RefreshmentFeedTask task = new RefreshmentFeedTask(BroadcastActivity.this, loadOfflineData);
        RssFeedTaskStatusListener listener = new RssFeedTaskStatusListener(task.getTaskID());
        task.addListener(listener);
        task.execute(pageLink);
    }

    private class RssFeedTaskStatusListener implements IAsyncTaskStatusListener {

        private String taskID;

        RssFeedTaskStatusListener(String taskID) {
            this.taskID = taskID;
        }

        @Override
        public void onPreStart(String taskID) {
            if(this.taskID.equals(taskID)) {
                showProgressDialog();
            }
        }

        @Override
        public void onFailure(String taskID, String errorMsg) {
            if(this.taskID.equals(taskID)) {
                Snackbar.make(squill_feeds, errorMsg, Snackbar.LENGTH_LONG).show();
            }
        }

        @Override
        public void onSuccess(String taskID, Object data) {
            if(this.taskID.equals(taskID) && data != null) {
                Pagination<JRssFeed> page = (Pagination<JRssFeed>) data;
                nextLink = page.getNextLink();
                prevLink = page.getPreviousLink();
                List<JRssFeed> list = page.getResult();
                adapter.getFeeds().addAll(list);
                adapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onPostComplete(String taskID) {
            if(this.taskID.equals(taskID)) {
                hideProgressDialog();
            }
        }
    }
}
