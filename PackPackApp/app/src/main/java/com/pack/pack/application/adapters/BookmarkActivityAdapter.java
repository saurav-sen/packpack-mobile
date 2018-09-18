package com.pack.pack.application.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pack.pack.application.R;
import com.pack.pack.application.activity.FullScreenBookmarkViewActivity;
import com.pack.pack.application.data.util.DownloadFeedImageTask;
import com.pack.pack.application.data.util.MediaUtil;
import com.pack.pack.application.db.Bookmark;

import java.util.List;

/**
 * Created by Saurav on 26-08-2018.
 */
public class BookmarkActivityAdapter extends ArrayAdapter<Bookmark> {

    private Activity activity;
    private LayoutInflater inflater;

    private TextView bookmark_rss_feed__name;
    private ImageView bookmark_rss_feed_image;
    private ImageView bookmark_rss_feed_video_play;
    private TextView bookmark_rss_feed_description;

    private ProgressBar loading_progress;

    private List<Bookmark> feeds;

    public BookmarkActivityAdapter(Activity activity, List<Bookmark> feeds) {
        super(activity, R.layout.bookmark_list_items, feeds.toArray(new Bookmark[feeds.size()]));
        this.activity = activity;
        this.feeds = feeds;
    }

    public List<Bookmark> getFeeds() {
        return feeds;
    }

    @Override
    public int getCount() {
        return getFeeds().size();
    }

    @Override
    public Bookmark getItem(int position) {
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
            convertView = inflater.inflate(R.layout.bookmark_list_items, null);
        }
        bookmark_rss_feed__name = (TextView) convertView.findViewById(R.id.bookmark_rss_feed__name);
        bookmark_rss_feed_image = (ImageView) convertView.findViewById(R.id.bookmark_rss_feed_image);
        bookmark_rss_feed_video_play = (ImageView) convertView.findViewById(R.id.bookmark_rss_feed_video_play);
        bookmark_rss_feed_description = (TextView) convertView.findViewById(R.id.bookmark_rss_feed_description);
        loading_progress = (ProgressBar) convertView.findViewById(R.id.loading_progress);
        loading_progress.setVisibility(View.VISIBLE);

        final Bookmark feed = getItem(position);
        if (feed != null) {
            bookmark_rss_feed__name.setText(feed.getTitle());
            String textSummary = feed.getDescription();
            if(textSummary == null) {
                textSummary = feed.getDescription();
            }
            bookmark_rss_feed_description.setText(textSummary);
            String mediaUrl = feed.getMediaUrl();
            String vUrl = null;
            if(mediaUrl != null && mediaUrl.contains("youtube")) {
                vUrl = mediaUrl;
            }
            String imgUrl = null;
            final String videoUrl = vUrl;
            if (videoUrl != null && !videoUrl.trim().isEmpty()) {
                bookmark_rss_feed_video_play.setVisibility(View.VISIBLE);
                bookmark_rss_feed_video_play.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!MediaUtil.playVideo(videoUrl, BookmarkActivityAdapter.this.activity)) {
                            openFullScreenBookmarkActivity(feed);
                        }
                    }
                });
            } else {
                imgUrl = mediaUrl;
                bookmark_rss_feed_video_play.setVisibility(View.GONE);
            }
            final String imageUrl = imgUrl;
            if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                new DownloadFeedImageTask(bookmark_rss_feed_image, 850, 600, BookmarkActivityAdapter.this.activity, loading_progress)
                        .execute(imageUrl);
                bookmark_rss_feed_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openFullScreenBookmarkActivity(feed);
                    }
                });
                bookmark_rss_feed_description.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openFullScreenBookmarkActivity(feed);
                    }
                });
            }
        }
        return convertView;
    }

    private void openFullScreenBookmarkActivity(final Bookmark feed) {
        if(feed.isUnderDeleteOperation())
            return;
        Intent intent = new Intent(getContext(), FullScreenBookmarkViewActivity.class);
        String newsTitle = feed.getTitle();
        String newsFullText = feed.getArticle();
        intent.putExtra(FullScreenBookmarkViewActivity.SOURCE_LINK, feed.getSourceUrl());
        intent.putExtra(FullScreenBookmarkViewActivity.NEWS_TITLE, newsTitle);
        intent.putExtra(FullScreenBookmarkViewActivity.NEWS_FULL_TEXT, newsFullText);
        getContext().startActivity(intent);
    }
}
