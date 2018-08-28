package com.pack.pack.application.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Saurav on 25-06-2016.
 */
public class SquillDbHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "squill";
    public static final int DB_VERSION = 1;

    public interface CreateQueries {

        public static final String BOOKMARK =
                "CREATE TABLE " + Bookmark.TABLE_NAME + " (" + Bookmark._ID
                        + " INTEGER PRIMARY KEY, " + Bookmark.ENTITY_ID + " TEXT, "
                        + Bookmark.TITLE+ " TEXT, " + Bookmark.DESCRIPTION
                        + " TEXT, " + Bookmark.MEDIA_URL + " TEXT, " + Bookmark.ARTICLE
                        + " TEXT, " + Bookmark.IMAGE_DATA + " BLOB, " + Bookmark.TIME_OF_ADD
                        + "INTEGER, " + Bookmark.IS_PROCESSED + " INTEGER DEFAULT 0, "
                        + Bookmark.IS_VIDEO + " INTEGER DEFAULT 0, " + Bookmark.SOURCE_URL + " TEXT)";

        public static final String JSON_MODEL =
                "CREATE TABLE " + JsonModel.TABLE_NAME + " (" + JsonModel._ID
                        + " INTEGER PRIMARY KEY, " + JsonModel.ENTITY_ID + " TEXT, "
                        + JsonModel.ENTITY_CONTAINER_ID + " TEXT, " + JsonModel.CONTENT
                        + " TEXT, " + JsonModel.CLASS_TYPE + " TEXT)";

        /*public static final String USER_INFO =
                "CREATE TABLE " + UserInfo.TABLE_NAME + " (" + UserInfo._ID
                        + " INTEGER PRIMARY KEY, " + UserInfo.ENTITY_ID + " TEXT, "
                        + UserInfo.USER_NAME + " TEXT, " + UserInfo.ACCESS_TOKEN
                        + " TEXT, " + UserInfo.ACCESS_TOKEN_SECRET + " TEXT, "
                        + UserInfo.FOLLWED_CATEGORIES + " TEXT)";*/
        public static final String USER_INFO =
                "CREATE TABLE " + UserInfo.TABLE_NAME + " (" + UserInfo._ID
                        + " INTEGER PRIMARY KEY, " + UserInfo.ENTITY_ID + " TEXT, "
                        + UserInfo.USER_NAME + " TEXT)";

       /* public static final String USER_OWNED_TOPIC_INFO =
                "CREATE TABLE " + UserOwnedTopicInfo.TABLE_NAME + " (" + UserOwnedTopicInfo._ID
                        + " INTEGER PRIMARY KEY, " + UserOwnedTopicInfo.ENTITY_ID + " TEXT, "
                        + UserOwnedTopicInfo.TOPIC_NAME + " TEXT, " + UserOwnedTopicInfo.TOPIC_DESCRIPTION
                        + " TEXT, " + UserOwnedTopicInfo.OWNER_ID + " TEXT, "
                        + UserOwnedTopicInfo.TOPIC_CATEGORY + " TEXT, "
                        + UserOwnedTopicInfo.TOPIC_WALLPAPER_URL + " TEXT)";*/

        /*public static final String ATTACHMENT_INFO =
                "CREATE TABLE " + AttachmentInfo.TABLE_NAME + "(" + AttachmentInfo._ID
                        + " INTEGER PRIMARY KEY, " + AttachmentInfo.ENTITY_ID + " TEXT, "
                        + AttachmentInfo.URL + " TEXT, " + AttachmentInfo.TYPE + " TEXT, "
                        + AttachmentInfo.CONTAINER_ID + " TEXT, " + AttachmentInfo.JSON_BODY
                        + " TEXT)";*/

        public static final String PAGINATION_INFO =
                "CREATE TABLE " + PaginationInfo.TABLE_NAME + "(" + PaginationInfo._ID
                        + " INTEGER PRIMARY KEY, " + PaginationInfo.ENTITY_ID + " TEXT, "
                        + PaginationInfo.CLASS_TYPE + " TEXT, " + PaginationInfo.NEXT_LINK
                        + " TEXT, " + PaginationInfo.PREVIOUS_LINK + " TEXT)";

        public static final String RESOURCE_URL =
                "CREATE TABLE " + ResourceURL.TABLE_NAME + "(" + ResourceURL._ID
                        + " INTEGER PRIMARY KEY, " + ResourceURL.URL + " TEXT, "
                        + ResourceURL.BLOB_CONTENT + " BLOB)";

        /*public static final String DISCUSSION_INFO = "CREATE TABLE " + DiscussionInfo.TABLE_NAME
                + "(" + DiscussionInfo._ID + " INTEGER PRIMARY KEY, " + DiscussionInfo.ENTITY_ID
                + " TEXT, " + DiscussionInfo.CONTAINER_TYPE + " TEXT, " + DiscussionInfo.CONTAINER_ID
                + " TEXT, " + DiscussionInfo.CONTENT + " TEXT, " + DiscussionInfo.FROM_USERNAME
                + " TEXT, " + DiscussionInfo.FROM_USER_FULL_NAME + " TEXT, " + DiscussionInfo.DATE_TIME
                + " TEXT)";*/
    }

    public interface DeleteQueries {

        public static final String BOOKMARK =
                "DROP TABLE IF EXISTS " + Bookmark.TABLE_NAME;

        public static final String JSON_MODEL =
                "DROP TABLE IF EXISTS " + JsonModel.TABLE_NAME;

        public static final String USER_INFO =
                "DROP TABLE IF EXISTS " + UserInfo.TABLE_NAME;

        /*public static final String USER_OWNED_TOPIC_INFO =
                "DROP TABLE IF EXISTS " + UserOwnedTopicInfo.TABLE_NAME;*/

        /*public static final String ATTACHMENT_INFO =
                "DROP TABLE IF EXISTS " + AttachmentInfo.TABLE_NAME;*/

        public static final String PAGINATION_INFO =
                "DROP TABLE IF EXISTS " + PaginationInfo.TABLE_NAME;

        public static final String RESOURCE_URL =
                "DROP TABLE IF EXISTS " + ResourceURL.TABLE_NAME;

        /*public static final String DISCUSSION_INFO =
                "DROP TABLE IF EXISTS " + DiscussionInfo.TABLE_NAME;*/
    }

    public interface InsertQueries {

    }

    public interface UpdateQueries {

    }

    public SquillDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CreateQueries.BOOKMARK);
        db.execSQL(CreateQueries.JSON_MODEL);
        db.execSQL(CreateQueries.USER_INFO);
        //db.execSQL(CreateQueries.USER_OWNED_TOPIC_INFO);
        //db.execSQL(CreateQueries.ATTACHMENT_INFO);
        db.execSQL(CreateQueries.PAGINATION_INFO);
        db.execSQL(CreateQueries.RESOURCE_URL);
        //db.execSQL(CreateQueries.DISCUSSION_INFO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DeleteQueries.JSON_MODEL);
        db.execSQL(DeleteQueries.USER_INFO);
        //db.execSQL(DeleteQueries.USER_OWNED_TOPIC_INFO);
        //db.execSQL(DeleteQueries.ATTACHMENT_INFO);
        db.execSQL(DeleteQueries.PAGINATION_INFO);
        db.execSQL(DeleteQueries.RESOURCE_URL);
        //db.execSQL(DeleteQueries.DISCUSSION_INFO);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
