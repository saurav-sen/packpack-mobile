package in.squill.squilloffice.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Saurav on 25-06-2016.
 */
public class SquillDbHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "squill";
    public static final int DB_VERSION = 2;

    public interface CreateQueries {

        public static final String BOOKMARK =
                "CREATE TABLE IF NOT EXISTS " + Bookmark.TABLE_NAME + " (" + Bookmark._ID
                        + " INTEGER PRIMARY KEY, " + Bookmark.ENTITY_ID + " TEXT, "
                        + Bookmark.TITLE+ " TEXT, " + Bookmark.DESCRIPTION
                        + " TEXT, " + Bookmark.MEDIA_URL + " TEXT, " + Bookmark.ARTICLE
                        + " TEXT, " + Bookmark.IMAGE_DATA + " BLOB, " + Bookmark.TIME_OF_ADD
                        + " INTEGER, " + Bookmark.IS_PROCESSED + " INTEGER DEFAULT 0, "
                        + Bookmark.IS_VIDEO + " INTEGER DEFAULT 0, " + Bookmark.SOURCE_URL + " TEXT)";

        public static final String JSON_MODEL =
                "CREATE TABLE IF NOT EXISTS " + JsonModel.TABLE_NAME + " (" + JsonModel._ID
                        + " INTEGER PRIMARY KEY, " + JsonModel.ENTITY_ID + " TEXT, "
                        + JsonModel.FEED_TYPE + " TEXT, " + JsonModel.CONTENT + " TEXT, "
                        + JsonModel.PAGE_NO + " INTEGER DEFAULT 0)";
    }

    public interface DeleteQueries {

        public static final String BOOKMARK =
                "DROP TABLE IF EXISTS " + Bookmark.TABLE_NAME;

        public static final String JSON_MODEL =
                "DROP TABLE IF EXISTS " + JsonModel.TABLE_NAME;
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
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DeleteQueries.BOOKMARK);
        db.execSQL(DeleteQueries.JSON_MODEL);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
