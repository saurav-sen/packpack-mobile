package com.pack.pack.application.topic.activity.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.pack.pack.model.web.JPackAttachment;

/**
 * Created by Saurav on 22-05-2016.
 */
public class ParcelableAttachment implements Parcelable {

    private String attachmentUrl;

    private String attachmentThumbnailUrl;

    private String mimeType;

    private String attachmentType;

    public ParcelableAttachment() {
    }

    public ParcelableAttachment(JPackAttachment attachment) {
        this(attachment.getAttachmentUrl(), attachment.getAttachmentThumbnailUrl(),
                attachment.getMimeType(), attachment.getAttachmentType());
    }

    public ParcelableAttachment(String attachmentUrl, String attachmentThumbnailUrl, String mimeType, String attachmentType) {
        setAttachmentUrl(attachmentUrl + "");
        setAttachmentThumbnailUrl(attachmentThumbnailUrl + "");
        setMimeType(mimeType + "");
        setAttachmentType(attachmentType + "");
    }

    public String getAttachmentUrl() {
        return attachmentUrl;
    }

    public void setAttachmentUrl(String attachmentUrl) {
        this.attachmentUrl = attachmentUrl;
    }

    public String getAttachmentThumbnailUrl() {
        return attachmentThumbnailUrl;
    }

    public void setAttachmentThumbnailUrl(String attachmentThumbnailUrl) {
        this.attachmentThumbnailUrl = attachmentThumbnailUrl;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getAttachmentType() {
        return attachmentType;
    }

    public void setAttachmentType(String attachmentType) {
        this.attachmentType = attachmentType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(attachmentUrl);
        parcel.writeString(attachmentThumbnailUrl);
        parcel.writeString(mimeType);
        parcel.writeString(attachmentType);
    }

    public static final Parcelable.Creator<ParcelableAttachment> CREATOR = new Parcelable.Creator<ParcelableAttachment>() {

        @Override
        public ParcelableAttachment createFromParcel(Parcel parcel) {
            String attachmentUrl = parcel.readString();
            String attachmentThumbnailUrl = parcel.readString();
            String mimeType = parcel.readString();
            String attachmentType = parcel.readString();
            return new ParcelableAttachment(attachmentUrl, attachmentThumbnailUrl, mimeType, attachmentType);
        }

        @Override
        public ParcelableAttachment[] newArray(int size) {
            return new ParcelableAttachment[size];
        }
    };
}
