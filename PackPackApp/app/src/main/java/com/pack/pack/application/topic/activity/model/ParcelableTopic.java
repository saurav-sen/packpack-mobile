package com.pack.pack.application.topic.activity.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Saurav on 12-05-2016.
 */
public class ParcelableTopic implements Parcelable {

    private String topicId;

    private String topicCategory;

    public ParcelableTopic(String topicId, String topicCategory) {
        this.topicId = topicId;
        this.topicCategory = topicCategory;
    }

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

    public static final Parcelable.Creator<ParcelableTopic> CREATOR = new Parcelable.Creator<ParcelableTopic>() {
        @Override
        public ParcelableTopic createFromParcel(Parcel parcel) {
            String topicId = parcel.readString();
            String topicCategory = parcel.readString();
            return new ParcelableTopic(topicId, topicCategory);
        }

        @Override
        public ParcelableTopic[] newArray(int size) {
            return new ParcelableTopic[size];
        }
    };
}
