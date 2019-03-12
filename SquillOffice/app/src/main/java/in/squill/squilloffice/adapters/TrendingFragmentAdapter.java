package in.squill.squilloffice.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squill.feed.web.model.JRssFeed;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import in.squill.squilloffice.FullScreenNewsViewActivity;
import in.squill.squilloffice.R;
import in.squill.squilloffice.data.util.DownloadFeedImageTask;

/**
 * Created by Saurav on 13-08-2017.
 */
public class TrendingFragmentAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;

    private TextView news_rss_feed__name;
    private ImageView news_rss_feed_image;
    private ImageView news_rss_feed_video_play;
    private TextView news_rss_feed_description;

    private ProgressBar loading_progress;

    private Button news_bookmark;

    private List<JRssFeed> feeds;

    private static final Object OBJECT = new Object();

    private Map<String, Object> map = new HashMap<String, Object>();

    public TrendingFragmentAdapter(Activity activity, List<JRssFeed> feeds) {
        super(activity, feeds, R.layout.news_list_items);
        this.activity = activity;
        this.feeds = feeds;
    }

    @Override
    protected void doClearState() {
        map.clear();
        feeds.clear();
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
            convertView = inflater.inflate(R.layout.news_list_items, null);
        }
        news_rss_feed__name = (TextView) convertView.findViewById(R.id.news_rss_feed__name);
        news_rss_feed_image = (ImageView) convertView.findViewById(R.id.news_rss_feed_image);
        news_rss_feed_video_play = (ImageView) convertView.findViewById(R.id.news_rss_feed_video_play);
        news_rss_feed_description = (TextView) convertView.findViewById(R.id.news_rss_feed_description);
        loading_progress = (ProgressBar) convertView.findViewById(R.id.loading_progress);
        loading_progress.setVisibility(View.VISIBLE);
        news_bookmark = (Button) convertView.findViewById(R.id.news_bookmark);

        final JRssFeed feed = getItem(position);
        if(feed != null) {
            news_rss_feed__name.setText(feed.getOgTitle());
            String textSummary = feed.getArticleSummaryText();
            if(textSummary == null) {
                textSummary = feed.getOgDescription();
            }
            news_rss_feed_description.setText(textSummary);
            final String imageUrl = feed.getOgImage();
            final String videoUrl = feed.getVideoUrl();
            //final String newsUrl = feed.getHrefSource();
            if(videoUrl != null && !videoUrl.trim().isEmpty()) {
                news_rss_feed_video_play.setVisibility(View.VISIBLE);
                news_rss_feed_video_play.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openFullScreenNewsActivity(feed);
                    }
                });
            } else {
                news_rss_feed_video_play.setVisibility(View.GONE);
            }
            if(imageUrl != null && !imageUrl.trim().isEmpty()) {
                new DownloadFeedImageTask(news_rss_feed_image, 850, 600, TrendingFragmentAdapter.this.activity, loading_progress)
                        .execute(imageUrl);
                news_rss_feed_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openFullScreenNewsActivity(feed);
                    }
                });
            } else {
                loading_progress.setVisibility(View.GONE);
            }
            news_rss_feed_description.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openFullScreenNewsActivity(feed);
                }
            });

            news_bookmark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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
        String htmlSnippet = feed.getHtmlSnippet();
        intent.putExtra(FullScreenNewsViewActivity.NEWS_HTML_CONTENT, htmlSnippet);
        intent.putExtra(FullScreenNewsViewActivity.NEWS_LINK, url);
        intent.putExtra(FullScreenNewsViewActivity.WEB_SHARE_LINK, shareUrl);
        intent.putExtra(FullScreenNewsViewActivity.SOURCE_LINK, feed.getOgUrl());
        intent.putExtra(FullScreenNewsViewActivity.NEWS_TITLE, newsTitle);
        intent.putExtra(FullScreenNewsViewActivity.NEWS_FULL_TEXT, newsFullText);
        getContext().startActivity(intent);
    }
}