package in.squill.squilloffice.fragments;

import com.squill.feed.web.model.JRssFeed;
import com.squill.feed.web.model.JRssFeedType;

import java.util.LinkedList;

import in.squill.squilloffice.R;
import in.squill.squilloffice.adapters.BaseAdapter;
import in.squill.squilloffice.adapters.TrendingFragmentAdapter;
import in.squill.squilloffice.data.util.FeedsLoadTask;
import in.squill.squilloffice.data.util.NewsFeedTask;

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
    protected FeedsLoadTask initNewTask() {
        return new NewsFeedTask(getActivity());
    }

    @Override
    protected JRssFeedType getFeedType() {
        return JRssFeedType.NEWS;
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
