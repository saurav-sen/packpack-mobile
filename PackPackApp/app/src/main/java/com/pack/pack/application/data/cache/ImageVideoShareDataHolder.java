package com.pack.pack.application.data.cache;

import com.pack.pack.model.web.JTopic;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by Saurav on 31-08-2017.
 */
public class ImageVideoShareDataHolder {

    private Map<String, JTopic> topicsMap = new HashMap<String, JTopic>();

    private static ImageVideoShareDataHolder instance = null;

    private static final Lock lock = new ReentrantReadWriteLock().readLock();

    private ImageVideoShareDataHolder() {
    }

    public static ImageVideoShareDataHolder getInstance() {
        lock.lock();
        try {
            if(instance == null) {
                instance = new ImageVideoShareDataHolder();
            }
        } finally {
            lock.unlock();
        }
        return instance;
    }

    public JTopic getTopic(String topicId) {
        return topicsMap.get(topicId);
    }

    public void addTopic(JTopic topic) {
        topicsMap.put(topic.getId(), topic);
    }

    public void clear(String topicId) {
        topicsMap.remove(topicId);
    }

    public void clearAll() {
        topicsMap.clear();
    }
}
