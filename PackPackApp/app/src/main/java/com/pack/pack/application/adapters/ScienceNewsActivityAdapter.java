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

import java.util.List;

/**
 * Created by Saurav on 25-03-2018.
 */
public class ScienceNewsActivityAdapter extends ArrayAdapter<JRssFeed> {

    private Activity activity;
    private LayoutInflater inflater;

    private TextView science_news_rss_feed__name;
    private ImageView science_news_rss_feed_image;
    private ImageView science_news_rss_feed_video_play;
    private TextView science_news_rss_feed_description;

    private ProgressBar loading_progress;

    private Button science_news_rss_share;

    private List<JRssFeed> feeds;

    public ScienceNewsActivityAdapter(Activity activity, List<JRssFeed> feeds) {
        super(activity, R.layout.science_news_list_items, feeds.toArray(new JRssFeed[feeds.size()]));
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
            convertView = inflater.inflate(R.layout.science_news_list_items, null);
        }
        science_news_rss_feed__name = (TextView) convertView.findViewById(R.id.science_news_rss_feed__name);
        science_news_rss_feed_image = (ImageView) convertView.findViewById(R.id.science_news_rss_feed_image);
        science_news_rss_feed_video_play = (ImageView) convertView.findViewById(R.id.science_news_rss_feed_video_play);
        science_news_rss_feed_description = (TextView) convertView.findViewById(R.id.science_news_rss_feed_description);
        loading_progress = (ProgressBar) convertView.findViewById(R.id.loading_progress);
        loading_progress.setVisibility(View.VISIBLE);
        science_news_rss_share = (Button) convertView.findViewById(R.id.science_news_rss_share);

        final JRssFeed feed = getItem(position);
        if (feed != null) {
            science_news_rss_feed__name.setText(feed.getOgTitle());
            String textSummary = feed.getArticleSummaryText();
            if(textSummary == null) {
                textSummary = feed.getOgDescription();
            }
            science_news_rss_feed_description.setText(textSummary);
            final String imageUrl = feed.getOgImage();
            final String videoUrl = feed.getVideoUrl();
            if (videoUrl != null && !videoUrl.trim().isEmpty()) {
                science_news_rss_feed_video_play.setVisibility(View.VISIBLE);
                science_news_rss_feed_video_play.setOnClickListener(new View.OnClickListener() {
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
                science_news_rss_feed_video_play.setVisibility(View.GONE);
            }
            if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                new DownloadFeedImageTask(science_news_rss_feed_image, 850, 600, ScienceNewsActivityAdapter.this.activity, loading_progress)
                        .execute(imageUrl);
                science_news_rss_feed_image.setOnClickListener(new View.OnClickListener() {
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
                science_news_rss_feed_description.setOnClickListener(new View.OnClickListener() {
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

            science_news_rss_share.setOnClickListener(new View.OnClickListener() {
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


