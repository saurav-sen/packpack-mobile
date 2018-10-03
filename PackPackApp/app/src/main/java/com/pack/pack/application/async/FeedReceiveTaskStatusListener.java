package com.pack.pack.application.async;

import android.os.Handler;
import android.util.Log;

import com.pack.pack.application.AppController;
import com.pack.pack.application.FeedReceiveState;
import com.pack.pack.application.data.util.IAsyncTaskStatusListener;
import com.pack.pack.model.web.Pagination;
import com.squill.feed.web.model.JRssFeed;
import com.squill.feed.web.model.JRssFeedType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FeedReceiveTaskStatusListener implements IAsyncTaskStatusListener {

    private Set<String> taskIDs = new HashSet<>();

    private Map<String, JRssFeedType> taskIdVsFeedType = new HashMap<>();

    private FeedReceiveCallback callback;

    public FeedReceiveTaskStatusListener(FeedReceiveCallback callback) {
        this.callback = callback;
    }

    public FeedReceiveTaskStatusListener addTaskID(String taskID, JRssFeedType feedType) {
        this.taskIDs.add(taskID);
        this.taskIdVsFeedType.put(taskID, feedType);
        return this;
    }

    @Override
    public void onPreStart(String taskID) {
        // Do nothing
    }

    @Override
    public void onFailure(String taskID, String errorMsg) {
        // Do nothing
        /*if(this.taskIDs.remove(taskID)) {
            JRssFeedType feedType = this.taskIdVsFeedType.get(taskID);
            if(feedType != null) {
                AppController.getInstance().getFeedReceiveState().setLastUpdateTimestamp(feedType, FeedReceiveState.DEFAULT_MIN_TIMESTAMP);
            }
        }*/
    }

    @Override
    public void onSuccess(String taskID, Object data) {
        if(this.taskIDs.remove(taskID) && data != null) {
            Pagination<JRssFeed> page = (Pagination<JRssFeed>) data;
            int nextPageNo = page.getNextPageNo();
            List<JRssFeed> list = page.getResult();
            if(list == null || list.isEmpty())
                return;
            JRssFeedType feedType = this.taskIdVsFeedType.get(taskID);
            if(feedType != null) {
                AppController.getInstance().getFeedReceiveState().set(feedType, page);
                Log.d("FeedReceive", "Resetting last updated data to avoid duplicate calls");
            }
        }
    }

    @Override
    public void onPostComplete(String taskID) {
        if(this.taskIDs.isEmpty()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                }
            }, 2000);
            callback.handleTaskComplete();
        }
    }
}