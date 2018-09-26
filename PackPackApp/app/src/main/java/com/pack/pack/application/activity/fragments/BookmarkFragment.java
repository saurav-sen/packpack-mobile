package com.pack.pack.application.activity.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.pack.pack.application.R;
import com.pack.pack.application.adapters.BookmarkFragmentAdapter;
import com.pack.pack.application.data.util.IAsyncTaskStatusListener;
import com.pack.pack.application.data.util.LoadBookmarkTask;
import com.pack.pack.application.db.Bookmark;
import com.pack.pack.application.db.PagedObject;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Saurav on 26-09-2018.
 */
public class BookmarkFragment extends Fragment {

    private ListView bookmark_feeds;

    private ProgressDialog progressDialog;

    private BookmarkFragmentAdapter adapter;

    private long currentPageRef = Long.MAX_VALUE;

    private static long END_OF_PAGE = Long.MIN_VALUE;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_bookmar, null);

        bookmark_feeds = (ListView) view.findViewById(R.id.bookmark_feeds);
        boolean isLoadData = (adapter == null);
        adapter = initBookmarkAdapter();
        bookmark_feeds.setAdapter(adapter);

        if(isLoadData) {
            loadBookmarks(0, true);
        }

        return view;
    }

    private BookmarkFragmentAdapter initBookmarkAdapter() {
        if(adapter == null) {
            List<Bookmark> feeds = new LinkedList<Bookmark>();
            adapter = new BookmarkFragmentAdapter(getActivity(), feeds);
        }
        return adapter;
    }

   /* @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser) {
            loadBookmarks(0, true);
        }
    }*/

    private void loadBookmarks(long currentPageRef, boolean showLoadingProgress) {
        if(END_OF_PAGE == currentPageRef) {
            return;
        }
        LoadBookmarkTask task = new LoadBookmarkTask(getActivity(), showLoadingProgress);
        BookmarkLoadTaskListener listener = new BookmarkLoadTaskListener(task.getTaskID(), showLoadingProgress);
        task.addListener(listener);
        task.execute(currentPageRef);
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
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
