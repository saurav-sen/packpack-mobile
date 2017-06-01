package com.pack.pack.application.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.design.widget.Snackbar;
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
import com.pack.pack.application.data.util.DownloadFeedImageTask;
import com.pack.pack.application.image.loader.DownloadImageTask;
import com.pack.pack.application.topic.activity.model.ParcellableRssFeed;
import com.pack.pack.model.web.JRssFeed;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class FullScreenRssFeedViewActivity extends AppCompatActivity {

    private ParcellableRssFeed input;

    public static final String PARCELLABLE_FEED = "parcel_feed";

    private static final String LOG_TAG = "FullScreenRssFeedView";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_rss_feed_view);

        input = (ParcellableRssFeed) getIntent().getParcelableExtra(PARCELLABLE_FEED);

        ImageView feed_imgDisplay = (ImageView) findViewById(R.id.feed_imgDisplay);
        //ImageView feed_imgDisplay_play = (ImageView) findViewById(R.id.feed_imgDisplay_play);
        TextView feed_titleText = (TextView) findViewById(R.id.feed_titleText);
        TextView feed_descriptionText = (TextView) findViewById(R.id.feed_descriptionText);

        ImageButton feed_share = (ImageButton) findViewById(R.id.feed_share);
        //Button feed_share = (Button) findViewById(R.id.feed_share);
        feed_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File file = createScreenCast(view);
                shareImage(file);
            }
        });
        TextView feed_creator = (TextView) findViewById(R.id.feed_creator);

        if(input != null) {
            String imageUrl = input.getOgImage();
            if(imageUrl != null) {
                new DownloadFeedImageTask(feed_imgDisplay, 850, 850, this, null)
                        .execute(imageUrl);
            }
            feed_descriptionText.setText(input.getOgDescription());
            feed_titleText.setText(input.getOgTitle());
            String videoUrl = input.getVideoUrl();
            if(videoUrl != null && !videoUrl.trim().isEmpty()) {
                //feed_imgDisplay_play.setVisibility(View.VISIBLE);
            }
        }
    }

    private void shareImage(File file) {
        if(file == null || !file.exists()) {
            return;
        }

        Uri uri = Uri.fromFile(file);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/*");

        intent.putExtra(Intent.EXTRA_SUBJECT, "");
        intent.putExtra(Intent.EXTRA_TEXT, "Download SQUILL @ " + AppController.getInstance().getApkUrl());
        intent.putExtra(Intent.EXTRA_STREAM, uri);

        startActivity(Intent.createChooser(intent, "Shared from SQUILL"));
    }

    private File createScreenCast(View view) {
        File file = null;
        View rootView = getWindow().getDecorView().findViewById(R.id.full_screen_rss_feed_layout);
        View screenView = rootView.getRootView();
        screenView.setDrawingCacheEnabled(true);
        Bitmap screenCast = Bitmap.createBitmap(screenView.getDrawingCache());
        screenView.setDrawingCacheEnabled(false);
        //String dirPath = TopicDetailActivity.this.getCacheDir().getAbsolutePath() + File.separator + "screencasts";
        String dirPath = Environment.getExternalStorageDirectory() + File.separator + "screencasts";
        File dir = new File(dirPath);
        if(!dir.exists()) {
            dir.mkdir();
        }
        String fileName = UUID.randomUUID().toString() + ".jpg";
        file = new File(dirPath + File.separator + fileName);
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            screenCast.compress(Bitmap.CompressFormat.JPEG, 85, outputStream);
            outputStream.flush();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Failed To Save Screencast", e);
            Snackbar.make(view, "Failed To Share", Snackbar.LENGTH_LONG);
        } finally {
            try {
                if(outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                Log.e(LOG_TAG, "Failed Closing Output Stream", e);
            }
        }
        return file;
    }
}
