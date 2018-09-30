package com.pack.pack.application.db;

import android.content.ContentValues;

import java.util.Collections;
import java.util.List;

/**
 * Created by Saurav on 30-09-2018.
 */
public class LoginInfo extends DbObjectImpl {

    public static final String TABLE_NAME = "LOGIN_INFO";

    public static final String ENTITY_ID = "entity_id";
    public static final String FEED_TYPE = "feedType";
    public static final String DATE_VALUE = "dateValue";

    private String feedType;

    private String dateValue;

    public String getFeedType() {
        return feedType;
    }

    public void setFeedType(String feedType) {
        this.feedType = feedType;
    }

    public String getDateValue() {
        return dateValue;
    }

    public void setDateValue(String dateValue) {
        this.dateValue = dateValue;
    }

    @Override
    public String getEntityId() {
        return feedType + "_" + dateValue;
    }

    @Override
    public ContentValues toContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ENTITY_ID, getEntityId());
        contentValues.put(FEED_TYPE, feedType);
        contentValues.put(DATE_VALUE, dateValue);
        return contentValues;
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    protected String getEntityIdColumnName() {
        return ENTITY_ID;
    }

    @Override
    public List<? extends DbObject> getChildrenObjects() {
        return Collections.emptyList();
    }
}
