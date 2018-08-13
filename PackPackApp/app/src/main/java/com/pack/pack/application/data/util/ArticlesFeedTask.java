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
public class ArticlesFeedTask extends FeedsLoadTask {

    public ArticlesFeedTask(Context context, boolean loadOfflineData) {
        super(context, JRssFeedType.ARTICLE, loadOfflineData);
    }

    public ArticlesFeedTask(Context context, boolean loadOfflineData, long timestamp) {
        super(context, JRssFeedType.ARTICLE, loadOfflineData, timestamp);
    }

    @Override
    protected COMMAND command() {
        return COMMAND.GET_ALL_ARTICLES_FEEDS;
    }
}

