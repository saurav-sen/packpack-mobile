package com.pack.pack.application.data.cache;

import com.squill.feed.web.model.JRssFeed;
import com.pack.pack.model.web.Pagination;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Saurav on 18-06-2017.
 */
public class RssFeedCache {

    public static final RssFeedCache INSTANCE = new RssFeedCache();

    private Map<String, Pagination<JRssFeed>> cache = new HashMap<String, Pagination<JRssFeed>>();

    private RssFeedCache() {

    }

    public Pagination<JRssFeed> readFromCache(String pageLink) {
        return cache.get(pageLink);
    }

    public void storeInCache(String pageLink, Pagination<JRssFeed> page) {
        // Not storing for now
        //cache.put(pageLink, page);
    }

    public void evitAll() {
        cache.clear();
    }
}
