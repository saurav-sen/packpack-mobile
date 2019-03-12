package in.squill.squilloffice.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.pack.pack.common.util.JSONUtil;
import com.pack.pack.model.web.JUser;
import com.pack.pack.services.exception.PackPackException;
import com.squill.feed.web.model.JRssFeed;
import com.squill.feed.web.model.JRssFeeds;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Saurav on 11-06-2016.
 */
public class DBUtil {

    private static final String LOG_TAG = "DBUtil";

    private DBUtil() {
    }

    public static DbObject convert(Object object, String containerId) {
        if(object == null)
            return null;
        if(object instanceof JRssFeed) {
            return convertToBookmark((JRssFeed)object);
        }
        return null;
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

    public static Bookmark storeNewBookmark(Bookmark bookmark, Context context) {
        Bookmark result = null;
        SQLiteDatabase wDB = null;
        try {
            SquillDbHelper squillDbHelper = new SquillDbHelper(context);
            wDB = squillDbHelper.getWritableDatabase();
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
        } finally {
            if(wDB != null && wDB.isOpen()) {
                wDB.close();
            }
        }
        return result;
    }

    /*public static int removeExpiredOfflineJsonModel(Context context) {
        SQLiteDatabase readable = null;
        SQLiteDatabase wDB = null;
        int noOfRows = -1;
        try {
            SquillDbHelper squillDbHelper = new SquillDbHelper(context);
            wDB = squillDbHelper.getWritableDatabase();
            readable = squillDbHelper.getReadableDatabase();
            JRssFeedType[] feedTypes = new JRssFeedType[] {JRssFeedType.NEWS, JRssFeedType.NEWS_SPORTS, JRssFeedType.NEWS_SCIENCE_TECHNOLOGY};
            for(JRssFeedType feedType : feedTypes) {
                String dateValue = getLastLoginInfoDateValue(readable, feedType.name());
                if(dateValue == null) {
                    dateValue = DateTimeUtil.today();
                }
                String deleteRowWhereClause = JsonModel.DATE_VALUE + "!='" + dateValue
                        + "' AND " + JsonModel.DATE_VALUE + "!='" + DateTimeUtil.today()
                        + "' AND " + JsonModel.FEED_TYPE + "=" + feedType.name();
                noOfRows = wDB.delete(JsonModel.TABLE_NAME, deleteRowWhereClause, null);
                Log.i(LOG_TAG, "No of Rows deleted for expired JsonModel = " + noOfRows);
            }
        } finally {
            if(readable != null && readable.isOpen()) {
                readable.close();
            }
            if(wDB != null && wDB.isOpen()) {
                wDB.close();
            }
        }
        return noOfRows;
    }*/

    public static void removeObsoletePages(SQLiteDatabase wDB, String feedType, int pageNo) {
        if(wDB == null)
            return;
        int noOfRows = wDB.delete(JsonModel.TABLE_NAME, (JsonModel.FEED_TYPE + "='" + feedType + "' AND " + JsonModel.PAGE_NO + ">=" + pageNo), null);
        if(noOfRows > 0) {
            Log.d(LOG_TAG, "Obsolete pages deleted pageNo > " + pageNo);
        } else {
            Log.d(LOG_TAG, "No pages found for pageNo > " + pageNo);
        }
    }

    public static JsonModel storeJsonModel(JsonModel jsonM, SQLiteDatabase readable, SQLiteDatabase wDB) {
        JsonModel result = null;
        if(jsonM == null)
            return result;
        if(jsonM.getContent() == null || jsonM.getContent().trim().isEmpty())
            return jsonM;
        JsonModel existingJsonM = loadJsonModel(readable, jsonM.getFeedType(), jsonM.getPageNo());
        if(existingJsonM != null) {
            existingJsonM.setContent(jsonM.getContent());
            int noOfRows = wDB.update(JsonModel.TABLE_NAME, existingJsonM.toContentValues(),
                    existingJsonM.updateRowWhereClause(), existingJsonM.updateRowWhereClauseArguments());
            Log.i(LOG_TAG, "JsonModel noOfRows updated = " + noOfRows);
            result = existingJsonM;
        } else {
            wDB.insert(JsonModel.TABLE_NAME, null, jsonM.toContentValues());
            result = jsonM;
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
            if(readable != null && readable.isOpen()) {
                readable.close();
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

    private static JsonModel loadJsonModel(SQLiteDatabase readable, String feedType, int pageNo) {
        JsonModel result = null;
        Cursor cursor = null;
        try {
            String __SQL = "SELECT " + JsonModel.ENTITY_ID + ", " + JsonModel.CONTENT
                    + ", " + JsonModel.PAGE_NO + ", "
                    + JsonModel.FEED_TYPE + " FROM " + JsonModel.TABLE_NAME + " WHERE "
                    + JsonModel.PAGE_NO + "=" + pageNo + " AND " + JsonModel.FEED_TYPE
                    + "='" + feedType + "'";
            cursor = readable.rawQuery(__SQL, null);
            if(cursor.moveToFirst()) {
                //entityId = cursor.getString(cursor.getColumnIndexOrThrow(JsonModel.ENTITY_ID));
                String content = cursor.getString(cursor.getColumnIndexOrThrow(JsonModel.CONTENT));
                int pNo = cursor.getInt(cursor.getColumnIndexOrThrow(JsonModel.PAGE_NO));
                String fType = cursor.getString(cursor.getColumnIndexOrThrow(JsonModel.FEED_TYPE));
                result = new JsonModel();
                result.setContent(content);
                result.setPageNo(pNo);
                result.setFeedType(fType);
            }
        } finally {
            if(cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return result;
    }

    public static int getNextPageNumber(SQLiteDatabase readable, String feedType, int pageNo) {
        int result = -1;
        Cursor cursor = null;
        try {
            Set<Integer> sortedSet = new TreeSet<>();
            String __SQL = "SELECT " + JsonModel.PAGE_NO + " FROM " + JsonModel.TABLE_NAME
                    + " WHERE " + JsonModel.FEED_TYPE + "='" + feedType + "'";
            cursor = readable.rawQuery(__SQL, null);
            if(cursor.moveToFirst()) {
                do {
                    int pNo = cursor.getInt(cursor.getColumnIndexOrThrow(JsonModel.PAGE_NO));
                    sortedSet.add(pNo);
                } while(cursor.moveToNext());
            }
            Iterator<Integer> itr = sortedSet.iterator();
            while(itr.hasNext()) {
                int pNo = itr.next();
                if(pNo > pageNo) {
                    result = pNo;
                    break;
                }
            }
        } finally {
            if(cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return result;
    }

    public static JRssFeeds loadRssFeedsByDateAndPageNo(SQLiteDatabase readable, String feedType, int pageNo) {
        JRssFeeds result = null;
        Cursor cursor = null;
        try {
            String __SQL = "SELECT " + JsonModel.CONTENT + " FROM " + JsonModel.TABLE_NAME
                    + " WHERE " + JsonModel.PAGE_NO + "=" + pageNo + " AND " + JsonModel.FEED_TYPE + "='"
                    + feedType + "'";
            cursor = readable.rawQuery(__SQL, null);
            if(cursor.moveToFirst()) {
                String json = cursor.getString(cursor.getColumnIndexOrThrow(JsonModel.CONTENT));
                result = convertFromJson(json, JRssFeeds.class);
            }
        } finally {
            if(cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return result;
    }

    /*public static JRssFeeds loadRssFeedsByDate(
            SQLiteDatabase readable, String feedType, String dateString) {
        Cursor cursor =  null;
        JRssFeeds result = null;
        try {
            String __SQL = "SELECT " + JsonModel.CONTENT + ", " + JsonModel.PAGE_NO + " FROM " + JsonModel.TABLE_NAME
                    + " WHERE " + JsonModel.DATE_VALUE + "='" + dateString + " AND " + JsonModel.FEED_TYPE + "='" + feedType
                    + "' ORDER BY " + JsonModel.PAGE_NO + " ASC";
            cursor = readable.rawQuery(__SQL, null);
            if(cursor.moveToFirst()) {
                do {
                    String json = cursor.getString(cursor.getColumnIndexOrThrow(JsonModel.CONTENT));
                    JRssFeeds r = convertFromJson(json, JRssFeeds.class);
                    if(r != null && !r.getFeeds().isEmpty()) {
                        if(result == null) {
                            result = new JRssFeeds();
                        }
                        result.getFeeds().addAll(r.getFeeds());
                    }
                } while(cursor.moveToNext());
            }
        } finally {
            if(cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return result != null && !result.getFeeds().isEmpty() ? result : null;
    }*/

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
