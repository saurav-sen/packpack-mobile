package com.pack.pack.application.topic.activity.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.pack.pack.model.web.JTopic;

/**
 * Created by Saurav on 12-05-2016.
 */
public class ParcelableTopic implements Parcelable {

    private String ownerId;

    private String topicId;

    private String topicCategory;

    private String wallpaperUrl;

    private String description;

    private String topicName;

    private double longitude;

    private double latitude;

    private String address;

    private boolean isFollowing;

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public boolean isFollowing() {
        return isFollowing;
    }

    public void setIsFollowing(boolean isFollowing) {
        this.isFollowing = isFollowing;
    }

    public ParcelableTopic() {
    }

    public ParcelableTopic(JTopic topic) {
        if(topic != null) {
            setTopicId(topic.getId() + "");
            setOwnerId(topic.getOwnerId() + "");
            setDescription(topic.getDescription() + "");
            setTopicCategory(topic.getCategory() + "");
            setWallpaperUrl(topic.getWallpaperUrl() + "");
            setTopicName(topic.getName() + "");
            setLongitude(topic.getLongitude());
            setLatitude(topic.getLatitude());
            setAddress(topic.getAddress() + "");
            setIsFollowing(topic.isFollowing());
        }
    }

    public ParcelableTopic(String topicId, String ownerId, String topicCategory, String wallpaperUrl,
                           String description, String name, double longitude, double latitude,
                           String address, boolean isFollowing) {
        this.topicId = topicId + "";
        this.ownerId = ownerId + "";
        this.topicCategory = topicCategory + "";
        this.wallpaperUrl = wallpaperUrl + "";
        this.description = description + "";
        this.topicName = name + "";
        this.longitude = longitude;
        this.latitude = latitude;
        this.address = address + "";
        this.isFollowing = isFollowing;
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
        parcel.writeString(ownerId);
        parcel.writeString(topicCategory);
        parcel.writeString(wallpaperUrl);
        parcel.writeString(description);
        parcel.writeString(topicName);
        parcel.writeDouble(longitude);
        parcel.writeDouble(latitude);
        parcel.writeString(address);
        parcel.writeString(String.valueOf(isFollowing));
    }

    public static final Parcelable.Creator<ParcelableTopic> CREATOR = new Parcelable.Creator<ParcelableTopic>() {
        @Override
        public ParcelableTopic createFromParcel(Parcel parcel) {
            String topicId = parcel.readString();
            String ownerId = parcel.readString();
            String topicCategory = parcel.readString();
            String wallpaperUrl = parcel.readString();
            String description = parcel.readString();
            String name = parcel.readString();
            double longitude = parcel.readDouble();
            double latitude = parcel.readDouble();
            String address = parcel.readString();
            boolean isFollowing = Boolean.parseBoolean(parcel.readString().trim());
            return new ParcelableTopic(topicId, ownerId, topicCategory, wallpaperUrl, description,
                    name, longitude, latitude, address, isFollowing);
        }

        @Override
        public ParcelableTopic[] newArray(int size) {
            return new ParcelableTopic[size];
        }
    };
}
