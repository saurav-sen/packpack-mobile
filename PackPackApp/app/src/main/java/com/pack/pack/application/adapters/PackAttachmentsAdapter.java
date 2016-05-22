package com.pack.pack.application.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;

import com.pack.pack.application.R;
import com.pack.pack.application.image.loader.DownloadImageTask;
import com.pack.pack.application.topic.activity.model.ParcelableAttachment;
import com.pack.pack.model.web.JPackAttachment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Saurav on 22-05-2016.
 */
public class PackAttachmentsAdapter extends ArrayAdapter<JPackAttachment> {

    private LayoutInflater inflater;
    private Activity activity;

    private List<JPackAttachment> attachments;

    public PackAttachmentsAdapter(Activity activity, List<JPackAttachment> attachments) {
        super(activity, R.layout.activity_pack_detail_item, attachments);
        this.activity = activity;
        this.attachments = attachments;
    }

    public List<JPackAttachment> getAttachments() {
        if(attachments == null) {
            attachments = new ArrayList<JPackAttachment>(10);
        }
        return attachments;
    }

    public void setAttachments(List<JPackAttachment> attachments) {
        this.attachments = attachments;
    }

    @Override
    public int getCount() {
        return attachments != null ? attachments.size() : 0;
    }

    @Override
    public JPackAttachment getItem(int position) {
        if(position >= attachments.size())
            return null;
        return attachments.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(inflater == null) {
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if(convertView == null) {
            convertView = inflater.inflate(R.layout.activity_pack_detail_item, null);
        }
        ImageView pack_attachment_img = (ImageView) convertView.findViewById(R.id.pack_attachment_img);

        Button pack_attachment_like = (Button) convertView.findViewById(R.id.pack_attachment_like);
        pack_attachment_like.setText("Like");
        pack_attachment_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO
            }
        });

        Button pack_attachment_comment = (Button) convertView.findViewById(R.id.pack_attachment_comment);
        pack_attachment_comment.setText("Comment");
        pack_attachment_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO
            }
        });

        Button pack_attachment_forward = (Button) convertView.findViewById(R.id.pack_attachment_forward);
        pack_attachment_forward.setText("Forward");
        pack_attachment_forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO
            }
        });

        JPackAttachment attachment = getItem(position);
        if(attachment != null) {
            new DownloadImageTask(pack_attachment_img).execute(attachment.getAttachmentUrl());
        }
        return convertView;
    }
}
