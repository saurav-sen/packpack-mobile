package com.pack.pack.application.db;

import android.content.ContentValues;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Saurav on 10-04-2016.
 */
public class UserInfo implements DbObject {

    public static final String TABLE_NAME = "USER_INFO";

    public static final String ENTITY_ID = "user_id";
    public static final String USER_NAME = "username";
    public static final String ACCESS_TOKEN = "access_token";
    public static final String ACCESS_TOKEN_SECRET = "access_token_secret";
    public static final String FOLLWED_CATEGORIES = "followed_categories";

    private String userId;

    private String username;

    private String accessToken;

    private String accessTokenSecret;

    public List<UserOwnedTopicInfo> getUserOwnedTopicInfos() {
        if(userOwnedTopicInfos == null) {
            userOwnedTopicInfos = new ArrayList<UserOwnedTopicInfo>();
        }
        return userOwnedTopicInfos;
    }

    public void setUserOwnedTopicInfos(List<UserOwnedTopicInfo> userOwnedTopicInfos) {
        this.userOwnedTopicInfos = userOwnedTopicInfos;
    }

    private List<UserOwnedTopicInfo> userOwnedTopicInfos;

    public String getFollowedCategories() {
        return followedCategories;
    }

    public void setFollowedCategories(String followedCategories) {
        this.followedCategories = followedCategories;
    }

    private String followedCategories;

    public UserInfo() {
    }

    public UserInfo(String username, String password) {
        this(username, null, null, null);
        setPassword(password);
    }

    public UserInfo(String username, String id, String followedCategories) {
        this(username, id, null, followedCategories);
    }

    public UserInfo(String username, String id, String accessToken, String followedCategories) {
        this(username, id, accessToken, null, followedCategories);
    }

    public UserInfo(String username, String id, String accessToken, String accessTokenSecret, String followedCategories) {
        setUsername(username);
        setUserId(id);
        setAccessToken(accessToken);
        setAccessTokenSecret(accessTokenSecret);
        setFollowedCategories(followedCategories);
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

    private String password;

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
        contentValues.put(ACCESS_TOKEN, accessToken);
        contentValues.put(ACCESS_TOKEN_SECRET, accessTokenSecret);
        return contentValues;
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public List<? extends DbObject> getChildrenObjects() {
        return getUserOwnedTopicInfos();
    }
}
