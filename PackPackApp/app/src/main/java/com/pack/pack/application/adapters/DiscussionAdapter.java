package com.pack.pack.application.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.pack.pack.application.Constants;
import com.pack.pack.application.R;
import com.pack.pack.application.activity.DiscussionDetailViewActivity;
import com.pack.pack.model.web.EntityType;
import com.pack.pack.model.web.JDiscussion;
import com.pack.pack.model.web.JUser;

import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Saurav on 19-10-2016.
 */
public class DiscussionAdapter extends ArrayAdapter<JDiscussion> implements IDiscussionAdapter {

    private Activity activity;

    private List<JDiscussion> discussions;

    private LayoutInflater layoutInflater;

    private TextView discussion_text_view;

    public DiscussionAdapter(Activity activity, List<JDiscussion> discussions) {
        super(activity, R.layout.discussion_items, discussions);
        this.activity = activity;
        this.discussions = discussions;
    }

    @Override
    public JDiscussion getItem(int position) {
        if(position < getDiscussions().size()) {
            return discussions.get(position);
        }
        return null;
    }

    public List<JDiscussion> getDiscussions() {
        if(discussions == null) {
            discussions = new ArrayList<JDiscussion>();
        }
        return discussions;
    }

    public void setDiscussions(List<JDiscussion> discussions) {
        this.discussions = discussions;
    }

    @Override
    public int getCount() {
        return getDiscussions().size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(layoutInflater == null) {
            layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if(convertView == null) {
            convertView = layoutInflater.inflate(R.layout.discussion_items, null);
        }
        discussion_text_view = (TextView) convertView.findViewById(R.id.discussion_text_view);
        discussion_text_view.setTag(position);
        JDiscussion discussion = getItem(position);
        if(discussion != null) {
            StringBuilder buffer = new StringBuilder();
            String content = discussion.getContent();
            content = content.replaceAll("&amp;", "&");
            buffer.append(StringEscapeUtils.unescapeHtml4(content));
            content = Jsoup.parse(buffer.toString()).text();
            int len = content.length();
            if(len > 200) {
                content = content.substring(0, 200) + "...";
            }
            discussion_text_view.setText(content);
        }
        discussion_text_view.setTag(position);
        discussion_text_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = (int) view.getTag();
                if(position < 0 || discussions == null || position >= discussions.size())
                    return;
                JDiscussion discussion = discussions.get(position);
                if(discussion == null)
                    return;
                Intent intent = new Intent(getContext(), DiscussionDetailViewActivity.class);
                intent.putExtra(Constants.DISCUSSION_ENTITY_ID, discussion.getId());
                intent.putExtra(Constants.DISCUSSION_ENTITY_TYPE, EntityType.DISCUSSION.name());
                getContext().startActivity(intent);
            }
        });
        return convertView;
    }
}
