package com.pack.pack.application.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;

import com.pack.pack.application.R;
import com.pack.pack.model.web.JDiscussion;
import com.pack.pack.model.web.JUser;

import org.apache.commons.lang3.StringEscapeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Saurav on 02-07-2016.
 */
public class DiscussionAdapter extends ArrayAdapter<JDiscussion> {

    private Activity activity;

    private List<JDiscussion> discussions;

    private LayoutInflater layoutInflater;

    private WebView discussion_web_view;

    public DiscussionAdapter(Activity activity, List<JDiscussion> discussions) {
        super(activity, R.layout.discusion_items, discussions);
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
            convertView = layoutInflater.inflate(R.layout.discusion_items, null);
        }
        discussion_web_view = (WebView) convertView.findViewById(R.id.discussion_web_view);
        JDiscussion discussion = getItem(position);
        if(discussion != null) {
            StringBuilder buffer = new StringBuilder();
            String content = discussion.getContent();
            /*if(!content.startsWith("&lt;")) {
               // content = "&lt;div&gt;" + content + "&lt;/div&gt;";
                content = content.substring(content.indexOf("&lt;") + 5);
            }*/
            content = content.replaceAll("&amp;", "&");
            buffer.append(StringEscapeUtils.unescapeHtml4(content));
            buffer.append("<div style=\"overflow: hidden; width: 100%\"><div style=\"float: left;padding-top: 50px; color: grey\">");
            JUser user = discussion.getFromUser();
            buffer.append(user.getUsername());
            buffer.append("</div><div style=\"float: right;padding-top: 50px; color: grey\">");
            buffer.append(user.getName());
            buffer.append("</div></div>");
            discussion_web_view.loadData(buffer.toString(), "text/html", null);
        }
        return convertView;
    }
}
