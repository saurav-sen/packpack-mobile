package com.pack.pack.application.activity.fragments;

import com.pack.pack.application.R;
import com.pack.pack.application.adapters.DiscoverFragmentAdapter;
import com.pack.pack.application.data.util.FeedsLoadTask;
import com.pack.pack.application.data.util.RefreshmentFeedTask;
import com.squill.feed.web.model.JRssFeed;
import com.squill.feed.web.model.JRssFeedType;

import java.util.LinkedList;

/**
 * Created by Saurav on 26-09-2018.
 */
public class DiscoverFragment extends BaseFragment {

    private BaseAdapter adapter;

    @Override
    protected int getViewLayoutId() {
        return R.layout.activity_broadcast;
    }

    @Override
    protected int getListViewId() {
        return R.id.squill_feeds;
    }

    @Override
    protected BaseAdapter initFragmentAdapter() {
        if(adapter == null) {
            adapter = new DiscoverFragmentAdapter(getActivity(), new LinkedList<JRssFeed>());
        }
        return adapter;
    }

    @Override
    protected JRssFeedType getFeedType() {
        return JRssFeedType.REFRESHMENT;
    }

    @Override
    protected FeedsLoadTask initNewTask() {
        return new RefreshmentFeedTask(getActivity());
    }
}
