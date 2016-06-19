package com.pack.pack.application.topic.activity.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.pack.pack.model.web.JTopic;

/**
 * Created by Saurav on 12-05-2016.
 */
public class ParcelableTopic implements Parcelable {

    private String topicId;

    private String topicCategory;

    private String wallpaperUrl;

    private String description;

    private String topicName;

    public ParcelableTopic() {
    }

    public ParcelableTopic(JTopic topic) {
        if(topic != null) {
            setTopicId(topic.getId());
            setDescription(topic.getDescription());
            setTopicCategory(topic.getCategory());
            setWallpaperUrl(topic.getWallpaperUrl());
            setTopicName(topic.getName());
        }
    }

    public ParcelableTopic(String topicId, String topicCategory, String wallpaperUrl, String description, String name) {
        this.topicId = topicId;
        this.topicCategory = topicCategory;
        this.wallpaperUrl = wallpaperUrl;
        this.description = description;
        this.topicName = name;
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

    public String getWallpaperUrl() {
        return wallpaperUrl;
    }

    public void setWallpaperUrl(String wallpaperUrl) {
        this.wallpaperUrl = wallpaperUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(topicId);
        parcel.writeString(topicCategory);
        parcel.writeString(wallpaperUrl);
        parcel.writeString(description);
        parcel.writeString(topicName);
    }

    public static final Parcelable.Creator<ParcelableTopic> CREATOR = new Parcelable.Creator<ParcelableTopic>() {
        @Override
        public ParcelableTopic createFromParcel(Parcel parcel) {
            String topicId = parcel.readString();
            String topicCategory = parcel.readString();
            String wallpaperUrl = parcel.readString();
            String description = parcel.readString();
            String name = parcel.readString();
            return new ParcelableTopic(topicId, topicCategory, wallpaperUrl, description, name);
        }

        @Override
        public ParcelableTopic[] newArray(int size) {
            return new ParcelableTopic[size];
        }
    };
}
