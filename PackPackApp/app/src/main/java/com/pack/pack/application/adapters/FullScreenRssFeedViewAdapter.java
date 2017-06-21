package com.pack.pack.application.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.pack.pack.application.AppController;
import com.pack.pack.application.R;
import com.pack.pack.application.activity.FullScreenPlayVideoActivity;
import com.pack.pack.application.data.util.ApiConstants;
import com.pack.pack.application.data.util.DownloadFeedImageTask;
import com.pack.pack.application.data.util.ImageUtil;
import com.pack.pack.application.image.loader.DownloadImageTask;
import com.pack.pack.application.topic.activity.model.ParcellableRssFeed;
import com.pack.pack.model.web.JPackAttachment;
import com.pack.pack.model.web.JRssSubFeed;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.UUID;

/**
 * Created by Saurav on 17-06-2017.
 */
public class FullScreenRssFeedViewAdapter extends PagerAdapter {

    private Activity activity;

    private List<ParcellableRssFeed> feeds;

    private int currentIndex = 0;

    private static final String LOG_TAG = "FullScreenRssFeedView";

    private ImageView feed_imgDisplay;

    private ImageView feed_imgDisplay_play;

    public FullScreenRssFeedViewAdapter(Activity activity, List<ParcellableRssFeed> feeds) {
        this.activity = activity;
        this.feeds = feeds;
    }

    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
    }

    @Override
    public int getCount() {
        return feeds != null ? feeds.size() : 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(inflater == null) {
            inflater = activity.getLayoutInflater();
        }
        View view = inflater.inflate(R.layout.layout_full_screen_rss_feed_view, container, false);

        feed_imgDisplay = (ImageView) view.findViewById(R.id.feed_imgDisplay);
        feed_imgDisplay_play = (ImageView) view.findViewById(R.id.feed_imgDisplay_play);
        TextView feed_titleText = (TextView) view.findViewById(R.id.feed_titleText);
        TextView feed_descriptionText = (TextView) view.findViewById(R.id.feed_descriptionText);

        ImageButton feed_share = (ImageButton) view.findViewById(R.id.feed_share);
        //Button feed_share = (Button) findViewById(R.id.feed_share);
        feed_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File file = createScreenCast(view);
                shareImage(file);
            }
        });
        TextView feed_creator = (TextView) view.findViewById(R.id.feed_creator);

        ParcellableRssFeed input = feeds.get(position);
        if(input != null) {
            String imageUrl = input.getOgImage();
            if(imageUrl != null) {
                /*new DownloadFeedImageTask(feed_imgDisplay, 850, 850, this, null)
                        .execute(imageUrl);*/
                //feed_imgDisplay.setImageBitmap(ImageUtil.getBitmap(imageUrl));
                prepareImageRender(input);
            }
            feed_descriptionText.setText(input.getOgDescription());
            feed_titleText.setText(input.getOgTitle());
            String videoUrl = input.getVideoUrl();
            if(videoUrl != null && !videoUrl.trim().isEmpty()) {
                feed_imgDisplay_play.setVisibility(View.VISIBLE);
                feed_imgDisplay_play.setTag(videoUrl);
                feed_imgDisplay_play.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        playVideo((String)feed_imgDisplay_play.getTag());
                    }
                });
            } else {
                feed_imgDisplay_play.setVisibility(View.GONE);
            }
        }

        ((ViewPager)container).addView(view);
        return view;
    }

    private void playVideo(String videoURL) {
        boolean isExternalLink = !(videoURL.startsWith(ApiConstants.BASE_URL));

        if (isExternalLink) {
            String VIDEO_ID = null;
            if (videoURL.contains("youtube")) {
                String[] split = videoURL.split("v=");
                if (split.length > 1) {
                    VIDEO_ID = split[1];
                }
            }
            if ((VIDEO_ID != null && !VIDEO_ID.isEmpty())) {
                Intent intent = YouTubeStandalonePlayer.createVideoIntent(activity, ApiConstants.YOUTUBE_API_KEY, VIDEO_ID);
                activity.startActivity(intent);
            } else {
                Snackbar.make(feed_imgDisplay_play, "Oops! Something went wrong", Snackbar.LENGTH_LONG).show();
            }
        } else {
            Intent intent = new Intent(activity, FullScreenPlayVideoActivity.class);
            intent.putExtra(FullScreenPlayVideoActivity.VIDEO_URL, videoURL);
            activity.startActivity(intent);
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

        activity.startActivity(Intent.createChooser(intent, "Shared from SQUILL"));
    }

    private File createScreenCast(View view) {
        File file = null;
        View rootView = activity.getWindow().getDecorView().findViewById(R.id.full_screen_rss_feed_layout);
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

    private void prepareImageRender(ParcellableRssFeed input) {
        String url = input.getOgImage();
        if(url == null)
            return;
        new DownloadFeedImageTask(feed_imgDisplay, 850, 850, this.activity, null).execute(url);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //super.destroyItem(container, position, object);
        View view = (View)object;
        ((ViewPager)container).removeView(view);
    }
}
