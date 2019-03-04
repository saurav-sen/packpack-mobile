package com.pack.pack.application.adapters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.pack.pack.application.R;
import com.pack.pack.application.activity.FullScreenBookmarkViewActivity;
import com.pack.pack.application.data.util.ApiConstants;
import com.pack.pack.application.data.util.BookmarkDeleteResult;
import com.pack.pack.application.data.util.BookmarkDeleteTask;
import com.pack.pack.application.data.util.Bookmarks;
import com.pack.pack.application.data.util.DownloadFeedImageTask;
import com.pack.pack.application.data.util.IAsyncTaskStatusListener;
import com.pack.pack.application.data.util.MediaUtil;
import com.pack.pack.application.db.Bookmark;

import java.util.List;

/**
 * Created by Saurav on 26-08-2018.
 */
public class BookmarkFragmentAdapter extends ArrayAdapter<Bookmark> {

    private Activity activity;
    private LayoutInflater inflater;

    private TextView bookmark_rss_feed__name;
    private ImageView bookmark_rss_feed_image;
    private ImageView bookmark_rss_feed_video_play;
    private TextView bookmark_rss_feed_description;

    private Button bookmark_delete;

    private ProgressBar loading_progress;

    private List<Bookmark> feeds;

    private ProgressDialog progressDialog;

    public BookmarkFragmentAdapter(Activity activity, List<Bookmark> feeds) {
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
        bookmark_delete = (Button) convertView.findViewById(R.id.bookmark_delete);

        loading_progress = (ProgressBar) convertView.findViewById(R.id.loading_progress);
        loading_progress.setVisibility(View.VISIBLE);

        final Bookmark feed = getItem(position);
        if (feed != null) {
            bookmark_rss_feed__name.setText(feed.getTitle());
            String textSummary = feed.getDescription();
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
                        if(!MediaUtil.playVideo(videoUrl, BookmarkFragmentAdapter.this.activity)) {
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
                new DownloadFeedImageTask(bookmark_rss_feed_image, 850, 600, BookmarkFragmentAdapter.this.activity, loading_progress)
                        .execute(imageUrl);
                bookmark_rss_feed_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openFullScreenBookmarkActivity(feed);
                    }
                });

            } else {
                loading_progress.setVisibility(View.GONE);
            }
            bookmark_rss_feed__name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openFullScreenBookmarkActivity(feed);
                }
            });
            bookmark_rss_feed_description.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openFullScreenBookmarkActivity(feed);
                }
            });

            bookmark_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteBookmark(feed, true);
                }
            });
        }
        return convertView;
    }

    private void deleteBookmark(Bookmark toDelete, boolean showLoadingProgress) {
        if(toDelete == null)
            return;
        Bookmarks bookmarks = new Bookmarks();
        bookmarks.getBookmarks().add(toDelete);
        BookmarkDeleteTask task = new BookmarkDeleteTask(activity, false);
        BookmarkDeleteTaskListener listener = new BookmarkDeleteTaskListener(task.getTaskID(), showLoadingProgress);
        task.addListener(listener);
        task.execute(bookmarks);
    }

    private void openFullScreenBookmarkActivity(final Bookmark feed) {
        if(feed.isUnderDeleteOperation())
            return;
        String mediaUrl = feed.getMediaUrl();
        if(mediaUrl != null && (mediaUrl.contains("youtube.com") || mediaUrl.contains("youtu.be")) && !(mediaUrl.startsWith(ApiConstants.BASE_URL))) {
            if(playVideo(mediaUrl))
                return;
        }
        Intent intent = new Intent(getContext(), FullScreenBookmarkViewActivity.class);
        String newsTitle = feed.getTitle();
        String newsFullText = feed.getArticle();
        String newsHtmlContent = feed.getHtmlSnippet();
        intent.putExtra(FullScreenBookmarkViewActivity.SOURCE_LINK, feed.getSourceUrl());
        intent.putExtra(FullScreenBookmarkViewActivity.NEWS_TITLE, newsTitle);
        intent.putExtra(FullScreenBookmarkViewActivity.NEWS_FULL_TEXT, newsFullText);
        intent.putExtra(FullScreenBookmarkViewActivity.NEWS_HTML_CONTENT, newsHtmlContent);
        getContext().startActivity(intent);
    }

    private boolean playVideo(String videoURL) {
        String VIDEO_ID = null;
        if (videoURL.contains("youtube") || videoURL.contains("youtu.be")) {
            String[] split = videoURL.split("v=");
            if (split.length > 1) {
                VIDEO_ID = split[1];
            }
        }
        if ((VIDEO_ID != null && !VIDEO_ID.isEmpty())) {
            Intent intent = YouTubeStandalonePlayer.createVideoIntent(activity, ApiConstants.YOUTUBE_API_KEY, VIDEO_ID);
            activity.startActivity(intent);
            return true;
        } else {
            return false;
        }
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(activity);
            progressDialog.setMessage("Removing...");
            progressDialog.show();
        }
    }

    private void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    private class BookmarkDeleteTaskListener implements IAsyncTaskStatusListener {

        private String taskID;

        private boolean showLoadingProgress;

        BookmarkDeleteTaskListener(String taskID, boolean showLoadingProgress) {
            this.taskID = taskID;
            this.showLoadingProgress = showLoadingProgress;
        }

        @Override
        public void onFailure(String taskID, String errorMsg) {
            if (this.taskID.equals(taskID)) {
                Snackbar.make(bookmark_delete, errorMsg, Snackbar.LENGTH_LONG).show();
            }
        }

        @Override
        public void onPreStart(String taskID) {
            if (this.taskID.equals(taskID) && showLoadingProgress) {
                showProgressDialog();
            }
        }

        @Override
        public void onPostComplete(String taskID) {
            if (this.taskID.equals(taskID) && showLoadingProgress) {
                hideProgressDialog();
            }
        }

        @Override
        public void onSuccess(String taskID, Object data) {
            if (this.taskID.equals(taskID) && data != null) {
                BookmarkDeleteResult result = (BookmarkDeleteResult) data;
                List<Bookmark> success = result.getSuccess();
                if(!success.isEmpty()) {
                    for(Bookmark s : success) {
                        BookmarkFragmentAdapter.this.getFeeds().remove(s);
                    }
                    BookmarkFragmentAdapter.this.notifyDataSetChanged();
                }
                List<Bookmark> failure = result.getFailure();
                if(!failure.isEmpty()) {
                    for(Bookmark f : failure) {
                        f.setUnderDeleteOperation(false);
                    }
                }
            }
        }
    }
}
