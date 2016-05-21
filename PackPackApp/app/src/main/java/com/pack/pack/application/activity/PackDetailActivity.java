package com.pack.pack.application.activity;

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
import com.pack.pack.application.topic.activity.model.ParcelablePack;

public class PackDetailActivity extends AppCompatActivity {

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
    }
}
