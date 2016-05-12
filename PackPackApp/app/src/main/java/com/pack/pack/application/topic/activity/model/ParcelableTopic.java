package com.pack.pack.application.topic.activity.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by CipherCloud on 12-05-2016.
 */
public class ParcelableTopic implements Parcelable {

    private String topicId;

    private String topicCategory;

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public String getTopicCategory() {
        return topicCategory;
    }

    public void setTopicCategory(String topicCategory) {
        this.topicCategory = topicCategory;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(topicId);
        parcel.writeString(topicCategory);
    }
}
