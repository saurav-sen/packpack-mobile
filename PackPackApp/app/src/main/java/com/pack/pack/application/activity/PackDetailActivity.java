package com.pack.pack.application.activity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.pack.pack.application.AppController;
import com.pack.pack.application.R;
import com.pack.pack.application.adapters.PackAttachmentsAdapter;
import com.pack.pack.application.topic.activity.model.ParcelableAttachment;
import com.pack.pack.application.topic.activity.model.ParcelablePack;
import com.pack.pack.application.topic.activity.model.ParcelableTopic;
import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIBuilder;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.model.web.JPack;
import com.pack.pack.model.web.JPackAttachment;
import com.pack.pack.model.web.Pagination;

import java.util.ArrayList;
import java.util.List;

public class PackDetailActivity extends AppCompatActivity {

    private PackAttachmentsAdapter adapter;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pack_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        TextView activity_pack_title = (TextView) findViewById(R.id.activity_pack_title);
        TextView activity_pack_story = (TextView) findViewById(R.id.activity_pack_story);
        ParcelablePack pack = (ParcelablePack) getIntent().getParcelableExtra(AppController.PACK_PARCELABLE_KEY);
        activity_pack_title.setText(pack.getTitle());
        activity_pack_story.setText(pack.getStory());

        ListView activity_pack_attachments = (ListView) findViewById(R.id.activity_pack_attachments);
        adapter = new PackAttachmentsAdapter(this, new ArrayList<JPackAttachment>(10));
        activity_pack_attachments.setAdapter(adapter);

        new LoadPackDetailTask().execute(pack.getId());
    }

    private class LoadPackDetailTask extends AsyncTask<String, Integer, List<JPackAttachment>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected List<JPackAttachment> doInBackground(String... packIds) {
            List<JPackAttachment> page = null;
            if(packIds == null || packIds.length == 0)
                return page;
            try {
                String packId = packIds[0];
                String oAuthToken = AppController.getInstance().getoAuthToken();
                String userId = AppController.getInstance().getUserId();
                API api = APIBuilder.create().setAction(COMMAND.GET_PACK_BY_ID)
                        .setOauthToken(oAuthToken)
                        //.addApiParam(APIConstants.User.ID, userId)
                        .addApiParam(APIConstants.Pack.ID, packId)
                        .build();
                JPack pack = (JPack) api.execute();
                page = (pack != null ? pack.getAttachments() : new ArrayList<JPackAttachment>(1));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return page;
        }

        @Override
        protected void onPostExecute(List<JPackAttachment> jPackAttachments) {
            super.onPostExecute(jPackAttachments);
            if(jPackAttachments != null) {
                adapter.setAttachments(jPackAttachments);
                adapter.notifyDataSetChanged();
            }
            hideProgressDialog();
        }

        private void showProgressDialog() {
            PackDetailActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog = new ProgressDialog(PackDetailActivity.this);
                    progressDialog.setMessage("Loading...");
                    progressDialog.show();
                }
            });
        }

        private void hideProgressDialog() {
            PackDetailActivity.this.runOnUiThread(new Runnable() {
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
