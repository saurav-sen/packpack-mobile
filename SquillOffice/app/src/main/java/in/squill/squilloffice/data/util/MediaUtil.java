package in.squill.squilloffice.data.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Matrix;

/**
 * Created by Saurav on 12-06-2016.
 */
public class MediaUtil {

    private static final String LOG_TAG = "MediaUtil";

    private static boolean ffMpegSupported = false;

    private MediaUtil() {
    }

    public static Bitmap downscaleBitmap(Bitmap bitmap, int preferredWidth, int preferredHeight) {
        return downscaleBitmap(bitmap, preferredWidth, preferredHeight, false);
    }

    public static Bitmap downscaleBitmap(Bitmap bitmap, int preferredWidth, int preferredHeight, boolean recycle) {
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
        if(recycle) {
            bitmap.recycle();
        }
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
}
