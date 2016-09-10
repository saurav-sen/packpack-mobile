package com.pack.pack.application.db;

import android.content.ContentValues;

import com.pack.pack.model.web.JTopic;

import java.util.Collections;
import java.util.List;

/**
 * Created by Saurav on 10-09-2016.
 */
public class UserOwnedTopicInfo implements DbObject {

    public static final String TABLE_NAME = "USER_OWNED_TOPIC_INFO";

    public static final String ENTITY_ID = "topic_id";
    public static final String TOPIC_NAME = "topic_name";
    public static final String TOPIC_DESCRIPTION = "topic_description";
    public static final String OWNER_ID = "owner_id";
    public static final String TOPIC_CATEGORY = "topic_category";
    public static final String TOPIC_WALLPAPER_URL = "topic_wallpaper_url";

    private String ownerId;
    private String name;
    private String description;
    private String id;
    private String category;
    private String wallpaperUrl;

    public UserOwnedTopicInfo(JTopic topic) {
        this(topic.getOwnerId(), topic.getName(), topic.getDescription(), topic.getId(),
                topic.getCategory(), topic.getWallpaperUrl());
    }

    public UserOwnedTopicInfo(String ownerId, String name, String description, String id,
                              String category, String wallpaperUrl) {
        this.ownerId = ownerId;
        this.name = name;
        this.description = description;
        this.id = id;
        this.category = category;
        this.wallpaperUrl = wallpaperUrl;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getWallpaperUrl() {
        return wallpaperUrl;
    }

    public void setWallpaperUrl(String wallpaperUrl) {
        this.wallpaperUrl = wallpaperUrl;
    }

    @Override
    public ContentValues toContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ENTITY_ID, getId());
        contentValues.put(TOPIC_NAME, getName());
        contentValues.put(TOPIC_DESCRIPTION, getDescription());
        contentValues.put(OWNER_ID, getOwnerId());
        contentValues.put(TOPIC_CATEGORY, getCategory());
        contentValues.put(TOPIC_WALLPAPER_URL, getWallpaperUrl());
        return contentValues;
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public String getEntityId() {
        return getId();
    }

    @Override
    public List<? extends DbObject> getChildrenObjects() {
        return Collections.emptyList();
    }
}
