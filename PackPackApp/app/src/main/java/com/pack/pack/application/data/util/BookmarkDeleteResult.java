package com.pack.pack.application.data.util;

import com.pack.pack.application.db.Bookmark;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Saurav on 16-09-2018.
 */
public class BookmarkDeleteResult {

    private List<Bookmark> success;

    private List<Bookmark> failure;

    BookmarkDeleteResult() {
        this(null, null);
    }

    BookmarkDeleteResult(List<Bookmark> success, List<Bookmark> failure) {
        this.success = success;
        this.failure = failure;
    }

    public List<Bookmark> getSuccess() {
        if(success == null) {
            success = Collections.emptyList();
        }
        return success;
    }

    public List<Bookmark> getFailure() {
        if(failure == null) {
            failure = Collections.emptyList();
        }
        return failure;
    }
}
