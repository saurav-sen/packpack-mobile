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
import com.pack.pack.application.adapters.HomeActivityAdapter;
import com.pack.pack.application.adapters.NewsActivityAdapter;
import com.pack.pack.application.data.util.IAsyncTaskStatusListener;
import com.pack.pack.application.data.util.NewsFeedTask;
import com.squill.feed.web.model.JRssFeed;
import com.pack.pack.model.web.Pagination;

import java.util.LinkedList;
import java.util.List;

public class NewsActivity extends AppCompatActivity {

    private NewsActivityAdapter adapter;

    private ListView news_feeds;

    private int nextPageNo = 0;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        news_feeds = (ListView) findViewById(R.id.news_feeds);
        List<JRssFeed> feeds = new LinkedList<JRssFeed>();
        adapter = new NewsActivityAdapter(this, feeds);
        news_feeds.setAdapter(adapter);
        news_feeds.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                int count = news_feeds.getCount();
                if (scrollState == SCROLL_STATE_IDLE) {
                    int c = (int) (count * 0.7f);
                    if (news_feeds.getLastVisiblePosition() >= c) {
                        loadNewsFeeds(nextPageNo, false);
                    }
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });

        nextPageNo = 0;
        loadNewsFeeds(nextPageNo, true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideProgressDialog();
    }

    private void showProgressDialog() {
        if(progressDialog == null) {
            progressDialog = new ProgressDialog(NewsActivity.this);
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

    private void loadNewsFeeds(int pageNo, boolean showLoadingProgress) {
        if(pageNo < 0)
            return;
        NewsFeedTask task = new NewsFeedTask(NewsActivity.this, pageNo);
        NewsFeedTaskStatusListener listener = new NewsFeedTaskStatusListener(task.getTaskID(), showLoadingProgress);
        task.addListener(listener);
        task.execute(String.valueOf(pageNo));
    }

    private class NewsFeedTaskStatusListener implements IAsyncTaskStatusListener {

        private String taskID;

        private boolean showLoadingProgress;

        NewsFeedTaskStatusListener(String taskID, boolean showLoadingProgress) {
            this.taskID = taskID;
            this.showLoadingProgress = showLoadingProgress;
        }

        @Override
        public void onPreStart(String taskID) {
            if(this.taskID.equals(taskID) && showLoadingProgress) {
                showProgressDialog();
            }
        }

        @Override
        public void onFailure(String taskID, String errorMsg) {
            if(this.taskID.equals(taskID)) {
                Snackbar.make(news_feeds, errorMsg, Snackbar.LENGTH_LONG).show();
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
                adapter.addNewFeeds(list);
                adapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onPostComplete(String taskID) {
            if(this.taskID.equals(taskID) && showLoadingProgress) {
                hideProgressDialog();
            }
        }
    }
}
