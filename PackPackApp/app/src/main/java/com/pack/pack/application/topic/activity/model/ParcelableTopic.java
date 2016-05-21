package com.pack.pack.application.topic.activity.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Saurav on 12-05-2016.
 */
public class ParcelableTopic implements Parcelable {

    private String topicId;

    private String topicCategory;

    private String wallpaperUrl;

    private String description;

    public ParcelableTopic() {
    }

    public ParcelableTopic(String topicId, String topicCategory, String wallpaperUrl, String description) {
        this.topicId = topicId;
        this.topicCategory = topicCategory;
        this.wallpaperUrl = wallpaperUrl;
        this.description = description;
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
    }

    public static final Parcelable.Creator<ParcelableTopic> CREATOR = new Parcelable.Creator<ParcelableTopic>() {
        @Override
        public ParcelableTopic createFromParcel(Parcel parcel) {
            String topicId = parcel.readString();
            String topicCategory = parcel.readString();
            String wallpaperUrl = parcel.readString();
            String description = parcel.readString();
            return new ParcelableTopic(topicId, topicCategory, wallpaperUrl, description);
        }

        @Override
        public ParcelableTopic[] newArray(int size) {
            return new ParcelableTopic[size];
        }
    };
}
