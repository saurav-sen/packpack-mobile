package com.pack.pack.application.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.pack.pack.application.R;
import com.pack.pack.application.image.loader.DownloadImageTask;
import com.pack.pack.application.view.util.ViewUtil;
import com.pack.pack.model.web.JTopic;

import java.util.List;

/**
 * Created by Saurav on 08-04-2016.
 */
public class TopicViewAdapter extends ArrayAdapter<JTopic> {

    private Activity activity;
    private LayoutInflater inflater;

    public List<JTopic> getTopics() {
        return topics;
    }

    public void setTopics(List<JTopic> topics) {
        this.topics = topics;
    }

    private List<JTopic> topics;

    private String categoryType;

    public TopicViewAdapter(Activity activity, List<JTopic> topics, String categoryType) {
        super(activity, ViewUtil.getListViewLayoutId(categoryType), topics);
        this.activity = activity;
        this.topics = topics;
        this.categoryType = categoryType;
    }

    @Override
    public int getCount() {
        return 20;//topics.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(inflater == null) {
            //inflater = LayoutInflater.from(activity);
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if(convertView == null) {
            convertView = inflater.inflate(R.layout.lifestyle_event_item, null);
        }
        NetworkImageView topicPicView = (NetworkImageView) convertView.findViewById(ViewUtil.getViewId(categoryType, "topicPic"));
        TextView nameTextView = (TextView) convertView.findViewById(ViewUtil.getViewId(categoryType, "topic_name"));
        TextView descriptionTextView = (TextView) convertView.findViewById(ViewUtil.getViewId(categoryType, "topic_description"));
       // TextView followersTextView = (TextView) convertView.findViewById(R.id.topic_followers);
        ImageView poster = (ImageView) convertView.findViewById(ViewUtil.getViewId(categoryType, "topicPoster"));

        if(position < topics.size()) {
            JTopic topic = topics.get(position);
           // topic.setName("MyName");
           // topic.setDescription("MyDescription");
           // topic.setFollowers(100);
            nameTextView.setText(topic.getName());
            descriptionTextView.setText(topic.getDescription());
           // followersTextView.setText(String.valueOf(topic.getFollowers()));
            if(topic.getWallpaperUrl() != null && !topic.getWallpaperUrl().trim().equals("")) {
                new DownloadImageTask(poster).execute(topic.getWallpaperUrl() + "?thumbnail=true");
            }
        }
        return convertView;
    }
}
