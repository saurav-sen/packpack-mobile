package com.pack.pack.application.topic.activity.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Saurav on 10-02-2017.
 */
public class UploadAttachmentData implements Parcelable {

    private Uri mediaFileUri;

    private String uploadEntityId;

    private String uploadEntityType;

    private String topicId;

    public UploadAttachmentData(Uri mediaFileUri, String uploadEntityId, String uploadEntityType, String topicId) {
        this.mediaFileUri = mediaFileUri;
        this.uploadEntityId = uploadEntityId;
        this.uploadEntityType = uploadEntityType;
        this.topicId = topicId;
    }

    public Uri getMediaFileUri() {
        return mediaFileUri;
    }

    public void setMediaFileUri(Uri mediaFileUri) {
        this.mediaFileUri = mediaFileUri;
    }

    public String getUploadEntityId() {
        return uploadEntityId;
    }

    public void setUploadEntityId(String uploadEntityId) {
        this.uploadEntityId = uploadEntityId;
    }

    public String getUploadEntityType() {
        return uploadEntityType;
    }

    public void setUploadEntityType(String uploadEntityType) {
        this.uploadEntityType = uploadEntityType;
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        //Uri.writeToParcel(parcel, mediaFileUri);
        parcel.writeString(mediaFileUri.toString());
        parcel.writeString(uploadEntityType);
        parcel.writeString(uploadEntityId);
        parcel.writeString(topicId);
    }

    public static final Parcelable.Creator<UploadAttachmentData> CREATOR = new Parcelable.Creator<UploadAttachmentData>() {
        @Override
        public UploadAttachmentData createFromParcel(Parcel parcel) {
            //Uri uri = Uri.CREATOR.createFromParcel(parcel);
            Uri mediaFileUri = Uri.parse(parcel.readString());
            String uploadEntityType = parcel.readString();
            String uploadEntityId = parcel.readString();
            String topicId = parcel.readString();
            return new UploadAttachmentData(mediaFileUri, uploadEntityId, uploadEntityType, topicId);
        }

        @Override
        public UploadAttachmentData[] newArray(int size) {
            return new UploadAttachmentData[size];
        }
    };
}
