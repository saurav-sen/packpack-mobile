package com.pack.pack.application.db;

import android.content.ContentValues;
import android.graphics.Bitmap;
import android.provider.BaseColumns;

/**
 * Created by Saurav on 25-06-2016.
 */
public class AttachmentInfo implements DbObject {

    public static final String TABLE_NAME = "ATTACHMENT_INFO";

    public static final String ENTITY_ID = "entity_id";
    public static final String URL = "URL";
    public static final String TYPE = "type";
    public static final String CONTAINER_ID = "container_id";

    public static final String JSON_BODY = "JSON_BODY";

    private String entityId;

    private String url;

    private String type;

    private String containerId;

    private String jsonBody;

    public String getJsonBody() {
        return jsonBody;
    }

    public void setJsonBody(String jsonBody) {
        this.jsonBody = jsonBody;
    }

    public String getContainerId() {
        return containerId;
    }

    public void setContainerId(String containerId) {
        this.containerId = containerId;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public ContentValues toContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ENTITY_ID, entityId);
        contentValues.put(URL, url);
        contentValues.put(TYPE, type);
        contentValues.put(CONTAINER_ID, containerId);
        contentValues.put(JSON_BODY, jsonBody);
        return contentValues;
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }
}
