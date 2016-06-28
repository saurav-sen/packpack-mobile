package com.pack.pack.application.db;

import android.content.ContentValues;

/**
 * Created by Saurav on 27-06-2016.
 */
public class PaginationInfo implements DbObject {

    public static final String TABLE_NAME = "PAGE_INFO";

    public static final String ENTITY_ID = "entity_id";
    public static final String CLASS_TYPE = "type";
    public static final String NEXT_LINK = "next_link";
    public static final String PREVIOUS_LINK = "previous_link";

    private String entityId;

    private String type;

    private String nextLink;

    private String previousLink;

    @Override
    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNextLink() {
        return nextLink;
    }

    public void setNextLink(String nextLink) {
        this.nextLink = nextLink;
    }

    public String getPreviousLink() {
        return previousLink;
    }

    public void setPreviousLink(String previousLink) {
        this.previousLink = previousLink;
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public ContentValues toContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ENTITY_ID, entityId);
        contentValues.put(CLASS_TYPE, type);
        contentValues.put(NEXT_LINK, nextLink);
        contentValues.put(PREVIOUS_LINK, previousLink);
        return contentValues;
    }
}
