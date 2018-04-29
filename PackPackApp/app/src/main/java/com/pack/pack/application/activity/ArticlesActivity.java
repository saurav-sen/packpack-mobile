package com.pack.pack.application.activity;

import android.app.ProgressDialog;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.AbsListView;
import android.widget.ListView;

import com.pack.pack.application.R;
import com.pack.pack.application.adapters.ArticlesActivityAdapter;
import com.pack.pack.application.data.util.ArticlesFeedTask;
import com.pack.pack.application.data.util.IAsyncTaskStatusListener;
import com.squill.feed.web.model.JRssFeed;
import com.pack.pack.model.web.Pagination;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Saurav on 25-03-2018.
 */
public class ArticlesActivity extends AppCompatActivity {

    private ArticlesActivityAdapter adapter;

    private ListView articles_feeds;

    private String nextLink;

    private String prevLink;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_articles);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        articles_feeds = (ListView) findViewById(R.id.articles_feeds);
        List<JRssFeed> feeds = new LinkedList<JRssFeed>();
        adapter = new ArticlesActivityAdapter(this, feeds);
        articles_feeds.setAdapter(adapter);
        articles_feeds.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                int count = articles_feeds.getCount();
                if (scrollState == SCROLL_STATE_IDLE) {
                    int c = count - 3;
                    if (articles_feeds.getLastVisiblePosition() >= c && c > 0 && !"END_OF_PAGE".equals(nextLink)) {
                        loadArticlesFeeds(nextLink, false);
                    }
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });

        loadArticlesFeeds(!"END_OF_PAGE".equals(nextLink) ? nextLink : prevLink, true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideProgressDialog();
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(ArticlesActivity.this);
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

    private void loadArticlesFeeds(String pageLink, boolean showLoadingProgress) {
        ArticlesFeedTask task = new ArticlesFeedTask(ArticlesActivity.this);
        ArticlesFeedTaskStatusListener listener = new ArticlesFeedTaskStatusListener(task.getTaskID(), showLoadingProgress);
        task.addListener(listener);
        task.execute(pageLink);
    }

    private class ArticlesFeedTaskStatusListener implements IAsyncTaskStatusListener {

        private String taskID;

        private boolean showLoadingProgress;

        ArticlesFeedTaskStatusListener(String taskID, boolean showLoadingProgress) {
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
                Snackbar.make(articles_feeds, errorMsg, Snackbar.LENGTH_LONG).show();
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

