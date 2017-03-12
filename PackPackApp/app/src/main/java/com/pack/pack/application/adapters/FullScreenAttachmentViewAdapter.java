package com.pack.pack.application.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import com.pack.pack.application.AppController;
import com.pack.pack.application.R;
import com.pack.pack.application.activity.FullScreenPlayVideoActivity;
import com.pack.pack.application.data.util.ApiConstants;
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

    //private MediaController mediaController;

    private ImageView imgDisplay;

    private ImageView imgDisplay_play;

    //private VideoView videoView;

    //private static final String LOG_TAG = "FullScreen";

   // private Mode mode;

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
        /*if(mediaController == null) {
            mediaController = new MediaController(this.activity);
        }*/
        View view = inflater.inflate(R.layout.layout_fullscreen_attachment, container, false);

        imgDisplay = (ImageView) view.findViewById(R.id.imgDisplay);
        //videoView = (VideoView) view.findViewById(R.id.videoDisplay);

        imgDisplay_play = (ImageView) view.findViewById(R.id.imgDisplay_play);

        /*if(mode == Mode.VIDEO) {
            imgDisplay.setVisibility(View.GONE);
            imgDisplay_play.setVisibility(View.GONE);
            videoView.setVisibility(View.VISIBLE);
        } else {
            videoView.setVisibility(View.VISIBLE);
            imgDisplay.setVisibility(View.VISIBLE);
            imgDisplay_play.setVisibility(View.GONE);
        }*/

        List<JPackAttachment> list = AppController.getInstance().getPackAttachments();
        if(list != null && !list.isEmpty() && position < list.size()) {
            final JPackAttachment attachment = list.get(position);
            if(PackAttachmentType.IMAGE.name().equalsIgnoreCase(attachment.getAttachmentType())) {
                prepareImageRender(attachment, false);
            } else if(PackAttachmentType.VIDEO.name().equalsIgnoreCase(attachment.getAttachmentType())) {
                /*if(mode == Mode.VIDEO) {
                    prepareVideoPlay(attachment);
                } else {
                    prepareImageRender(attachment, true);
                }*/
                prepareImageRender(attachment, true);
            }
        }

        imgDisplay_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JPackAttachment attachment = attachments.get(currentIndex);
                Intent intent = new Intent(activity, FullScreenPlayVideoActivity.class);
                intent.putExtra(FullScreenPlayVideoActivity.VIDEO_URL, attachment.getAttachmentUrl());
                activity.startActivity(intent);
            }
        });

        ((ViewPager)container).addView(view);
        return view;
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        currentIndex = position;
        JPackAttachment attachment = attachments.get(position);
        if(PackAttachmentType.IMAGE.name().equalsIgnoreCase(attachment.getAttachmentType())) {
            imgDisplay_play.setVisibility(View.GONE);
        } else if(PackAttachmentType.VIDEO.name().equalsIgnoreCase(attachment.getAttachmentType())) {
            imgDisplay_play.setVisibility(View.VISIBLE);
        }
    }

    private void prepareImageRender(JPackAttachment attachment, boolean thumbnail) {
        //videoView.setVisibility(View.GONE);
        boolean isIncludeOauthToken = false;
        String url = attachment.getAttachmentUrl();
        if(thumbnail) {
            url = attachment.getAttachmentThumbnailUrl();
        }
        if(url == null)
            return;
        if(url != null && url.contains(ApiConstants.BASE_URL)) {
            isIncludeOauthToken = true;
        }
        new DownloadImageTask(imgDisplay, 900, 900, FullScreenAttachmentViewAdapter.this.activity, null, isIncludeOauthToken)
                .execute(url);
    }

    /*private void prepareVideoPlay(JPackAttachment attachment) {
        imgDisplay.setVisibility(View.GONE);
        videoView.setMediaController(mediaController);
        final Map<String, String> __HTTP_REQUEST_HEADERS = new HashMap<String, String>();
        String url = attachment.getAttachmentUrl();
        if(url != null && url.contains(ApiConstants.BASE_URL)) {
            __HTTP_REQUEST_HEADERS.put(APIConstants.AUTHORIZATION_HEADER, AppController.getInstance().getoAuthToken());
        }
        videoView.setVideoURI(Uri.parse(url), __HTTP_REQUEST_HEADERS);
        mediaController.setAnchorView(videoView);
        //mediaController.hide();
        videoView.requestFocus();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                videoView.seekTo(0);
                videoView.start();
            }
        });
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                //videoView.setVideoURI(Uri.parse(attachment.getAttachmentUrl()), __HTTP_REQUEST_HEADERS);
                //videoView.requestFocus();
                videoView.seekTo(0);
                //mediaController.show();
            }
        });
    }*/

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //super.destroyItem(container, position, object);
        View view = (View)object;
        ((ViewPager)container).removeView(view);
    }

}
