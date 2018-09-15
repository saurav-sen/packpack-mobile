package com.pack.pack.application.view.util;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.pack.pack.application.db.Bookmark;
import com.pack.pack.application.db.DBUtil;
import com.pack.pack.application.service.AddBookmarkService;
import com.squill.feed.web.model.JRssFeed;

import java.util.UUID;

/**
 * Created by Saurav on 12-09-2018.
 */
public class BookmarkUtil {

    public static void addBookmark(JRssFeed feed, Activity activity, View view) {
        Bookmark bookmark = new Bookmark();
        bookmark.setProcessed(false);
        bookmark.setEntityId(UUID.randomUUID().toString());
        bookmark.setSourceUrl(feed.getOgUrl());
        bookmark.setTimeOfAdd(System.currentTimeMillis());
        bookmark = DBUtil.storeNewBookmark(bookmark, activity);
        if(bookmark == null || bookmark.getEntityId() == null || bookmark.getEntityId().trim().isEmpty()) {
            Snackbar.make(view, "Failed to process link", Snackbar.LENGTH_LONG);
        } else {
            Intent intent = new Intent(activity, AddBookmarkService.class);
            intent.putExtra(AddBookmarkService.BOOKMARK_ENTITY_ID, bookmark.getEntityId());
            activity.startService(intent);
            Snackbar.make(view, "Added to bookmark list", Snackbar.LENGTH_LONG);
        }
    }
}
