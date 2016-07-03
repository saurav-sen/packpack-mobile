package com.pack.pack.application.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import com.pack.pack.application.Constants;
import com.pack.pack.application.R;
import com.pack.pack.application.adapters.DiscussionAdapter;
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
public class DiscussionViewActivity extends AppCompatActivity implements IAsyncTaskStatusListener {

    private ListView discussionView;

    private String entityId;
    private String entityType;

    private DiscussionAdapter adapter;

    private List<JDiscussion> discussions = new LinkedList<JDiscussion>();

    private ScrollableDiscussion currentScrollableDiscussion;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discussion_view);

        entityId = getIntent().getStringExtra(Constants.DISCUSSION_ENTITY_ID);
        entityType = getIntent().getStringExtra(Constants.DISCUSSION_ENTITY_TYPE);

        currentScrollableDiscussion = new ScrollableDiscussion();
        currentScrollableDiscussion.entityId = entityId;
        currentScrollableDiscussion.entityType = entityType;

        discussionView = (ListView) findViewById(R.id.discussions_list);
        adapter = new DiscussionAdapter(this, discussions);
        discussionView.setAdapter(adapter);
        discussionView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                int count = discussionView.getCount();
                if (scrollState == SCROLL_STATE_IDLE) {
                    if (discussionView.getLastVisiblePosition() > count - 1) {
                        new FetchDiscussionTask(DiscussionViewActivity.this).execute();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DiscussionViewActivity.this, DiscussionCreateActivity.class);
                startActivityForResult(intent, Constants.DISCUSSION_CREATE_REQUEST_CODE);
            }
        });
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
    }
}
