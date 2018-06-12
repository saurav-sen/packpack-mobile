package com.pack.pack.application.data.util;

import android.content.Context;
import android.util.Log;

import com.pack.pack.application.AppController;
import com.pack.pack.application.data.cache.RssFeedCache;
import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;
import com.squill.feed.web.model.JRssFeed;
import com.pack.pack.model.web.Pagination;
import com.squill.feed.web.model.JRssFeedType;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Saurav on 25-03-2018.
 */
public class SportsFeedTask extends FeedsLoadTask {

    public SportsFeedTask(Context context, boolean loadOfflineData) {
        super(context, JRssFeedType.NEWS_SPORTS, loadOfflineData);
    }

    @Override
    protected COMMAND command() {
        return COMMAND.GET_ALL_SPORTS_NEWS_FEEDS;
    }

    @Override
    protected Map<String, Object> doPrepareApiParams(String inputObject) {
        Map<String, Object> apiParams = new HashMap<String, Object>();
        String pageLink = inputObject;
        String userId = AppController.getInstance().getUserId();
        apiParams.put(APIConstants.User.ID, userId);
        apiParams.put(APIConstants.PageInfo.PAGE_LINK, pageLink);
        return apiParams;
    }
}

