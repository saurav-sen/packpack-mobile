package com.pack.pack.application.data.cache;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.pack.pack.application.Constants;
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

/**
 * Created by Saurav on 18-06-2017.
 */
public class RssFeedCache {

    private Context context;

    private JRssFeedType feedType;

    //private static final String SETTINGS = "SETTINGS";

    private static final String LOG_TAG = "RssFeedCache";

   // private int MAX_NO_FEEDS_THRESHOLD = 300;

    public RssFeedCache(Context context, JRssFeedType feedType) {
        this.context = context;
        this.feedType = feedType;
    }

    public List<JRssFeed> readOfflineData() {
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
    }
}
