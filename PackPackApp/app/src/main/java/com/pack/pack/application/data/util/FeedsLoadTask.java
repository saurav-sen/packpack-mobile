package com.pack.pack.application.data.util;

import android.content.Context;
import android.util.Log;

import com.pack.pack.application.AppController;
import com.pack.pack.application.Constants;
import com.pack.pack.application.Mode;
import com.pack.pack.application.data.cache.RssFeedCache;
import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.PageUtil;
import com.pack.pack.model.web.Pagination;
import com.squill.feed.web.model.JRssFeed;
import com.squill.feed.web.model.JRssFeedType;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Saurav on 13-06-2018.
 */
public abstract class FeedsLoadTask extends AbstractNetworkTask<String, Integer, Pagination<JRssFeed>> {

    private String pageLink;

    private String errorMsg;

    private boolean loadOfflineData;

    private JRssFeedType feedType;

    private static final String LOG_TAG = "FeedsLoadTask";

    public FeedsLoadTask(Context context, JRssFeedType feedType, boolean loadOfflineData) {
        super(false, false, context, false);
        this.feedType = feedType;
        this.loadOfflineData = loadOfflineData;
    }

    @Override
    protected String getFailureMessage() {
        return errorMsg;
    }

    @Override
    protected Pagination<JRssFeed> executeApi(API api) throws Exception {
        RssFeedCache cache = new RssFeedCache(getContext(), feedType);
        /*if(pageLink == null) {
            pageLink = cache.readLastPageLink();
            if(pageLink == null) {
                pageLink = Constants.FIRST_PAGE;
            }
            if(Constants.END_OF_PAGE.equals(pageLink) && loadOfflineData) {
                pageLink = Constants.END_OF_PAGE;
            }
        }
        boolean loadStatus = AppController.getInstance().getLoadStatus(feedType);
        if(!loadStatus) {
            long lastReceiveTimestamp = cache.readLastReceiveTimestamp();
            pageLink = PageUtil.buildNextPageLink(lastReceiveTimestamp);
            AppController.getInstance().setLoadStatus(feedType, true);
        }*/

        long lastReceiveTimestamp = cache.readLastReceiveTimestamp();
        pageLink = PageUtil.buildNextPageLink(lastReceiveTimestamp);
        AppController.getInstance().setLoadStatus(feedType, true);

        fireOnPreStart();
        Pagination<JRssFeed> page = null;
        try {
            page = doExecute(api, cache, lastReceiveTimestamp);
        } catch (Exception e) {
            Log.d(LOG_TAG, e.getMessage());
            errorMsg = "Oops! Something went wrong";
            fireOnFailure(errorMsg);
        }
        /*Pagination<JRssFeed> page = null;
        try {
            page = (Pagination<JRssFeed>)api.execute();
            if(loadOfflineData) {
                List<JRssFeed> list = cache.readOfflineData();
                page.getResult().addAll(list);
                List<JRssFeed> list2 = new ArrayList<>();
                list2.addAll(new LinkedHashSet<>(page.getResult()));
                page.getResult().clear();
                page.getResult().addAll(list2);
            }
            cache.storeOfflineData(page);
        } catch (Exception e) {
            Log.d(LOG_TAG, e.getMessage());
            errorMsg = "Oops! Something went wrong";
            fireOnFailure(errorMsg);
        }*/
        return page;
    }

    @Override
    protected String getContainerIdForObjectStore() {
        return null;
    }

    private Pagination<JRssFeed> doExecute(API api, RssFeedCache cache, long lastReceiveTimestamp) throws Exception {
        Pagination<JRssFeed> page = new Pagination<JRssFeed>();
        if(AppController.getInstance().getExecutionMode() == Mode.ONLINE) {
            page = (Pagination<JRssFeed>)api.execute();
            if(loadOfflineData) {
                List<JRssFeed> list = cache.readOfflineData();
                page.getResult().addAll(list);
                List<JRssFeed> list2 = new ArrayList<>();
                list2.addAll(new LinkedHashSet<>(page.getResult()));
                page.getResult().clear();
                page.getResult().addAll(list2);
            }
            cache.storeOfflineData(page);
        } else {
            List<JRssFeed> list = cache.readOfflineData();
            page.getResult().addAll(list);
            page.setTimestamp(lastReceiveTimestamp);
            page.setPreviousLink(Constants.END_OF_PAGE + "_1");
            page.setNextLink(pageLink);
        }
        return page;
    }

    @Override
    protected final Map<String, Object> prepareApiParams(String inputObject) {
        return doPrepareApiParams(pageLink);
    }

    /*@Override
    protected final Map<String, Object> prepareApiParams(String inputObject) {
        Map<String, Object> apiParams = doPrepareApiParams(inputObject);
        pageLink = (String) apiParams.get(APIConstants.PageInfo.PAGE_LINK);
        return apiParams;
    }*/

    protected abstract Map<String, Object> doPrepareApiParams(String inputObject);
}
