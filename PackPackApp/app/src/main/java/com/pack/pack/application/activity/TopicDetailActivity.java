package com.pack.pack.application.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.pack.pack.application.AppController;
import com.pack.pack.application.R;
import com.pack.pack.application.adapters.TopicDetailAdapter;
import com.pack.pack.application.image.loader.DownloadImageTask;
import com.pack.pack.application.topic.activity.model.ParcelableTopic;
import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIBuilder;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.model.web.JPack;
import com.pack.pack.model.web.JTopic;
import com.pack.pack.model.web.Pagination;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class TopicDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.topic_detail_toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.upload_pack);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        final ParcelableTopic topic = (ParcelableTopic) getIntent().getParcelableExtra(AppController.TOPIC_PARCELABLE_KEY);

        TextView topic_description_text = (TextView) findViewById(R.id.topic_description_text);
        topic_description_text.setText(topic.getDescription() + "");

        ImageView topic_wallpaper_img = (ImageView) findViewById(R.id.topic_wallpaper_img);
        new DownloadImageTask(topic_wallpaper_img).execute(topic.getWallpaperUrl());


        Button enterTopic = (Button) findViewById(R.id.enter_topic_detail);
        enterTopic.setText("Enter");
        enterTopic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TopicDetailActivity.this, InsideTopicActivity.class);
                intent.putExtra(AppController.TOPIC_PARCELABLE_KEY, topic);
                startActivity(intent);
            }
        });

        Button followTopic = (Button) findViewById(R.id.follow_not_follow_topic);
        followTopic.setText("Follow");
    }
}
