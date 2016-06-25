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

        public static final String JSON_MODEL =
                "CREATE TABLE " + JsonModel.TABLE_NAME + " (" + JsonModel._ID
                        + " INTEGER PRIMARY KEY, " + JsonModel.ENTITY_ID + " TEXT, "
                        + JsonModel.CONTENT + " TEXT, " + JsonModel.CLASS_TYPE + " TEXT, "
                        + JsonModel.HAS_ATTACHMENT + " INTEGER, " + JsonModel.COMMAND
                        + " TEXT)";

        public static final String USER_INFO =
                "CREATE TABLE " + UserInfo.TABLE_NAME + " (" + UserInfo._ID
                        + " INTEGER PRIMARY KEY, " + UserInfo.ENTITY_ID + " TEXT, "
                        + UserInfo.USER_NAME + " TEXT, " + UserInfo.PASSWORD
                        + " TEXT, " + UserInfo.ACCESS_TOKEN + " TEXT, "
                        + UserInfo.REFRESH_TOKEN + " TEXT)";

        public static final String ATTACHMENT_INFO =
                "CREATE TABLE " + AttachmentInfo.TABLE_NAME + "(" + AttachmentInfo._ID
                        + " INTEGER PRIMARY KEY, " + AttachmentInfo.ENTITY_ID + " TEXT, "
                        + AttachmentInfo.URL + " TEXT, " + AttachmentInfo.TYPE + " TEXT, "
                        + AttachmentInfo.BITMAP_CONTENT + " BLOB)";
    }

    public interface DeleteQueries {

        public static final String JSON_MODEL =
                "DROP TABLE IF EXISTS " + JsonModel.TABLE_NAME;

        public static final String USER_INFO =
                "DROP TABLE IF EXISTS " + UserInfo.TABLE_NAME;

        public static final String ATTACHMENT_INFO = "" +
                "DROP TABLE IF EXISTS " + AttachmentInfo.TABLE_NAME;
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
        db.execSQL(CreateQueries.JSON_MODEL);
        db.execSQL(CreateQueries.USER_INFO);
        db.execSQL(CreateQueries.ATTACHMENT_INFO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DeleteQueries.JSON_MODEL);
        db.execSQL(DeleteQueries.USER_INFO);
        db.execSQL(DeleteQueries.ATTACHMENT_INFO);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
