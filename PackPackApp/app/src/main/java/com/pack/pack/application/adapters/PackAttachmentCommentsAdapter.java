package com.pack.pack.application.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.pack.pack.application.AppController;
import com.pack.pack.application.R;
import com.pack.pack.application.data.cache.InMemory;
import com.pack.pack.application.data.util.DateTimeUtil;
import com.pack.pack.application.image.loader.DownloadImageTask;
import com.pack.pack.application.view.CircleImageView;
import com.pack.pack.model.web.JComment;
import com.pack.pack.model.web.JPackAttachment;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * Created by Saurav on 03-06-2016.
 */
public class PackAttachmentCommentsAdapter extends ArrayAdapter<JComment> {

    private List<JComment> comments;

    private LayoutInflater inflater;
    private Activity activity;

    private View view;

    public PackAttachmentCommentsAdapter(Activity activity) {
        super(activity, R.layout.activity_attachment_comments_item);
        this.activity = activity;
    }

    public List<JComment> getComments() {
        if(comments == null) {
            comments = new ArrayList<JComment>();
        }
        return comments;
    }

    @Override
    public int getCount() {
        return getComments() != null ? getComments().size() : 0;
    }

    @Override
    public JComment getItem(int position) {
        if(position >= getComments().size())
            return null;
        return getComments().get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(inflater == null) {
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if(convertView == null) {
            convertView = inflater.inflate(R.layout.activity_attachment_comments_item, null);
        }
        if(view == null) {
            this.view = convertView;
        }

        JComment comment = getItem(position);

        CircleImageView user_profile_picture = (CircleImageView) convertView.findViewById(R.id.user_profile_picture);
        user_profile_picture.setImageResource(R.drawable.default_profile_picture_big);

        TextView user_name = (TextView) convertView.findViewById(R.id.user_name);
        TextView comment_create_time = (TextView) convertView.findViewById(R.id.comment_create_time);
        ImageButton deleteComment = (ImageButton) convertView.findViewById(R.id.deleteComment);
        deleteComment.setVisibility(View.GONE);

        TextView attachment_comment = (TextView) convertView.findViewById(R.id.attachment_comment);

        if(comment != null) {
            String userDisplayName = comment.getFromUserDisplayName();
            if(userDisplayName != null) {
                user_name.setText(userDisplayName);
            }

            String fromUserId = comment.getFromUserId();
            String currentUserId = AppController.getInstance().getUserId();
            if(currentUserId.equals(fromUserId)) {
                deleteComment.setVisibility(View.VISIBLE);
            }

            long t1 = comment.getDateTime();
            long t2 = InMemory.INSTANCE.getServerCurrentTimeInMillis();
            comment_create_time.setText(DateTimeUtil.sentencify(t1, t2));

            attachment_comment.setText(comment.getComment() + "");

            String profilePictureUrl = comment.getFromUserProfilePictureUrl();
            if(profilePictureUrl != null && !profilePictureUrl.trim().isEmpty()) {
                new DownloadImageTask(user_profile_picture, getContext()).execute(profilePictureUrl);
            }
        }

        return convertView;
    }

    //    @Override
//    public PackAttachmentCommentsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(
//                R.layout.activity_attachment_comments_recycler, parent,
//                false);
//        return new PackAttachmentCommentsViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(PackAttachmentCommentsViewHolder holder, int position) {
//        if(comments != null && position < comments.size()) {
//            JComment comment = comments.get(position);
//            holder.getUserName().setText(comment.getFromUserName());
//            holder.getComment().setText(comment.getComment());
//        }
//    }
//
//    @Override
//    public int getItemCount() {
//        return comments != null ? comments.size(): 0;
//    }
}
