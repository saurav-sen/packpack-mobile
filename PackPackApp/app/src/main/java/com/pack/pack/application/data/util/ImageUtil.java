package com.pack.pack.application.data.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.FFmpegExecuteResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpegLoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

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

    private static boolean ffMpegSupported = false;

    private ImageUtil() {
    }

    public static void loadFFMpeg(Context context) {
        try {
            FFmpeg.getInstance(context).loadBinary(new FFmpegLoadBinaryResponseHandler() {
                @Override
                public void onFailure() {
                    Log.d(LOG_TAG, "Failed to load FFMpeg");
                    ffMpegSupported = false;
                }

                @Override
                public void onSuccess() {
                    Log.d(LOG_TAG, "Successfully loaded FFMpeg");
                    ffMpegSupported = true;
                }

                @Override
                public void onStart() {

                }

                @Override
                public void onFinish() {

                }
            });
        } catch (FFmpegNotSupportedException e) {
            Log.d(LOG_TAG, "[FFMpeg Not Supported] " + e.getMessage(), e);
            ffMpegSupported = false;
        }
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

    private static void showAlert(Context context, String message) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setMessage(message);
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    private static final int EXPECTED_MIN_VIDEO_WIDTH_FOR_COMPRESSION = 200;//400;
    private static final int EXPECTED_MIN_VIDEO_HEIGHT_FOR_COMPRESSION = 200;//600;

    public static void compressVideo(final Context context, String inputFilePath, String outputFilePath, final CompressionStatusListener listener) throws Exception {
        // Using H.264 AI backed compression to compromise minimum on quality
        // @ http://writingminds.github.io/ffmpeg-android-java/
        //final String cmd = "-i " + inputFilePath + " -c:v libx264 -crf 24 -b:v 1M -c:a aac " + outputFilePath;
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(inputFilePath);
        int width = Integer.parseInt(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH).trim());
        int height = Integer.parseInt(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT).trim());
        String command = null;
        if(ffMpegSupported) {
            if (width <= EXPECTED_MIN_VIDEO_WIDTH_FOR_COMPRESSION && height <= EXPECTED_MIN_VIDEO_HEIGHT_FOR_COMPRESSION) {
                command = null;
            } else if (Math.abs(width - EXPECTED_MIN_VIDEO_WIDTH_FOR_COMPRESSION) > 10 && Math.abs(height - EXPECTED_MIN_VIDEO_HEIGHT_FOR_COMPRESSION) > 10) {
                int w = EXPECTED_MIN_VIDEO_WIDTH_FOR_COMPRESSION;
                int h = EXPECTED_MIN_VIDEO_HEIGHT_FOR_COMPRESSION;
                if (width < EXPECTED_MIN_VIDEO_WIDTH_FOR_COMPRESSION) {
                    w = width;
                }
                if (height < EXPECTED_MIN_VIDEO_HEIGHT_FOR_COMPRESSION) {
                    h = height;
                }
            /*int w = 200;
            int h = 200;*/
                command = "-i " + inputFilePath + " -vf scale=" + w + ":" + h + " " + outputFilePath;// + " -preset ultrafast";
                //command = "-i " + inputFilePath + " -c:v libx264 -crf 24 -b:v 1M -c:a aac " + outputFilePath;
            }
        }
        if(command == null) {
            listener.onFailure("");
            return;
        }
        final String cmd = command;
        String[] compress = new String[] {cmd};
        if(FFmpeg.getInstance(context).isFFmpegCommandRunning()) {
            FFmpeg.getInstance(context).killRunningProcesses();
        }
        FFmpeg.getInstance(context).execute(compress, new FFmpegExecuteResponseHandler() {
            @Override
            public void onSuccess(String message) {
                Log.d(LOG_TAG, "Compression On Success :: " + message);
                if (listener != null) {
                    listener.onSuccess(message);
                }
                //showAlert(context, "FFMpeg Success");
            }

            @Override
            public void onProgress(String message) {
                Log.d(LOG_TAG, "Compression On Progress :: " + message);
            }

            @Override
            public void onFailure(String message) {
                Log.d(LOG_TAG, "Compression Failed :: " + message);
                if (listener != null) {
                    listener.onFailure(message);
                }
                //showAlert(context, "FFMpeg Failure");
            }

            @Override
            public void onStart() {
                Log.d(LOG_TAG, "Compression Started.");
            }

            @Override
            public void onFinish() {
                Log.d(LOG_TAG, "Compression Finished.");
                if (listener != null) {
                    listener.onFinish();
                }
            }
        });
    }
}
