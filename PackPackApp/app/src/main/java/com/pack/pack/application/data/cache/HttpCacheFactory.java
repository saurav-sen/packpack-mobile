package com.pack.pack.application.data.cache;

import com.pack.pack.client.internal.response.cache.HttpResponseCacheDelegate;
import com.pack.pack.client.internal.response.cache.HttpResponseCacheDelegateFactory;

/**
 * Created by Saurav on 05-03-2017.
 */
public class HttpCacheFactory implements HttpResponseCacheDelegateFactory {

    private static final HttpResponseCacheDelegate delegate = new HttpCache();

    @Override
    public HttpResponseCacheDelegate getDelegate() {
        return delegate;
    }
}
