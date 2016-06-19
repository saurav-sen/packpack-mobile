package com.pack.pack.application.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.android.volley.toolbox.ImageLoader;
import com.pack.pack.application.AppController;
import com.pack.pack.application.R;
import com.pack.pack.application.topic.activity.model.TopicEvent;

import java.util.List;

/**
 *
 * Created by Saurav on 08-04-2016.
 *
 */
public class HomeActivityAdapter extends ArrayAdapter<TopicEvent> {

    private Activity activity;
    private LayoutInflater inflater;
    private ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public HomeActivityAdapter(Activity activity, List<TopicEvent> events) {
        super(activity, R.layout.home_topic_view, events.toArray(new TopicEvent[events.size()]));
        this.activity = activity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(inflater == null) {
            inflater = activity.getLayoutInflater();
        }
        if(convertView == null) {
            convertView = inflater.inflate(R.layout.home_event_item, null);
        }
        return convertView;
    }
}
