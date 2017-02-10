package com.pack.pack.application.data.util;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

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
}
