package com.pack.pack.application.fragments;

import com.pack.pack.model.web.dto.RssFeedSourceType;

/**
 * Created by Saurav on 03-07-2017.
 */
public class NewsViewFragment extends HomeViewFragment {

    @Override
    protected String getFeedApiType() {
        return RssFeedSourceType.NEWS_API;
    }
}
