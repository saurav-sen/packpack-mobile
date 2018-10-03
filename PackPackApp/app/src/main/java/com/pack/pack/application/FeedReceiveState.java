package com.pack.pack.application;

import com.pack.pack.model.web.Pagination;
import com.squill.feed.web.model.JRssFeed;
import com.squill.feed.web.model.JRssFeedType;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by Saurav on 12-09-2018.
 */
public class FeedReceiveState {

    private Map<JRssFeedType, Pagination<JRssFeed>> map = new HashMap<>();

    private Map<JRssFeedType, Long> timestampMap = new HashMap<>();

    FeedReceiveState() {
    }

    public void set(JRssFeedType feedType, Pagination<JRssFeed> page) {
        if(feedType == null)
            return;
        map.put(feedType, page);
        timestampMap.put(feedType, System.currentTimeMillis());
    }

    public Pagination<JRssFeed> get(JRssFeedType feedType) {
        if(feedType == null)
            return null;
        Pagination<JRssFeed> page = map.get(feedType);
        if(page != null && !page.getResult().isEmpty()) {
            Long obj = timestampMap.get(feedType);
            if(obj == null) { // There is something wrong while storing. Reject it.
                clear(feedType);
                return null;
            }
            long t0 = obj;
            long t1 = System.currentTimeMillis();
            int diff = (int)(((t1 - t0)/1000)/60); // diff in minutes
            if(diff >= 20) { // If diff is greater than 20 minutes, then just reject it (as it may be obsolete data in the store)
                clear(feedType);
                page = null;
            }
        }
        return page;
    }

    public void clear(JRssFeedType feedType) {
        if(feedType == null)
            return;
        map.remove(feedType);
        timestampMap.remove(feedType);
    }
}
