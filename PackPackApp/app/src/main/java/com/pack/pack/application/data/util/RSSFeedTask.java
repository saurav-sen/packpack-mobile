package com.pack.pack.application.data.util;

import android.content.Context;
import android.util.Log;

import com.pack.pack.application.AppController;
import com.pack.pack.application.data.cache.RssFeedCache;
import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.model.web.JRssFeed;
import com.pack.pack.model.web.Pagination;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Saurav
 *
 */
public class RSSFeedTask extends AbstractNetworkTask<String, Integer, Pagination<JRssFeed>> {

    private String pageLink;

    private String errorMsg;

    private static final String LOG_TAG = "RSSFeedTask";

    public RSSFeedTask(Context context) {
        super(false, false, context, false);
    }

    @Override
    protected String getFailureMessage() {
        return errorMsg;
    }

    @Override
    protected Pagination<JRssFeed> executeApi(API api) throws Exception {
        if(pageLink == null) {
            pageLink = "FIRST_PAGE";
        }
        fireOnPreStart();
        Pagination<JRssFeed> page = RssFeedCache.INSTANCE.readFromCache(pageLink);
        if(page != null && page.getResult() != null && !page.getResult().isEmpty()) {
            return page;
        }
        try {
            page = (Pagination<JRssFeed>)api.execute();
            RssFeedCache.INSTANCE.storeInCache(pageLink, page);
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

    @Override
    protected COMMAND command() {
        return COMMAND.GET_ALL_PROMOTIONAL_FEEDS;
    }

    @Override
    protected Map<String, Object> prepareApiParams(String inputObject) {
        Map<String, Object> apiParams = new HashMap<String, Object>();
        pageLink = inputObject;
        String userId = AppController.getInstance().getUserId();
        apiParams.put(APIConstants.User.ID, userId);
        apiParams.put(APIConstants.PageInfo.PAGE_LINK, pageLink);
        return apiParams;
    }
}