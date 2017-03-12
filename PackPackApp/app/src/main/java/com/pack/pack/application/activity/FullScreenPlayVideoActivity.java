package com.pack.pack.application.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.MediaController;
import android.widget.VideoView;

import com.pack.pack.application.R;

/**
 * Created by Saurav on 12-03-2017.
 */
public class FullScreenPlayVideoActivity extends Activity {

    ProgressDialog pDialog;
    VideoView videoDisplay;

    private static final String LOG_TAG = "PlayVideo";

    public static final String VIDEO_URL = "VIDEO_URL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_play_video_fullscreen);
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
            Uri video = Uri.parse(videoURL);
            videoDisplay.setMediaController(mediacontroller);
            videoDisplay.setVideoURI(video);

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