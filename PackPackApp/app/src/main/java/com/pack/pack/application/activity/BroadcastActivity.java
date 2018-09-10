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

    private HomeActivityAdapter adapter;

    private ListView squill_feeds;

    private int nextPageNo;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broadcast);

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
                    if (squill_feeds.getLastVisiblePosition() > count - 1) {
                        loadRssFeeds(nextPageNo, false);
                    }
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });

        nextPageNo = 0;
        loadRssFeeds(nextPageNo, true);
    }

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

    private void loadRssFeeds(int pageNo, boolean loadOfflineData) {
        if(pageNo < 0)
            return;
        RefreshmentFeedTask task = new RefreshmentFeedTask(BroadcastActivity.this, pageNo);
        RssFeedTaskStatusListener listener = new RssFeedTaskStatusListener(task.getTaskID());
        task.addListener(listener);
        task.execute(String.valueOf(pageNo));
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
                nextPageNo = page.getNextPageNo();
                List<JRssFeed> list = page.getResult();
                if(list == null || list.isEmpty())
                    return;
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
