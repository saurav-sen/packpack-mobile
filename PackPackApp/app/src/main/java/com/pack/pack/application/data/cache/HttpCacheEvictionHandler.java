package com.pack.pack.application.data.cache;

import android.content.Context;

import com.pack.pack.client.api.COMMAND;

/**
 * Created by Saurav on 29-03-2017.
 */
public class HttpCacheEvictionHandler {

    public static final HttpCacheEvictionHandler INSTANCE = new HttpCacheEvictionHandler();

    private HttpCacheEvictionHandler() {

    }

    public void evict(Context context, COMMAND command, Object response, boolean flushOld) {
        HttpCache cache = (HttpCache) HttpCache.open(context);
        if(cache != null && flushOld) {
            cache.flushAll();
        }
    }
}
