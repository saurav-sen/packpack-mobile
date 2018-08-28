package com.pack.pack.application.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.pack.pack.common.util.JSONUtil;
import com.pack.pack.model.web.JUser;
import com.pack.pack.services.exception.PackPackException;
import com.squill.feed.web.model.JRssFeed;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Saurav on 11-06-2016.
 */
public class DBUtil {

    private static final String LOG_TAG = "DBUtil";

    private DBUtil() {
    }

    public static UserInfo loadLastLoggedInUserInfo(SQLiteDatabase readable) {
        Cursor cursor = null;
        UserInfo userInfo = null;
        try {
            String[] projection = new String[] {UserInfo._ID, UserInfo.ENTITY_ID,
                    UserInfo.USER_NAME, UserInfo.FOLLWED_CATEGORIES};
            try {
                cursor = readable.query(UserInfo.TABLE_NAME, projection, null, null,
                        null, null, null);

                if(cursor.moveToFirst()) {
                    do {
                        long id = cursor.getLong(cursor.getColumnIndexOrThrow(UserInfo._ID));
                        String userId = cursor.getString(cursor.getColumnIndexOrThrow(UserInfo.ENTITY_ID));
                        String userName = cursor.getString(cursor.getColumnIndexOrThrow(UserInfo.USER_NAME));
                        /*String accessToken = cursor.getString(cursor.getColumnIndexOrThrow(UserInfo.ACCESS_TOKEN));
                        String accessTokenSecret = cursor.getString(cursor.getColumnIndexOrThrow(UserInfo.ACCESS_TOKEN_SECRET));*/
                        //String followedCategories = cursor.getString(cursor.getColumnIndexOrThrow(UserInfo.FOLLWED_CATEGORIES));
                        userInfo = new UserInfo(userName, userId, null);//, followedCategories);
                        break;
                    } while(cursor.moveToNext());
                }
            } finally {
                if(cursor != null && !cursor.isClosed()) {
                    cursor.close();
                }
            }
        } catch (Exception e) {
            Log.d(LOG_TAG, e.getMessage());
        }
        return userInfo;
    }

    public static DbObject convert(Object object, String containerId) {
        if(object == null)
            return null;
        if(object instanceof  JRssFeed) {
            return convertToBookmark((JRssFeed)object);
        }
        /*if(object instanceof JTopic) {
            return convertToJsonModel((JTopic) object, containerId);
        } else if(object instanceof JPack) {
            return convertToJsonModel((JPack)object, containerId);
        } else if(object instanceof JPackAttachment) {
            return convertJPackAttachment((JPackAttachment) object, containerId);
        }*/
        return null;
    }

    private static Bookmark convertToBookmark(JRssFeed feed) {
        Bookmark bookmark = new Bookmark();
        bookmark.setEntityId(feed.getId());
        bookmark.setTitle(feed.getOgTitle());
        bookmark.setDescription(feed.getArticleSummaryText());
        if(feed.getVideoUrl() != null) {
            bookmark.setMediaUrl(feed.getVideoUrl());
        } else {
            bookmark.setMediaUrl(feed.getOgImage());
        }
        bookmark.setArticle(feed.getArticleSummaryText());
        bookmark.setTimeOfAdd(feed.getUploadTime());
        bookmark.setSourceUrl(feed.getOgUrl());
        return bookmark;
    }

   /* private static JsonModel convertToJsonModel(JTopic topic, String containerId) {
        JsonModel jsonModel = new JsonModel();
        try {
            jsonModel.setEntityId(topic.getId());
            jsonModel.setClassType(JTopic.class.getName());
            jsonModel.setContent(JSONUtil.serialize(topic));
            jsonModel.setEntityContainerId(containerId);
        } catch (PackPackException e) {
            Log.d(LOG_TAG, e.getMessage());
        }
        return jsonModel;
    }*/

    /*private static JsonModel convertToJsonModel(JPack pack, String containerId) {
        JsonModel jsonModel = new JsonModel();
        try {
            jsonModel.setEntityId(pack.getId());
            jsonModel.setClassType(JPack.class.getName());
            jsonModel.setContent(JSONUtil.serialize(pack));
            jsonModel.setEntityContainerId(containerId);
        } catch (PackPackException e) {
            Log.d(LOG_TAG, e.getMessage());
        }
        return jsonModel;
    }*/

   /* private static AttachmentInfo convertJPackAttachment(JPackAttachment attachment, String containerId) {
        AttachmentInfo attachmentInfo = null;
        try {
            attachmentInfo = new AttachmentInfo();
            attachmentInfo.setUrl(attachment.getAttachmentUrl());
            attachmentInfo.setType(attachment.getAttachmentType());
            attachmentInfo.setContainerId(containerId);
            attachmentInfo.setEntityId(attachment.getId());
            attachmentInfo.setJsonBody(JSONUtil.serialize(attachment));
            return attachmentInfo;
        } catch (PackPackException e) {
            Log.d(LOG_TAG, e.getMessage());
        }
        return attachmentInfo;
    }*/

    public static JUser convertUserInfo(UserInfo userInfo) {
        JUser user = new JUser();
        user.setId(userInfo.getEntityId());
        user.setUsername(userInfo.getUsername());
        return user;
    }

   /* public static List<JTopic> convertUserOwnedTopicInfo(List<UserOwnedTopicInfo> userOwnedTopicInfos) {
        List<JTopic> result = new ArrayList<JTopic>();
        if(userOwnedTopicInfos != null && !userOwnedTopicInfos.isEmpty()) {
            for(UserOwnedTopicInfo userOwnedTopicInfo : userOwnedTopicInfos) {
                result.add(convertUserOwnedTopicInfo(userOwnedTopicInfo));
            }
        }
        return result;
    }

    private static JTopic convertUserOwnedTopicInfo(UserOwnedTopicInfo userOwnedTopicInfo) {
        JTopic topic = new JTopic();
        topic.setId(userOwnedTopicInfo.getId());
        topic.setName(userOwnedTopicInfo.getName());
        topic.setDescription(userOwnedTopicInfo.getDescription());
        topic.setCategory(userOwnedTopicInfo.getCategory());
        topic.setWallpaperUrl(userOwnedTopicInfo.getWallpaperUrl());
        return topic;
    }*/

    public static Bookmark storeNewBookmark(Bookmark bookmark, Context context) {
        Bookmark result = null;
        SquillDbHelper squillDbHelper = new SquillDbHelper(context);
        SQLiteDatabase wDB = squillDbHelper.getWritableDatabase();
        Bookmark exisitngBookmark = loadBookmarkByEntityId(bookmark.getEntityId(), context);
        if(exisitngBookmark != null) {
            exisitngBookmark.setTitle(bookmark.getTitle());
            exisitngBookmark.setDescription(bookmark.getDescription());
            exisitngBookmark.setMediaUrl(bookmark.getMediaUrl());
            exisitngBookmark.setImage(bookmark.getImage());
            exisitngBookmark.setArticle(bookmark.getArticle());
            exisitngBookmark.setTimeOfAdd(bookmark.getTimeOfAdd());
            exisitngBookmark.setSourceUrl(bookmark.getSourceUrl());
            exisitngBookmark.setProcessed(bookmark.isProcessed());
            exisitngBookmark.setIsVideo(bookmark.isVideo());
            wDB.update(Bookmark.TABLE_NAME, exisitngBookmark.toContentValues(), null, null);
            result = exisitngBookmark;
        } else {
            wDB.insert(Bookmark.TABLE_NAME, null, bookmark.toContentValues());
            result = bookmark;
        }
        return result;
    }

    public static Bookmark loadBookmarkByEntityId(String entityId, Context context) {
        Cursor cursor =  null;
        SQLiteDatabase readable = new SquillDbHelper(context).getReadableDatabase();
        try {
            String __SQL = "SELECT " + Bookmark.ENTITY_ID + ", " + Bookmark.TITLE + ", "
                    + Bookmark.DESCRIPTION + ", " + Bookmark.MEDIA_URL + ", " + Bookmark.ARTICLE + ", "
                    + Bookmark.IMAGE_DATA + ", " + Bookmark.TIME_OF_ADD  + ", " + Bookmark.SOURCE_URL + ", "
                    + Bookmark.IS_PROCESSED + ", " + Bookmark.IS_VIDEO + " FROM " + Bookmark.TABLE_NAME
                    + " WHERE " + Bookmark.ENTITY_ID
                    + " = " + entityId;
            cursor = readable.rawQuery(__SQL, null);
            if(cursor.moveToFirst()) {
                do {
                    entityId = cursor.getString(cursor.getColumnIndexOrThrow(Bookmark.ENTITY_ID));
                    String title = cursor.getString(cursor.getColumnIndexOrThrow(Bookmark.TITLE));
                    String description = cursor.getString(cursor.getColumnIndexOrThrow(Bookmark.DESCRIPTION));
                    String mediaUrl = cursor.getString(cursor.getColumnIndexOrThrow(Bookmark.MEDIA_URL));
                    String article = cursor.getString(cursor.getColumnIndexOrThrow(Bookmark.ARTICLE));
                    byte[] imageData = cursor.getBlob(cursor.getColumnIndexOrThrow(Bookmark.IMAGE_DATA));
                    long timeOfAdd = cursor.getLong(cursor.getColumnIndexOrThrow(Bookmark.TIME_OF_ADD));
                    String sourceUrl = cursor.getString(cursor.getColumnIndexOrThrow(Bookmark.SOURCE_URL));
                    boolean processed = cursor.getInt(cursor.getColumnIndexOrThrow(Bookmark.IS_PROCESSED)) == 0 ? false : true;
                    boolean isVideo = cursor.getInt(cursor.getColumnIndexOrThrow(Bookmark.IS_VIDEO)) == 0 ? false : true;

                    Bookmark bookmark = new Bookmark();
                    bookmark.setEntityId(entityId);
                    bookmark.setTitle(title);
                    bookmark.setDescription(description);
                    bookmark.setMediaUrl(mediaUrl);
                    bookmark.setArticle(article);
                    bookmark.setImage(imageData);
                    bookmark.setTimeOfAdd(timeOfAdd);
                    bookmark.setSourceUrl(sourceUrl);
                    bookmark.setProcessed(processed);
                    bookmark.setIsVideo(isVideo);

                   return bookmark;
                } while(cursor.moveToNext());
            }
        } finally {
            if(cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return null;
    }

    public static PagedObject<Bookmark> loadBookmarks(long currentPageRef, SQLiteDatabase readable) {
        Cursor cursor =  null;
        PagedObject<Bookmark> bookmarks = new PagedObject<Bookmark>();
        List<Bookmark> result = new ArrayList<Bookmark>();
        if(currentPageRef <= 0) {
            currentPageRef = Integer.MAX_VALUE;
        }
        long nextPageRef = currentPageRef;
        try {
            String __SQL = "SELECT " + Bookmark.ENTITY_ID + ", " + Bookmark.TITLE + ", "
                    + Bookmark.DESCRIPTION + ", " + Bookmark.MEDIA_URL + ", " + Bookmark.ARTICLE + ", "
                    + Bookmark.IMAGE_DATA + ", " + Bookmark.TIME_OF_ADD + ", " + Bookmark.SOURCE_URL + ", "
                    + Bookmark.IS_PROCESSED + ", " + Bookmark.IS_VIDEO + " FROM " + Bookmark.TABLE_NAME
                    + " WHERE " + Bookmark.TIME_OF_ADD + " <= " + currentPageRef + " ORDER BY " + Bookmark.TIME_OF_ADD
                    + " LIMIT 10";
            cursor = readable.rawQuery(__SQL, null);
            if(cursor.moveToFirst()) {
                do {
                    String entityId = cursor.getString(cursor.getColumnIndexOrThrow(Bookmark.ENTITY_ID));
                    String title = cursor.getString(cursor.getColumnIndexOrThrow(Bookmark.TITLE));
                    String description = cursor.getString(cursor.getColumnIndexOrThrow(Bookmark.DESCRIPTION));
                    String mediaUrl = cursor.getString(cursor.getColumnIndexOrThrow(Bookmark.MEDIA_URL));
                    String article = cursor.getString(cursor.getColumnIndexOrThrow(Bookmark.ARTICLE));
                    byte[] imageData = cursor.getBlob(cursor.getColumnIndexOrThrow(Bookmark.IMAGE_DATA));
                    long timeOfAdd = cursor.getLong(cursor.getColumnIndexOrThrow(Bookmark.TIME_OF_ADD));
                    String sourceUrl = cursor.getString(cursor.getColumnIndexOrThrow(Bookmark.SOURCE_URL));
                    boolean processed = cursor.getInt(cursor.getColumnIndexOrThrow(Bookmark.IS_PROCESSED)) == 0 ? false : true;
                    boolean isVideo = cursor.getInt(cursor.getColumnIndexOrThrow(Bookmark.IS_VIDEO)) == 0 ? false : true;

                    if(timeOfAdd < nextPageRef) {
                        nextPageRef = timeOfAdd;
                    }

                    Bookmark bookmark = new Bookmark();
                    bookmark.setEntityId(entityId);
                    bookmark.setTitle(title);
                    bookmark.setDescription(description);
                    bookmark.setMediaUrl(mediaUrl);
                    bookmark.setArticle(article);
                    bookmark.setImage(imageData);
                    bookmark.setTimeOfAdd(timeOfAdd);
                    bookmark.setSourceUrl(sourceUrl);
                    bookmark.setProcessed(processed);
                    bookmark.setIsVideo(isVideo);

                    result.add(bookmark);
                } while(cursor.moveToNext());
            }
        } finally {
            if(cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        bookmarks.setResult(result);
        bookmarks.setNextPageRef(nextPageRef);
        return bookmarks;
    }

    public static List<Bookmark> loadAllUnprocessedBookmarks(SQLiteDatabase readable) {
        Cursor cursor =  null;
        List<Bookmark> result = new ArrayList<Bookmark>();
        try {
            String __SQL = "SELECT " + Bookmark.ENTITY_ID + ", " + Bookmark.TITLE + ", "
                    + Bookmark.DESCRIPTION + ", " + Bookmark.MEDIA_URL + ", " + Bookmark.ARTICLE + ", "
                    + Bookmark.IMAGE_DATA + ", " + Bookmark.TIME_OF_ADD + ", " + Bookmark.SOURCE_URL + ", "
                    + Bookmark.IS_PROCESSED  + ", " + Bookmark.IS_VIDEO + " FROM " + Bookmark.TABLE_NAME
                    + " WHERE " + Bookmark.IS_PROCESSED + " <= 0 ORDER BY " + Bookmark.TIME_OF_ADD;
            cursor = readable.rawQuery(__SQL, null);
            if(cursor.moveToFirst()) {
                do {
                    String entityId = cursor.getString(cursor.getColumnIndexOrThrow(Bookmark.ENTITY_ID));
                    String title = cursor.getString(cursor.getColumnIndexOrThrow(Bookmark.TITLE));
                    String description = cursor.getString(cursor.getColumnIndexOrThrow(Bookmark.DESCRIPTION));
                    String mediaUrl = cursor.getString(cursor.getColumnIndexOrThrow(Bookmark.MEDIA_URL));
                    String article = cursor.getString(cursor.getColumnIndexOrThrow(Bookmark.ARTICLE));
                    byte[] imageData = cursor.getBlob(cursor.getColumnIndexOrThrow(Bookmark.IMAGE_DATA));
                    long timeOfAdd = cursor.getLong(cursor.getColumnIndexOrThrow(Bookmark.TIME_OF_ADD));
                    String sourceUrl = cursor.getString(cursor.getColumnIndexOrThrow(Bookmark.SOURCE_URL));
                    boolean processed = cursor.getInt(cursor.getColumnIndexOrThrow(Bookmark.IS_PROCESSED)) == 0 ? false : true;
                    boolean isVideo = cursor.getInt(cursor.getColumnIndexOrThrow(Bookmark.IS_VIDEO)) == 0 ? false : true;

                    Bookmark bookmark = new Bookmark();
                    bookmark.setEntityId(entityId);
                    bookmark.setTitle(title);
                    bookmark.setDescription(description);
                    bookmark.setMediaUrl(mediaUrl);
                    bookmark.setArticle(article);
                    bookmark.setImage(imageData);
                    bookmark.setTimeOfAdd(timeOfAdd);
                    bookmark.setSourceUrl(sourceUrl);
                    bookmark.setProcessed(processed);
                    bookmark.setIsVideo(isVideo);

                    result.add(bookmark);
                } while(cursor.moveToNext());
            }
        } finally {
            if(cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return result;
    }

    public static <T> T loadJsonModelByEntityId(SQLiteDatabase writable, String entityId, Class<T> classType) {
        String type = classType.getName();
        T result = null;
        Cursor cursor = null;
        try {
            String __SQL = "SELECT " + JsonModel.CONTENT + " FROM " + JsonModel.TABLE_NAME
                    + " WHERE " + JsonModel.ENTITY_ID + "='" + entityId + "' AND "
                    + JsonModel.CLASS_TYPE + "='" + type + "'";
            cursor = writable.rawQuery(__SQL, null);
            if(cursor.moveToFirst()) {
                String json = cursor.getString(cursor.getColumnIndexOrThrow(JsonModel.CONTENT));
                result = convertFromJson(json, classType);
            }
        } finally {
            if(cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return result;
    }

    public static <T> List<T> loadAllJsonModelByContainerId(
            SQLiteDatabase readable, String entityContainerId,
            Class<T> classType) {
        Cursor cursor =  null;
        List<T> result = new ArrayList<T>();
        String type = classType.getName();
        try {
            String __SQL = "SELECT " + JsonModel.CONTENT + " FROM " + JsonModel.TABLE_NAME
                    + " WHERE " + JsonModel.ENTITY_CONTAINER_ID + "='" + entityContainerId
                    + "' AND " + JsonModel.CLASS_TYPE + "='" + type + "'";
            cursor = readable.rawQuery(__SQL, null);
            if(cursor.moveToFirst()) {
                do {
                    String json = cursor.getString(cursor.getColumnIndexOrThrow(JsonModel.CONTENT));
                    T r = convertFromJson(json, classType);
                    if(r != null) {
                        result.add(r);
                    }
                } while(cursor.moveToNext());
            }
        } finally {
            if(cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return result != null && !result.isEmpty() ? result : null;
    }

    private static <T> T convertFromJson(String json, Class<T> classType) {
        T result = null;
        try {
            result = JSONUtil.deserialize(json, classType, true);
        } catch (PackPackException e) {
            Log.i(LOG_TAG, e.getMessage());
        }
        return result;
    }

    public static PaginationInfo loadPaginationInfo(SQLiteDatabase readable, String entityId) {
        return loadPaginationInfo(readable, entityId, null);
    }

    /*public static List<JPackAttachment> loadAllAttachmentInfo(SQLiteDatabase readable, String containerId) {
        Cursor cursor = null;
        List<JPackAttachment> attachments = new LinkedList<JPackAttachment>();
        try {
            String __SQL = "SELECT " + AttachmentInfo.JSON_BODY + " FROM " + AttachmentInfo.TABLE_NAME
                    + " WHERE " + AttachmentInfo.CONTAINER_ID + "='" + containerId + "'";
            cursor = readable.rawQuery(__SQL, null);
            if(cursor.moveToFirst()) {
                do {
                    String json = cursor.getString(cursor.getColumnIndexOrThrow(AttachmentInfo.JSON_BODY));
                    JPackAttachment attachment = JSONUtil.deserialize(json, JPackAttachment.class, true);
                    if(attachment != null) {
                        attachments.add(attachment);
                    }
                } while (cursor.moveToNext());
            }
        } catch (PackPackException e) {
            Log.i(LOG_TAG, e.getMessage());
        } finally {
            if(cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return attachments != null && !attachments.isEmpty() ? attachments : null;
    }*/

    public static PaginationInfo loadPaginationInfo(SQLiteDatabase readable, String entityId, String type) {
        Cursor cursor = null;
        PaginationInfo paginationInfo = null;
        try {
            String __SQL = "SELECT " + PaginationInfo._ID + ", " + PaginationInfo.ENTITY_ID
                    + ", " + PaginationInfo.NEXT_LINK + ", " + PaginationInfo.PREVIOUS_LINK
                    + "  FROM " + PaginationInfo.TABLE_NAME  + " WHERE " + PaginationInfo.ENTITY_ID
                    + "='" + entityId + "'";
            if(type != null && !type.trim().isEmpty()) {
                __SQL = __SQL + " AND " + PaginationInfo.CLASS_TYPE + "='" + type + "'";
            }
            cursor = readable.rawQuery(__SQL, null);
            if(cursor.moveToFirst()) {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(PaginationInfo._ID));
                String objId = cursor.getString(cursor.getColumnIndexOrThrow(PaginationInfo.ENTITY_ID));
                String nextLink = cursor.getString(cursor.getColumnIndexOrThrow(PaginationInfo.NEXT_LINK));
                String previousLink = cursor.getString(cursor.getColumnIndexOrThrow(PaginationInfo.PREVIOUS_LINK));
                paginationInfo = new PaginationInfo();
                paginationInfo.setEntityId(entityId);
                paginationInfo.setNextLink(nextLink);
                paginationInfo.setPreviousLink(previousLink);
            }
        } finally {
            if(cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return paginationInfo;
    }

    /*public static List<JDiscussion> getDiscussionInfosBasedUponContainerId(SQLiteDatabase readable, String containerId, String containerType) {
        Cursor cursor = null;
        List<JDiscussion> infos = null;
        try {
            String __SQL = "SELECT " + DiscussionInfo._ID + ", " + DiscussionInfo.ENTITY_ID + ", "
                    + DiscussionInfo.DATE_TIME + ", " + DiscussionInfo.CONTAINER_TYPE + ", "
                    + DiscussionInfo.CONTAINER_ID + ", " + DiscussionInfo.CONTENT + ", "
                    + DiscussionInfo.FROM_USERNAME + ", " + DiscussionInfo.FROM_USER_FULL_NAME
                    + " FROM " + DiscussionInfo.TABLE_NAME + " WHERE " + DiscussionInfo.CONTAINER_ID
                    + "='" + containerId + "' AND " + DiscussionInfo.CONTAINER_TYPE + "='"
                    + containerType + "'";
            cursor = readable.rawQuery(__SQL, null);
            if(cursor.moveToFirst()) {
                infos = new ArrayList<JDiscussion>();
                do {
                    String eId = cursor.getString(cursor.getColumnIndexOrThrow(DiscussionInfo.ENTITY_ID));
                    String cId = cursor.getString(cursor.getColumnIndexOrThrow(DiscussionInfo.CONTAINER_ID));
                    String cType = cursor.getString(cursor.getColumnIndexOrThrow(DiscussionInfo.CONTAINER_TYPE));
                    String content = cursor.getString(cursor.getColumnIndexOrThrow(DiscussionInfo.CONTENT));
                    String userName = cursor.getString(cursor.getColumnIndexOrThrow(DiscussionInfo.FROM_USERNAME));
                    String userFullName = cursor.getString(cursor.getColumnIndexOrThrow(DiscussionInfo.FROM_USER_FULL_NAME));
                    long dateTime = cursor.getLong(cursor.getColumnIndexOrThrow(DiscussionInfo.DATE_TIME));
                    JDiscussion info = new JDiscussion();
                    info.setId(eId);
                    info.setContent(content);
                    info.setDateTime(dateTime);
                    JUser user = new JUser();
                    user.setUsername(userName);
                    user.setName(userFullName);
                    info.setFromUser(user);
                    infos.add(info);
                } while (cursor.moveToNext());
            }
        } finally {
            if(cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return infos;
    }*/
}
