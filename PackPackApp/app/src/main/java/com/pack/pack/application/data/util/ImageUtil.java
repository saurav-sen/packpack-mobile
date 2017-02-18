package com.pack.pack.application.data.util;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.FFmpegExecuteResponseHandler;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.pack.pack.application.AppController.APP_NAME;
import static com.pack.pack.application.AppController.MEDIA_TYPE_IMAGE;
import static com.pack.pack.application.AppController.MEDIA_TYPE_VIDEO;

/**
 * Created by Saurav on 12-06-2016.
 */
public class ImageUtil {

    private static final String LOG_TAG = "ImageUtil";

    private ImageUtil() {
    }

    public static Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    public static File getOutputMediaFile(int type) {
        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                APP_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(LOG_TAG, "Oops! Failed create "
                        + APP_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

   /* public static String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }*/

    public static Bitmap downscaleBitmap(Bitmap bitmap, int preferredWidth, int preferredHeight) {
        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();

        // FALSE case (ignore it to save time)
        if(preferredWidth >= originalWidth && preferredHeight >= originalHeight) {
            return bitmap;
        }

        float widthScaleRatio = preferredWidth < originalWidth ? ((float)preferredWidth) / originalWidth : 1.0f;
        float heightScaleRatio = preferredHeight < originalHeight ? ((float)preferredHeight) / originalHeight : 1.0f;

        Matrix transformation = new Matrix();
        transformation.postScale(widthScaleRatio, heightScaleRatio);

        Bitmap downscaleBitmap = Bitmap.createBitmap(bitmap, 0, 0, originalWidth, originalHeight, transformation, true);
        bitmap.recycle();
        return downscaleBitmap;
    }

    public static void compressVideo(final Context context, String inputFilePath, String outputFilePath, final CompressionStatusListener listener) throws Exception {
        // Using H.264 AI backed compression to compromise minimum on quality
        // @ http://writingminds.github.io/ffmpeg-android-java/
        //final String cmd = "-i " + inputFilePath + " -c:v libx264 -crf 24 -b:v 1M -c:a aac " + outputFilePath;
        final String cmd = "-i " + inputFilePath + " -vf scale=400:600 " + outputFilePath;
        String[] compress = new String[] {cmd};
        FFmpeg.getInstance(context).execute(compress, new FFmpegExecuteResponseHandler() {
            @Override
            public void onSuccess(String message) {
                if(listener != null) {
                    listener.onSuccess(message);
                }
            }

            @Override
            public void onProgress(String message) {

            }

            @Override
            public void onFailure(String message) {
                if(listener != null) {
                    listener.onFailure(message);
                }
            }

            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {
                if(listener != null) {
                    listener.onFinish();
                }
            }
        });
    }
}
