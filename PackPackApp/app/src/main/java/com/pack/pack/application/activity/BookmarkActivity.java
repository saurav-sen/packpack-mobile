package com.pack.pack.application.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.AbsListView;
import android.widget.ListView;

import com.pack.pack.application.R;
import com.pack.pack.application.adapters.BookmarkActivityAdapter;
import com.pack.pack.application.data.util.IAsyncTaskStatusListener;
import com.pack.pack.application.data.util.LoadBookmarkTask;
import com.pack.pack.application.db.Bookmark;
import com.pack.pack.application.db.PagedObject;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Saurav on 26-08-2018.
 */
public class BookmarkActivity extends AppCompatActivity {

    private ListView bookmark_feeds;

    private ProgressDialog progressDialog;

    private BookmarkActivityAdapter adapter;

    private long currentPageRef = Long.MAX_VALUE;

    private static long END_OF_PAGE = Long.MIN_VALUE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        bookmark_feeds = (ListView) findViewById(R.id.bookmark_feeds);
        List<Bookmark> feeds = new LinkedList<Bookmark>();
        adapter = new BookmarkActivityAdapter(this, feeds);
        bookmark_feeds.setAdapter(adapter);
        bookmark_feeds.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                int count = bookmark_feeds.getCount();
                if (scrollState == SCROLL_STATE_IDLE) {
                    int c = count - 3;
                    if (bookmark_feeds.getLastVisiblePosition() >= c && c > 0) {
                        loadBookmarks(currentPageRef, false);
                    }
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });

        loadBookmarks(currentPageRef, true);
    }

    private void loadBookmarks(long currentPageRef, boolean showLoadingProgress) {
        if(END_OF_PAGE == currentPageRef) {
            return;
        }
        LoadBookmarkTask task = new LoadBookmarkTask(BookmarkActivity.this, showLoadingProgress);
        BookmarkLoadTaskListener listener = new BookmarkLoadTaskListener(task.getTaskID(), showLoadingProgress);
        task.addListener(listener);
        task.execute(currentPageRef);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideProgressDialog();
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(BookmarkActivity.this);
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

    private class BookmarkLoadTaskListener implements IAsyncTaskStatusListener {

        private String taskID;

        private boolean showLoadingProgress;

        BookmarkLoadTaskListener(String taskID, boolean showLoadingProgress) {
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
                Snackbar.make(bookmark_feeds, errorMsg, Snackbar.LENGTH_LONG).show();
            }
        }

        @Override
        public void onPostComplete(String taskID) {
            if (this.taskID.equals(taskID) && showLoadingProgress) {
                hideProgressDialog();
            }
        }

        @Override
        public void onSuccess(String taskID, Object data) {
            if (this.taskID.equals(taskID) && data != null) {
                PagedObject<Bookmark> pagedObject = (PagedObject<Bookmark>) data;
                List<Bookmark> bookmarks = pagedObject.getResult();
                if(bookmarks != null && !bookmarks.isEmpty()) {
                    adapter.getFeeds().addAll(bookmarks);
                    adapter.notifyDataSetChanged();
                    currentPageRef = pagedObject.getNextPageRef();
                } else {
                    currentPageRef = END_OF_PAGE;
                }
            }
        }
    }
}
