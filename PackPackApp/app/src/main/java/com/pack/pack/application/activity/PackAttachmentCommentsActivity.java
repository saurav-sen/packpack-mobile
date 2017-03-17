package com.pack.pack.application.activity;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.pack.pack.application.AppController;
import com.pack.pack.application.R;
import com.pack.pack.application.adapters.PackAttachmentCommentsAdapter;
import com.pack.pack.application.data.util.ApiConstants;
import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIBuilder;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.model.web.JPackAttachment;

/**
 *
 * Created by Saurav on 03-06-2016.
 */
public class PackAttachmentCommentsActivity extends AbstractAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attachment_comments);

        String packAttachmentId = getIntent().getStringExtra(AppController.PACK_ATTACHMENT_ID_KEY);
        if(packAttachmentId != null && !packAttachmentId.trim().isEmpty()) {
            TextView pack_attachment_like_count_box = (TextView) findViewById(R.id.pack_attachment_like_count_box);
            RecyclerView pack_attachment_comment_recycler_view = (RecyclerView) findViewById(R.id.pack_attachment_comment_recycler_view);
            EditText pack_attachment_comment_edit_text = (EditText) findViewById(R.id.pack_attachment_comment_edit_text);
            ImageButton pack_attachment_add_comment_btn = (ImageButton) findViewById(R.id.pack_attachment_add_comment_btn);
            pack_attachment_add_comment_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

            UIElements uiInfo = new UIElements(pack_attachment_like_count_box, pack_attachment_comment_recycler_view);
            new FetchCommentsTask(uiInfo).execute(packAttachmentId);
        }
    }

    private class UIElements {
        private TextView likeCountTextBox;
        private RecyclerView commentRecyclerView;

        UIElements(TextView likeCountTextBox, RecyclerView commentRecyclerView) {
            this.likeCountTextBox = likeCountTextBox;
            this.commentRecyclerView = commentRecyclerView;
        }

        public TextView getLikeCountTextBox() {
            return likeCountTextBox;
        }

        public RecyclerView getCommentRecyclerView() {
            return commentRecyclerView;
        }
    }

    private class FetchCommentsTask extends AsyncTask<String, Void, JPackAttachment> {

        private UIElements uiInfo;

        FetchCommentsTask(UIElements uiInfo) {
            this.uiInfo = uiInfo;
        }

        @Override
        protected JPackAttachment doInBackground(String... ids) {
            JPackAttachment attachment = null;
            if(ids != null && ids.length > 0) {
                String id = ids[0];
                attachment = AppController.getInstance().getPackAttachmentFromCache(id);
                try {
                    if(attachment == null && uiInfo != null) {
                        API api = APIBuilder.create(ApiConstants.BASE_URL)
                                .setAction(COMMAND.GET_PACK_ATTACHMENT_BY_ID)
                                .setOauthToken(AppController.getInstance().getoAuthToken())
                                .addApiParam(APIConstants.PackAttachment.ID, id)
                                .build();
                        attachment = (JPackAttachment) api.execute();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return attachment;
        }

        @Override
        protected void onPostExecute(JPackAttachment attachment) {
            super.onPostExecute(attachment);
            TextView likeCountTextBox = uiInfo.getLikeCountTextBox();
            RecyclerView commentRecyclerView = uiInfo.getCommentRecyclerView();

            likeCountTextBox.setText(String.valueOf(attachment.getLikes()));
            commentRecyclerView.setAdapter(new PackAttachmentCommentsAdapter(attachment.getComments()));
        }
    }
}
