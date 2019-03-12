package in.squill.squilloffice.data.util;

import java.util.ArrayList;
import java.util.List;

import in.squill.squilloffice.db.Bookmark;

/**
 * Created by Saurav on 16-09-2018.
 */
public class Bookmarks {

    private List<Bookmark> bookmarks;

    public List<Bookmark> getBookmarks() {
        if(bookmarks == null) {
            bookmarks = new ArrayList<>();
        }
        return bookmarks;
    }
}
