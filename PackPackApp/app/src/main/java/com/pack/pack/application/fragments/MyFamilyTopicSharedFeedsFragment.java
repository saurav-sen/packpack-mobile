package com.pack.pack.application.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

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
 * Created by Saurav on 27-08-2017.
 */
public class MyFamilyTopicSharedFeedsFragment extends Fragment implements IAsyncTaskStatusListener {

    private ParcelableTopic topic;

    public static final String TOPIC = "TOPIC";

    private TopicSharedFeedsAdapter adapter;

    @Override
    public void setArguments(Bundle args) {
        topic = (ParcelableTopic) args.getParcelable(TOPIC);
        super.setArguments(args);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.myfamily_fragment_topic_shared_feeds, container, false);
        if (topic != null) {
            InMemory.INSTANCE.add(topic);
        }
        ListView shared_feeds = (ListView) rootView.findViewById(R.id.shared_feeds);
        adapter = new TopicSharedFeedsAdapter(this.getActivity(), new ArrayList<JPackAttachment>(0), topic);
        shared_feeds.setAdapter(adapter);
        new LoadTopicFeedsTask(this.getActivity(), topic.getTopicId()).addListener(this).execute(CommonConstants.NULL_PAGE_LINK);
        return rootView;
    }

    @Override
    public void onSuccess(String taskID, Object data) {
        if (data != null) {
            Pagination<JPackAttachment> page = (Pagination<JPackAttachment>) data;
            handleSuccess(page.getResult());
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

    public void handleSuccess(List<JPackAttachment> attachments) {
        if (attachments == null)
            return;
        adapter.getAttachments().addAll(attachments);
        adapter.notifyDataSetChanged();
    }

    public void handleFailure(String errorMsg) {
        Toast.makeText(this.getActivity(), errorMsg, Toast.LENGTH_LONG).show();
    }
}
