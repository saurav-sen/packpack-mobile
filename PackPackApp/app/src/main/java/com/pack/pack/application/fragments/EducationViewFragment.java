package com.pack.pack.application.fragments;

import com.pack.pack.application.AppController;
import com.pack.pack.application.activity.TopicDetailActivity;
import com.pack.pack.application.topic.activity.model.ParcelableTopic;
import com.pack.pack.model.web.JTopic;

/**
 * Created by Saurav on 28-07-2016.
 */
public class EducationViewFragment extends TopicViewFragment {

    @Override
    protected void handleItemClick(JTopic topic) {
        ParcelableTopic parcel = new ParcelableTopic(topic.getId(), topic.getCategory(),
                topic.getWallpaperUrl(), topic.getDescription(), topic.getName());
        openDetailActivity(AppController.TOPIC_PARCELABLE_KEY, parcel, TopicDetailActivity.class);
    }
}
