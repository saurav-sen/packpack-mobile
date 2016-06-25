package com.pack.pack.application.db;

import android.content.ContentValues;
import android.provider.BaseColumns;

/**
 * Created by Saurav on 10-04-2016.
 */
public class UserInfo implements DbObject {

    public static final String TABLE_NAME = "USER_INFO";

    public static final String ENTITY_ID = "user_id";
    public static final String USER_NAME = "username";
    public static final String PASSWORD = "password";
    public static final String ACCESS_TOKEN = "access_token";
    public static final String REFRESH_TOKEN = "refresh_token";

    private String userId;

    private String username;

    private String password;

    private String accessToken;

    private String refreshToken;

    public UserInfo(String username, String password) {
        this(username, password, null);
    }

    public UserInfo(String username, String password, String id) {
        this(username, password, id, null);
    }

    public UserInfo(String username, String password, String id, String accessToken) {
        this(username, password, id, accessToken, null);
    }

    public UserInfo(String username, String password, String id, String accessToken, String refreshToken) {
        setUsername(username);
        setPassword(password);
        setUserId(id);
        setAccessToken(accessToken);
        setRefreshToken(refreshToken);
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public ContentValues toContentValues() {
        return null;
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }
}
