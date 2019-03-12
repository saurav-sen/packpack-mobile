package in.squill.squilloffice.fragments;

import com.squill.feed.web.model.JRssFeed;
import com.squill.feed.web.model.JRssFeedType;

import java.util.LinkedList;

import in.squill.squilloffice.R;
import in.squill.squilloffice.adapters.BaseAdapter;
import in.squill.squilloffice.adapters.DiscoverFragmentAdapter;
import in.squill.squilloffice.data.util.ArticlesFeedTask;
import in.squill.squilloffice.data.util.FeedsLoadTask;

/**
 * Created by Saurav on 26-09-2018.
 */
public class DiscoverFragment extends BaseFragment {

    private BaseAdapter adapter;

    @Override
    protected int getViewLayoutId() {
        return R.layout.activity_science;
    }

    @Override
    protected int getListViewId() {
        return R.id.science_feeds;
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
        return JRssFeedType.NEWS_SCIENCE_TECHNOLOGY;
    }

    @Override
    protected FeedsLoadTask initNewTask() {
        return new ArticlesFeedTask(getActivity());
    }
}
