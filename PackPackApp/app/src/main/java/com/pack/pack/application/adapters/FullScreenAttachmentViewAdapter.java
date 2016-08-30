package com.pack.pack.application.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import com.pack.pack.application.AppController;
import com.pack.pack.application.R;
import com.pack.pack.application.image.loader.DownloadImageTask;
import com.pack.pack.application.view.TouchImageView;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.model.web.JPackAttachment;
import com.pack.pack.model.web.PackAttachmentType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Saurav on 06-06-2016.
 */
public class FullScreenAttachmentViewAdapter extends PagerAdapter {

    private Activity activity;
    private List<JPackAttachment> attachments;

    private int currentIndex = 0;

    private MediaController mediaController;

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
        if(mediaController == null) {
            mediaController = new MediaController(this.activity);
        }
        View view = inflater.inflate(R.layout.layout_fullscreen_attachment, container, false);
        ImageView imgDisplay = (ImageView) view.findViewById(R.id.imgDisplay);
        final VideoView videoView = (VideoView) view.findViewById(R.id.videoDisplay);

        List<JPackAttachment> list = AppController.getInstance().getPackAttachments();
        if(list != null && !list.isEmpty() && position < list.size()) {
            JPackAttachment attachment = list.get(position);
            if(PackAttachmentType.IMAGE.name().equalsIgnoreCase(attachment.getAttachmentType())) {
                videoView.setVisibility(View.GONE);
                new DownloadImageTask(imgDisplay, 900, 900, FullScreenAttachmentViewAdapter.this.activity)
                         .execute(attachment.getAttachmentUrl());
            } else if(PackAttachmentType.VIDEO.name().equalsIgnoreCase(attachment.getAttachmentType())) {
                imgDisplay.setVisibility(View.GONE);
                videoView.setMediaController(mediaController);
                Map<String, String> __HTTP_REQUEST_HEADERS = new HashMap<String, String>();
                __HTTP_REQUEST_HEADERS.put(APIConstants.AUTHORIZATION_HEADER, AppController.getInstance().getoAuthToken());
                videoView.setVideoURI(Uri.parse(attachment.getAttachmentUrl()), __HTTP_REQUEST_HEADERS);
                mediaController.setAnchorView(videoView);
                videoView.requestFocus();
                videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        videoView.seekTo(0);
                        videoView.start();
                    }
                });
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
