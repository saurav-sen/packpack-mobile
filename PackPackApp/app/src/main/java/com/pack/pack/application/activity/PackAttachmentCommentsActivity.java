package com.pack.pack.application.activity;

import android.content.Context;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.pack.pack.application.AppController;
import com.pack.pack.application.R;
import com.pack.pack.application.adapters.PackAttachmentCommentsAdapter;
import com.pack.pack.application.data.util.AbstractNetworkTask;
import com.pack.pack.application.data.util.ApiConstants;
import com.pack.pack.application.data.util.IAsyncTaskStatusListener;
import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIBuilder;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.model.web.JComment;
import com.pack.pack.model.web.JComments;
import com.pack.pack.model.web.JPackAttachment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * Created by Saurav on 03-06-2016.
 */
public class PackAttachmentCommentsActivity extends AbstractAppCompatActivity {

    private static final String LOG_TAG = "AttachmentComments";

    private PackAttachmentCommentsAdapter adapter;

    private EditText pack_attachment_comment_edit_text;

    private String packAttachmentId;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attachment_comments);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar == null) {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            actionBar = getSupportActionBar();
        }

        actionBar.setDisplayHomeAsUpEnabled(true);

        this.packAttachmentId = getIntent().getStringExtra(AppController.PACK_ATTACHMENT_ID_KEY);
        if(packAttachmentId != null && !packAttachmentId.trim().isEmpty()) {
            pack_attachment_comment_edit_text = (EditText) findViewById(R.id.pack_attachment_comment_edit_text);
            Button pack_attachment_add_comment_btn = (Button) findViewById(R.id.pack_attachment_add_comment_btn);
            pack_attachment_add_comment_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String userId = AppController.getInstance().getUserId();
                    Editable editable = PackAttachmentCommentsActivity.this.pack_attachment_comment_edit_text.getText();
                    if(editable == null) {
                        Snackbar.make(PackAttachmentCommentsActivity.this.pack_attachment_comment_edit_text,
                                "Please add some text", Snackbar.LENGTH_LONG).show();
                        return;
                    }
                    String comment = editable.toString().trim();
                    if(comment.isEmpty()) {
                        Snackbar.make(PackAttachmentCommentsActivity.this.pack_attachment_comment_edit_text,
                                "Please add some text", Snackbar.LENGTH_LONG).show();
                        return;
                    }
                    AddCommentInfo addCommentInfo = new AddCommentInfo(comment,
                            PackAttachmentCommentsActivity.this.packAttachmentId, userId);
                    new AddCommentToAttachmentTask(PackAttachmentCommentsActivity.this).addListener(
                            new AddCommentStatusListener(adapter))
                            .execute(addCommentInfo);
                }
            });

            ListView attachment_comments_list = (ListView) findViewById(R.id.attachment_comments_list);
            adapter = new PackAttachmentCommentsAdapter(this);
            attachment_comments_list.setAdapter(adapter);

//            AbstractNetworkTask fetchCommentsTask = new FetchCommentsTask(this);
//            fetchCommentsTask.addListener(new CommentsLoadStatusListener(adapter));
//            fetchCommentsTask.execute(packAttachmentId);

            new FetchCommentsTask(this).addListener(
                    new CommentsLoadStatusListener(adapter))
                    .execute(packAttachmentId);
        }
    }

    private class CommentsLoadStatusListener implements IAsyncTaskStatusListener {

        private PackAttachmentCommentsAdapter commentsAdapter;

        private String taskID;

        CommentsLoadStatusListener(PackAttachmentCommentsAdapter commentsAdapter) {
            this.commentsAdapter = commentsAdapter;
        }

        @Override
        public void onPostComplete(String taskID) {

        }

        @Override
        public void onPreStart(String taskID) {
            this.taskID = taskID;
        }

        @Override
        public void onFailure(String taskID, String errorMsg) {
            Snackbar.make(pack_attachment_comment_edit_text, errorMsg, Snackbar.LENGTH_LONG).show();
        }

        @Override
        public void onSuccess(String taskID, Object data) {
            if(!this.taskID.equals(taskID)) {
                return;
            }
            JComments comments = (JComments) data;
            if(comments != null) {
                Collections.sort(comments.getComments(), new Comparator<JComment>() {
                    @Override
                    public int compare(JComment lhs, JComment rhs) {
                        return (int) (lhs.getDateTime() - rhs.getDateTime());
                    }
                });
                commentsAdapter.getComments().addAll(comments.getComments());
                commentsAdapter.notifyDataSetChanged();
            }
        }
    }

    private class FetchCommentsTask extends AbstractNetworkTask<String, Void, JComments> {

        private String errorMsg;

        @Override
        protected String getFailureMessage() {
            return errorMsg;
        }

        @Override
        protected String getContainerIdForObjectStore() {
            return null;
        }

        FetchCommentsTask(Context context) {
            super(false, false, false, context, false, true);
        }

        @Override
        protected JComments executeApi(API api) throws Exception {
            JComments comments = null;
            try {
                comments = (JComments) api.execute();
            } catch (Exception e) {
                Log.e(LOG_TAG, "Fetch comments from attachment has failed", e);
                errorMsg = "Failed to load comments";
            }
            return comments;
        }

        @Override
        protected Map<String, Object> prepareApiParams(String inputObject) {
            Map<String, Object> apiParams = new HashMap<String, Object>();
            apiParams.put(APIConstants.PackAttachment.ID, inputObject);
            return apiParams;
        }

        @Override
        protected COMMAND command() {
            return COMMAND.GET_ALL_ATTACHMENT_COMMENTS;
        }
    }

    private class AddCommentInfo {

        private String comment;

        private String packAttachmentId;

        private String fromUserId;

        AddCommentInfo(String comment, String packAttachmentId, String fromUserId) {
            this.comment = comment;
            this.packAttachmentId = packAttachmentId;
            this.fromUserId = fromUserId;
        }

        public String getComment() {
            return comment;
        }

        public String getPackAttachmentId() {
            return packAttachmentId;
        }

        public String getFromUserId() {
            return fromUserId;
        }
    }

    private class AddCommentToAttachmentTask extends AbstractNetworkTask<AddCommentInfo, Integer, JComment> {

        private String errorMsg;

        AddCommentToAttachmentTask(Context context) {
            super(false, false, false, context, true, true);
        }

        @Override
        protected String getFailureMessage() {
            return errorMsg;
        }

        @Override
        protected String getContainerIdForObjectStore() {
            return null;
        }

        @Override
        protected Map<String, Object> prepareApiParams(AddCommentInfo inputObject) {
            Map<String, Object> apiParams = new HashMap<String, Object>();
            apiParams.put(APIConstants.PackAttachment.ID, inputObject.getPackAttachmentId());
            apiParams.put(APIConstants.Comment.FROM_USER_ID, inputObject.getFromUserId());
            apiParams.put(APIConstants.Comment.COMMENT, inputObject.getComment());
            return apiParams;
        }

        @Override
        protected COMMAND command() {
            return COMMAND.ADD_COMMENT_TO_PACK_ATTACHMENT;
        }

        @Override
        protected JComment executeApi(API api) throws Exception {
            JComment comment = null;
            try {
                comment = (JComment) api.execute();
            } catch (Exception e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                errorMsg = "Failed Adding Comment";
            }
            return comment;
        }
    }

    private class AddCommentStatusListener implements IAsyncTaskStatusListener {

        private PackAttachmentCommentsAdapter commentsAdapter;

        private String taskID;

        AddCommentStatusListener(PackAttachmentCommentsAdapter commentsAdapter) {
            this.commentsAdapter = commentsAdapter;
        }

        @Override
        public void onPostComplete(String taskID) {

        }

        @Override
        public void onPreStart(String taskID) {
            this.taskID = taskID;
        }

        @Override
        public void onFailure(String taskID, String errorMsg) {
            Snackbar.make(pack_attachment_comment_edit_text, errorMsg, Snackbar.LENGTH_LONG).show();
        }

        @Override
        public void onSuccess(String taskID, Object data) {
            if(!this.taskID.equals(taskID)) {
                return;
            }
            JComment comment = (JComment) data;
            if(comment != null) {
                commentsAdapter.getComments().add(comment);
                commentsAdapter.notifyDataSetChanged();
            }
            pack_attachment_comment_edit_text.setText("");
        }
    }
}
