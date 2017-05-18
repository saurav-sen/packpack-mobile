package com.pack.pack.application.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.pack.pack.application.AppController;
import com.pack.pack.application.adapters.TopicViewAdapter;
import com.pack.pack.application.data.util.AbstractNetworkTask;
import com.pack.pack.application.data.util.IAsyncTaskStatusListener;
import com.pack.pack.application.db.DBUtil;
import com.pack.pack.application.db.PaginationInfo;
import com.pack.pack.application.db.UserInfo;
import com.pack.pack.application.topic.activity.model.ParcelableTopic;
import com.pack.pack.application.view.util.ViewUtil;
import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.common.util.CommonConstants;
import com.pack.pack.model.web.JTopic;
import com.pack.pack.model.web.Pagination;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Saurav on 08-04-2016.
 */
public abstract class TopicViewFragment extends Fragment {

    // private ProgressDialog progressDialog;

    private ListView listView;

    private TabType tabType;

    private TopicViewAdapter adapter;

    private Map<String, PageInfo> pagesMap = new HashMap<String, PageInfo>();

    public void setTabType(TabType tabType) {
        this.tabType = tabType;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //outState.
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
                        String[] categories = getSimilarCategoriesList();
                        TopicLoadListener listener = new TopicLoadListener(categories.length);
                        for(String category : categories) {
                            new LoadTopicTask(category).addListener(listener).execute();
                        }
                    }
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                JTopic topic = (JTopic) adapterView.getAdapter().getItem(i);
                // JTopic topic = (JTopic) listView.getSelectedItem();
                handleItemClick(topic);
            }
        });
        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser) {
            //page = null;
            String[] categories = getSimilarCategoriesList();
            TopicLoadListener listener = new TopicLoadListener(categories.length);
            //boolean found = false;
            int count = adapter == null ? 0 : adapter.getCount();
            for(String category : categories) {
                PageInfo pageInfo = pagesMap.get(category);
                //pagesMap.remove(category);
                if(count == 0) {
                    pagesMap.remove(category);
                    pageInfo = null;
                }
                if(pageInfo != null && (pageInfo.getNextLink() == null
                        || CommonConstants.END_OF_PAGE.equalsIgnoreCase(pageInfo.getNextLink()))) {
                    continue;
                }
                //found = true;
                new LoadTopicTask(category).addListener(listener).execute(AppController.getInstance().getUserId());
            }
        }
    }

    protected void openDetailActivity(String parcelKey, ParcelableTopic parcel, Class<?> activityClass) {
        Intent intent = new Intent(getContext(), activityClass);
        intent.putExtra(parcelKey, parcel);
        startActivity(intent);
    }

    protected abstract void handleItemClick(JTopic topic);

    private String getCategoryType() {
        if(tabType == null) {
            tabType = initTabType();
        }
        return tabType.getType();
    }

    private String[] getSimilarCategoriesList() {
        if(tabType == null) {
            tabType = initTabType();
        }
        return tabType.getSimilarCategories();
    }

    protected abstract TabType initTabType();

    private int getViewLayoutId() {
        return ViewUtil.getViewLayoutId(getCategoryType());
    }

    private int getListViewId() {
        return ViewUtil.getListViewId(getCategoryType());
    }

    private class TopicLoadListener implements IAsyncTaskStatusListener {

        private int countOfTasks;

        TopicLoadListener(int countOfTasks) {
            this.countOfTasks = countOfTasks;
        }

        @Override
        public void onPreStart(String taskID) {

        }

        @Override
        public void onFailure(String taskID, String errorMsg) {
            countOfTasks--;
            if(countOfTasks == 0) {
                adapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onSuccess(String taskID, Object data) {
            countOfTasks--;
            if(countOfTasks == 0) {
                adapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onPostComplete(String taskID) {

        }
    }

    private class LoadTopicTask extends AbstractNetworkTask<String, Integer, Pagination<JTopic>> {

        private String errorMsg;

        private String category;

        public LoadTopicTask(String category) {
            super(false, false, getActivity(), false);
            this.category = category;
        }

        /*@Override
        protected Pagination<JTopic> doInBackground(Void... inputObjects) {
            *//*if(xes == null || xes.length == 0)
                return null;*//*
            //setInputObject(null);
            Pagination<JTopic> page = null;
            page = doRetrieveFromDB(getSquillDbHelper().getReadableDatabase(), getInputObject());
            if(page == null) {
                page = doExecuteInBackground(null);
            }
            return page;
        }*/

        @Override
        protected String getFailureMessage() {
            return errorMsg;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
            AppController.getInstance().waitForLoginSuccess();;
        }

        @Override
        protected String getPaginationContainerId() {
            return AppController.getInstance().getUserId();
        }

        @Override
        protected String getPaginationContainerClassName() {
            return UserInfo.class.getName() + ":TOPIC";
        }

        @Override
        protected String getContainerIdForObjectStore() {
            return AppController.getInstance().getUserId();
        }

        @Override
        protected Pagination<JTopic> doRetrieveFromDB(SQLiteDatabase readable, String inputObject) {
            Pagination<JTopic> page = null;
            List<JTopic> result = DBUtil.loadAllJsonModelByContainerId(readable,
                    AppController.getInstance().getUserId(), JTopic.class);
            String userId = inputObject;//AppController.getInstance().getUserId();
            if(result != null && !result.isEmpty()) {
                page = new Pagination<JTopic>();
                PaginationInfo paginationInfo = DBUtil.loadPaginationInfo(
                        readable, userId, getPaginationContainerClassName());
                if(paginationInfo != null) {
                    page.setNextLink(paginationInfo.getNextLink());
                    page.setPreviousLink(paginationInfo.getPreviousLink());
                }
                page.setResult(result);
            }
            return page;
        }

        @Override
        protected void onPostExecute(Pagination<JTopic> jTopicPagination) {
            if(jTopicPagination != null) {
                List<JTopic> topics = jTopicPagination.getResult();
                if(topics != null) {
                    adapter.setTopics(topics);
                    //adapter.notifyDataSetChanged();
                }
            }
            super.onPostExecute(jTopicPagination);
            hideProgressDialog();
        }

        @Override
        protected COMMAND command() {
            return COMMAND.GET_USER_FOLLOWED_TOPIC_LIST;
        }

        @Override
        protected Pagination<JTopic> executeApi(API api) throws Exception {
            Pagination<JTopic> page = null;
            try {
                showProgressDialog();
                page = (Pagination<JTopic>) api.execute();
                pagesMap.put(this.category, new PageInfo(page.getNextLink(), page.getPreviousLink()));
            } catch (Exception e) {
                errorMsg = e.getMessage();
            } finally {
                //hideProgressDialog();
            }
            return page;
        }

        @Override
        protected Map<String, Object> prepareApiParams(String inputObject) {
            Map<String, Object> apiParams = new HashMap<String, Object>();
            String userId = inputObject;//AppController.getInstance().getUserId();
            apiParams.put(APIConstants.User.ID, userId);
            //Pagination<JTopic> page = pagesMap.get(this.category);
            PageInfo page = pagesMap.get(this.category);
            String nextLink = page != null ? page.getNextLink() : "";
            nextLink = nextLink + "";
            if("NOT_FOLLOWINGEND_OF_PAGE".equalsIgnoreCase(nextLink)) {
                nextLink = "FIRST_PAGE";
            }
            //String categoryName = getCategoryType();
            String categoryName = this.category;
            apiParams.put(APIConstants.PageInfo.PAGE_LINK, nextLink);
            apiParams.put(APIConstants.Topic.CATEGORY, categoryName);
            return apiParams;
        }
    }

    private void showProgressDialog() {
        /*TopicViewFragment.this.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Loading...");
                progressDialog.show();
            }
        });*/
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
        /*TopicViewFragment.this.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(progressDialog != null) {
                    progressDialog.dismiss();
                    progressDialog = null;
                }
            }
        });*/
    }

    private class PageInfo {

        private String nextLink;

        private String prevLink;

        PageInfo(String nextLink, String prevLink) {
            this.nextLink = nextLink;
            this.prevLink = prevLink;
        }

        private String getNextLink() {
            return nextLink;
        }

        private String getPrevLink() {
            return prevLink;
        }
    }
}
