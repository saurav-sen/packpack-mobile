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
import com.pack.pack.application.view.util.BookmarkUtil;
import com.pack.pack.application.view.util.ExternalLinkShareUtil;
import com.squill.feed.web.model.JRssFeed;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    private Button articles_bookmark;

    private List<JRssFeed> feeds;

    private static final Object OBJECT = new Object();

    private Map<String, Object> map = new HashMap<String, Object>();

    public ArticlesActivityAdapter(Activity activity, List<JRssFeed> feeds) {
        super(activity, R.layout.articles_list_items, feeds.toArray(new JRssFeed[feeds.size()]));
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
            convertView = inflater.inflate(R.layout.articles_list_items, null);
        }
        articles_rss_feed__name = (TextView) convertView.findViewById(R.id.articles_rss_feed__name);
        articles_rss_feed_image = (ImageView) convertView.findViewById(R.id.articles_rss_feed_image);
        articles_rss_feed_video_play = (ImageView) convertView.findViewById(R.id.articles_rss_feed_video_play);
        articles_rss_feed_description = (TextView) convertView.findViewById(R.id.articles_rss_feed_description);
        loading_progress = (ProgressBar) convertView.findViewById(R.id.loading_progress);
        loading_progress.setVisibility(View.VISIBLE);
        articles_bookmark = (Button) convertView.findViewById(R.id.articles_bookmark);

        final JRssFeed feed = getItem(position);
        if (feed != null) {
            articles_rss_feed__name.setText(feed.getOgTitle());
            String textSummary = feed.getArticleSummaryText();
            if(textSummary == null) {
                textSummary = feed.getOgDescription();
            }
            articles_rss_feed_description.setText(textSummary);
            final String imageUrl = feed.getOgImage();
            final String videoUrl = feed.getVideoUrl();
            if (videoUrl != null && !videoUrl.trim().isEmpty()) {
                articles_rss_feed_video_play.setVisibility(View.VISIBLE);
                articles_rss_feed_video_play.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openFullScreenNewsActivity(feed);
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
                        openFullScreenNewsActivity(feed);
                    }
                });
                articles_rss_feed_description.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openFullScreenNewsActivity(feed);
                    }
                });
            }

            articles_bookmark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BookmarkUtil.addBookmark(feed, activity, articles_bookmark);
                   // shareUrl(feed);
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


