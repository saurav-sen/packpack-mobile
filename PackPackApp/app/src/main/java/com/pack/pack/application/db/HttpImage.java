package com.pack.pack.application.db;

import android.content.ContentValues;

import java.util.List;

/**
 * Created by Saurav on 04-10-2018.
 */
public class HttpImage extends DbObjectImpl {

    public static final String TABLE_NAME = "HTTP_IMAGE";

    public static final String URL = "url";
    public static final String TIMESTAMP = "timestamp";

    private String url;
    private String timestamp;

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    protected String getEntityIdColumnName() {
        return URL;
    }

    @Override
    public String getEntityId() {
        return getUrl();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public ContentValues toContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(URL, getUrl());
        contentValues.put(TIMESTAMP, getTimestamp());
        return contentValues;
    }

    @Override
    public List<? extends DbObject> getChildrenObjects() {
        return null;
    }
}
