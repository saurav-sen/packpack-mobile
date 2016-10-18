package com.pack.pack.application.adapters;

import android.widget.ListAdapter;

import com.pack.pack.model.web.JDiscussion;

import java.util.List;

/**
 * Created by Saurav on 19-10-2016.
 */
public interface IDiscussionAdapter extends ListAdapter {

    public void setDiscussions(List<JDiscussion> discussions);

    public void notifyDataSetChanged();
}
