package com.pack.pack.application.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.pack.pack.application.R;
import com.pack.pack.model.web.JPack;
import com.pack.pack.model.web.JPackAttachment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CipherCloud on 02-05-2016.
 */
public class TopicDetailAdapter extends ArrayAdapter<JPack> {

    private LayoutInflater inflator;

    private Activity activity;

    public void setPacks(List<JPack> packs) {
        this.packs = packs;
    }

    private List<JPack> packs;

    public TopicDetailAdapter(Activity activity, List<JPack> packs) {
        super(activity, R.layout.topic_detail_item);
        this.activity = activity;
        this.packs = packs;
    }

    @Override
    public int getCount() {
        return 20;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(inflator == null) {
            inflator = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if(convertView == null) {
            convertView = inflator.inflate(R.layout.topic_detail_item, null);
        }
        TextView packTitle = (TextView) convertView.findViewById(R.id.pack_title);
        TextView packStory = (TextView) convertView.findViewById(R.id.pack_story);
        GridView packAttachmentsGrid = (GridView) convertView.findViewById(R.id.pack_attachments);
        if(position < packs.size()) {
            JPack pack = packs.get(position);
            packTitle.setText(pack.getTitle());
            packStory.setText(pack.getStory());
            List<JPackAttachment> attachments = pack.getAttachments();
            if(attachments != null && !attachments.isEmpty()) {
                ImageGridAdapter adapter = new ImageGridAdapter(getContext());
                List<String> imageUrls = new ArrayList<String>();
                for(int j=0; j<imageUrls.size(); j++) {
                    JPackAttachment attachment = attachments.get(j);
                    imageUrls.add(attachment.getAttachmentThumbnailUrl());
                }
                adapter.getImageUrls().addAll(imageUrls);
                packAttachmentsGrid.setAdapter(adapter);
            }
        }
        return convertView;
    }
}
