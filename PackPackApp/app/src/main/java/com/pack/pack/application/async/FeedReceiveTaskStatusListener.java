package com.pack.pack.application.async;

import android.os.Handler;

import com.pack.pack.application.AppController;
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
        if(this.taskIDs.remove(taskID)) {
            JRssFeedType feedType = this.taskIdVsFeedType.get(taskID);
            if(feedType != null) {
                switch (feedType) {
                    case NEWS:
                        AppController.getInstance().getFeedReceiveState().setIsFirstPageNewsReceived(false);
                        AppController.getInstance().getFeedReceiveState().setNewsNextPageNo(0);
                        break;
                    case NEWS_SPORTS:
                        AppController.getInstance().getFeedReceiveState().setIsFirstPageSportsNewsReceived(false);
                        AppController.getInstance().getFeedReceiveState().setSportsNewsNextPageNo(0);
                        break;
                    case NEWS_SCIENCE_TECHNOLOGY:
                        AppController.getInstance().getFeedReceiveState().setIsFirstPageScienceNewsReceived(false);
                        AppController.getInstance().getFeedReceiveState().setScienceNewsNextPageNo(0);
                        break;
                    case ARTICLE:
                        AppController.getInstance().getFeedReceiveState().setIsFirstPageArticlesReceived(false);
                        AppController.getInstance().getFeedReceiveState().setArticlesNextPageNo(0);
                        break;
                }
            }
        }
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
                switch (feedType) {
                    case NEWS:
                        AppController.getInstance().getFeedReceiveState().setIsFirstPageNewsReceived(true);
                        AppController.getInstance().getFeedReceiveState().setNewsNextPageNo(nextPageNo);
                        break;
                    case NEWS_SPORTS:
                        AppController.getInstance().getFeedReceiveState().setIsFirstPageSportsNewsReceived(true);
                        AppController.getInstance().getFeedReceiveState().setSportsNewsNextPageNo(nextPageNo);
                        break;
                    case NEWS_SCIENCE_TECHNOLOGY:
                        AppController.getInstance().getFeedReceiveState().setIsFirstPageScienceNewsReceived(true);
                        AppController.getInstance().getFeedReceiveState().setScienceNewsNextPageNo(nextPageNo);
                        break;
                    case ARTICLE:
                        AppController.getInstance().getFeedReceiveState().setIsFirstPageArticlesReceived(true);
                        AppController.getInstance().getFeedReceiveState().setArticlesNextPageNo(nextPageNo);
                        break;
                }
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