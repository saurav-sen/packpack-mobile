package com.pack.pack.application.data.util;

import com.pack.pack.application.db.Bookmark;

import java.util.ArrayList;
import java.util.List;

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
