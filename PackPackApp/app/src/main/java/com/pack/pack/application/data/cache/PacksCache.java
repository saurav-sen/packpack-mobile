package com.pack.pack.application.data.cache;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.util.Log;

import com.pack.pack.application.cz.fhucho.android.util.SimpleDiskCache;
import com.pack.pack.common.util.JSONUtil;
import com.pack.pack.model.web.JPack;
import com.pack.pack.model.web.JPackAttachments;
import com.pack.pack.services.exception.PackPackException;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Saurav on 04-03-2017.
 */
public class PacksCache {

    private static PacksCache instance;

    private SimpleDiskCache diskCache;

    private static Object lock = new Object();

    private static final long MAX_SIZE = 1024 * 1024;

    private static final String LOG_TAG = "PacksCache";

    private PacksCache() {

    }

    public static final PacksCache open(Context context) {
        try {
            synchronized (lock) {
                if (instance == null) {
                    instance = new PacksCache();
                }
                if (instance.diskCache == null) {
                    instance.load(context);
                }
            }
            return instance;
        } catch (Exception e) {
            Log.d(LOG_TAG, e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }

    private void load(Context context) throws Exception {
        PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        int appVersion = pInfo.versionCode;
        File cacheDir = context.getCacheDir();
        diskCache = SimpleDiskCache.open(cacheDir, appVersion, MAX_SIZE);
    }

    public List<JPack> getAllPacks(String topicId) {
        if(diskCache != null) {
            return Collections.emptyList();
        }

        try {
            String json = null;
            SimpleDiskCache.StringEntry stringEntry = diskCache.getString(topicId);
            if(stringEntry != null) {
                json = stringEntry.getString();
            }

            if(json != null) {
                JPacks c = JSONUtil.deserialize(json, JPacks.class, true);
                if(c != null) {
                    return c.getPacks();
                }
            }
        } catch (IOException e) {
            Log.d(LOG_TAG, e.getMessage(), e);
        } catch (PackPackException e) {
            Log.d(LOG_TAG, e.getMessage(), e);
        }
        return new LinkedList<JPack>();
    }

    public void addPacks(String topicId, List<JPack> packs) {
        if(packs == null || packs.isEmpty()) {
            return;
        }
        if(diskCache == null) {
            return;
        }

        try {
            List<JPack> allPacks = getAllPacks(topicId);
            allPacks.addAll(packs);
            JPacks c = new JPacks();
            c.setPacks(allPacks);

            String json = JSONUtil.serialize(c);
            diskCache.put(topicId, json);
        } catch (IOException e) {
            Log.d(LOG_TAG, e.getMessage(), e);
        } catch (PackPackException e) {
            Log.d(LOG_TAG, e.getMessage(), e);
        }
    }

    private class JPacks {

        private List<JPack> packs;

        public List<JPack> getPacks() {
            if(packs == null) {
                packs = new LinkedList<JPack>();
            }
            return packs;
        }

        public void setPacks(List<JPack> packs) {
            this.packs = packs;
        }
    }
}
