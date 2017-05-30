package com.pack.pack.application.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.pack.pack.application.AppController;
import com.pack.pack.application.R;
import com.pack.pack.application.activity.FullScreenRssFeedViewActivity;
import com.pack.pack.application.data.util.DownloadFeedImageTask;
import com.pack.pack.application.image.loader.DownloadImageTask;
import com.pack.pack.application.topic.activity.model.ParcellableRssFeed;
import com.pack.pack.application.view.util.ViewUtil;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.model.web.JRssFeed;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * Created by Saurav on 08-04-2016.
 *
 */
public class HomeActivityAdapter extends ArrayAdapter<JRssFeed> {

    private Activity activity;
    private LayoutInflater inflater;
    private ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    private TextView home_rss_feed__name;
    private ImageView home_rss_feed_image;
    private TextView home_rss_feed_description;

    private ProgressBar loading_progress;

    private List<JRssFeed> feeds;

    public HomeActivityAdapter(Activity activity, List<JRssFeed> feeds) {
        super(activity, R.layout.home_event_item, feeds.toArray(new JRssFeed[feeds.size()]));
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
        if(position < getFeeds().size()) {
            return getFeeds().get(position);
        }
        return null;
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
        home_rss_feed_description = (TextView) convertView.findViewById(R.id.home_rss_feed_description);
        loading_progress = (ProgressBar) convertView.findViewById(R.id.loading_progress);
        loading_progress.setVisibility(View.VISIBLE);
        final JRssFeed feed = getItem(position);
        if(feed != null) {
            home_rss_feed__name.setText(feed.getOgTitle());
            home_rss_feed_description.setText(feed.getOgDescription());
            final String imageUrl = feed.getOgImage();
            if(imageUrl != null && !imageUrl.trim().isEmpty()) {
                new DownloadFeedImageTask(home_rss_feed_image, 850, 600, HomeActivityAdapter.this.activity, loading_progress)
                        .execute(imageUrl);
                home_rss_feed_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                       /*Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(feed.getOgUrl()));
                        getContext().startActivity(intent);*/

                        Intent intent = new Intent(getContext(), FullScreenRssFeedViewActivity.class);
                        intent.putExtra(FullScreenRssFeedViewActivity.PARCELLABLE_FEED, new ParcellableRssFeed(feed));
                        getContext().startActivity(intent);
                    }
                });
            }
        }
        return convertView;
    }
}
