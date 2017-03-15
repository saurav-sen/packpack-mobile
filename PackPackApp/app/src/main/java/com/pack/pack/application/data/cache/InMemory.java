package com.pack.pack.application.data.cache;

import com.pack.pack.application.topic.activity.model.ParcelableTopic;
import com.pack.pack.model.web.JTopic;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Saurav on 15-03-2017.
 */
public class InMemory {

    private Map<String, ParcelableTopic> topicMap = new HashMap<String, ParcelableTopic>();

    public static final InMemory INSTANCE = new InMemory();

    private InMemory() {
    }

    public void add(ParcelableTopic topic) {
        topicMap.put(topic.getTopicId(), topic);
    }

    public ParcelableTopic get(String topicId) {
        return topicMap.get(topicId);
    }
}
