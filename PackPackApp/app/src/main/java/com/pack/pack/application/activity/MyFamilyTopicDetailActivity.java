package com.pack.pack.application.activity;

import android.content.Intent;

import com.pack.pack.application.AppController;
import com.pack.pack.application.topic.activity.model.ParcelableTopic;

/**
 * Created by Saurav on 03-07-2017.
 */
public class MyFamilyTopicDetailActivity extends TopicDetailActivity {

    @Override
    protected void openTopic(ParcelableTopic topic) {
        Intent intent = new Intent(MyFamilyTopicDetailActivity.this, MyFamilyActivity.class);
        intent.putExtra(AppController.TOPIC_PARCELABLE_KEY, topic);
        startActivity(intent);
    }
}
