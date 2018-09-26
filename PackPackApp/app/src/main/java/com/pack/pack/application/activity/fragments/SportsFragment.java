package com.pack.pack.application.activity.fragments;

import com.pack.pack.application.R;
import com.pack.pack.application.adapters.SportsFragmentAdapter;
import com.pack.pack.application.data.util.FeedsLoadTask;
import com.pack.pack.application.data.util.SportsFeedTask;
import com.squill.feed.web.model.JRssFeed;

import java.util.LinkedList;

/**
 * Created by Saurav on 26-09-2018.
 */
public class SportsFragment extends BaseFragment {

    private BaseAdapter adapter;

    @Override
    protected int getViewLayoutId() {
        return R.layout.activity_sports;
    }

    @Override
    protected int getListViewId() {
        return R.id.sports_feeds;
    }

    @Override
    protected BaseAdapter initFragmentAdapter() {
        if(adapter == null) {
            adapter = new SportsFragmentAdapter(getActivity(), new LinkedList<JRssFeed>());
        }
        return adapter;
    }

    @Override
    protected FeedsLoadTask initNewTask(int pageNo) {
        return new SportsFeedTask(getActivity(), pageNo);
    }
}
