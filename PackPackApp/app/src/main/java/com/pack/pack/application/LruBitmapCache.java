package com.pack.pack.application;

import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.pack.pack.application.cz.fhucho.android.util.SimpleDiskCache;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.util.Log;

import java.io.IOException;

public class LruBitmapCache extends LruCache<String, Bitmap> implements
        ImageCache {

    //private SimpleDiskCache diskCache;

    private static final String LOG_TAG = "LruBitmapCache";

    public static int getDefaultLruCacheSize() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;

        return cacheSize;
    }

    public LruBitmapCache() {
        this(getDefaultLruCacheSize());
    }

    public LruBitmapCache(int sizeInKiloBytes) {
        super(sizeInKiloBytes);
        //diskCache = SimpleDiskCache.getInstance()
    }

    @Override
    protected int sizeOf(String key, Bitmap value) {
        return value.getRowBytes() * value.getHeight() / 1024;
    }

    @Override
    public Bitmap getBitmap(String url) {
        Bitmap bitmap = null;
        try {
            bitmap = get(url);
            if(bitmap == null) {
                SimpleDiskCache.BitmapEntry bitmapEntry = SimpleDiskCache.getInstance().getBitmap(url);
                if(bitmapEntry == null) {
                    return bitmap;
                }
                bitmap = bitmapEntry.getBitmap();
                put(url, bitmap);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(LOG_TAG, e.getMessage(), e);
        }
        return bitmap;
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        if(url == null || bitmap == null) {
            return;
        }
        try {
            SimpleDiskCache.getInstance().put(url, bitmap);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(LOG_TAG, e.getMessage(), e);
        }
        put(url, bitmap);
    }
}
