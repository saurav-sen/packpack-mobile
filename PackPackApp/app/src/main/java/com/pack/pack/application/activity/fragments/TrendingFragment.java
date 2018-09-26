package com.pack.pack.application.activity.fragments;

import com.pack.pack.application.R;
import com.pack.pack.application.adapters.TrendingFragmentAdapter;
import com.pack.pack.application.data.util.FeedsLoadTask;
import com.pack.pack.application.data.util.NewsFeedTask;
import com.squill.feed.web.model.JRssFeed;

import java.util.LinkedList;

/**
 * Created by Saurav on 26-09-2018.
 */
public class TrendingFragment extends BaseFragment {

    private BaseAdapter adapter;

    @Override
    protected BaseAdapter initFragmentAdapter() {
        if(adapter == null) {
            adapter = new TrendingFragmentAdapter(getActivity(), new LinkedList<JRssFeed>());
        }
        return adapter;
    }

    @Override
    protected FeedsLoadTask initNewTask(int pageNo) {
        return new NewsFeedTask(getActivity(), pageNo);
    }

    @Override
    protected int getViewLayoutId() {
        return R.layout.activity_news;
    }

    @Override
    protected int getListViewId() {
        return R.id.news_feeds;
    }
}
