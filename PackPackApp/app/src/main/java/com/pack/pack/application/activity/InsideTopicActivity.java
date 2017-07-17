package com.pack.pack.application.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
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
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

//import com.google.firebase.messaging.FirebaseMessaging;
import com.pack.pack.application.AppController;
import com.pack.pack.application.Constants;
import com.pack.pack.application.R;
import com.pack.pack.application.adapters.TopicDetailAdapter;
import com.pack.pack.application.data.cache.InMemory;
import com.pack.pack.application.data.util.AbstractNetworkTask;
import com.pack.pack.application.data.util.IAsyncTaskStatusListener;
import com.pack.pack.application.data.util.LoadPackTask;
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
import com.pack.pack.model.web.JUser;
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
public class InsideTopicActivity extends AbstractAppCompatActivity {

    private Pagination<JPack> page;

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

        topic = (ParcelableTopic) getIntent().getParcelableExtra(AppController.TOPIC_PARCELABLE_KEY);
        this.topicId = topic.getTopicId();

        InMemory.INSTANCE.add(topic);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InsideTopicActivity.this, CreatePackActivity.class);
                intent.putExtra(TOPIC_ID_KEY, topic.getTopicId());
                startActivityForResult(intent, Constants.PACK_CREATE_REQUEST_CODE);
                /*if(topic.isFollowing()) {
                    Intent intent = new Intent(InsideTopicActivity.this, CreatePackActivity.class);
                    intent.putExtra(TOPIC_ID_KEY, topic.getTopicId());
                    startActivityForResult(intent, Constants.PACK_CREATE_REQUEST_CODE);
                } else {
                    Toast.makeText(InsideTopicActivity.this, "You are not following the topic.", Toast.LENGTH_LONG).show();
                }*/
            }
        });

        if(topic.isFollowing()) {
            fab.setVisibility(View.VISIBLE);
        } else {
            fab.setVisibility(View.GONE);
        }

        adapter = new TopicDetailAdapter(this, new ArrayList<JPack>());
        final ListView listView = (ListView) findViewById(R.id.topic_detail_list);
        listView.setAdapter(adapter);

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                int count = listView.getCount();
                if (scrollState == SCROLL_STATE_IDLE) {
                    if (listView.getLastVisiblePosition() > count - 1) {
                        new LoadPackTask(InsideTopicActivity.this, adapter).execute();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });

        new LoadPackTask(InsideTopicActivity.this, adapter).execute(topic);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.inside_topic_menu, menu);
        MenuItem topic_follow = menu.findItem(R.id.topic_follow);
        if(topic_follow != null) {
            if(!topic.isFollowing()) {
                topic_follow.setVisible(true);
            } else {
                topic_follow.setVisible(false);
            }
        }
        MenuItem topic_neglect = menu.findItem(R.id.topic_neglect);
        if(topic_neglect != null) {
            if(!topic.isFollowing()) {
                topic_neglect.setVisible(false);
            } else {
                topic_neglect.setVisible(true);
            }
        }
        MenuItem topic_details = menu.findItem(R.id.topic_details);
        if(topic_details != null) {
            topic_details.setVisible(true);
        }
        /*MenuItem item1 = menu.findItem(R.id.enter_forum);
        if(item1 != null) {
            item1.setVisible(true);
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
            /*case R.id.enter_forum:
                Intent intent = new Intent(InsideTopicActivity.this, DiscussionViewActivity.class);
                intent.putExtra(Constants.DISCUSSION_ENTITY_ID, topic.getTopicId());
                intent.putExtra(Constants.DISCUSSION_ENTITY_TYPE, EntityType.TOPIC.name());
                startActivity(intent);
                break;*/
            case R.id.topic_follow:
                FollowTopicTask task0 = new FollowTopicTask(InsideTopicActivity.this, topic, COMMAND.FOLLOW_TOPIC);
                task0.addListener(new FollowTopicTaskListener(task0.getTaskID(), true));
                task0.execute(topic.getTopicId());
                break;
            case R.id.topic_neglect:
                FollowTopicTask task1 = new FollowTopicTask(InsideTopicActivity.this, topic, COMMAND.NEGLECT_TOPIC);
                task1.addListener(new FollowTopicTaskListener(task1.getTaskID(), false));
                task1.execute(topic.getTopicId());
                break;
            case R.id.topic_details:
                Intent intent_0 = new Intent(InsideTopicActivity.this, TopicDetailActivity.class);
                intent_0.putExtra(AppController.TOPIC_PARCELABLE_KEY, topic);
                startActivity(intent_0);
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
                if(Build.VERSION.SDK_INT >= 11) {
                    recreate();
                } else {
                    finish();
                    startActivity(getIntent());
                }
            } else if(resultCode == RESULT_CANCELED) {
                String errorMsg = data != null ? data.getStringExtra(Constants.ERROR_MSG) : null;
                if(errorMsg == null || errorMsg.trim().isEmpty()) {
                    errorMsg = "You have cancelled to create new album";
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

    private class FollowTopicTask extends AbstractNetworkTask<String, Integer, Void> {

        private ParcelableTopic topic;

        private COMMAND command;

        public FollowTopicTask(Context context, ParcelableTopic topic, COMMAND command) {
            super(false, false, false, context, true, true);
            this.topic = topic;
        }

        @Override
        protected String getContainerIdForObjectStore() {
            return null;
        }

        @Override
        protected Void executeApi(API api) throws Exception {
            api.execute();
            return null;
        }

        @Override
        protected COMMAND command() {
            /*if(!topic.isFollowing()) {
                return COMMAND.FOLLOW_TOPIC;
            }
            return COMMAND.NEGLECT_TOPIC;*/
            return command;
        }

        @Override
        protected void fireOnSuccess(Object data) {
            topic.setIsFollowing(!topic.isFollowing());
            super.fireOnSuccess(data);
        }

        @Override
        protected Map<String, Object> prepareApiParams(String inputObject) {
            JUser user = AppController.getInstance().getUser();
            Map<String, Object> apiParams = new HashMap<String, Object>();
            apiParams.put(APIConstants.User.ID, user.getId());
            apiParams.put(APIConstants.Topic.ID, inputObject);
            return apiParams;
        }

        @Override
        protected String getFailureMessage() {
            return "Failed following topic command";
        }
    }

    private class FollowTopicTaskListener implements IAsyncTaskStatusListener {

        private String taskID;

        private boolean isFollow;

        FollowTopicTaskListener(String taskID, boolean isFollow) {
            this.taskID = taskID;
            this.isFollow = isFollow;
        }

        @Override
        public void onPreStart(String taskID) {

        }

        @Override
        public void onSuccess(String taskID, Object data) {
            InsideTopicActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    topic.setIsFollowing(isFollow);
                    if (isFollow) {
                       // FirebaseMessaging.getInstance().subscribeToTopic(topic.getTopicId());
                        //InsideTopicActivity.this.getMe
                        //imageButton.setImageResource(R.drawable.follow_topic);
                    } else {
                       // FirebaseMessaging.getInstance().unsubscribeFromTopic(topic.getTopicId());
                        //imageButton.setImageResource(R.drawable.neglect_topic);
                    }
                }
            });
        }

        @Override
        public void onFailure(String taskID, String errorMsg) {

        }

        @Override
        public void onPostComplete(String taskID) {

        }
    }
}
