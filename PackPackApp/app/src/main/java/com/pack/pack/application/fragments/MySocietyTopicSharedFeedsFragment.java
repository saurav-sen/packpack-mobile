package com.pack.pack.application.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;

import com.pack.pack.application.AppController;
import com.pack.pack.application.R;
import com.pack.pack.application.adapters.TopicSharedFeedsAdapter;
import com.pack.pack.application.data.cache.InMemory;
import com.pack.pack.application.data.util.IAsyncTaskStatusListener;
import com.pack.pack.application.data.util.LoadTopicFeedsTask;
import com.pack.pack.application.topic.activity.model.ParcelableTopic;
import com.pack.pack.common.util.CommonConstants;
import com.pack.pack.model.web.JPackAttachment;
import com.pack.pack.model.web.Pagination;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Saurav on 23-08-2017.
 */
public class MySocietyTopicSharedFeedsFragment extends Fragment implements IAsyncTaskStatusListener {

    private ParcelableTopic topic;

    public static final String TOPIC = "TOPIC";

    private TopicSharedFeedsAdapter adapter;

    private String nextLink;

    private String prevLink;

    private ListView shared_feeds;

    @Override
    public void setArguments(Bundle args) {
        topic = (ParcelableTopic) args.getParcelable(TOPIC);
        AppController.getInstance().getPackAttachments().clear();
        super.setArguments(args);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.mysociety_fragment_topic_shared_feeds, container, false);
        if(topic != null) {
            InMemory.INSTANCE.add(topic);
        }
        shared_feeds = (ListView) rootView.findViewById(R.id.shared_feeds);
        shared_feeds.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                int count = shared_feeds.getCount();
                if (scrollState == SCROLL_STATE_IDLE) {
                    int c = count - 3;
                    if (shared_feeds.getLastVisiblePosition() >= c && c > 0 && !"END_OF_PAGE".equals(nextLink)) {
                        loadTopicSharedFeeds(nextLink);
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
        adapter = new TopicSharedFeedsAdapter(this.getActivity(), new ArrayList<JPackAttachment>(0), topic);
        shared_feeds.setAdapter(adapter);
        loadTopicSharedFeeds(!"END_OF_PAGE".equals(nextLink) ? nextLink : prevLink);
        return rootView;
    }

    private void loadTopicSharedFeeds(String pageLink) {
        String link = pageLink;
        if(link == null) {
            link = CommonConstants.NULL_PAGE_LINK;
        }
        new LoadTopicFeedsTask(this.getActivity(), topic.getTopicId()).addListener(this).execute(link);
    }

    @Override
    public void onSuccess(String taskID, Object data) {
        if(data != null) {
            Pagination<JPackAttachment> page = (Pagination<JPackAttachment>) data;
            handleSuccess(page);
        }
    }

    @Override
    public void onFailure(String taskID, String errorMsg) {
        handleFailure(errorMsg);
    }

    @Override
    public void onPostComplete(String taskID) {

    }

    @Override
    public void onPreStart(String taskID) {

    }

    public void handleSuccess(Pagination<JPackAttachment> page) {
        List<JPackAttachment> attachments = page.getResult();
        nextLink = page.getNextLink();
        prevLink = page.getPreviousLink();
        if(attachments == null)
            return;
        adapter.getAttachments().addAll(attachments);
        adapter.notifyDataSetChanged();
        AppController.getInstance().getPackAttachments().clear();
        AppController.getInstance().getPackAttachments().addAll(adapter.getAttachments());
    }

    public void handleFailure(String errorMsg) {
        Toast.makeText(this.getActivity(), errorMsg, Toast.LENGTH_LONG).show();
    }
}
