package com.pack.pack.application.data.cache;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.pack.pack.application.cz.fhucho.android.util.SimpleDiskCache;
import com.pack.pack.application.cz.fhucho.android.util.SimpleDiskCacheInitializer;
import com.pack.pack.client.internal.response.cache.HttpCacheEntry;
import com.pack.pack.client.internal.response.cache.HttpCacheUpdateCallback;
import com.pack.pack.client.internal.response.cache.HttpCacheUpdateException;
import com.pack.pack.client.internal.response.cache.HttpResponseCacheDelegate;

import java.io.File;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Saurav on 05-03-2017.
 */
public class HttpCache implements HttpResponseCacheDelegate {

    private static HttpCache instance;

    private static final Object lock = new Object();

    private static final String LOG_TAG = "HttpCache";

   // private SimpleDiskCache diskCache;

    private Map<String, SoftReference<HttpCacheEntry>> inMemoryCache
            = new HashMap<String, SoftReference<HttpCacheEntry>>();

    private HttpCache() {
    }

    static final HttpResponseCacheDelegate open(Context context) {
        try {
            if(instance == null) {
                synchronized (lock) {
                    instance = new HttpCache();
                    /*if (instance.diskCache == null) {
                        SimpleDiskCacheInitializer.prepare(context);
                        instance.diskCache = SimpleDiskCache.getInstance();
                    }*/
                }
            }
            return instance;
        } catch (Exception e) {
            Log.d(LOG_TAG, e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void put(String s, HttpCacheEntry httpCacheEntry) throws IOException {
        inMemoryCache.put(s, new SoftReference<HttpCacheEntry>(httpCacheEntry));
    }

    @Override
    public HttpCacheEntry get(String s) throws IOException {
        SoftReference<HttpCacheEntry> ref = inMemoryCache.get(s);
        if(ref == null) {
            return null;
        }
        return ref.get();
    }

    @Override
    public void remove(String s) throws IOException {
        inMemoryCache.remove(s);
    }

    @Override
    public void update(String s, HttpCacheUpdateCallback httpCacheUpdateCallback) throws IOException, HttpCacheUpdateException {
        HttpCacheEntry entry = get(s);
        if(httpCacheUpdateCallback != null && entry != null) {
            entry = httpCacheUpdateCallback.update(entry);
            if(entry == null) {
                inMemoryCache.remove(s);
            } else {
                put(s, entry);
            }
        }
    }
}