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
import com.pack.pack.model.web.JRssFeed;

import java.util.List;

/**
 * Created by Saurav on 25-03-2018.
 */
public class ArticlesActivityAdapter extends ArrayAdapter<JRssFeed> {

    private Activity activity;
    private LayoutInflater inflater;

    private TextView articles_rss_feed__name;
    private ImageView articles_rss_feed_image;
    private ImageView articles_rss_feed_video_play;
    private TextView articles_rss_feed_description;

    private ProgressBar loading_progress;

    private Button articles_rss_share;

    private List<JRssFeed> feeds;

    public ArticlesActivityAdapter(Activity activity, List<JRssFeed> feeds) {
        super(activity, R.layout.articles_list_items, feeds.toArray(new JRssFeed[feeds.size()]));
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
            convertView = inflater.inflate(R.layout.articles_list_items, null);
        }
        articles_rss_feed__name = (TextView) convertView.findViewById(R.id.articles_rss_feed__name);
        articles_rss_feed_image = (ImageView) convertView.findViewById(R.id.articles_rss_feed_image);
        articles_rss_feed_video_play = (ImageView) convertView.findViewById(R.id.articles_rss_feed_video_play);
        articles_rss_feed_description = (TextView) convertView.findViewById(R.id.articles_rss_feed_description);
        loading_progress = (ProgressBar) convertView.findViewById(R.id.loading_progress);
        loading_progress.setVisibility(View.VISIBLE);
        articles_rss_share = (Button) convertView.findViewById(R.id.articles_rss_share);

        final JRssFeed feed = getItem(position);
        if (feed != null) {
            articles_rss_feed__name.setText(feed.getOgTitle());
            articles_rss_feed_description.setText(feed.getOgDescription());
            final String imageUrl = feed.getOgImage();
            final String videoUrl = feed.getVideoUrl();
            if (videoUrl != null && !videoUrl.trim().isEmpty()) {
                articles_rss_feed_video_play.setVisibility(View.VISIBLE);
                articles_rss_feed_video_play.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), FullScreenNewsViewActivity.class);
                        String url = feed.getHrefSource() != null ? feed.getHrefSource() : feed.getOgUrl();
                        String shareUrl = feed.getShareableUrl() != null ? feed.getShareableUrl() : url;
                        intent.putExtra(FullScreenNewsViewActivity.NEWS_LINK, url);
                        intent.putExtra(FullScreenNewsViewActivity.WEB_SHARE_LINK, shareUrl);
                        getContext().startActivity(intent);
                    }
                });
            } else {
                articles_rss_feed_video_play.setVisibility(View.GONE);
            }
            if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                new DownloadFeedImageTask(articles_rss_feed_image, 850, 600, ArticlesActivityAdapter.this.activity, loading_progress)
                        .execute(imageUrl);
                articles_rss_feed_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getContext(), FullScreenNewsViewActivity.class);
                        String url = feed.getHrefSource() != null ? feed.getHrefSource() : feed.getOgUrl();
                        String shareUrl = feed.getShareableUrl() != null ? feed.getShareableUrl() : url;
                        intent.putExtra(FullScreenNewsViewActivity.NEWS_LINK, url);
                        intent.putExtra(FullScreenNewsViewActivity.WEB_SHARE_LINK, shareUrl);
                        getContext().startActivity(intent);
                    }
                });
                articles_rss_feed_description.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), FullScreenNewsViewActivity.class);
                        String url = feed.getHrefSource() != null ? feed.getHrefSource() : feed.getOgUrl();
                        String shareUrl = feed.getShareableUrl() != null ? feed.getShareableUrl() : url;
                        intent.putExtra(FullScreenNewsViewActivity.NEWS_LINK, url);
                        intent.putExtra(FullScreenNewsViewActivity.WEB_SHARE_LINK, shareUrl);
                        getContext().startActivity(intent);
                    }
                });
            }

            articles_rss_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shareUrl(feed);
                }
            });
        }
        return convertView;
    }

    private void shareUrl(JRssFeed feed) {
        ExternalLinkShareUtil.shareUrl(getContext(), feed);
    }
}


