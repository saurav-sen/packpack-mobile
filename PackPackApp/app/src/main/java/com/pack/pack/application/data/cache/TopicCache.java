package com.pack.pack.application.data.cache;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.util.Log;

import com.pack.pack.application.cz.fhucho.android.util.SimpleDiskCache;
import com.pack.pack.application.cz.fhucho.android.util.SimpleDiskCacheInitializer;
import com.pack.pack.common.util.JSONUtil;
import com.pack.pack.model.web.JPack;
import com.pack.pack.model.web.JTopic;
import com.pack.pack.services.exception.PackPackException;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Saurav on 04-03-2017.
 */
public class TopicCache {

    private static TopicCache instance;

    private SimpleDiskCache diskCache;

    private static final Object lock = new Object();

    private static final String LOG_TAG = "TopicCache";

    private TopicCache() {

    }

    public static final TopicCache open(Context context) {
        try {
            synchronized (lock) {
                if (instance == null) {
                    instance = new TopicCache();
                }
                if (instance.diskCache == null) {
                    SimpleDiskCacheInitializer.prepare(context);
                    instance.diskCache = SimpleDiskCache.getInstance();
                }
            }
            return instance;
        } catch (Exception e) {
            Log.d(LOG_TAG, e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }

    public List<JTopic> getAllTopics(String category) {
        if(diskCache != null) {
            return Collections.emptyList();
        }

        try {
            String json = null;
            SimpleDiskCache.StringEntry stringEntry = diskCache.getString(category);
            if(stringEntry != null) {
                json = stringEntry.getString();
            }

            if(json != null) {
                JTopics c = JSONUtil.deserialize(json, JTopics.class, true);
                if(c != null) {
                    return c.getTopics();
                }
            }
        } catch (IOException e) {
            Log.d(LOG_TAG, e.getMessage(), e);
        } catch (PackPackException e) {
            Log.d(LOG_TAG, e.getMessage(), e);
        }
        return new LinkedList<JTopic>();
    }

    public void addTopics(String category, List<JTopic> topics) {
        if(topics == null || topics.isEmpty()) {
            return;
        }
        if(diskCache == null) {
            return;
        }

        try {
            List<JTopic> allTopics = getAllTopics(category);
            allTopics.addAll(topics);
            JTopics c = new JTopics();
            c.setTopics(allTopics);

            String json = JSONUtil.serialize(c);
            diskCache.put(category, json);
        } catch (IOException e) {
            Log.d(LOG_TAG, e.getMessage(), e);
        } catch (PackPackException e) {
            Log.d(LOG_TAG, e.getMessage(), e);
        }
    }

    private class JTopics {

        private List<JTopic> topics;

        public List<JTopic> getTopics() {
            if(topics == null) {
                topics = new LinkedList<JTopic>();
            }
            return topics;
        }

        public void setTopics(List<JTopic> topics) {
            this.topics = topics;
        }
    }
}
