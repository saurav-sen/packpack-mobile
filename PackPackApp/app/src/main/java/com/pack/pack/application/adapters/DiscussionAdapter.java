package com.pack.pack.application.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
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

    //private ImageButton discussion_open;

    private TextView discussion_title;

    private TextView discussion_description;

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

       // discussion_open = (ImageButton) convertView.findViewById(R.id.discussion_open);
       // discussion_open.setTag(position);
        discussion_title = (TextView) convertView.findViewById(R.id.discussion_title);
        discussion_title.setTag(position);
        discussion_description = (TextView) convertView.findViewById(R.id.discussion_description);
        discussion_description.setTag(position);

        JDiscussion discussion = getItem(position);
        if(discussion != null) {
            String title = discussion.getTitle();
            SpannableString spannableString = new SpannableString(title);
            spannableString.setSpan(new UnderlineSpan(), 0, spannableString.length(), 0);
            spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, spannableString.length(), 0);
            discussion_title.setText(spannableString);

            String content = discussion.getContent();
            content = content.replaceAll("&amp;", "&");
            content = StringEscapeUtils.unescapeHtml4(content);
            if(content.length() > 200) {
                content = content.substring(0, 200);
                content = content + "...";
            }

            content = Jsoup.parse(content).text();
            discussion_description.setText(content);
        }

        discussion_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = (int) view.getTag();
                openDiscussion(position);
            }
        });

        discussion_description.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = (int) view.getTag();
                openDiscussion(position);
            }
        });

        return convertView;
    }

    private void openDiscussion(int position) {
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
}
