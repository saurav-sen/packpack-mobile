package com.pack.pack.application;

import com.squill.feed.web.model.JRssFeedType;

/**
 * Created by Saurav on 12-09-2018.
 */
public class FeedReceiveState {

    private boolean isFirstPageNewsReceived;

    private boolean isFirstPageScienceNewsReceived;

    private boolean isFirstPageSportsNewsReceived;

    private boolean isFirstPageArticlesReceived;

    private int newsNextPageNo;

    private int sportsNewsNextPageNo;

    private int scienceNewsNextPageNo;

    private int articlesNextPageNo;

    FeedReceiveState() {
        this.isFirstPageNewsReceived = false;
        this.isFirstPageScienceNewsReceived = false;
        this.isFirstPageSportsNewsReceived = false;
        this.isFirstPageArticlesReceived = false;
        this.newsNextPageNo = 0;
        this.sportsNewsNextPageNo = 0;
        this.scienceNewsNextPageNo = 0;
        this.articlesNextPageNo = 0;
    }

    public boolean isFirstPageNewsReceived() {
        return isFirstPageNewsReceived;
    }

    public void setIsFirstPageNewsReceived(boolean isFirstPageNewsReceived) {
        this.isFirstPageNewsReceived = isFirstPageNewsReceived;
    }

    public boolean isFirstPageScienceNewsReceived() {
        return isFirstPageScienceNewsReceived;
    }

    public void setIsFirstPageScienceNewsReceived(boolean isFirstPageScienceNewsReceived) {
        this.isFirstPageScienceNewsReceived = isFirstPageScienceNewsReceived;
    }

    public boolean isFirstPageSportsNewsReceived() {
        return isFirstPageSportsNewsReceived;
    }

    public void setIsFirstPageSportsNewsReceived(boolean isFirstPageSportsNewsReceived) {
        this.isFirstPageSportsNewsReceived = isFirstPageSportsNewsReceived;
    }

    public boolean isFirstPageArticlesReceived() {
        return isFirstPageArticlesReceived;
    }

    public void setIsFirstPageArticlesReceived(boolean isFirstPageArticlesReceived) {
        this.isFirstPageArticlesReceived = isFirstPageArticlesReceived;
    }

    public int getNewsNextPageNo() {
        return newsNextPageNo;
    }

    public void setNewsNextPageNo(int newsNextPageNo) {
        this.newsNextPageNo = newsNextPageNo;
    }

    public int getSportsNewsNextPageNo() {
        return sportsNewsNextPageNo;
    }

    public void setSportsNewsNextPageNo(int sportsNewsNextPageNo) {
        this.sportsNewsNextPageNo = sportsNewsNextPageNo;
    }

    public int getScienceNewsNextPageNo() {
        return scienceNewsNextPageNo;
    }

    public void setScienceNewsNextPageNo(int scienceNewsNextPageNo) {
        this.scienceNewsNextPageNo = scienceNewsNextPageNo;
    }

    public int getArticlesNextPageNo() {
        return articlesNextPageNo;
    }

    public void setArticlesNextPageNo(int articlesNextPageNo) {
        this.articlesNextPageNo = articlesNextPageNo;
    }

    public boolean isFirstPageReceived(JRssFeedType feedType) {
        boolean result = false;
        if(feedType != null) {
            switch (feedType) {
                case NEWS:
                    result = isFirstPageNewsReceived();
                    break;
                case NEWS_SPORTS:
                    result = isFirstPageSportsNewsReceived();
                    break;
                case NEWS_SCIENCE_TECHNOLOGY:
                    result = isFirstPageScienceNewsReceived();
                    break;
                case ARTICLE:
                    result = isFirstPageArticlesReceived();
                    break;
            }
        }
        return result;
    }

    public int getNextPageNo(JRssFeedType feedType) {
        int result = 0;
        if(feedType != null) {
            switch (feedType) {
                case NEWS:
                    result = getNewsNextPageNo();
                    break;
                case NEWS_SPORTS:
                    result = getSportsNewsNextPageNo();
                    break;
                case NEWS_SCIENCE_TECHNOLOGY:
                    result = getScienceNewsNextPageNo();
                    break;
                case ARTICLE:
                    result = getArticlesNextPageNo();
                    break;
            }
        }
        return result;
    }
}
