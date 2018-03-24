package com.pack.pack.application.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.pack.pack.application.AppController;
import com.pack.pack.application.R;
import com.pack.pack.application.adapters.FullScreenRssFeedViewAdapter;
import com.pack.pack.application.data.util.DownloadFeedImageTask;
import com.pack.pack.application.image.loader.DownloadImageTask;
import com.pack.pack.application.topic.activity.model.ParcellableRssFeed;
import com.pack.pack.application.view.AttachmentViewPager;
import com.pack.pack.model.web.JRssFeed;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.UUID;

public class FullScreenRssFeedViewActivity extends AppCompatActivity {

    private AttachmentViewPager rss_feed_view_pager;
    private FullScreenRssFeedViewAdapter adapter;

    private int currentIndex;

    private List<ParcellableRssFeed> input;

    public static final String PARCELLABLE_FEEDS = "parcel_feeds";

    private static final String LOG_TAG = "FullScreenRssFeedView";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_rss_feed_view);

        currentIndex = getIntent().getIntExtra("index", 0);
        input = getIntent().getParcelableArrayListExtra(PARCELLABLE_FEEDS);
        //input = (List<ParcellableRssFeed>) getIntent().getParcelableExtra(PARCELLABLE_FEEDS);

        rss_feed_view_pager = (AttachmentViewPager) findViewById(R.id.rss_feed_view_pager);
        rss_feed_view_pager.setPaginationSupported(true);

        adapter = new FullScreenRssFeedViewAdapter(this, input);
        rss_feed_view_pager.setAdapter(adapter);
        rss_feed_view_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentIndex = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        adapter.setCurrentIndex(currentIndex);

        rss_feed_view_pager.setCurrentItem(currentIndex);

        /*Button btnClose = (Button) findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });*/
    }
}
