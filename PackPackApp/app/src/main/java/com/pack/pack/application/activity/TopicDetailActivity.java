package com.pack.pack.application.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.pack.pack.application.AppController;
import com.pack.pack.application.R;
import com.pack.pack.application.image.loader.DownloadImageTask;
import com.pack.pack.application.topic.activity.model.ParcelableTopic;

/**
 *
 * @author Saurav
 *
 */
public class TopicDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.topic_detail_toolbar);
        setSupportActionBar(toolbar);

        final ParcelableTopic topic = (ParcelableTopic) getIntent().getParcelableExtra(AppController.TOPIC_PARCELABLE_KEY);

        TextView topic_name_text = (TextView) findViewById(R.id.topic_name_text);
        topic_name_text.setText((topic.getTopicName() + "").trim());

        TextView topic_description_text = (TextView) findViewById(R.id.topic_description_text);
        topic_description_text.setText((topic.getDescription() + "").trim());

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
