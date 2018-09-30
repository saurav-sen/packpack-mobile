package com.pack.pack.application;

import com.squill.feed.web.model.JRssFeedType;

import java.util.concurrent.TimeUnit;

/**
 * Created by Saurav on 12-09-2018.
 */
public class FeedReceiveState {

    public static final long DEFAULT_MIN_TIMESTAMP = -1;

    public static final long DEFAULT_UPDATE_INTERVAL = 1;

    public static final TimeUnit DEFAULT_UPDATE_INTERVAL_UNIT = TimeUnit.MINUTES;

    private long newsLastUpdateTimestamp = DEFAULT_MIN_TIMESTAMP;

    private long sportsLastUpdateTimestamp = DEFAULT_MIN_TIMESTAMP;

    private long articlesLastUpdateTimestamp = DEFAULT_MIN_TIMESTAMP;

    FeedReceiveState() {
    }

    public long getLastUpdateTimestamp(JRssFeedType feedType) {
        long timestamp = -1;
        if(feedType == null)
            return timestamp;
        switch (feedType) {
            case NEWS:
                timestamp = newsLastUpdateTimestamp;
                break;
            case NEWS_SPORTS:
                timestamp = sportsLastUpdateTimestamp;
                break;
            case NEWS_SCIENCE_TECHNOLOGY:
            case ARTICLE:
                timestamp = articlesLastUpdateTimestamp;
                break;
        }
        return timestamp;
    }

    public void setLastUpdateTimestamp(JRssFeedType feedType, long currentTimestamp) {
        if(feedType != null) {
            switch (feedType) {
                case NEWS:
                    newsLastUpdateTimestamp = currentTimestamp;
                    break;
                case NEWS_SPORTS:
                    sportsLastUpdateTimestamp = currentTimestamp;
                    break;
                case NEWS_SCIENCE_TECHNOLOGY:
                case ARTICLE:
                    articlesLastUpdateTimestamp = currentTimestamp;
                    break;
            }
        }
    }
}
