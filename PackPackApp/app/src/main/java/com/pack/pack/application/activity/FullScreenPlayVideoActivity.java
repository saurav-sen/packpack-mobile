package com.pack.pack.application.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.MediaController;
import android.widget.VideoView;

import com.google.android.youtube.player.YouTubePlayerView;
import com.pack.pack.application.AppController;
import com.pack.pack.application.R;
import com.pack.pack.application.data.util.ApiConstants;
import com.pack.pack.client.api.APIConstants;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Saurav on 12-03-2017.
 */
public class FullScreenPlayVideoActivity extends AbstractActivity {

    private ProgressDialog pDialog;
    private VideoView videoDisplay;

    //private YouTubePlayerView videoDisplayYoutube;

    private static final String LOG_TAG = "PlayVideo";

    public static final String VIDEO_URL = "VIDEO_URL";
    //public static final String IS_YOUTUBE = "IS_YOUTUBE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_play_video_fullscreen);
        //videoDisplayYoutube = (YouTubePlayerView) findViewById(R.id.videoDisplayYoutube);
        videoDisplay = (VideoView) findViewById(R.id.videoDisplay);

        pDialog = new ProgressDialog(FullScreenPlayVideoActivity.this);
        pDialog.setMessage("Loading...");
        pDialog.setIndeterminate(true);
        pDialog.setCancelable(false);
        pDialog.show();

        try {
            MediaController mediacontroller = new MediaController(
                    FullScreenPlayVideoActivity.this);
            mediacontroller.setAnchorView(videoDisplay);

            String videoURL = getIntent().getStringExtra(VIDEO_URL);
            if(videoURL == null || videoURL.trim().isEmpty()) {
                throw new Exception("VIDEO_URL can't be null");
            }
            final Map<String, String> __HTTP_REQUEST_HEADERS = new HashMap<String, String>();
            if(videoURL != null && videoURL.contains(ApiConstants.BASE_URL)) {
                __HTTP_REQUEST_HEADERS.put(APIConstants.AUTHORIZATION_HEADER, AppController.getInstance().getUserEmail());
            }
            videoDisplay.setMediaController(mediacontroller);
            videoDisplay.setVideoURI(Uri.parse(videoURL), __HTTP_REQUEST_HEADERS);
        } catch (Exception e) {
            Log.d(LOG_TAG, e.getMessage());
            e.printStackTrace();
        }

        videoDisplay.requestFocus();
        videoDisplay.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            public void onPrepared(MediaPlayer mp) {
                pDialog.dismiss();
                videoDisplay.start();
                videoDisplay.seekTo(0);
            }
        });
        videoDisplay.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                videoDisplay.stopPlayback();
                finish();
            }
        });

    }
}