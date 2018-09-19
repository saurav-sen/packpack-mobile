package com.pack.pack.application.data.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.pack.pack.application.db.Bookmark;
import com.pack.pack.application.db.DBUtil;
import com.pack.pack.application.db.PagedObject;

/**
 * Created by Saurav on 26-08-2018.
 */
public class LoadBookmarkTask extends AbstractDbLoadTask<Long, Integer, PagedObject<Bookmark>> {

    public LoadBookmarkTask(Context context, boolean showLoadingProgress) {
        super(context, showLoadingProgress);
    }

    @Override
    protected PagedObject<Bookmark> doFetchFromDb(SQLiteDatabase readable, Long inputObject) {
        if(inputObject == null) {
            inputObject = Long.MAX_VALUE;
        }
        //return DBUtil.loadBookmarks(inputObject, readable);
        return DBUtil.loadBookmarks(readable);
    }

    @Override
    protected String getFailureMessage() {
        return "Failed to load Bookmarks";
    }
}
