package com.pack.pack.application.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.URLSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.pack.pack.application.AppController;
import com.pack.pack.application.R;
import com.pack.pack.application.activity.PackDetailActivity;
import com.pack.pack.application.topic.activity.model.ParcelablePack;
import com.pack.pack.model.web.JPack;
import com.pack.pack.model.web.JPackAttachment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Saurav on 02-05-2016.
 */
public class TopicDetailAdapter extends ArrayAdapter<JPack> {

    private LayoutInflater inflator;

    private Activity activity;

    public void setPacks(List<JPack> packs) {
        this.packs = packs;
    }

    private List<JPack> packs;

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int position = (int) view.getTag();
            if(position < 0 || packs == null || position >= packs.size())
                return;
            JPack pack = (JPack) packs.get(position);
            ParcelablePack parcel = new ParcelablePack(pack);
            Intent intent = new Intent(getContext(), PackDetailActivity.class);
            intent.putExtra(AppController.PACK_PARCELABLE_KEY, parcel);
            getContext().startActivity(intent);
        }
    };

    public TopicDetailAdapter(Activity activity, List<JPack> packs) {
        super(activity, R.layout.inside_topic_detail_item);
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
            convertView = inflator.inflate(R.layout.inside_topic_detail_item, null);
        }
        TextView packTitle = (TextView) convertView.findViewById(R.id.pack_title);
        packTitle.setTag(position);
        packTitle.setOnClickListener(onClickListener);

        TextView packStory = (TextView) convertView.findViewById(R.id.pack_story);
        packStory.setTag(position);
        packStory.setOnClickListener(onClickListener);

        TextView packStoryContinue = (TextView) convertView.findViewById(R.id.pack_story_continue);

        GridView packAttachmentsGrid = (GridView) convertView.findViewById(R.id.pack_attachments);
        packAttachmentsGrid.setTag(position);
        //packAttachmentsGrid.setOnClickListener(onClickListener);

        if(position < packs.size()) {
            JPack pack = packs.get(position);
            packTitle.setText(pack.getTitle());
            StringBuilder story = new StringBuilder(pack.getStory());
            String storyContent = story.toString();
            packStory.setText(storyContent);//pack.getStory());
            if(storyContent.length() > 1000) {
                packStoryContinue.setText(Html.fromHtml("<a href=\\\"#\\\">Continue</a> "));
                packStoryContinue.setTextColor(0x0a80d1);

                stripUnderlines(packStoryContinue);
            }


            List<JPackAttachment> attachments = pack.getAttachments();
            if(attachments != null && !attachments.isEmpty()) {
                ImageGridAdapter adapter = new ImageGridAdapter(getContext());
                List<String> imageUrls = new ArrayList<String>();
                for(int j=0; j<attachments.size(); j++) {
                    JPackAttachment attachment = attachments.get(j);
                    imageUrls.add(attachment.getAttachmentThumbnailUrl());
                }
                adapter.getImageUrls().addAll(imageUrls);
                packAttachmentsGrid.setAdapter(adapter);
            }
        }
        return convertView;
    }

    private void stripUnderlines(TextView textView) {
        Spannable s = new SpannableString(textView.getText());
        URLSpan[] spans = s.getSpans(0, s.length(), URLSpan.class);
        for (URLSpan span: spans) {
            int start = s.getSpanStart(span);
            int end = s.getSpanEnd(span);
            s.removeSpan(span);
            span = new URLSpanNoUnderline(span.getURL());
            s.setSpan(span, start, end, 0);
        }
        textView.setText(s);
    }

    private class URLSpanNoUnderline extends URLSpan {
        public URLSpanNoUnderline(String url) {
            super(url);
        }
        @Override public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(false);
        }
    }
}
