package com.pack.pack.application.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.pack.pack.application.R;
import com.pack.pack.application.adapters.DiscussionAdapter;
import com.pack.pack.application.adapters.IDiscussionAdapter;
import com.pack.pack.application.adapters.TopicDetailAdapter;
import com.pack.pack.application.data.util.FetchDiscussionTask;
import com.pack.pack.application.data.util.IAsyncTaskStatusListener;
import com.pack.pack.application.data.util.LoadPackTask;
import com.pack.pack.application.data.util.ScrollableDiscussion;
import com.pack.pack.application.topic.activity.model.ParcelableTopic;
import com.pack.pack.model.web.EntityType;
import com.pack.pack.model.web.JDiscussion;
import com.pack.pack.model.web.JPack;
import com.pack.pack.model.web.Pagination;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Saurav on 03-07-2017.
 */
public class MySocietyDiscussionsFragment extends Fragment implements IAsyncTaskStatusListener {

    private ParcelableTopic topic;

    public static final String TOPIC = "TOPIC";

    private List<JDiscussion> discussions = new LinkedList<JDiscussion>();

    private IDiscussionAdapter adapter;

    private ScrollableDiscussion currentScrollableDiscussion;

    private ListView mysociety_discussions_list;

    private ProgressDialog progressDialog;

    @Override
    public void setArguments(Bundle args) {
        topic = (ParcelableTopic) args.getParcelable(TOPIC);
        super.setArguments(args);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my_society_discussions, container, false);

        currentScrollableDiscussion = new ScrollableDiscussion();
        currentScrollableDiscussion.entityId = topic.getTopicId();
        currentScrollableDiscussion.entityType = EntityType.TOPIC.name();

        mysociety_discussions_list = (ListView) rootView.findViewById(R.id.mysociety_discussions_list);
        new DiscussionAdapter(this.getActivity(), discussions);
        mysociety_discussions_list.setAdapter(adapter);
        mysociety_discussions_list.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                int count = mysociety_discussions_list.getCount();
                if (scrollState == SCROLL_STATE_IDLE) {
                    if (mysociety_discussions_list.getLastVisiblePosition() > count - 1) {
                        new FetchDiscussionTask(MySocietyDiscussionsFragment.this.getActivity(), MySocietyDiscussionsFragment.this).execute(currentScrollableDiscussion);
                    }
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });
        return rootView;
    }

    @Override
    public void onPreStart(String taskID) {
        showProgressDialog();
    }

    @Override
    public void onPostComplete(String taskID) {
        hideProgressDialog();
    }

    @Override
    public void onSuccess(String taskID, Object data) {
        Pagination<JDiscussion> page = (Pagination<JDiscussion>)data;
        if(page != null) {
            List<JDiscussion> discussions = page.getResult();
            if(discussions != null && !discussions.isEmpty()) {
                adapter.setDiscussions(discussions);
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onFailure(String taskID, String errorMsg) {
        Snackbar.make(mysociety_discussions_list, errorMsg, Snackbar.LENGTH_LONG).show();
    }

    private void showProgressDialog() {
        MySocietyDiscussionsFragment.this.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog = new ProgressDialog(MySocietyDiscussionsFragment.this.getActivity());
                progressDialog.setMessage("Loading...");
                progressDialog.show();
            }
        });
    }

    private void hideProgressDialog() {
        MySocietyDiscussionsFragment.this.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                    progressDialog = null;
                }
            }
        });
    }
}
