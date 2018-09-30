package com.pack.pack.application.db;

import android.content.ContentValues;
import android.provider.BaseColumns;

import java.util.Collections;
import java.util.List;

/**
 * Created by Saurav on 25-06-2016.
 */
public class JsonModel extends DbObjectImpl {

    public static final String TABLE_NAME = "JSON_MODEL";

    public static final String ENTITY_ID = "entity_id";
    public static final String CONTENT = "content";

    public static final String FEED_TYPE = "feedType";
    public static final String PAGE_NO = "pageNo";
    public static final String DATE_VALUE = "dateValue";

    private String content;

    private String dateString;

    private int pageNo;

    private String feedType;

    public String getFeedType() {
        return feedType;
    }

    public void setFeedType(String feedType) {
        this.feedType = feedType;
    }

    public String getDateString() {
        return dateString;
    }

    public void setDateString(String dateString) {
        this.dateString = dateString;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public String getEntityId() {
        return feedType + "_" + dateString + "_" + pageNo;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public ContentValues toContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ENTITY_ID, getEntityId());
        contentValues.put(CONTENT, content);
        contentValues.put(FEED_TYPE, feedType);
        contentValues.put(PAGE_NO, pageNo);
        contentValues.put(DATE_VALUE, dateString);
        return contentValues;
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public List<? extends DbObject> getChildrenObjects() {
        return Collections.emptyList();
    }

    @Override
    protected String getEntityIdColumnName() {
        return ENTITY_ID;
    }
}
