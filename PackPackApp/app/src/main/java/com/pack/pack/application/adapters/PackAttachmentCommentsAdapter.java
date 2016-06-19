package com.pack.pack.application.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pack.pack.application.R;
import com.pack.pack.model.web.JComment;

import java.util.List;

/**
 *
 * Created by Saurav on 03-06-2016.
 */
public class PackAttachmentCommentsAdapter extends RecyclerView.Adapter<PackAttachmentCommentsViewHolder> {

    private List<JComment> comments;

    public PackAttachmentCommentsAdapter(List<JComment> comments) {
        this.comments = comments;
    }

    @Override
    public PackAttachmentCommentsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.activity_attachment_comments_recycler, parent,
                false);
        return new PackAttachmentCommentsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PackAttachmentCommentsViewHolder holder, int position) {
        if(comments != null && position < comments.size()) {
            JComment comment = comments.get(position);
            holder.getUserName().setText(comment.getFromUserName());
            holder.getComment().setText(comment.getComment());
        }
    }

    @Override
    public int getItemCount() {
        return comments != null ? comments.size(): 0;
    }
}
