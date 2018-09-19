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
                    UserInfo.USER_NAME, UserInfo.DISPLAY_NAME};
            try {
                cursor = readable.query(UserInfo.TABLE_NAME, projection, null, null,
                        null, null, null);

                if(cursor.moveToFirst()) {
                    do {
                        long id = cursor.getLong(cursor.getColumnIndexOrThrow(UserInfo._ID));
                        String userId = cursor.getString(cursor.getColumnIndexOrThrow(UserInfo.ENTITY_ID));
                        String userName = cursor.getString(cursor.getColumnIndexOrThrow(UserInfo.USER_NAME));
                        String displayName = cursor.getString(cursor.getColumnIndexOrThrow(UserInfo.DISPLAY_NAME));
                        userInfo = new UserInfo(userName, userId);
                        userInfo.setDisplayName(displayName);
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
        } else if(object instanceof JUser) {
            return convertToUserInfo((JUser)object);
        }
        return null;
    }

    private static UserInfo convertToUserInfo(JUser user) {
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setDisplayName(user.getDisplayName());
        return userInfo;
    }

    private static Bookmark convertToBookmark(JRssFeed feed) {
        Bookmark bookmark = new Bookmark();
        bookmark.setEntityId(feed.getOgUrl());
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

    public static JUser convertUserInfo(UserInfo userInfo) {
        JUser user = new JUser();
        user.setId(userInfo.getEntityId());
        user.setUsername(userInfo.getUsername());
        return user;
    }

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
            int noOfRows = wDB.update(Bookmark.TABLE_NAME, exisitngBookmark.toContentValues(),
                    exisitngBookmark.updateRowWhereClause(), exisitngBookmark.updateRowWhereClauseArguments());
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
                    + " = '" + entityId + "'";
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

    public static boolean deleteBookmark(Bookmark bookmark, SQLiteDatabase wDB) {
        if(bookmark == null)
            return false;
        int noOfRows = wDB.delete(Bookmark.TABLE_NAME, bookmark.deleteRowWhereClause(),
                bookmark.deleteRowWhereClauseArguments());
        return noOfRows > 0;
    }

    public static PagedObject<Bookmark> loadBookmarks(/*long currentPageRef, */SQLiteDatabase readable) {
        Cursor cursor =  null;
        PagedObject<Bookmark> bookmarks = new PagedObject<Bookmark>();
        List<Bookmark> result = new ArrayList<Bookmark>();
        /*if(currentPageRef <= 0) {
            currentPageRef = Integer.MAX_VALUE;
        }*/
        //long nextPageRef = currentPageRef;
        try {
            /*String __SQL = "SELECT " + Bookmark.ENTITY_ID + ", " + Bookmark.TITLE + ", "
                    + Bookmark.DESCRIPTION + ", " + Bookmark.MEDIA_URL + ", " + Bookmark.ARTICLE + ", "
                    + Bookmark.IMAGE_DATA + ", " + Bookmark.TIME_OF_ADD + ", " + Bookmark.SOURCE_URL + ", "
                    + Bookmark.IS_PROCESSED + ", " + Bookmark.IS_VIDEO + " FROM " + Bookmark.TABLE_NAME
                    + " WHERE " + Bookmark.TIME_OF_ADD + " <= " + currentPageRef + " AND " + Bookmark.TITLE +
                    " IS NOT NULL AND (" + Bookmark.DESCRIPTION + " IS NOT NULL OR " + Bookmark.ARTICLE + " IS NOT NULL)"
                    + " ORDER BY " + Bookmark.TIME_OF_ADD + " DESC";*/
            String __SQL = "SELECT " + Bookmark.ENTITY_ID + ", " + Bookmark.TITLE + ", "
                    + Bookmark.DESCRIPTION + ", " + Bookmark.MEDIA_URL + ", " + Bookmark.ARTICLE + ", "
                    + Bookmark.IMAGE_DATA + ", " + Bookmark.TIME_OF_ADD + ", " + Bookmark.SOURCE_URL + ", "
                    + Bookmark.IS_PROCESSED + ", " + Bookmark.IS_VIDEO + " FROM " + Bookmark.TABLE_NAME
                    + " ORDER BY " + Bookmark.TIME_OF_ADD + " DESC";
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

                    /*if(timeOfAdd < nextPageRef) {
                        nextPageRef = timeOfAdd;
                    }*/

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
       // bookmarks.setNextPageRef(nextPageRef);
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

    public static PaginationInfo loadPaginationInfo(SQLiteDatabase readable, String entityId, String type) {
        Cursor cursor = null;
        PaginationInfo paginationInfo = null;
        try {
            String __SQL = "SELECT " + PaginationInfo._ID + ", " + PaginationInfo.ENTITY_ID
                    + ", " + PaginationInfo.NEXT_PAGE_NO + "  FROM " + PaginationInfo.TABLE_NAME  +
                    " WHERE " + PaginationInfo.ENTITY_ID
                    + "='" + entityId + "'";
            if(type != null && !type.trim().isEmpty()) {
                __SQL = __SQL + " AND " + PaginationInfo.CLASS_TYPE + "='" + type + "'";
            }
            cursor = readable.rawQuery(__SQL, null);
            if(cursor.moveToFirst()) {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(PaginationInfo._ID));
                String objId = cursor.getString(cursor.getColumnIndexOrThrow(PaginationInfo.ENTITY_ID));
                int nextPageNo = cursor.getInt(cursor.getColumnIndexOrThrow(PaginationInfo.NEXT_PAGE_NO));
                paginationInfo = new PaginationInfo();
                paginationInfo.setEntityId(entityId);
                paginationInfo.setNextPageNo(nextPageNo);
            }
        } finally {
            if(cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return paginationInfo;
    }
}
