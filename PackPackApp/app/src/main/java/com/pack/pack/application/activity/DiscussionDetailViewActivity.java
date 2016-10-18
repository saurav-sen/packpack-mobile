package com.pack.pack.application.activity;

import android.content.Intent;

import com.pack.pack.application.Constants;
import com.pack.pack.application.adapters.DiscussionDetailAdapter;
import com.pack.pack.application.adapters.IDiscussionAdapter;

/**
 * Created by Saurav on 18-10-2016.
 */
public class DiscussionDetailViewActivity extends DiscussionViewActivity {

    @Override
    protected IDiscussionAdapter createAdapterInstance() {
        return new DiscussionDetailAdapter(this, discussions);
    }

    @Override
    protected void handleCreateAction() {
        Intent intent = new Intent(DiscussionDetailViewActivity.this, DiscussionCreateActivity.class);
        intent.putExtra(Constants.DISCUSSION_IS_REPLY, true);
        intent.putExtra(Constants.DISCUSSION_ENTITY_ID, entityId);
        intent.putExtra(Constants.DISCUSSION_ENTITY_TYPE, entityType);
        startActivityForResult(intent, Constants.DISCUSSION_CREATE_REQUEST_CODE);
    }
}
