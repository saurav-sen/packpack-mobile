package com.pack.pack.application.db;

import android.content.ContentValues;
import android.provider.BaseColumns;

import com.squill.feed.web.model.JRssFeed;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Created by Saurav on 26-08-2018.
 */
public class Bookmark implements DbObject {

    public static final String TABLE_NAME = "BOOKMARK";

    public static final String ENTITY_ID = "entity_id";

    public static final String TITLE = "title";

    public static final String DESCRIPTION = "description";

    public static final String MEDIA_URL = "media_url";

    public static final String ARTICLE = "article";

    public static final String IMAGE_DATA = "image_data";

    public static final String TIME_OF_ADD = "time_of_add";

    public static final String SOURCE_URL = "source_url";

    public static final String IS_PROCESSED = "is_processed";

    public static final String IS_VIDEO = "is_video";

    private String entityId;

    private String title;

    private String mediaUrl;

    private String description;

    private String article;

    private byte[] image;

    private long timeOfAdd;

    private String sourceUrl;

    private boolean processed = false;

    private boolean isVideo = false;

    public Bookmark() {
        this.entityId = UUID.randomUUID().toString();
    }

    public boolean isVideo() {
        return isVideo;
    }

    public void setIsVideo(boolean isVideo) {
        this.isVideo = isVideo;
    }

    public boolean isProcessed() {
        return processed;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public long getTimeOfAdd() {
        return timeOfAdd;
    }

    public void setTimeOfAdd(long timeOfAdd) {
        this.timeOfAdd = timeOfAdd;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getArticle() {
        return article;
    }

    public void setArticle(String article) {
        this.article = article;
    }

    @Override
    public ContentValues toContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ENTITY_ID, entityId);
        contentValues.put(TITLE, title);
        contentValues.put(DESCRIPTION, description);
        contentValues.put(MEDIA_URL, mediaUrl);
        contentValues.put(ARTICLE, article);
        contentValues.put(IMAGE_DATA, image);
        contentValues.put(TIME_OF_ADD, timeOfAdd);
        contentValues.put(SOURCE_URL, sourceUrl);
        contentValues.put(IS_PROCESSED, isProcessed() ? 1 : 0);
        contentValues.put(IS_VIDEO, isVideo() ? 1 : 0);
        return contentValues;
    }

    @Override
    public List<? extends DbObject> getChildrenObjects() {
        return Collections.emptyList();
    }

    public static Bookmark convert(JRssFeed feed) {
        Bookmark bookmark = new Bookmark();
        bookmark.setProcessed(true);
        bookmark.setTimeOfAdd(System.currentTimeMillis());
        bookmark.setSourceUrl(feed.getOgUrl());
        bookmark.setTitle(feed.getOgTitle());
        bookmark.setArticle(feed.getFullArticleText());
        bookmark.setEntityId(feed.getId());
        bookmark.setDescription(feed.getArticleSummaryText());
        if(feed.getVideoUrl() != null) {
            bookmark.setMediaUrl(feed.getVideoUrl());
            bookmark.setIsVideo(true);
        } else {
            bookmark.setMediaUrl(feed.getOgImage());
            bookmark.setIsVideo(false);
        }
        return bookmark;
    }
}
