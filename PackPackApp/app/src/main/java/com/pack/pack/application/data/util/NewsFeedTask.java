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
 *
 * Created by Saurav on 13-08-2017.
 *
 */
public class NewsFeedTask extends FeedsLoadTask {

    public NewsFeedTask(Context context, boolean loadOfflineData) {
        super(context, JRssFeedType.NEWS, loadOfflineData);
    }

    public NewsFeedTask(Context context, boolean loadOfflineData, long timestamp) {
        super(context, JRssFeedType.NEWS, loadOfflineData, timestamp);
    }

    @Override
    protected COMMAND command() {
        return COMMAND.GET_ALL_NEWS_FEEDS;
    }
}
