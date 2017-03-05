package com.pack.pack.application.data.cache;

import android.content.Context;

import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.internal.response.cache.HttpResponseCacheDelegate;
import com.pack.pack.client.internal.response.cache.HttpResponseCacheDelegateFactory;

/**
 * Created by Saurav on 05-03-2017.
 */
public class HttpCacheFactory implements HttpResponseCacheDelegateFactory {

    private static HttpResponseCacheDelegate delegate;

    public static void prepare(Context context) {
        delegate = HttpCache.open(context);
        System.setProperty(APIConstants.CACHE_STORAGE, HttpCacheFactory.class.getName());
    }

    @Override
    public HttpResponseCacheDelegate getDelegate() {
        return delegate;
    }
}
