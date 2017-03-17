package com.pack.pack.application.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.pack.pack.application.Constants;
import com.pack.pack.application.R;
import com.pack.pack.application.adapters.DiscussionAdapter;
import com.pack.pack.application.adapters.DiscussionDetailAdapter;
import com.pack.pack.application.adapters.IDiscussionAdapter;
import com.pack.pack.application.data.util.FetchDiscussionTask;
import com.pack.pack.application.data.util.IAsyncTaskStatusListener;
import com.pack.pack.application.data.util.ScrollableDiscussion;
import com.pack.pack.model.web.JDiscussion;
import com.pack.pack.model.web.Pagination;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Saurav
 *
 */
public class DiscussionViewActivity extends AbstractAppCompatActivity implements IAsyncTaskStatusListener {

    private ListView discussionView;

    protected String entityId;
    protected String entityType;

    private IDiscussionAdapter adapter;

    protected List<JDiscussion> discussions = new LinkedList<JDiscussion>();

    private ScrollableDiscussion currentScrollableDiscussion;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discussion_view);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        entityId = getIntent().getStringExtra(Constants.DISCUSSION_ENTITY_ID);
        entityType = getIntent().getStringExtra(Constants.DISCUSSION_ENTITY_TYPE);

        currentScrollableDiscussion = new ScrollableDiscussion();
        currentScrollableDiscussion.entityId = entityId;
        currentScrollableDiscussion.entityType = entityType;

        discussionView = (ListView) findViewById(R.id.discussions_list);
        adapter = createAdapterInstance();
        discussionView.setAdapter(adapter);
        discussionView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                int count = discussionView.getCount();
                if (scrollState == SCROLL_STATE_IDLE) {
                    if (discussionView.getLastVisiblePosition() > count - 1) {
                        new FetchDiscussionTask(DiscussionViewActivity.this, DiscussionViewActivity.this).execute(currentScrollableDiscussion);
                    }
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });
        /*discussionView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                JDiscussion discussion = adapter.getItem(position);
                if(discussion == null)
                    return;
                Intent intent = new Intent(DiscussionViewActivity.this, DiscussionDetailViewActivity.class);
                intent.putExtra(Constants.DISCUSSION_ENTITY_ID, discussion.getId());
                intent.putExtra(Constants.DISCUSSION_ENTITY_TYPE, EntityType.DISCUSSION.name());
                startActivity(intent);
            }
        });*/

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleCreateAction();
            }
        });

        new FetchDiscussionTask(DiscussionViewActivity.this, DiscussionViewActivity.this).execute(currentScrollableDiscussion);
    }

    protected IDiscussionAdapter createAdapterInstance() {
        return new DiscussionAdapter(this, discussions);
    }

    protected void handleCreateAction() {
        Intent intent = new Intent(DiscussionViewActivity.this, DiscussionStartActivity.class);
        intent.putExtra(Constants.DISCUSSION_IS_REPLY, false);
        intent.putExtra(Constants.DISCUSSION_ENTITY_ID, entityId);
        intent.putExtra(Constants.DISCUSSION_ENTITY_TYPE, entityType);
        startActivityForResult(intent, Constants.DISCUSSION_CREATE_REQUEST_CODE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharSequence(Constants.DISCUSSION_ENTITY_ID, entityId);
        outState.putCharSequence(Constants.DISCUSSION_ENTITY_TYPE, entityType);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        entityId = savedInstanceState.getString(Constants.DISCUSSION_ENTITY_ID);
        entityType = savedInstanceState.getString(Constants.DISCUSSION_ENTITY_TYPE);

        currentScrollableDiscussion = new ScrollableDiscussion();
        currentScrollableDiscussion.entityId = entityId;
        currentScrollableDiscussion.entityType = entityType;
    }

    @Override
    public void onPreStart() {
        showProgressDialog();
    }

    @Override
    public void onPostComplete() {
        hideProgressDialog();
    }

    @Override
    public void onSuccess(Object data) {
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
    public void onFailure(String errorMsg) {
        Snackbar.make(discussionView, errorMsg, Snackbar.LENGTH_LONG).show();
    }

    private void showProgressDialog() {
        DiscussionViewActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog = new ProgressDialog(DiscussionViewActivity.this);
                progressDialog.setMessage("Loading...");
                progressDialog.show();
            }
        });
    }

    private void hideProgressDialog() {
        DiscussionViewActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                    progressDialog = null;
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(Constants.DISCUSSION_CREATE_REQUEST_CODE == requestCode) {
            if(RESULT_OK == resultCode) {
                /*ParcelableDiscussion parcelableDiscussion = (ParcelableDiscussion) getIntent().getParcelableExtra(
                        Constants.PARCELLABLE_DISCUSSION_KEY);
                JDiscussion discussion = parcelableDiscussion.convert(parcelableDiscussion);
                if(discussion != null) {
                    adapter.getDiscussions().add(discussion);
                    adapter.notifyDataSetChanged();;
                }*/
                new FetchDiscussionTask(DiscussionViewActivity.this, DiscussionViewActivity.this).execute(currentScrollableDiscussion);
            }
        }
    }
}
