package com.pack.pack.application.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;

import com.pack.pack.application.AppController;
import com.pack.pack.application.Constants;
import com.pack.pack.application.R;
import com.pack.pack.application.adapters.TopicDetailAdapter;
import com.pack.pack.application.data.util.AbstractNetworkTask;
import com.pack.pack.application.db.DBUtil;
import com.pack.pack.application.db.PaginationInfo;
import com.pack.pack.application.topic.activity.model.ParcelablePack;
import com.pack.pack.application.topic.activity.model.ParcelableTopic;
import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.model.web.EntityType;
import com.pack.pack.model.web.JPack;
import com.pack.pack.model.web.JPackAttachment;
import com.pack.pack.model.web.JTopic;
import com.pack.pack.model.web.Pagination;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.pack.pack.application.AppController.TOPIC_ID_KEY;
import static com.pack.pack.application.AppController.UPLOAD_ENTITY_ID_KEY;
import static com.pack.pack.application.AppController.UPLOAD_ENTITY_TYPE_KEY;

/**
 *
 * @author Saurav
 *
 */
public class InsideTopicActivity extends AppCompatActivity {

    private Pagination<JPack> page;

    private ProgressDialog progressDialog;

    private TopicDetailAdapter adapter;

    private Toolbar toolbar;

    ParcelableTopic topic;

    private String topicId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inside_topic);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InsideTopicActivity.this, CreatePackActivity.class);
                intent.putExtra(TOPIC_ID_KEY, topic.getTopicId());
                startActivityForResult(intent, Constants.PACK_CREATE_REQUEST_CODE);
            }
        });

        adapter = new TopicDetailAdapter(this, new ArrayList<JPack>());
        final ListView listView = (ListView) findViewById(R.id.topic_detail_list);
        listView.setAdapter(adapter);

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                int count = listView.getCount();
                if (scrollState == SCROLL_STATE_IDLE) {
                    if (listView.getLastVisiblePosition() > count - 1) {
                        new LoadPackTask().execute();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });

        topic = (ParcelableTopic) getIntent().getParcelableExtra(AppController.TOPIC_PARCELABLE_KEY);
        this.topicId = topic.getTopicId();
        new LoadPackTask().execute(topic);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_menu, menu);
        MenuItem item0 = menu.findItem(R.id.app_settings);
        if(item0 != null) {
            item0.setVisible(true);
        }
        MenuItem item1 = menu.findItem(R.id.enter_forum);
        if(item1 != null) {
            item1.setVisible(true);
        }
        invalidateOptionsMenu();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.enter_forum:
                Intent intent = new Intent(InsideTopicActivity.this, DiscussionViewActivity.class);
                intent.putExtra(Constants.DISCUSSION_ENTITY_ID, topic.getTopicId());
                intent.putExtra(Constants.DISCUSSION_ENTITY_TYPE, EntityType.TOPIC.name());
                startActivity(intent);
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(TOPIC_ID_KEY, this.topicId);
        outState.putParcelable(AppController.TOPIC_PARCELABLE_KEY, topic);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        this.topicId = savedInstanceState.getString(TOPIC_ID_KEY);
        topic = (ParcelableTopic) savedInstanceState.getParcelable(AppController.TOPIC_PARCELABLE_KEY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Constants.PACK_CREATE_REQUEST_CODE) {
            if(resultCode == RESULT_OK) {
                /*ParcelablePack pack = data.getParcelableExtra(AppController.PACK_PARCELABLE_KEY);
                Intent intent = new Intent(InsideTopicActivity.this, ImageVideoCaptureActivity.class);
                intent.putExtra(TOPIC_ID_KEY, topicId);
                intent.putExtra(UPLOAD_ENTITY_ID_KEY, pack.getId());
                intent.putExtra(UPLOAD_ENTITY_TYPE_KEY, JPackAttachment.class.getName());
                startActivityForResult(intent, Constants.PACK_ATTACHMENT_UPLOAD_REQUEST_CODE);*/
                finish();
                startActivity(getIntent());
            } else if(resultCode == RESULT_CANCELED) {
                String errorMsg = data.getStringExtra(Constants.ERROR_MSG);
                if(errorMsg == null || errorMsg.trim().isEmpty()) {
                    errorMsg = "Cancelled to create new pack";
                }
                Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
            }
        } /*else if(requestCode == Constants.PACK_ATTACHMENT_UPLOAD_REQUEST_CODE) {
            if(resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Cancelled to upload media file(s)", Toast.LENGTH_LONG).show();
            }
            //Intent intent = new Intent(InsideTopicActivity.this, PackDetailActivity.class);
            //intent.putExtra(TOPIC_ID_KEY, topicId);
            //startActivity(intent);
            if(topic != null) {
                //new LoadPackTask().execute(topic);
                finish();
                //Intent intent = new Intent(InsideTopicActivity.this, InsideTopicActivity.class);
                //intent.putExtra(AppController.TOPIC_PARCELABLE_KEY, topic);
                startActivity(getIntent());
            }
        }*/
    }

    private class LoadPackTask extends AbstractNetworkTask<ParcelableTopic, Integer, Pagination<JPack>> {

        public LoadPackTask() {
            super(true, true, InsideTopicActivity.this);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected Map<String, Object> prepareApiParams(ParcelableTopic inputObject) {
            Map<String, Object> apiParams = new HashMap<String, Object>();
            String userId = AppController.getInstance().getUserId();
            apiParams.put(APIConstants.User.ID, userId);
            apiParams.put(APIConstants.Topic.ID, inputObject.getTopicId());
            apiParams.put(APIConstants.Topic.CATEGORY, inputObject.getTopicCategory());
            return apiParams;
        }

        @Override
        protected Pagination<JPack> executeApi(API api) throws Exception {
            return (Pagination<JPack>) api.execute();
        }

        @Override
        protected COMMAND command() {
            return COMMAND.GET_ALL_PACKS_IN_TOPIC;
        }

        @Override
        protected String getFailureMessage() {
            return "Failed to load details";
        }

        @Override
        protected String getContainerIdForObjectStore() {
            return getInputObject().getTopicId();
        }

        @Override
        protected String getPaginationContainerId() {
            return getInputObject().getTopicId() + "::Packs";
        }

        @Override
        protected String getPaginationContainerClassName() {
            return JTopic.class.getName();
        }

        @Override
        protected Pagination<JPack> doRetrieveFromDB(SQLiteDatabase readable, ParcelableTopic inputObject) {
            Pagination<JPack> page = null;
            List<JPack> packs = DBUtil.loadAllJsonModelByContainerId(readable, inputObject.getTopicId(), JPack.class);
            if(packs != null && !packs.isEmpty()) {
                PaginationInfo paginationInfo = DBUtil.loadPaginationInfo(readable, inputObject.getTopicId());
                page = new Pagination<JPack>();
                page.setResult(packs);
                if(paginationInfo != null) {
                    page.setNextLink(paginationInfo.getNextLink());
                    page.setPreviousLink(paginationInfo.getPreviousLink());
                }
            }
            return page;
        }

        @Override
        protected void onPostExecute(Pagination<JPack> jPackPagination) {
            super.onPostExecute(jPackPagination);
            if(jPackPagination != null) {
                List<JPack> packs = jPackPagination.getResult();
                if(packs != null && !packs.isEmpty()) {
                    adapter.setPacks(packs);
                    adapter.notifyDataSetChanged();
                }
            }
            hideProgressDialog();
        }

        private void showProgressDialog() {
            InsideTopicActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog = new ProgressDialog(InsideTopicActivity.this);
                    progressDialog.setMessage("Loading...");
                    progressDialog.show();
                }
            });
        }

        private void hideProgressDialog() {
            InsideTopicActivity.this.runOnUiThread(new Runnable() {
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
}
