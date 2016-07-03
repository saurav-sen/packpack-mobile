package com.pack.pack.application.db;

import android.content.ContentValues;

/**
 * Created by Saurav on 04-07-2016.
 */
public class DiscussionInfo implements DbObject {

    public static final String TABLE_NAME = "DISCUSSION_INFO";

    public static final String ENTITY_ID = "entity_id";
    public static final String CONTAINER_TYPE = "container_type";
    public static final String CONTAINER_ID = "container_id";

    public static final String CONTENT = "content";
    public static final String FROM_USERNAME = "from_username";
    public static final String FROM_USER_FULL_NAME = "from_user_full_name";
    public static final String DATE_TIME = "dateTime";

    private String entityId;
    private String content;
    private String containerId;

    private String fromUsername;
    private String fromUserFullName;

    private long dateTime;

    public long getDateTime() {
        return dateTime;
    }

    public void setDateTime(long dateTime) {
        this.dateTime = dateTime;
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContainerId() {
        return containerId;
    }

    public void setContainerId(String containerId) {
        this.containerId = containerId;
    }

    public String getFromUsername() {
        return fromUsername;
    }

    public void setFromUsername(String fromUsername) {
        this.fromUsername = fromUsername;
    }

    public String getFromUserFullName() {
        return fromUserFullName;
    }

    public void setFromUserFullName(String fromUserFullName) {
        this.fromUserFullName = fromUserFullName;
    }

    @Override
    public ContentValues toContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ENTITY_ID, getEntityId());
        contentValues.put(CONTAINER_ID, getContainerId());
        contentValues.put(CONTENT, getContent());
        contentValues.put(FROM_USERNAME, getFromUsername());
        contentValues.put(FROM_USER_FULL_NAME, getFromUserFullName());
        return contentValues;
    }
}
