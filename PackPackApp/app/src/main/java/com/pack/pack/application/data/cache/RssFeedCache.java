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
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Saurav on 18-06-2017.
 */
public class RssFeedCache {

    private String pageLink;

    private Context context;

    private JRssFeedType feedType;

    private static final String NEXT_PAGE_LINK = "NEXT_PAGE_LINK";

    private static final String LOG_TAG = "RssFeedCache";

    public RssFeedCache(Context context, JRssFeedType feedType) {
        this.context = context;
        this.feedType = feedType;
    }

    public List<JRssFeed> readOfflineData() {
        if(!Constants.FIRST_PAGE.equals(pageLink)) {
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
                return filter(c.getFeeds());
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
        }
        return Collections.emptyList();
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

    public String readLastPageLink() {
        SharedPreferences settings = context.getSharedPreferences(NEXT_PAGE_LINK, 0);
        pageLink = settings.getString(NEXT_PAGE_LINK, Constants.FIRST_PAGE);
        return pageLink;
    }

    public void storeOfflineData(Pagination<JRssFeed> page) {
        if(page == null)
            return;
        FileOutputStream fileOutputStream4Data = null;
        try {
            fileOutputStream4Data = context.openFileOutput(feedType.name(), Context.MODE_PRIVATE);
            SharedPreferences settings = context.getSharedPreferences(NEXT_PAGE_LINK, 0);
            JRssFeeds c = new JRssFeeds();
            c.getFeeds().addAll(page.getResult());
            String json = JSONUtil.serialize(c);
            fileOutputStream4Data.write(json.getBytes());
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(NEXT_PAGE_LINK, page.getNextLink()).commit();
        } catch (FileNotFoundException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        } catch (PackPackException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
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
}
