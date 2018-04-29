package com.pack.pack.application.activity;

import android.app.ProgressDialog;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.AbsListView;
import android.widget.ListView;

import com.pack.pack.application.R;
import com.pack.pack.application.adapters.ScienceNewsActivityAdapter;
import com.pack.pack.application.data.util.IAsyncTaskStatusListener;
import com.pack.pack.application.data.util.ScienceNewsFeedTask;
import com.squill.feed.web.model.JRssFeed;
import com.pack.pack.model.web.Pagination;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Saurav on 25-03-2018.
 */
public class ScienceNewsActivity extends AppCompatActivity {

    private ScienceNewsActivityAdapter adapter;

    private ListView science_feeds;

    private String nextLink;

    private String prevLink;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_science);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        science_feeds = (ListView) findViewById(R.id.science_feeds);
        List<JRssFeed> feeds = new LinkedList<JRssFeed>();
        adapter = new ScienceNewsActivityAdapter(this, feeds);
        science_feeds.setAdapter(adapter);
        science_feeds.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                int count = science_feeds.getCount();
                if (scrollState == SCROLL_STATE_IDLE) {
                    int c = count - 3;
                    if (science_feeds.getLastVisiblePosition() >= c && c > 0 && !"END_OF_PAGE".equals(nextLink)) {
                        loadScienceFeeds(nextLink, false);
                    }
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });

        loadScienceFeeds(!"END_OF_PAGE".equals(nextLink) ? nextLink : prevLink, true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideProgressDialog();
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(ScienceNewsActivity.this);
            progressDialog.setMessage("Loading...");
            progressDialog.show();
        }
    }

    private void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    private void loadScienceFeeds(String pageLink, boolean showLoadingProgress) {
        ScienceNewsFeedTask task = new ScienceNewsFeedTask(ScienceNewsActivity.this);
        ScienceNewsFeedTaskStatusListener listener = new ScienceNewsFeedTaskStatusListener(task.getTaskID(), showLoadingProgress);
        task.addListener(listener);
        task.execute(pageLink);
    }

    private class ScienceNewsFeedTaskStatusListener implements IAsyncTaskStatusListener {

        private String taskID;

        private boolean showLoadingProgress;

        ScienceNewsFeedTaskStatusListener(String taskID, boolean showLoadingProgress) {
            this.taskID = taskID;
            this.showLoadingProgress = showLoadingProgress;
        }

        @Override
        public void onPreStart(String taskID) {
            if (this.taskID.equals(taskID) && showLoadingProgress) {
                showProgressDialog();
            }
        }

        @Override
        public void onFailure(String taskID, String errorMsg) {
            if (this.taskID.equals(taskID)) {
                Snackbar.make(science_feeds, errorMsg, Snackbar.LENGTH_LONG).show();
            }
        }

        @Override
        public void onSuccess(String taskID, Object data) {
            if (this.taskID.equals(taskID) && data != null) {
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
            if (this.taskID.equals(taskID) && showLoadingProgress) {
                hideProgressDialog();
            }
        }
    }
}

