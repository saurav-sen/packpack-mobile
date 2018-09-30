package com.pack.pack.application.data.cache;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.pack.pack.application.Constants;
import com.pack.pack.application.data.util.DateTimeUtil;
import com.pack.pack.application.db.DBUtil;
import com.pack.pack.application.db.JsonModel;
import com.pack.pack.application.db.LoginInfo;
import com.pack.pack.application.db.SquillDbHelper;
import com.pack.pack.common.util.JSONUtil;
import com.pack.pack.services.exception.PackPackException;
import com.squill.feed.web.model.JRssFeed;
import com.pack.pack.model.web.Pagination;
import com.squill.feed.web.model.JRssFeedType;
import com.squill.feed.web.model.JRssFeeds;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

/**
 * Created by Saurav on 18-06-2017.
 */
public class RssFeedCache {

    private Context context;

    private JRssFeedType feedType;

    private static final String LOG_TAG = "RssFeedCache";

    public RssFeedCache(Context context, JRssFeedType feedType) {
        this.context = context;
        this.feedType = feedType;
    }

    private List<JRssFeed> readOfflineData(SQLiteDatabase readable, int pageNo) {
        List<JRssFeed> after = Collections.emptyList();
        JRssFeeds c = DBUtil.loadRssFeedsByDateAndPageNo(readable, feedType.name(), DateTimeUtil.today(), pageNo);
        if(c != null && !c.getFeeds().isEmpty()) {
            after = c.getFeeds();
        }
        return after;
    }

   public Pagination<JRssFeed> readOfflineData(int pageNo) {
        SQLiteDatabase readable = null;
        Pagination<JRssFeed> page = new Pagination<JRssFeed>();
        try {
            readable = new SquillDbHelper(context).getReadableDatabase();
            List<JRssFeed> after = readOfflineData(readable, pageNo);
            page.getResult().addAll(after);
            int nextPageNo = DBUtil.getNextPageNumber(readable, feedType.name(), pageNo);
            page.setNextPageNo(nextPageNo);
        } finally {
            if(readable != null && readable.isOpen()) {
                readable.close();
            }
        }
        return page;
    }

    private List<JRssFeed> readLastLoggedInOfflineData(SQLiteDatabase readable, String dateValue, int pageNo) {
        List<JRssFeed> after = Collections.emptyList();
        JRssFeeds c = DBUtil.loadRssFeedsByDateAndPageNo(readable, feedType.name(), dateValue, pageNo);
        if(c != null && !c.getFeeds().isEmpty()) {
            after = c.getFeeds();
        }
        return after;
    }

    public Pagination<JRssFeed> readLastLoggedInOfflineData(int pageNo) {
        SQLiteDatabase readable = null;
        Pagination<JRssFeed> page = new Pagination<JRssFeed>();
        try {
            readable = new SquillDbHelper(context).getReadableDatabase();
            String dateValue = DBUtil.getLastLoginInfoDateValue(readable, feedType.name());
            if(dateValue == null || dateValue.trim().isEmpty()) {
                dateValue = DateTimeUtil.today();
            }
            List<JRssFeed> after = readLastLoggedInOfflineData(readable, dateValue, pageNo);
            page.getResult().addAll(after);
            int nextPageNo = DBUtil.getNextPageNumber(readable, feedType.name(), pageNo);
            page.setNextPageNo(nextPageNo);
        } finally {
            if(readable != null && readable.isOpen()) {
                readable.close();
            }
        }
        return page;
    }

    public void removeExpiredOfflineJsonModel() {
        DBUtil.removeExpiredOfflineJsonModel(context, feedType.name());
    }

    public boolean updateLastLoggedInInfo() {
        if(context == null)
            return false;
        boolean result = false;
        SQLiteDatabase readable = null;
        SQLiteDatabase wDB = null;
        try {
            SquillDbHelper squillDbHelper = new SquillDbHelper(context);
            readable = squillDbHelper.getReadableDatabase();
            String dateValue = DateTimeUtil.today();
            LoginInfo loginInfo = DBUtil.loadLoginInfo(readable, feedType.name(), dateValue);
            if(loginInfo == null) {
                result = true;
                loginInfo = new LoginInfo();
                loginInfo.setFeedType(feedType.name());
                loginInfo.setDateValue(dateValue);
                wDB = squillDbHelper.getWritableDatabase();
                wDB.insert(LoginInfo.TABLE_NAME, null, loginInfo.toContentValues());
            }
        } finally {
            if(readable != null && readable.isOpen()) {
                readable.close();
            }
            if(wDB != null && wDB.isOpen()) {
                wDB.close();
            }
        }
        return result;
    }

    public void storeData4OfflineUse(List<JRssFeed> feeds, int pageNo) {
        if(feeds == null || feeds.isEmpty())
            return;
        else {
            SQLiteDatabase readable = null;
            try {
                JRssFeeds c = new JRssFeeds();
                readable = new SquillDbHelper(context).getReadableDatabase();
                List<JRssFeed> older = readOfflineData(readable, pageNo);
                MergeResult mergeResult = deDuplicatedMerge(feeds, older);
                if(mergeResult.isAnyChange() || older == null || older.isEmpty()) {
                    feeds = mergeResult.getFeeds();
                    c.getFeeds().addAll(feeds);
                    JsonModel jsonM = new JsonModel();
                    jsonM.setFeedType(feedType.name());
                    jsonM.setContent(JSONUtil.serialize(c));
                    jsonM.setPageNo(pageNo);
                    jsonM.setDateString(DateTimeUtil.today());
                    DBUtil.storeJsonModel(jsonM, feedType.name(), context);
                }
            } catch (PackPackException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
            } finally {
                if(readable != null && readable.isOpen()) {
                    readable.close();
                }
            }
        }
    }

    private MergeResult deDuplicatedMerge(List<JRssFeed> newer, List<JRssFeed> older) {
        Set<JRssFeed> set = new LinkedHashSet<>();
        set.addAll(newer);
        boolean anyChange = set.addAll(older);
        return new MergeResult(anyChange, new ArrayList<>(set));
    }

    private class MergeResult {

        private boolean anyChange;

        private List<JRssFeed> feeds;

        private MergeResult(boolean anyChange, List<JRssFeed> feeds) {
            this.anyChange = anyChange;
            this.feeds = feeds;
        }

        private List<JRssFeed> getFeeds() {
            if(this.feeds == null) {
                this.feeds = new ArrayList<>();
            }
            return feeds;
        }

        private boolean isAnyChange() {
            return anyChange;
        }
    }

   /* public List<JRssFeed> readOfflineData() {
        List<JRssFeed> after = Collections.emptyList();
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = context.openFileInput(feedType.name());
            StringBuilder json = new StringBuilder();
            byte[] buffer = new byte[1024];
            int n = -1;
            while ((n = fileInputStream.read(buffer)) != -1) {
                json.append(new String(buffer, 0, n));
            }
            JRssFeeds c = JSONUtil.deserialize(json.toString(), JRssFeeds.class, true);
            if(c == null) {
                return Collections.emptyList();
            }
            List<JRssFeed> before = c.getFeeds();
            after = filter(before);
            if(before.size() != after.size()) {
                c.setFeeds(after);
                storeFeeds4OfflineUsage(c);
            }
        } catch (FileNotFoundException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        } catch (PackPackException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        } finally {
            try {
                if(fileInputStream != null) {
                    fileInputStream.close();
                }
            } catch (IOException e) {
                Log.d(LOG_TAG, e.getMessage(), e);
            }
        }
        return after;
    }

    private List<JRssFeed> filter(List<JRssFeed> feeds) {
        if(feeds == null) {
            return Collections.emptyList();
        }
        List<JRssFeed> result = new LinkedList<>();
        result.addAll(feeds);
        long twentyFourHoursAgo = System.currentTimeMillis() - 24 * 60 * 60 * 1000;
        Iterator<JRssFeed> itr = result.iterator();
        while(itr.hasNext()) {
            JRssFeed feed = itr.next();
            if(feed.getUploadTime() < twentyFourHoursAgo) {
                itr.remove();
            }
        }
        return result;
    }

    private void storeFeeds4OfflineUsage(JRssFeeds c) throws FileNotFoundException, IOException, PackPackException {
        FileOutputStream fileOutputStream4Data = null;
        try {
            fileOutputStream4Data = context.openFileOutput(feedType.name(), Context.MODE_PRIVATE);
            String json = JSONUtil.serialize(c);
            fileOutputStream4Data.write(json.getBytes());
        } finally {
            try {
                if(fileOutputStream4Data != null) {
                    fileOutputStream4Data.close();
                }
            } catch (IOException e) {
                Log.d(LOG_TAG, e.getMessage(), e);
            }
        }
    }

    public void storeOfflineData(List<JRssFeed> newFeeds, int requestedPageNo) {
        try {
            if (newFeeds == null || newFeeds.isEmpty())
                return;
            Set<JRssFeed> set3 = new LinkedHashSet<JRssFeed>();
            if(requestedPageNo > 0) {
                List<JRssFeed> existingFeeds = readOfflineData();
                set3.addAll(existingFeeds);
            }
            set3.addAll(newFeeds);
            JRssFeeds c = new JRssFeeds();
            c.getFeeds().addAll(new ArrayList<JRssFeed>(set3));
            storeFeeds4OfflineUsage(c);
        } catch (FileNotFoundException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        } catch (PackPackException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }
    }*/
}
