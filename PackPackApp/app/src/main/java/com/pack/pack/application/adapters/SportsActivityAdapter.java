package com.pack.pack.application.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pack.pack.application.R;
import com.pack.pack.application.activity.FullScreenNewsViewActivity;
import com.pack.pack.application.data.util.DownloadFeedImageTask;
import com.pack.pack.application.view.util.ExternalLinkShareUtil;
import com.squill.feed.web.model.JRssFeed;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Saurav on 25-03-2018.
 */
public class SportsActivityAdapter extends ArrayAdapter<JRssFeed> {

    private Activity activity;
    private LayoutInflater inflater;

    private TextView sports_rss_feed__name;
    private ImageView sports_rss_feed_image;
    private ImageView sports_rss_feed_video_play;
    private TextView sports_rss_feed_description;

    private ProgressBar loading_progress;

    private Button sports_rss_share;

    private List<JRssFeed> feeds;

    private static final Object OBJECT = new Object();

    private Map<String, Object> map = new HashMap<String, Object>();

    public SportsActivityAdapter(Activity activity, List<JRssFeed> feeds) {
        super(activity, R.layout.sports_list_items, feeds.toArray(new JRssFeed[feeds.size()]));
        this.activity = activity;
        this.feeds = feeds;
    }

    private List<JRssFeed> getFeeds() {
        return feeds;
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
    public int getCount() {
        return getFeeds().size();
    }

    @Override
    public JRssFeed getItem(int position) {
        if (position < getFeeds().size()) {
            return getFeeds().get(position);
        }
        return null;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater == null) {
            inflater = activity.getLayoutInflater();
        }
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.sports_list_items, null);
        }
        sports_rss_feed__name = (TextView) convertView.findViewById(R.id.sports_rss_feed__name);
        sports_rss_feed_image = (ImageView) convertView.findViewById(R.id.sports_rss_feed_image);
        sports_rss_feed_video_play = (ImageView) convertView.findViewById(R.id.sports_rss_feed_video_play);
        sports_rss_feed_description = (TextView) convertView.findViewById(R.id.sports_rss_feed_description);
        loading_progress = (ProgressBar) convertView.findViewById(R.id.loading_progress);
        loading_progress.setVisibility(View.VISIBLE);
        sports_rss_share = (Button) convertView.findViewById(R.id.sports_rss_share);

        final JRssFeed feed = getItem(position);
        if (feed != null) {
            sports_rss_feed__name.setText(feed.getOgTitle());
            String textSummary = feed.getArticleSummaryText();
            if(textSummary == null) {
                textSummary = feed.getOgDescription();
            }
            sports_rss_feed_description.setText(textSummary);
            final String imageUrl = feed.getOgImage();
            final String videoUrl = feed.getVideoUrl();
            if (videoUrl != null && !videoUrl.trim().isEmpty()) {
                sports_rss_feed_video_play.setVisibility(View.VISIBLE);
                sports_rss_feed_video_play.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openFullScreenNewsActivity(feed);
                    }
                });
            } else {
                sports_rss_feed_video_play.setVisibility(View.GONE);
            }
            if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                new DownloadFeedImageTask(sports_rss_feed_image, 850, 600, SportsActivityAdapter.this.activity, loading_progress)
                        .execute(imageUrl);
                sports_rss_feed_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openFullScreenNewsActivity(feed);
                    }
                });
                sports_rss_feed_description.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openFullScreenNewsActivity(feed);
                    }
                });
            }

            sports_rss_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shareUrl(feed);
                }
            });
        }
        return convertView;
    }

    private void openFullScreenNewsActivity(final JRssFeed feed) {
        Intent intent = new Intent(getContext(), FullScreenNewsViewActivity.class);
        String url = feed.getSquillUrl();
        String shareUrl = feed.getShareableUrl() != null ? feed.getShareableUrl() : url;
        String newsTitle = feed.getOgTitle();
        String newsFullText = feed.getFullArticleText();
        intent.putExtra(FullScreenNewsViewActivity.NEWS_LINK, url);
        intent.putExtra(FullScreenNewsViewActivity.WEB_SHARE_LINK, shareUrl);
        intent.putExtra(FullScreenNewsViewActivity.SOURCE_LINK, feed.getOgUrl());
        intent.putExtra(FullScreenNewsViewActivity.NEWS_TITLE, newsTitle);
        intent.putExtra(FullScreenNewsViewActivity.NEWS_FULL_TEXT, newsFullText);
        getContext().startActivity(intent);
    }

    private void shareUrl(JRssFeed feed) {
        ExternalLinkShareUtil.shareUrl(getContext(), feed);
    }
}

