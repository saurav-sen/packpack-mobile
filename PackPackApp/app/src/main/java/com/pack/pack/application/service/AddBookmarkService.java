package com.pack.pack.application.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.pack.pack.application.AppController;
import com.pack.pack.application.Mode;
import com.pack.pack.application.data.util.ApiConstants;
import com.pack.pack.application.data.util.DownloadFeedImageTask;
import com.pack.pack.application.db.Bookmark;
import com.pack.pack.application.db.DBUtil;
import com.pack.pack.application.db.SquillDbHelper;
import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIBuilder;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;
import com.squill.feed.web.model.JRssFeed;

import java.util.List;

/**
 * Created by Saurav on 28-08-2018.
 */
public class AddBookmarkService extends Service {

    public static final String BOOKMARK_ENTITY_ID = "BOOKMARK_ENTITY_ID";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null) {
            String entityId = intent.getStringExtra(BOOKMARK_ENTITY_ID);
            if (entityId != null) {
                ExecutorsPool.INSTANCE.submit(new AddBookmarkTask(entityId));
            }
        }
        List<Bookmark> unprocessedBookmarks = DBUtil.loadAllUnprocessedBookmarks(new SquillDbHelper(this).getReadableDatabase());
        if(unprocessedBookmarks != null && !unprocessedBookmarks.isEmpty()) {
            for(Bookmark unprocessedBookmark : unprocessedBookmarks) {
                ExecutorsPool.INSTANCE.submit(new ProcessBookmark(unprocessedBookmark));
            }
        }
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private JRssFeed processBookmark(String link) {
        JRssFeed result = null;
        try {
            String userEmail = AppController.getInstance().getUserEmail();
            API api = APIBuilder.create(ApiConstants.ML_BASE_URL)
                    .setAction(COMMAND.PROCESS_BOOKMARK).setUserName(userEmail)
                    .addApiParam(APIConstants.Bookmark.WEB_LINK, link)
                    .addApiParam(APIConstants.User.USERNAME, userEmail).build();

            result = (JRssFeed) api.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private void addBookmark(String entityId) {
        if(entityId == null || entityId.trim().isEmpty())
            return;
        Bookmark bookmark = DBUtil.loadBookmarkByEntityId(entityId, this);
        if(bookmark == null)
            return;
        String sourceUrl = bookmark.getSourceUrl();
        if(sourceUrl == null || sourceUrl.trim().isEmpty())
            return;
        if(NetworkUtil.checkConnectivity(this)) {
            JRssFeed feed = processBookmark(sourceUrl);
            if(feed != null) {
                bookmark = Bookmark.convert(feed);
                DBUtil.storeNewBookmark(bookmark, this);
                if(bookmark != null && !bookmark.isVideo()) {
                    new DownloadFeedImageTask(null, 850, 600, this, null)
                            .execute(bookmark.getMediaUrl());
                }
            }
        }
    }

    private void updateExistingBookmark(Bookmark bookmark) {
        if(bookmark == null || bookmark.getSourceUrl() == null || bookmark.getSourceUrl().trim().isEmpty())
            return;
        if(NetworkUtil.checkConnectivity(this)) {
            String entityId = bookmark.getEntityId();
            if(entityId == null)
                return;
            JRssFeed feed = processBookmark(bookmark.getSourceUrl().trim());
            if(feed != null) {
                bookmark = Bookmark.convert(feed);
                if(bookmark != null && !bookmark.isVideo() && bookmark.getMediaUrl() != null
                        && !bookmark.getMediaUrl().trim().isEmpty()) {
                    bookmark.setEntityId(entityId);
                    bookmark.setTimeOfAdd(System.currentTimeMillis());
                    DBUtil.storeNewBookmark(bookmark, AddBookmarkService.this);
                    new DownloadFeedImageTask(null, 850, 600, this, null)
                            .execute(bookmark.getMediaUrl());
                }
            }
        }
    }

    private class AddBookmarkTask implements Runnable {

        private String entityId;

        private AddBookmarkTask(String entityId) {
            this.entityId = entityId;
        }

        @Override
        public void run() {
            addBookmark(entityId);
        }
    }

    private class ProcessBookmark implements Runnable {

        private Bookmark unprocessedBookmark;

        private ProcessBookmark(Bookmark unprocessedBookmark) {
            this.unprocessedBookmark = unprocessedBookmark;
        }

        @Override
        public void run() {
            updateExistingBookmark(unprocessedBookmark);
        }
    }
}
