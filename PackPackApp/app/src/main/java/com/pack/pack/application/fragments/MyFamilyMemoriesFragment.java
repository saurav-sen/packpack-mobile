package com.pack.pack.application.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.pack.pack.application.R;
import com.pack.pack.application.adapters.TopicDetailAdapter;
import com.pack.pack.application.data.util.LoadPackTask;
import com.pack.pack.application.topic.activity.model.ParcelableTopic;
import com.pack.pack.model.web.JPack;

import java.util.ArrayList;

/**
 * Created by Saurav on 03-07-2017.
 */
public class MyFamilyMemoriesFragment extends Fragment {

    private ParcelableTopic topic;

    public static final String TOPIC = "TOPIC";

    private TopicDetailAdapter adapter;

    @Override
    public void setArguments(Bundle args) {
        topic = (ParcelableTopic) args.getParcelable(TOPIC);
        super.setArguments(args);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my_family_memories, container, false);

        adapter = new TopicDetailAdapter(this.getActivity(), new ArrayList<JPack>());
        final ListView listView = (ListView) rootView.findViewById(R.id.myfamily_memories_list);
        listView.setAdapter(adapter);

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                int count = listView.getCount();
                if (scrollState == SCROLL_STATE_IDLE) {
                    if (listView.getLastVisiblePosition() > count - 1) {
                        new LoadPackTask(MyFamilyMemoriesFragment.this.getActivity(), adapter).execute();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });

        new LoadPackTask(MyFamilyMemoriesFragment.this.getActivity(), adapter).execute(topic);
        return rootView;
    }
}
