package com.pack.pack.application.fragments;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.pack.pack.application.AppController;
import com.pack.pack.application.R;
import com.pack.pack.application.adapters.HomeActivityAdapter;
import com.pack.pack.application.data.util.ApiConstants;
import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIBuilder;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.model.web.JRssFeed;
import com.pack.pack.model.web.Pagination;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Saurav
 *
 */
public class HomeViewFragment extends Fragment {

    private TabType tabType;

    private ProgressDialog progressDialog;

    private String nextLink = "FIRST_PAGE";
    private String prevLink = null;

    private static final String LOG_TAG = "HomeViewFragment";

    private HomeActivityAdapter adapter;

    private ListView listView;

    public void setTabType(TabType tabType) {
       this.tabType = tabType;
   }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
       // progressDialog = new ProgressDialog(getActivity());
       // progressDialog.setMessage("Loading...");
        //progressDialog.show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View view = (View) inflater.inflate(R.layout.home_topic_view, container, false);
        listView = (ListView) view.findViewById(R.id.home_events);
        List<JRssFeed> feeds = new LinkedList<JRssFeed>();
        adapter = new HomeActivityAdapter(getActivity(), feeds);
        listView.setAdapter(adapter);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                int count = listView.getCount();
                if (scrollState == SCROLL_STATE_IDLE) {
                    if (listView.getLastVisiblePosition() > count - 1 && !"END_OF_PAGE".equals(nextLink)) {
                        new RSSFeedTask().execute(nextLink);
                    }
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });
        new RSSFeedTask().execute(!"END_OF_PAGE".equals(nextLink) ? nextLink : prevLink);
        //hideProgressDialog();
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hideProgressDialog();
    }

    private void hideProgressDialog() {
        if(progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    private class RSSFeedTask extends AsyncTask<String, Integer, Pagination<JRssFeed>> {

        private String pageLink;

        public RSSFeedTask() {
        }

        @Override
        protected Pagination<JRssFeed> doInBackground(String... pageLinks) {
            if(pageLinks == null || pageLinks.length == 0)
                return null;
            pageLink = pageLinks[0];
            Pagination<JRssFeed> page = null;
            try {
                String userId = AppController.getInstance().getUserId();
                String oAuthToken = AppController.getInstance().getoAuthToken();
                API api = APIBuilder.create(ApiConstants.BASE_URL)
                        .setAction(COMMAND.GET_ALL_PROMOTIONAL_FEEDS)
                        .setOauthToken(oAuthToken)
                        .addApiParam(APIConstants.User.ID, userId)
                        .addApiParam(APIConstants.PageInfo.PAGE_LINK, pageLink)
                        .build();
                page = (Pagination<JRssFeed>) api.execute();
            } catch (Exception e) {
                Log.d(LOG_TAG, e.getMessage());
            }
            return page;
        }

        @Override
        protected void onPostExecute(Pagination<JRssFeed> page) {
            super.onPostExecute(page);
            if(page != null) {
                nextLink = page.getNextLink();
                prevLink = page.getPreviousLink();
                List<JRssFeed> list = page.getResult();
                adapter.getFeeds().addAll(list);
                adapter.notifyDataSetChanged();
            }
        }
    }
}
