package com.pack.pack.application.data.util;

import android.content.Context;
import android.util.Log;

import com.pack.pack.application.AppController;
import com.pack.pack.application.data.cache.RssFeedCache;
import com.pack.pack.application.service.NetworkUtil;
import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.model.web.Pagination;
import com.squill.feed.web.model.JRssFeed;
import com.squill.feed.web.model.JRssFeedType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Saurav on 13-06-2018.
 */
public abstract class FeedsLoadTask extends AbstractNetworkTask<String, Integer, Pagination<JRssFeed>> {

    private String errorMsg;

    private JRssFeedType feedType;

    private int pageNo;

    private static final String LOG_TAG = "FeedsLoadTask";

    public FeedsLoadTask(Context context, JRssFeedType feedType, int pageNo) {
        super(false, false, context, false);
        this.feedType = feedType;
        this.pageNo = pageNo;
    }

    @Override
    protected String getFailureMessage() {
        return errorMsg;
    }

    @Override
    protected Pagination<JRssFeed> executeApi(API api) throws Exception {
        RssFeedCache cache = new RssFeedCache(getContext(), feedType);

        fireOnPreStart();
        Pagination<JRssFeed> page = null;
        try {
            page = doExecute(api, cache);
            AppController.getInstance().setLoadStatus(feedType, true);
        } catch (Exception e) {
            Log.d(LOG_TAG, e.getMessage());
            errorMsg = "Oops! Something went wrong";
            fireOnFailure(errorMsg);
        }
        return page;
    }

    @Override
    protected String getContainerIdForObjectStore() {
        return null;
    }

    private Pagination<JRssFeed> doExecute(API api, RssFeedCache cache) throws Exception {
        Pagination<JRssFeed> page = new Pagination<JRssFeed>();
        boolean readOfflineData = true;
        if(NetworkUtil.checkConnectivity(getContext())) {
            readOfflineData = false;
            if(pageNo == 0 && AppController.getInstance().getFeedReceiveState().isFirstPageReceived(feedType)) {
                readOfflineData = true;
                pageNo = AppController.getInstance().getFeedReceiveState().getNextPageNo(feedType);
            } else {
                page = eliminateDuplicatesIfAny((Pagination<JRssFeed>)api.execute());
                if(page != null && !page.getResult().isEmpty()) {
                    cache.storeOfflineData(page.getResult(), pageNo);
                }
                if(pageNo == 0 && (page == null || page.getResult().isEmpty()))
                    readOfflineData = true;
            }
        }
        if(readOfflineData) {
            List<JRssFeed> list = cache.readOfflineData();
            page.getResult().addAll(list);
            page.setNextPageNo(pageNo);
        }
        return page;
    }

    private Pagination<JRssFeed> eliminateDuplicatesIfAny(Pagination<JRssFeed> page) {
        List<JRssFeed> list = page.getResult();
        Set<JRssFeed> set = new HashSet<>();
        set.addAll(list);
        list = new ArrayList<>(set);
        page.setResult(list);
        return page;
    }

    @Override
    protected final Map<String, Object> prepareApiParams(String inputObject) {
        Map<String, Object> apiParams = new HashMap<String, Object>();
        String userId = AppController.getInstance().getUserId();
        apiParams.put(APIConstants.User.ID, userId);
        int pNo = pageNo;
        if(pNo == 0 && AppController.getInstance().getFeedReceiveState().isFirstPageReceived(feedType)) {
            pNo = AppController.getInstance().getFeedReceiveState().getNextPageNo(feedType);
        }
        apiParams.put(APIConstants.PageInfo.PAGE_NO, pNo);
        return apiParams;
    }
}
