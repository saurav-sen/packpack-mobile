package com.pack.pack.application.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pack.pack.application.R;
import com.pack.pack.application.image.loader.DownloadImageTask;
import com.pack.pack.model.web.JPack;
import com.pack.pack.model.web.JTopic;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Saurav on 19-08-2017.
 */
public class TopicThumbnailViewAdapter extends ArrayAdapter<JTopic>  {

    private List<JTopic> topics;

    private LayoutInflater inflator;

    private Activity activity;

    public TopicThumbnailViewAdapter(Activity activity, List<JTopic> topics) {
        super(activity, R.layout.activity_image_video_share_receive_list);
        this.activity = activity;
        this.topics = topics;
    }

    public List<JTopic> getTopics() {
        if(topics == null) {
            topics = new ArrayList<JTopic>();
        }
        return topics;
    }

    public void setTopics(List<JTopic> topics) {
        this.topics = topics;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(inflator == null) {
            inflator = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if(convertView == null) {
            convertView = inflator.inflate(R.layout.activity_image_video_share_receive_list, null);
        }

        ImageView topic_image = (ImageView) convertView.findViewById(R.id.topic_image);
        TextView topic_title = (TextView) convertView.findViewById(R.id.topic_title);

        if(position < topics.size()) {
            JTopic topic = topics.get(position);
            new DownloadImageTask(topic_image, 70, 70, activity, null, false, true, true).execute(topic.getWallpaperUrl());
            topic_title.setText(topic.getName());
        }
        return convertView;
    }
}
