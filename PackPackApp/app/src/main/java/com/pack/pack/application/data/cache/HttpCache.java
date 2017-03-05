package com.pack.pack.application.data.cache;

import com.pack.pack.client.internal.response.cache.HttpResponseCacheDelegate;

import org.apache.http.client.cache.HttpCacheEntry;
import org.apache.http.client.cache.HttpCacheUpdateCallback;
import org.apache.http.client.cache.HttpCacheUpdateException;

import java.io.IOException;

/**
 * Created by Saurav on 05-03-2017.
 */
public class HttpCache implements HttpResponseCacheDelegate {

    @Override
    public void putEntry(String s, HttpCacheEntry httpCacheEntry) throws IOException {

    }

    @Override
    public HttpCacheEntry getEntry(String s) throws IOException {
        return null;
    }

    @Override
    public void removeEntry(String s) throws IOException {

    }

    @Override
    public void updateEntry(String s, HttpCacheUpdateCallback httpCacheUpdateCallback) throws IOException, HttpCacheUpdateException {

    }
}
