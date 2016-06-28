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
    public static final String ACCESS_TOKEN_SECRET = "access_token_secret";

    private String userId;

    private String username;

    private String password;

    private String accessToken;

    private String accessTokenSecret;

    public UserInfo() {
    }

    public UserInfo(String username, String password) {
        this(username, password, null);
    }

    public UserInfo(String username, String password, String id) {
        this(username, password, id, null);
    }

    public UserInfo(String username, String password, String id, String accessToken) {
        this(username, password, id, accessToken, null);
    }

    public UserInfo(String username, String password, String id, String accessToken, String accessTokenSecret) {
        setUsername(username);
        setPassword(password);
        setUserId(id);
        setAccessToken(accessToken);
        setAccessTokenSecret(accessTokenSecret);
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessTokenSecret() {
        return accessTokenSecret;
    }

    public void setAccessTokenSecret(String accessTokenSecret) {
        this.accessTokenSecret = accessTokenSecret;
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
        ContentValues contentValues = new ContentValues();
        contentValues.put(ENTITY_ID, userId);
        contentValues.put(USER_NAME, username);
        contentValues.put(PASSWORD, password);
        contentValues.put(ACCESS_TOKEN, accessToken);
        contentValues.put(ACCESS_TOKEN_SECRET, accessTokenSecret);
        return contentValues;
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }
}
