package com.pack.pack.application.cz.fhucho.android.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * Created by Saurav on 05-03-2017.
 */
public class SimpleDiskCacheInitializer {

    private static final String LOG_TAG = "DiskCacheInitializer";

    private static final long MAX_SIZE = 1024 * 1024 * 30;

    private static boolean isPrepared = false;

    private static final Object lock = new Object();

    private SimpleDiskCacheInitializer(){

    }

    public static void prepare(Context context) {
        synchronized (lock) {
           if(!isPrepared) {
               try {
                   PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                   int appVersion = pInfo.versionCode;
                   File cacheDir = context.getCacheDir();
                   SimpleDiskCache.open(cacheDir, appVersion, MAX_SIZE);
                   isPrepared = true;
               } catch (PackageManager.NameNotFoundException e) {
                   Log.d(LOG_TAG, e.getMessage(), e);
                   throw new RuntimeException(e.getMessage());
               } catch (IOException e) {
                   Log.d(LOG_TAG, e.getMessage(), e);
                   throw new RuntimeException(e.getMessage());
               }
           }
        }
    }
}
