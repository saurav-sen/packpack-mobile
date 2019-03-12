package in.squill.squilloffice.data.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import in.squill.squilloffice.db.Bookmark;
import in.squill.squilloffice.db.DBUtil;

/**
 * Created by Saurav on 16-09-2018.
 */
public class BookmarkDeleteTask extends AbstractDbEntityDeleteTask<Bookmarks, Integer, BookmarkDeleteResult> {

    public BookmarkDeleteTask(Context context) {
        this(context, true);
    }

    public BookmarkDeleteTask(Context context, boolean showLoadingProgress) {
        super(context, showLoadingProgress);
    }

    @Override
    protected BookmarkDeleteResult deleteFromDb(SQLiteDatabase wDB, Bookmarks inputObject) {
        List<Bookmark> bookmarks = inputObject.getBookmarks();
        if(bookmarks == null || bookmarks.isEmpty())
            return new BookmarkDeleteResult();
        BookmarkDeleteResult result = new BookmarkDeleteResult(new ArrayList<Bookmark>(), new ArrayList<Bookmark>());
        int len = bookmarks.size();
        for(int i=0; i<len; i++) {
            Bookmark bookmark = bookmarks.get(i);
           if(DBUtil.deleteBookmark(bookmark, wDB)) {
               result.getSuccess().add(bookmark);
           } else {
               result.getFailure().add(bookmark);
           }
        }
        return result;
    }

    @Override
    protected String getFailureMessage() {
        return "Failed to delete Bookmarks";
    }
}
