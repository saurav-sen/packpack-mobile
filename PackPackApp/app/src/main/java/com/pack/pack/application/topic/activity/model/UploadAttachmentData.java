package com.pack.pack.application.topic.activity.model;

import android.net.Uri;

/**
 * Created by Saurav on 10-02-2017.
 */
public class UploadAttachmentData {

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
}
