package com.pack.pack.application.activity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.pack.pack.application.AppController;
import com.pack.pack.application.R;
import com.pack.pack.application.adapters.TopicViewAdapter;
import com.pack.pack.application.data.util.AbstractNetworkTask;
import com.pack.pack.application.data.util.ApiConstants;
import com.pack.pack.application.db.DBUtil;
import com.pack.pack.application.db.PaginationInfo;
import com.pack.pack.application.db.UserInfo;
import com.pack.pack.application.fragments.JTopicClickHandler;
import com.pack.pack.application.topic.activity.model.ParcelableTopic;
import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.model.web.JTopic;
import com.pack.pack.model.web.Pagination;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.pack.pack.application.AppController.CREATE_TOPIC_REQUSET_CODE;

public class GenericTopicListActivity extends AppCompatActivity implements JTopicClickHandler {

    private ListView generic_topic_list;

    private String categoryType;

    public static final String CATEGORY_TYPE = "CATEGORY_TYPE";

    private TopicViewAdapter adapter;

    private Pagination<JTopic> page;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generic_topic_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        categoryType = getIntent().getStringExtra(CATEGORY_TYPE);

        generic_topic_list = (ListView) findViewById(R.id.generic_topic_list);

        List<JTopic> topics = new ArrayList<JTopic>();
        adapter = new TopicViewAdapter(this, this, topics, categoryType);
        generic_topic_list.setAdapter(adapter);
        generic_topic_list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                int count = generic_topic_list.getCount();
                if (scrollState == SCROLL_STATE_IDLE) {
                    if (generic_topic_list.getLastVisiblePosition() > count - 1) {
                        new LoadTopicTask().execute(AppController.getInstance().getUserId());
                    }
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });

        generic_topic_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //JTopic topic = (JTopic) adapterView.getAdapter().getItem(i);
                //handleItemClick(topic);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GenericTopicListActivity.this, TopicCreateActivity.class);
                intent.putExtra(TopicCreateActivity.INTENDED_CATEGORY, categoryType);
                startActivityForResult(intent, CREATE_TOPIC_REQUSET_CODE);
            }
        });

        new LoadTopicTask().execute(AppController.getInstance().getUserId());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_menu, menu);
        /*MenuItem item0 = menu.findItem(R.id.app_settings);
        if(item0 != null) {
            item0.setVisible(true);
        }*/
        /*MenuItem item1 = menu.findItem(R.id.enter_forum);
        if(item1 != null) {
            item1.setVisible(false);
        }*/
        invalidateOptionsMenu();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.app_settings:
                Intent intent = new Intent(GenericTopicListActivity.this, SettingsActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }

    @Override
    public void doHandleItemClick(JTopic topic) {
        if(ApiConstants.FAMILY.equals(categoryType)) {
            ParcelableTopic parcel = new ParcelableTopic(topic);
            //Intent intent = new Intent(this, MyFamilyTopicDetailActivity.class);
            Intent intent = new Intent(this, MyFamilyActivity.class);
            intent.putExtra(AppController.TOPIC_PARCELABLE_KEY, parcel);
            startActivity(intent);
        } if(ApiConstants.SOCIETY.equals(categoryType)) {
            ParcelableTopic parcel = new ParcelableTopic(topic);
            //Intent intent = new Intent(this, MySocietyTopicDetailActivity.class);
            Intent intent = new Intent(this, MySocietyActivity.class);
            intent.putExtra(AppController.TOPIC_PARCELABLE_KEY, parcel);
            startActivity(intent);
        }
    }

    private class LoadTopicTask extends AbstractNetworkTask<String, Integer, Pagination<JTopic>> {

        private String errorMsg;

        public LoadTopicTask() {
            super(false, false, GenericTopicListActivity.this, false);
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

        @Override
        protected COMMAND command() {
            return COMMAND.GET_USER_FOLLOWED_TOPIC_LIST;
        }

        @Override
        protected Pagination<JTopic> executeApi(API api) throws Exception {
            try {
                showProgressDialog();
                page = (Pagination<JTopic>) api.execute();
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
            String nextLink = page != null ? page.getNextLink() : "";
            String categoryName = categoryType;
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
}
