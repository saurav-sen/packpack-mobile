package com.pack.pack.application.fragments;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.pack.pack.application.AppController;
import com.pack.pack.application.R;
import com.pack.pack.application.adapters.TopicViewAdapter;
import com.pack.pack.application.topic.activity.model.TopicEvent;
import com.pack.pack.application.view.util.ViewUtil;
import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIBuilder;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.model.web.JTopic;
import com.pack.pack.model.web.Pagination;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Created by Saurav on 08-04-2016.
 */
public abstract class TopicViewFragment extends Fragment {

    private ProgressDialog progressDialog;

    private ListView listView;

    private TabType tabType;

    private TopicViewAdapter adapter;

    private Pagination<JTopic> page;

    public void setTabType(TabType tabType) {
        this.tabType = tabType;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getViewLayoutId(), container, false);
        listView = (ListView) view.findViewById(getListViewId());
        List<JTopic> topics = new ArrayList<JTopic>();
        adapter = new TopicViewAdapter(getActivity(), topics, getCategoryType());
        listView.setAdapter(adapter);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                int count = listView.getCount();
                if (scrollState == SCROLL_STATE_IDLE) {
                    if (listView.getLastVisiblePosition() > count - 1) {
                        new LoadTopicTask().execute();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });
       new LoadTopicTask().execute();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                handleItemClick();
            }
        });
        return view;
    }

    protected abstract void handleItemClick();

    private String getCategoryType() {
        return tabType.getType();
    }

    private int getViewLayoutId() {
        return ViewUtil.getViewLayoutId(getCategoryType());
    }

    private int getListViewId() {
        return ViewUtil.getListViewId(getCategoryType());
    }

    private class LoadTopicTask extends AsyncTask<Void, Integer, Pagination<JTopic>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
            AppController.getInstance().waitForLoginSuccess();;
        }

        @Override
        protected Pagination<JTopic> doInBackground(Void... voids) {
            return loadNextPage();
        }

        @Override
        protected void onPostExecute(Pagination<JTopic> jTopicPagination) {
            super.onPostExecute(jTopicPagination);
            if(jTopicPagination != null) {
                List<JTopic> topics = jTopicPagination.getResult();
                if(topics != null) {
                    adapter.setTopics(topics);
                    adapter.notifyDataSetChanged();
                }
            }
            hideProgressDialog();
        }

        private Pagination<JTopic> loadNextPage() {
            try {
                showProgressDialog();
                String oAuthToken = AppController.getInstance().getoAuthToken();
                String userId = AppController.getInstance().getUserId();
                String nextLink = page != null ? page.getNextLink() : "";
                String categoryName = getCategoryType();
                API api = APIBuilder.create()
                        .setAction(COMMAND.GET_USER_FOLLOWED_TOPIC_LIST)
                        .setOauthToken(oAuthToken)
                        .addApiParam(APIConstants.User.ID, userId)
                        .addApiParam(APIConstants.PageInfo.PAGE_LINK, nextLink)
                        .addApiParam(APIConstants.Topic.CATEGORY, categoryName)
                        .build();
                page = (Pagination<JTopic>) api.execute();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                hideProgressDialog();
            }
            return page;
        }
    }

    private void showProgressDialog() {
        TopicViewFragment.this.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Loading...");
                progressDialog.show();
            }
        });
    }

    /*private void loadPreviousPage() {
        try {
            String oAuthToken = AppController.getInstance().getoAuthToken();
            String userId = AppController.getInstance().getUserId();
            String nextLink = page != null ? page.getNextLink() : "";
            String categoryName = getCategoryName();
            API api = APIBuilder.create()
                    .setAction(COMMAND.GET_USER_FOLLOWED_TOPIC_LIST)
                    .setOauthToken(oAuthToken)
                    .addApiParam(APIConstants.User.ID, userId)
                    .addApiParam(APIConstants.PageInfo.PAGE_LINK, nextLink)
                    .build();
            page = (Pagination<JTopic>) api.execute();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }*/

    private void hideProgressDialog() {
        TopicViewFragment.this.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(progressDialog != null) {
                    progressDialog.dismiss();
                    progressDialog = null;
                }
            }
        });
    }
}
