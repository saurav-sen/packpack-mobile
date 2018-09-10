package com.pack.pack.application.activity;

import android.app.ProgressDialog;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.AbsListView;
import android.widget.ListView;

import com.pack.pack.application.Constants;
import com.pack.pack.application.R;
import com.pack.pack.application.adapters.SportsActivityAdapter;
import com.pack.pack.application.data.util.IAsyncTaskStatusListener;
import com.pack.pack.application.data.util.SportsFeedTask;
import com.squill.feed.web.model.JRssFeed;
import com.pack.pack.model.web.Pagination;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Saurav on 25-03-2018.
 */
public class SportsActivity extends AppCompatActivity {

    private SportsActivityAdapter adapter;

    private ListView sports_feeds;

    private int nextPageNo = 0;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sports);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        sports_feeds = (ListView) findViewById(R.id.sports_feeds);
        List<JRssFeed> feeds = new LinkedList<JRssFeed>();
        adapter = new SportsActivityAdapter(this, feeds);
        sports_feeds.setAdapter(adapter);
        sports_feeds.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                int count = sports_feeds.getCount();
                if (scrollState == SCROLL_STATE_IDLE) {
                    int c = (int) (count * 0.7f);
                    if (sports_feeds.getLastVisiblePosition() >= c) {
                        loadSportsFeeds(nextPageNo, false);
                    }
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });

        nextPageNo = 0;
        loadSportsFeeds(nextPageNo, true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideProgressDialog();
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(SportsActivity.this);
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

    private void loadSportsFeeds(int pageNo, boolean showLoadingProgress) {
        if(pageNo < 0)
            return;
        SportsFeedTask task = new SportsFeedTask(SportsActivity.this, pageNo);
        SportsFeedTaskStatusListener listener = new SportsFeedTaskStatusListener(task.getTaskID(), showLoadingProgress);
        task.addListener(listener);
        task.execute(String.valueOf(pageNo));
    }

    private class SportsFeedTaskStatusListener implements IAsyncTaskStatusListener {

        private String taskID;

        private boolean showLoadingProgress;

        SportsFeedTaskStatusListener(String taskID, boolean showLoadingProgress) {
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
                Snackbar.make(sports_feeds, errorMsg, Snackbar.LENGTH_LONG).show();
            }
        }

        @Override
        public void onSuccess(String taskID, Object data) {
            if (this.taskID.equals(taskID) && data != null) {
                Pagination<JRssFeed> page = (Pagination<JRssFeed>) data;
                nextPageNo = page.getNextPageNo();
                List<JRssFeed> list = page.getResult();
                if(list == null || list.isEmpty())
                    return;
                adapter.addNewFeeds(list);
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
