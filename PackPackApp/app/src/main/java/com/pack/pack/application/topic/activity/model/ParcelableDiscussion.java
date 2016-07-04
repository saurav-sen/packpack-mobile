package com.pack.pack.application.topic.activity.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.pack.pack.model.web.JDiscussion;
import com.pack.pack.model.web.JUser;

/**
 * Created by Saurav on 04-07-2016.
 */
public class ParcelableDiscussion implements Parcelable {

    private String id;

    private String content;

    private String parentEntityId;

    private String parentEntityType;

    private String fromUserId;

    private String userName;

    private String userFullName;

    private String userProfilePicture;

    public ParcelableDiscussion() {
    }

    public ParcelableDiscussion(JDiscussion discussion) {
        setId(discussion.getId());
        setContent(discussion.getContent());
        setParentEntityId(discussion.getParentId());
        setParentEntityType(discussion.getParentType());
        JUser user = discussion.getFromUser();
        setUserFullName(user.getName());
        setUserName(user.getUsername());
        setFromUserId(user.getId());
        setUserProfilePicture(user.getProfilePictureUrl());
    }

    public static JDiscussion convert(ParcelableDiscussion parcelableDiscussion) {
        JDiscussion discussion = new JDiscussion();
        discussion.setId(parcelableDiscussion.getId());
        discussion.setContent(parcelableDiscussion.getContent());
        discussion.setParentId(parcelableDiscussion.getParentEntityId());
        discussion.setParentType(parcelableDiscussion.getParentEntityType());
        JUser user = new JUser();
        user.setName(parcelableDiscussion.getUserFullName());
        user.setUsername(parcelableDiscussion.getUserName());
        user.setId(parcelableDiscussion.fromUserId);
        user.setProfilePictureUrl(parcelableDiscussion.getUserProfilePicture());
        discussion.setFromUser(user);
        return discussion;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(getId());
        parcel.writeString(getContent());
        parcel.writeString(getFromUserId());
        parcel.writeString(getUserName());
        parcel.writeString(getUserFullName());
        parcel.writeString(getUserProfilePicture());
        parcel.writeString(getParentEntityId());
        parcel.writeString(getParentEntityType());
    }

    public static final Parcelable.Creator<ParcelableDiscussion> CREATOR = new Parcelable.Creator<ParcelableDiscussion>() {

        @Override
        public ParcelableDiscussion createFromParcel(Parcel parcel) {
            ParcelableDiscussion discussion = new ParcelableDiscussion();
            discussion.setId(parcel.readString());
            discussion.setContent(parcel.readString());
            discussion.setFromUserId(parcel.readString());
            discussion.setUserName(parcel.readString());
            discussion.setUserFullName(parcel.readString());
            discussion.setUserProfilePicture(parcel.readString());
            discussion.setParentEntityId(parcel.readString());
            discussion.setParentEntityType(parcel.readString());
            return discussion;
        }

        @Override
        public ParcelableDiscussion[] newArray(int size) {
            return new ParcelableDiscussion[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getParentEntityId() {
        return parentEntityId;
    }

    public void setParentEntityId(String parentEntityId) {
        this.parentEntityId = parentEntityId;
    }

    public String getParentEntityType() {
        return parentEntityType;
    }

    public void setParentEntityType(String parentEntityType) {
        this.parentEntityType = parentEntityType;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public String getUserProfilePicture() {
        return userProfilePicture;
    }

    public void setUserProfilePicture(String userProfilePicture) {
        this.userProfilePicture = userProfilePicture;
    }

    public String getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }
}