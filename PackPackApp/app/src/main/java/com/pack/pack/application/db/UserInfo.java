package com.pack.pack.application.db;

import android.content.ContentValues;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Saurav on 10-04-2016.
 */
public class UserInfo extends DbObjectImpl {

    public static final String TABLE_NAME = "USER_INFO";

    public static final String ENTITY_ID = "user_id";
    public static final String USER_NAME = "username";

    public static final String DISPLAY_NAME = "name";

    private String userId;

    private String username;

    private String displayName;

    public UserInfo() {
    }

    public UserInfo(String username) {
        this(username, null);
    }

    public UserInfo(String username, String id) {
        setUsername(username);
        setUserId(id);
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String getEntityId() {
        return userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public ContentValues toContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ENTITY_ID, userId);
        contentValues.put(USER_NAME, username);
        contentValues.put(DISPLAY_NAME, displayName);
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
