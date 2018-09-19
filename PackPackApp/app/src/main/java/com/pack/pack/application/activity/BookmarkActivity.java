package com.pack.pack.application.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.ListView;

import com.pack.pack.application.R;
import com.pack.pack.application.adapters.BookmarkActivityAdapter;
import com.pack.pack.application.data.util.BookmarkDeleteResult;
import com.pack.pack.application.data.util.BookmarkDeleteTask;
import com.pack.pack.application.data.util.Bookmarks;
import com.pack.pack.application.data.util.IAsyncTaskStatusListener;
import com.pack.pack.application.data.util.LoadBookmarkTask;
import com.pack.pack.application.db.Bookmark;
import com.pack.pack.application.db.PagedObject;

import java.util.ArrayList;
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
        /*bookmark_feeds.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                int count = bookmark_feeds.getCount();
                if (scrollState == SCROLL_STATE_IDLE) {
                    int c = (int) (count * 0.7f);
                    if (bookmark_feeds.getLastVisiblePosition() >= c) {
                        loadBookmarks(currentPageRef, false);
                    }
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });*/
       /* bookmark_feeds.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        bookmark_feeds.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                mode.setTitle("" + bookmark_feeds.getCheckedItemCount() + " selected");
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater menuInflater = mode.getMenuInflater();
                menuInflater.inflate(R.menu.bookmark_contextual_menu, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.bookmark_delete:
                        List<Bookmark> toDelete = new ArrayList<>();
                        SparseBooleanArray checkedItemPositions = bookmark_feeds.getCheckedItemPositions();
                        int len = checkedItemPositions.size();
                        for (int i = 0; i < len; i++) {
                            if (checkedItemPositions.valueAt(i)) {
                                if (i < adapter.getFeeds().size()) {
                                    Bookmark bookmark = adapter.getFeeds().get(i);
                                    if (bookmark != null) {
                                        bookmark.setUnderDeleteOperation(true);
                                        toDelete.add(bookmark);
                                    }
                                }
                            }
                        }
                        if (!toDelete.isEmpty()) {
                            deleteBookmarks(mode, toDelete, true);
                        }
                        return true;
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
            }
        });*/

        /*bookmark_feeds.setOnTouchListener(new SwipeDismissListViewTouchListener(bookmark_feeds, new SwipeDismissListViewTouchListener.DismissCallbacks() {
            @Override
            public boolean canDismiss(int position) {
                return true;
            }

            @Override
            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                List<Bookmark> toDelete = new ArrayList<Bookmark>(reverseSortedPositions.length * 2);
                int len = adapter.getFeeds().size();
                for (int position : reverseSortedPositions) {
                    if(position >= len)
                        continue;
                    Bookmark bookmark = adapter.getFeeds().get(position);
                    toDelete.add(bookmark);
                }
                if(!toDelete.isEmpty()) {
                    deleteBookmarks(toDelete, true);
                }
            }
        }));*/

        loadBookmarks(currentPageRef, true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*private void deleteBookmarks(List<Bookmark> toDelete, boolean showLoadingProgress) {
        deleteBookmarks(null, toDelete, showLoadingProgress);
    }*/

    /*private void deleteBookmarks(ActionMode mode, List<Bookmark> toDelete, boolean showLoadingProgress) {
        if(toDelete.isEmpty())
            return;
        Bookmarks bookmarks = new Bookmarks();
        bookmarks.getBookmarks().addAll(toDelete);
        BookmarkDeleteTask task = new BookmarkDeleteTask(BookmarkActivity.this, false);
        BookmarkDeleteTaskListener listener = new BookmarkDeleteTaskListener(mode, task.getTaskID(), showLoadingProgress);
        task.addListener(listener);
        task.execute(bookmarks);
    }*/

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

    private class BookmarkDeleteTaskListener implements IAsyncTaskStatusListener {

        private String taskID;

        private boolean showLoadingProgress;

        private ActionMode mode;

        BookmarkDeleteTaskListener(String taskID, boolean showLoadingProgress) {
            this(null, taskID, showLoadingProgress);
        }

        BookmarkDeleteTaskListener(ActionMode mode, String taskID, boolean showLoadingProgress) {
            this.taskID = taskID;
            this.showLoadingProgress = showLoadingProgress;
            this.mode = mode;
        }

        @Override
        public void onFailure(String taskID, String errorMsg) {
            if (this.taskID.equals(taskID)) {
                Snackbar.make(bookmark_feeds, errorMsg, Snackbar.LENGTH_LONG).show();
            }
        }

        @Override
        public void onPreStart(String taskID) {
            if (this.taskID.equals(taskID) && showLoadingProgress) {
                showProgressDialog();
            }
        }

        @Override
        public void onPostComplete(String taskID) {
            if (this.taskID.equals(taskID) && showLoadingProgress) {
                hideProgressDialog();
                if(mode != null) {
                    mode.finish();
                }
            }
        }

        @Override
        public void onSuccess(String taskID, Object data) {
            if (this.taskID.equals(taskID) && data != null) {
                BookmarkDeleteResult result = (BookmarkDeleteResult) data;
                List<Bookmark> success = result.getSuccess();
                if(!success.isEmpty()) {
                    for(Bookmark s : success) {
                        adapter.getFeeds().remove(s);
                    }
                    adapter.notifyDataSetChanged();
                }
                List<Bookmark> failure = result.getFailure();
                if(!failure.isEmpty()) {
                    for(Bookmark f : failure) {
                        f.setUnderDeleteOperation(false);
                    }
                }
            }
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
