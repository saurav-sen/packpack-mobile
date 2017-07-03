package com.pack.pack.application.fragments;

import com.pack.pack.model.web.dto.RssFeedSourceType;

/**
 * Created by Saurav on 01-07-2017.
 */
public class SquillTeamViewFragment extends HomeViewFragment {

    @Override
    protected String getFeedApiType() {
        return RssFeedSourceType.SQUILL_TEAM;
    }
}
