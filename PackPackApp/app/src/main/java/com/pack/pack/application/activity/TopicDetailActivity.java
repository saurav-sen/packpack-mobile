package com.pack.pack.application.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.pack.pack.application.AppController;
import com.pack.pack.application.Constants;
import com.pack.pack.application.R;
import com.pack.pack.application.image.loader.DownloadImageTask;
import com.pack.pack.application.topic.activity.model.ParcelableTopic;
import com.pack.pack.model.web.EntityType;

/**
 *
 * @author Saurav
 *
 */
public class TopicDetailActivity extends AppCompatActivity {

    private ParcelableTopic topic;

   @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /*Toolbar toolbar = (Toolbar) findViewById(R.id.topic_detail_toolbar);
        toolbar.inflateMenu(R.menu.inside_topic);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return onOptionsItemSelected(item);
            }
        });*/
        getMenuInflater().inflate(R.menu.inside_topic, menu);
        return true;
    }

   @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.enter_forum:
                Intent intent = new Intent(TopicDetailActivity.this, DiscussionViewActivity.class);
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.topic_detail_toolbar);
        //setActionBar(toolbar);
        setSupportActionBar(toolbar);

        topic = (ParcelableTopic) getIntent().getParcelableExtra(AppController.TOPIC_PARCELABLE_KEY);

        TextView topic_name_text = (TextView) findViewById(R.id.topic_name_text);
        topic_name_text.setText((topic.getTopicName() + "").trim());

        TextView topic_description_text = (TextView) findViewById(R.id.topic_description_text);
        topic_description_text.setText((topic.getDescription() + "").trim());

        ImageView topic_wallpaper_img = (ImageView) findViewById(R.id.topic_wallpaper_img);
        new DownloadImageTask(topic_wallpaper_img, this).execute(topic.getWallpaperUrl());


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
