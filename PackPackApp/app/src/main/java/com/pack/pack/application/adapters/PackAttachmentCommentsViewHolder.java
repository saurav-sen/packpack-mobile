package com.pack.pack.application.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.pack.pack.application.R;
import com.pack.pack.model.web.JPackAttachment;

/**
 *
 * Created by Saurav on 03-06-2016.
 */
class PackAttachmentCommentsViewHolder extends RecyclerView.ViewHolder {

    private ImageView userProfilePic;

    private TextView userName;

    private TextView comment;

    PackAttachmentCommentsViewHolder(View view) {
        super(view);
        userProfilePic = (ImageView) view.findViewById(R.id.user_pic);
        userName = (TextView) view.findViewById(R.id.from_user);
        comment = (TextView) view.findViewById(R.id.user_comment);
    }

    public ImageView getUserProfilePic() {
        return userProfilePic;
    }

    public TextView getUserName() {
        return userName;
    }

    public TextView getComment() {
        return comment;
    }
}
