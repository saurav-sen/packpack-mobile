package in.squill.squilloffice.fragments;

import android.app.Activity;
import android.widget.ArrayAdapter;

import com.squill.feed.web.model.JRssFeed;

import java.util.List;

/**
 * Created by Saurav on 26-09-2018.
 */
public abstract class BaseAdapter extends ArrayAdapter<JRssFeed> {

    private Activity activity;

    private List<JRssFeed> feeds;

    protected BaseAdapter(Activity activity, List<JRssFeed> feeds, int listViewId) {
        super(activity, listViewId, feeds.toArray(new JRssFeed[feeds.size()]));
        this.activity = activity;
        this.feeds = feeds;
    }

    public final void clearState() {
        clearFeeds();
        doClearState();
    }

    protected abstract void doClearState();

    protected Activity getActivity() {
        return activity;
    }

    private List<JRssFeed> getFeeds() {
        return feeds;
    }

    public void addNewFeeds(List<JRssFeed> newFeeds) {
        addNewFeeds(-1, newFeeds);
    }

    public abstract void addNewFeeds(int location, List<JRssFeed> newFeeds);

    private void clearFeeds() {
        if(feeds != null) {
            getFeeds().clear();
        }
    }
}
