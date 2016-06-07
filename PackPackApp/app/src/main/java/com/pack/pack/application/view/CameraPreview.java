package com.pack.pack.application.view;

import android.content.Context;
import android.hardware.Camera;
import android.os.Build;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

/**
 * Created by Saurav on 04-06-2016.
 */
public class CameraPreview  extends SurfaceView implements SurfaceHolder.Callback {

    private Camera camera;

    private static final String LOG_TAG = "CameraPreview";

    public CameraPreview(Context context) {
        super(context);
        camera = initCamera();
        getHolder().addCallback(this);
    }

    private Camera initCamera() {
        Camera camera = null;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                camera = Camera.open(0); // 0 for back facing camera, 1 for front-facing camera
            } else {
                camera = Camera.open();
            }
        } catch (Exception e) {
            Log.d(LOG_TAG, "Failed to initialize camera: " + e.getMessage());
        }
        return camera;
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        try {
            if(camera == null) {
                camera = initCamera();
            }
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        } catch (IOException e) {
            Log.d(LOG_TAG, "Error setting camera preview: " + e.getMessage());
            camera.release();
            camera = null;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if(getHolder().getSurface() == null) {
            Log.d(LOG_TAG, "Camera Preview Surface Doesn't Exist");
            return;
        }
        try {
            if(camera != null) {
                camera.stopPreview();
            }
        } catch (Exception e) {
            Log.d(LOG_TAG, "Error: " + e.getMessage());
        }

        try {
            if(camera != null) {
                camera.setPreviewDisplay(getHolder());
                camera.startPreview();
            }
        } catch (IOException e) {
            Log.d(LOG_TAG, "Error setting camera preview: " + e.getMessage());
            if(camera != null) {
                camera.release();
                camera = null;
            }
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        if(camera != null) {
            camera.release();
            camera = null;
        }
    }

    public void stop() {
        if(camera != null) {
            camera.release();
            camera = null;
        }
    }
}
