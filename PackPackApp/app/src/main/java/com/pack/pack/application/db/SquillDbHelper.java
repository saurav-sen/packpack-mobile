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
                        + " INTEGER, " + Bookmark.IS_PROCESSED + " INTEGER DEFAULT 0, "
                        + Bookmark.IS_VIDEO + " INTEGER DEFAULT 0, " + Bookmark.SOURCE_URL + " TEXT)";

        public static final String LOGIN_INFO =
                "CREATE TABLE " + LoginInfo.TABLE_NAME + " (" + LoginInfo._ID
                        + " INTEGER PRIMARY KEY, " + LoginInfo.ENTITY_ID + " TEXT, "
                        + LoginInfo.FEED_TYPE + " TEXT, " + LoginInfo.DATE_VALUE + " TEXT)";

        public static final String JSON_MODEL =
                "CREATE TABLE " + JsonModel.TABLE_NAME + " (" + JsonModel._ID
                        + " INTEGER PRIMARY KEY, " + JsonModel.ENTITY_ID + " TEXT, "
                        + JsonModel.FEED_TYPE + " TEXT, " + JsonModel.CONTENT + " TEXT, "
                        + JsonModel.PAGE_NO + " INTEGER DEFAULT 0, " + JsonModel.DATE_VALUE + " TEXT)";

        public static final String USER_INFO =
                "CREATE TABLE " + UserInfo.TABLE_NAME + " (" + UserInfo._ID
                        + " INTEGER PRIMARY KEY, " + UserInfo.ENTITY_ID + " TEXT, "
                        + UserInfo.USER_NAME + " TEXT, " + UserInfo.DISPLAY_NAME + " TEXT)";

        public static final String PAGINATION_INFO =
                "CREATE TABLE " + PaginationInfo.TABLE_NAME + "(" + PaginationInfo._ID
                        + " INTEGER PRIMARY KEY, " + PaginationInfo.ENTITY_ID + " TEXT, "
                        + PaginationInfo.CLASS_TYPE + " TEXT, " + PaginationInfo.NEXT_PAGE_NO
                        + " INTEGER)";

        public static final String RESOURCE_URL =
                "CREATE TABLE " + ResourceURL.TABLE_NAME + "(" + ResourceURL._ID
                        + " INTEGER PRIMARY KEY, " + ResourceURL.URL + " TEXT, "
                        + ResourceURL.BLOB_CONTENT + " BLOB)";
    }

    public interface DeleteQueries {

        public static final String BOOKMARK =
                "DROP TABLE IF EXISTS " + Bookmark.TABLE_NAME;

        public static final String LOGIN_INFO =
                "DROP TABLE IF EXISTS " + LoginInfo.TABLE_NAME;

        public static final String JSON_MODEL =
                "DROP TABLE IF EXISTS " + JsonModel.TABLE_NAME;

        public static final String USER_INFO =
                "DROP TABLE IF EXISTS " + UserInfo.TABLE_NAME;

        public static final String PAGINATION_INFO =
                "DROP TABLE IF EXISTS " + PaginationInfo.TABLE_NAME;

        public static final String RESOURCE_URL =
                "DROP TABLE IF EXISTS " + ResourceURL.TABLE_NAME;
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
        db.execSQL(CreateQueries.LOGIN_INFO);
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
       // db.execSQL(DeleteQueries.BOOKMARK);
        db.execSQL(DeleteQueries.LOGIN_INFO);
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
