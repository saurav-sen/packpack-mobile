package com.pack.pack.application.fragments;

import com.pack.pack.application.R;
import com.pack.pack.application.activity.TopicDetailActivity;
import com.pack.pack.application.topic.activity.model.ParcelableTopic;
import com.pack.pack.model.web.JTopic;

/**
 * Created by Saurav on 01-05-2016.
 */
public class LifestyleViewFragment  extends TopicViewFragment {

    @Override
    protected void handleItemClick(JTopic topic) {
        ParcelableTopic parcel = new ParcelableTopic(topic.getId(), topic.getCategory());
        openDetailActivity(TopicDetailActivity.PARCELABLE_KEY, parcel, TopicDetailActivity.class);
    }
}