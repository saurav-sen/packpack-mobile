package com.pack.pack.application.topic.activity.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.pack.pack.model.web.JRssFeed;

/**
 * Created by Saurav on 29-05-2017.
 */
public class ParcellableRssFeed implements Parcelable {

    private String ogTitle;
    private String ogDescription;
    private String ogImage;
    private String videoUrl;

    public ParcellableRssFeed() {
    }

    public ParcellableRssFeed(JRssFeed feed) {
        if(feed != null) {
            setOgTitle(feed.getOgTitle());
            setOgDescription(feed.getOgDescription());
            setOgImage(feed.getOgImage());
            setVideoUrl(feed.getVideoUrl());
        }
    }

    public ParcellableRssFeed(String ogTitle, String ogDescription, String ogImage, String videoUrl) {
        this.ogTitle = ogTitle;
        this.ogDescription = ogDescription;
        this.ogImage = ogImage;
        this.videoUrl = videoUrl;
    }

    public String getOgTitle() {
        return this.ogTitle;
    }

    public void setOgTitle(String ogTitle) {
        this.ogTitle = ogTitle;
    }

    public String getOgDescription() {
        return this.ogDescription;
    }

    public void setOgDescription(String ogDescription) {
        this.ogDescription = ogDescription;
    }

    public String getOgImage() {
        return this.ogImage;
    }

    public void setOgImage(String ogImage) {
        this.ogImage = ogImage;
    }

    public String getVideoUrl() {
        return this.videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(ogTitle);
        parcel.writeString(ogDescription);
        parcel.writeString(ogImage);
        parcel.writeString(videoUrl);
    }

    public static final Parcelable.Creator<ParcellableRssFeed> CREATOR = new Parcelable.Creator<ParcellableRssFeed>() {
        @Override
        public ParcellableRssFeed createFromParcel(Parcel parcel) {
            String ogTitle = parcel.readString();
            String ogDescription = parcel.readString();
            String ogImage = parcel.readString();
            String videoUrl = parcel.readString();
            return new ParcellableRssFeed(ogTitle, ogDescription, ogImage, videoUrl);
        }

        @Override
        public ParcellableRssFeed[] newArray(int size) {
            return new ParcellableRssFeed[size];
        }
    };
}
