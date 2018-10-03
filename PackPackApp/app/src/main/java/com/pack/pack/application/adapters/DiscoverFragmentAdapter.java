package com.pack.pack.application.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.pack.pack.application.AppController;
import com.pack.pack.application.R;
import com.pack.pack.application.activity.FullScreenPlayVideoActivity;
import com.pack.pack.application.activity.FullScreenRssFeedViewActivity;
import com.pack.pack.application.activity.fragments.BaseAdapter;
import com.pack.pack.application.data.util.ApiConstants;
import com.pack.pack.application.data.util.DownloadFeedImageTask;
import com.pack.pack.application.topic.activity.model.ParcellableRssFeed;
import com.pack.pack.application.view.util.ExternalLinkShareUtil;
import com.pack.pack.application.view.util.ViewUtil;
import com.squill.feed.web.model.JRssFeed;
import com.squill.feed.web.model.JRssSubFeed;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * Created by Saurav on 08-04-2016.
 *
 */
public class DiscoverFragmentAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    private TextView home_rss_feed__name;
    private ImageView home_rss_feed_image;
    private ImageView home_rss_feed_video_play;
    private TextView home_rss_feed_description;

    private ProgressBar loading_progress;

    private Button home_rss_share;

    private List<JRssFeed> feeds;

    private static final Object OBJECT = new Object();

    private Map<String, Object> map = new HashMap<String, Object>();

    public DiscoverFragmentAdapter(Activity activity, List<JRssFeed> feeds) {
        super(activity, feeds, R.layout.home_event_item);
        this.activity = activity;
        this.feeds = feeds;
    }

    public List<JRssFeed> getFeeds() {
        return feeds;
    }

    @Override
    public int getCount() {
        return getFeeds().size();
    }

    @Override
    protected void doClearState() {
        map.clear();
        feeds.clear();
    }

    @Override
    public JRssFeed getItem(int position) {
        if(position < getFeeds().size()) {
            return getFeeds().get(position);
        }
        return null;
    }

    public void addNewFeeds(List<JRssFeed> newFeeds) {
        addNewFeeds(-1, newFeeds);
    }

    public void addNewFeeds(int location, List<JRssFeed> newFeeds) {
        if(newFeeds == null || newFeeds.isEmpty())
            return;
        if(map.isEmpty()) {
            for (JRssFeed feed : feeds) {
                map.put(feed.getOgUrl(), OBJECT);
            }
        }
        Iterator<JRssFeed> itr = newFeeds.iterator();
        while (itr.hasNext()) {
            JRssFeed newFeed = itr.next();
            if(map.get(newFeed.getOgUrl()) != null) {
                itr.remove();
            } else {
                map.put(newFeed.getOgUrl(), OBJECT);
            }
        }
        if(location >= 0) {
            feeds.addAll(location, newFeeds);
        } else {
            feeds.addAll(newFeeds);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(inflater == null) {
            inflater = activity.getLayoutInflater();
        }
        if(convertView == null) {
            convertView = inflater.inflate(ViewUtil.getListViewLayoutId("home"), null);
        }
        home_rss_feed__name = (TextView) convertView.findViewById(R.id.home_rss_feed__name);
        home_rss_feed_image = (ImageView) convertView.findViewById(R.id.home_rss_feed_image);
        home_rss_feed_video_play = (ImageView) convertView.findViewById(R.id.home_rss_feed_video_play);
        home_rss_feed_description = (TextView) convertView.findViewById(R.id.home_rss_feed_description);
        loading_progress = (ProgressBar) convertView.findViewById(R.id.loading_progress);
        loading_progress.setVisibility(View.VISIBLE);
        home_rss_share = (Button) convertView.findViewById(R.id.home_rss_share);

        final JRssFeed feed = getItem(position);
        if(feed != null) {
            home_rss_feed__name.setText(feed.getOgTitle());
            home_rss_feed_description.setText(feed.getOgDescription());
            final String imageUrl = feed.getOgImage();
            final String videoUrl = feed.getVideoUrl();
            if(videoUrl != null && !videoUrl.trim().isEmpty()) {
                home_rss_feed_video_play.setVisibility(View.VISIBLE);
                home_rss_feed_video_play.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String videoUrl = feed.getVideoUrl();
                        if (videoUrl != null && !videoUrl.trim().isEmpty()) {
                            playVideo(videoUrl.trim());
                        } else {
                            Intent intent = new Intent(getContext(), FullScreenRssFeedViewActivity.class);
                            intent.putParcelableArrayListExtra(FullScreenRssFeedViewActivity.PARCELLABLE_FEEDS, prepareParcel(feed));
                            getContext().startActivity(intent);
                        }
                    }
                });
            } else {
                home_rss_feed_video_play.setVisibility(View.GONE);
            }
            if(imageUrl != null && !imageUrl.trim().isEmpty()) {
                new DownloadFeedImageTask(home_rss_feed_image, 850, 600, DiscoverFragmentAdapter.this.activity, loading_progress)
                        .execute(imageUrl);
                home_rss_feed_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                       /*Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(feed.getOgUrl()));
                        getContext().startActivity(intent);*/
                        String videoUrl = feed.getVideoUrl();
                        if (videoUrl != null && !videoUrl.trim().isEmpty()) {
                            playVideo(videoUrl.trim());
                        } else {
                            Intent intent = new Intent(getContext(), FullScreenRssFeedViewActivity.class);
                            intent.putParcelableArrayListExtra(FullScreenRssFeedViewActivity.PARCELLABLE_FEEDS, prepareParcel(feed));
                            getContext().startActivity(intent);
                        }
                    }
                });
                home_rss_feed_description.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String videoUrl = feed.getVideoUrl();
                        if (videoUrl != null && !videoUrl.trim().isEmpty()) {
                            playVideo(videoUrl.trim());
                        } else {
                            Intent intent = new Intent(getContext(), FullScreenRssFeedViewActivity.class);
                            intent.putParcelableArrayListExtra(FullScreenRssFeedViewActivity.PARCELLABLE_FEEDS, prepareParcel(feed));
                            getContext().startActivity(intent);
                        }
                    }
                });
            }

            home_rss_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String publicUrl = imageUrl;
                    if(videoUrl != null) {
                        publicUrl = videoUrl;
                    }

                    if(publicUrl != null && !publicUrl.isEmpty() && publicUrl.contains("youtube.com")) {
                        shareUrl(feed, publicUrl);
                    } else {
                        shareUrl(feed, null);
                    }
                }
            });
        }
        return convertView;
    }

    private void shareUrl(JRssFeed feed, String url) {
        ExternalLinkShareUtil.shareUrl(getContext(), feed, url);
    }

    private ArrayList<ParcellableRssFeed> prepareParcel(JRssFeed feed) {
        ArrayList<ParcellableRssFeed> feeds = new ArrayList<ParcellableRssFeed>();

        feeds.add(new ParcellableRssFeed(feed));

        List<JRssSubFeed> siblings = feed.getSiblings();
        if(siblings != null && !siblings.isEmpty()) {
            for(JRssSubFeed sibling : siblings) {
                JRssFeed subFeed = new JRssFeed();
                subFeed.setOgTitle(sibling.getOgTitle());
                subFeed.setOgDescription(sibling.getOgDescription());
                subFeed.setOgImage(sibling.getOgImage());
                subFeed.setVideoUrl(sibling.getVideoUrl());
                subFeed.setHrefSource(sibling.getHrefSource());
                feeds.add(new ParcellableRssFeed(subFeed));
            }
        }
        return feeds;
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
                Snackbar.make(home_rss_feed_video_play, "Oops! Something went wrong", Snackbar.LENGTH_LONG).show();
            }
        } else {
            Intent intent = new Intent(activity, FullScreenPlayVideoActivity.class);
            intent.putExtra(FullScreenPlayVideoActivity.VIDEO_URL, videoURL);
            activity.startActivity(intent);
        }
    }
}
