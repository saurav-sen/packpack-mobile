package com.pack.pack.application.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.pack.pack.application.AppController;
import com.pack.pack.application.R;
import com.pack.pack.application.image.loader.DownloadImageTask;
import com.pack.pack.application.view.TouchImageView;
import com.pack.pack.model.web.JPackAttachment;

import java.util.List;

/**
 * Created by Saurav on 06-06-2016.
 */
public class FullScreenAttachmentViewAdapter extends PagerAdapter {

    private Activity activity;
    private List<JPackAttachment> attachments;

    private int currentIndex = 0;

    public FullScreenAttachmentViewAdapter(Activity activity, List<JPackAttachment> attachments) {
        this.activity = activity;
        this.attachments = attachments;
    }

    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
    }

    @Override
    public int getCount() {
        return attachments.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(inflater == null) {
            inflater = activity.getLayoutInflater();
        }
        View view = inflater.inflate(R.layout.layout_fullscreen_attachment, container, false);
        //TouchImageView imgDisplay = (TouchImageView) view.findViewById(R.id.imgDisplay);
        ImageView imgDisplay = (ImageView) view.findViewById(R.id.imgDisplay);
        //ImageView imgDisplay = (ImageView) view.findViewById(R.id.imgDisplay);
        Button btnClose = (Button) view.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.finish();
            }
        });
        List<JPackAttachment> list = AppController.getInstance().getPackAttachments();
        if(list != null && !list.isEmpty() && position < list.size()) {
            JPackAttachment attachment = list.get(position);
            if("IMAGE".equalsIgnoreCase(attachment.getAttachmentType())) {
                 new DownloadImageTask(imgDisplay, 900, 900, FullScreenAttachmentViewAdapter.this.activity)
                         .execute(attachment.getAttachmentUrl());
            } else if("VIDEO".equalsIgnoreCase(attachment.getAttachmentType())) {
               // new DownloadImageTask(imgDisplay, -1, -1).execute(attachment.getAttachmentUrl());
            }
        }
        ((ViewPager)container).addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //super.destroyItem(container, position, object);
        ((ViewPager)container).removeView((View)object);
    }
}
