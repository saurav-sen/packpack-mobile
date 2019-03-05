package in.squill.squilloffice.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.pack.pack.application.AppController;
import com.pack.pack.application.FeedReceiveState;
import com.pack.pack.application.R;
import com.pack.pack.application.data.util.DateTimeUtil;
import com.pack.pack.application.data.util.FeedsLoadTask;
import com.pack.pack.application.data.util.IAsyncTaskStatusListener;
import com.pack.pack.application.data.util.NewsFeedTask;
import com.pack.pack.model.web.Pagination;
import com.squill.feed.web.model.JRssFeed;
import com.squill.feed.web.model.JRssFeedType;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Saurav on 26-09-2018.
 */
public abstract class BaseFragment extends Fragment {

    private BaseAdapter adapter;

    private ListView listView;

    private int nextPageNo = 0;

    private ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getViewLayoutId(), null);

        listView = (ListView) view.findViewById(getListViewId());
        adapter = initFragmentAdapter();
        listView.setAdapter(adapter);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                int count = listView.getCount();
                if (scrollState == SCROLL_STATE_IDLE) {
                    int c = (int) (count * 0.7f);
                    if (listView.getLastVisiblePosition() >= c) {
                        loadNewFeeds(nextPageNo, false);
                    }
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });

        nextPageNo = 0;
        loadNewFeeds(nextPageNo, true);
        return view;
    }

    protected abstract int getListViewId();

    protected abstract int getViewLayoutId();

    protected abstract BaseAdapter initFragmentAdapter();

    protected abstract FeedsLoadTask initNewTask();

    protected abstract JRssFeedType getFeedType();

    public void onNetworkStateChange(boolean isPrevConnected, boolean isNowConnected) {
        if(!isPrevConnected && isNowConnected) {
            reload();
        }
    }

    public void reload() {
        nextPageNo = 0;
        adapter.clearState();
        clearState();
        loadNewFeeds(nextPageNo, true);
    }

    protected void clearState() {
        // DO nothing
    }

    /*@Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser) {
            loadNewFeeds(nextPageNo, true);
        }
    }*/

    protected void showProgressDialog() {
        if(progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading...");
            progressDialog.show();
        }
    }

    protected void hideProgressDialog() {
        if(progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    private void loadNewFeeds(int pageNo, boolean showLoadingProgress) {
        if(pageNo < 0)
            return;
        FeedsLoadTask task = initNewTask();
        FeedLoadTaskStatusListener listener = new FeedLoadTaskStatusListener(task.getTaskID(), pageNo, showLoadingProgress);
        task.addListener(listener);
        task.execute(String.valueOf(pageNo));
    }

    private class FeedLoadTaskStatusListener implements IAsyncTaskStatusListener {

        private String taskID;

        private boolean showLoadingProgress;

        private int pNo;

        FeedLoadTaskStatusListener(String taskID, int pNo, boolean showLoadingProgress) {
            this.taskID = taskID;
            this.pNo = pNo;
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
                Snackbar.make(listView, errorMsg, Snackbar.LENGTH_LONG).show();
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
                if(showLoadingProgress) {
                    adapter.clearState();
                }
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
