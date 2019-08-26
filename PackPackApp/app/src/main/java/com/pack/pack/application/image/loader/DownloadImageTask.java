package com.pack.pack.application.image.loader;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.pack.pack.application.AppController;
import com.pack.pack.application.R;
import com.pack.pack.application.data.util.AbstractNetworkTask;
import com.pack.pack.application.data.util.ApiConstants;
import com.pack.pack.application.db.DBUtil;
import com.pack.pack.application.db.DbObject;
import com.pack.pack.application.db.ResourceURL;
import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIBuilder;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Saurav on 21-04-2016.
 */
public class DownloadImageTask extends AbstractNetworkTask<String, Void, Bitmap> {

    private ImageView imageView;

    private int imageWidth = 0;

    private int imageHeight = 0;

    private String errorMsg;

    private ProgressBar progressBar;

    private boolean includeOauthToken;

    private boolean blindResize;

    private boolean noCaching;

    private static final String LOG_TAG = "DownloadImageTask";

    public static class SamplingOption {
        public boolean considerSuppliedDimensionDuringSampling = true;
        public int minimumInSampleSize = 2;
        public boolean blindResize = false;
    }

    private SamplingOption samplingOption = new SamplingOption();

    public DownloadImageTask setSamplingOption(SamplingOption samplingOption) {
        if(samplingOption != null) {
            this.samplingOption = samplingOption;
        }
        return this;
    }

    public DownloadImageTask(ImageView imageView, Context context) {
        this(imageView, context, null);
        //this(imageView, 900, 700, context);
    }

    public DownloadImageTask(ImageView imageView, Context context, ProgressBar progressBar) {
        this(imageView, -1, -1, context, progressBar);
        //this(imageView, 900, 700, context);
    }

    public DownloadImageTask(ImageView imageView, int imageWidth, int imageHeight, Context context) {
        this(imageView, imageWidth, imageHeight, context, null);
    }

    public DownloadImageTask(ImageView imageView, int imageWidth, int imageHeight, Context context, ProgressBar progressBar) {
        this(imageView, imageWidth, imageHeight, context, progressBar, false);
    }

    public DownloadImageTask(ImageView imageView, int imageWidth, int imageHeight, Context context, ProgressBar progressBar, boolean includeOauthToken) {
        this(imageView, imageWidth, imageHeight, context, progressBar, includeOauthToken, false);
    }

    public DownloadImageTask(ImageView imageView, int imageWidth, int imageHeight, Context context, ProgressBar progressBar, boolean includeOauthToken, boolean blindResize) {
        this(imageView, imageWidth, imageHeight, context, progressBar, includeOauthToken, blindResize, false);
    }

    public DownloadImageTask(ImageView imageView, int imageWidth, int imageHeight, Context context, ProgressBar progressBar, boolean includeOauthToken, boolean blindResize, boolean noCaching) {
        super(false, false, context, false);
        this.imageView = imageView;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.progressBar = progressBar;
        this.includeOauthToken = includeOauthToken;
        this.blindResize = blindResize;
        this.noCaching = noCaching;
    }

    @Override
    protected COMMAND command() {
        if(ApiConstants.IS_PRODUCTION_ENV) {
            return COMMAND.LOAD_EXTERNAL_RESOURCE;
        }
        return COMMAND.LOAD_RESOURCE;
    }

    @Override
    protected Map<String, Object> prepareApiParams(String inputObject) {
        if(ApiConstants.IS_PRODUCTION_ENV) {
            Map<String, Object> apiParams = new HashMap<String, Object>();
            apiParams.put(APIConstants.ExternalResource.RESOURCE_URL, inputObject);
            apiParams.put(APIConstants.ExternalResource.INCLUDE_OAUTH_TOKEN, String.valueOf(includeOauthToken));
            return apiParams;
        }
        Map<String, Object> apiParams = new HashMap<String, Object>();
        apiParams.put(APIConstants.ProtectedResource.RESOURCE_URL, inputObject);
        apiParams.put(APIConstants.Image.WIDTH, imageWidth);
        apiParams.put(APIConstants.Image.HEIGHT, imageHeight);
        return apiParams;
    }

    protected String lookupURL(String url) {
        //if(!ApiConstants.IS_AWS_S3_LINK_FOR_IMAGE) {
            //url = url != null ? url.trim() : url;
            //url = url + "?w=" + imageWidth + "&h=" + imageHeight;
        //}
        return url;
    }

    private class ImageDimension {
        private int newHeight;
        private int newWidth;

        ImageDimension(int newHeight, int newWidth) {
            this.newHeight = newHeight;
            this.newWidth = newWidth;
        }
    }

    private ImageDimension calculateResizeDimensions(Bitmap bitmap) {
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        return calculateResizeDimensions(height, width);
    }

    private ImageDimension calculateResizeDimensions(int bitmapHeight, int bitmapWidth) {
        //int shortSideMax = 900;
        //int longSideMax = 1440;

        Display display = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        int shortSideMax = size.x;
        int longSideMax = size.x+200;//(int)(size.y * 0.6f);

        int height = bitmapHeight;
        int width = bitmapWidth;

        float aspectRatio = (float)width / (float)height;
        if(aspectRatio >= 1.2f) {
            longSideMax = size.x;
            shortSideMax = (int) (longSideMax / 1.2f);
        } else if(aspectRatio <= 0.8f) {
            shortSideMax = size.x;
            longSideMax = (int) (shortSideMax / 0.8f);
        }

        float resizeRatio = 1.0f;
        if(width >= height) {
            if(width <= longSideMax && height <= shortSideMax) {
                float wRatio = (float)longSideMax / (float)width;
                float hRatio = (float)shortSideMax / (float)height;
                resizeRatio = Math.min(wRatio, hRatio);
                //return new ImageDimension(height, width);
            } else {
                float wRatio = (float)longSideMax / (float)width;
                float hRatio = (float)shortSideMax / (float)height;
                resizeRatio = Math.min(wRatio, hRatio);
            }
            /*if(resizeRatio == 1.0f) {
                resizeRatio = 1.2f;
            }*/
        } else {
            if(height <= longSideMax && width <= shortSideMax) {
                float wRatio = (float)longSideMax / (float)width;
                float hRatio = (float)shortSideMax / (float)height;
                resizeRatio = Math.min(wRatio, hRatio);
                //return new ImageDimension(height, width);
            } else {
                float wRatio = (float)shortSideMax / (float)width;
                float hRatio = (float)longSideMax / (float)height;
                resizeRatio = Math.min(wRatio, hRatio);
            }
            /*if(resizeRatio == 1.0f) {
                resizeRatio = 1.2f;
            }*/
        }
        height = (int)(height * resizeRatio);
        width = (int)(width * resizeRatio);
        return new ImageDimension(height, width);
    }

    private int copyStream(InputStream is, OutputStream os) {
        int total = 0;
        final int buffer_size = 1024;
        try {
            byte[] bytes = new byte[buffer_size];
            int count = is.read(bytes);
            while (count != -1) {
                total = total + count;
                os.write(bytes, 0, count);
                count = is.read(bytes);
            }
            return total;
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            return -1;
        }
    }

    private Bitmap decodeSampledBitmapFromStream(InputStream stream) {
        File file = null;
        Bitmap bitmap = null;
        try {
            file = File.createTempFile("img", ".tmp", getContext().getCacheDir());
            file.deleteOnExit();
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            file = null;
        }
        if(file == null) {
            bitmap = decodeDefaultSampledBitmapFromStream(stream);
        } else {
            try {
                OutputStream fileOutputStream = new FileOutputStream(file);
                int count = copyStream(stream, fileOutputStream);
                if(count == -1) {
                    Log.e(LOG_TAG, "Failed copying stream");
                    return null;
                }
                fileOutputStream.flush();
                fileOutputStream.close();

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(new FileInputStream(file), null, options);

                if(imageHeight > 0 && imageWidth > 0 && samplingOption != null && samplingOption.considerSuppliedDimensionDuringSampling) {
                    options.inSampleSize = calculateInSampleSize(options, imageWidth, imageHeight);
                } else {
                    ImageDimension dimension = calculateResizeDimensions(options.outHeight, options.outWidth);
                    options.inSampleSize = calculateInSampleSize(options, dimension.newWidth, dimension.newHeight);
                }

                // Lets make it half atleast to reduce memory usage
                if(options.inSampleSize < 2) {
                    options.inSampleSize = 2;
                }

                if(options.inSampleSize < samplingOption.minimumInSampleSize) {
                    options.inSampleSize = samplingOption.minimumInSampleSize;
                }

                // This may destroy image quality
               /* if(options.inSampleSize > 4) {
                    options.inSampleSize = 4;
                }*/

                options.inJustDecodeBounds = false;
                bitmap = BitmapFactory.decodeStream(new FileInputStream(file), null, options);
            } catch (FileNotFoundException e) {
                bitmap = decodeDefaultSampledBitmapFromStream(stream);
            } catch (Exception e) {
                bitmap = null;
            }
        }
        return bitmap;
    }

    private Bitmap decodeDefaultSampledBitmapFromStream(InputStream stream) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        return BitmapFactory.decodeStream(stream, null, options);
    }

    private int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    @Override
    protected Bitmap executeApi(API api) throws Exception {
        InputStream stream = null;
        Bitmap bitmap = null;
        try {
            String url = getInputObject();
            bitmap = AppController.getInstance().getLruBitmapCache().getBitmap(lookupURL(url));
            if(bitmap != null) {
                /*int ht = bitmap.getHeight();
                int wd = bitmap.getWidth();
                if(ht == imageHeight && wd == imageWidth) {
                    return bitmap;
                }
                ImageDimension dimension = calculateResizeDimensions(bitmap);
                if(ht != dimension.newHeight || wd != dimension.newWidth) {
                    bitmap = Bitmap.createScaledBitmap(bitmap, dimension.newWidth, dimension.newHeight, true);
                    String key = lookupURL(url);
                    AppController.getInstance().getLruBitmapCache().putBitmap(key, bitmap);
                }*/
                return bitmap;
            }
            stream = (InputStream) api.execute();
            if(stream != null) {

                /*===============================================*/
                //bitmap = BitmapFactory.decodeStream(stream);
                /*===============================================*/

                bitmap = decodeSampledBitmapFromStream(stream);

                if(bitmap != null) {
                    /*if (imageWidth > 0 && imageHeight > 0) {
                        bitmap = Bitmap.createScaledBitmap(bitmap, imageWidth, imageHeight, true);
                    } else {
                        bitmap = Bitmap.createScaledBitmap(bitmap, 900, 700, true);
                    }*/
                    if((blindResize || samplingOption.blindResize) && imageWidth > 0 && imageHeight > 0) {
                        bitmap = Bitmap.createScaledBitmap(bitmap, imageWidth, imageHeight, true);
                    } else {
                        ImageDimension dimension = calculateResizeDimensions(bitmap);
                        bitmap = Bitmap.createScaledBitmap(bitmap, dimension.newWidth, dimension.newHeight, true);
                    }
                }
                /*if(bitmap == null) {
                    bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.noimage);
                }*/
                String key = lookupURL(url);
                AppController.getInstance().getLruBitmapCache().putBitmap(key, bitmap);
            }
        } catch (Exception e) {
            errorMsg = e.getMessage();
        } finally {
            try {
                if(stream != null) {
                    stream.close();
                }
            } catch (IOException e) {
            }
        }
        return bitmap;
    }

    @Override
    protected DbObject convertObjectForStore(Bitmap successResult, String containerIdForObjectStore) {
        if(successResult == null)
            return null;
        String url = getInputObject();
        byte[] rawBytes = readRawBytes_compressed(successResult);
        ResourceURL resourceURL = new ResourceURL();
        resourceURL.setUrl(lookupURL(url));
        resourceURL.setBytes(rawBytes);
        return resourceURL;
    }

    private byte[] readRawBytes(Bitmap bitmap) {
        int size = bitmap.getRowBytes() * bitmap.getHeight();
        ByteBuffer byteBuffer = ByteBuffer.allocate(size);
        bitmap.copyPixelsToBuffer(byteBuffer);
        byte[] rawBytes = new byte[size];
        try {
            byteBuffer.get(rawBytes, 0, size);
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return rawBytes;
    }

    private byte[] readRawBytes_compressed(Bitmap bitmap) {
        byte[] rawBytes = null;
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream);
            rawBytes = byteArrayOutputStream.toByteArray();
        } finally {
            try {
                if(byteArrayOutputStream != null) {
                    byteArrayOutputStream.close();
                }
            } catch (IOException e) {
                // e.printStackTrace();
            }
        }
        return rawBytes;
    }

    @Override
    protected Bitmap doRetrieveFromDB(SQLiteDatabase readable, String inputObject) {
        Cursor cursor = null;
        Bitmap bitmap = null;
        try {
            String __SQL = "SELECT "+ ResourceURL.BLOB_CONTENT + " FROM " + ResourceURL.TABLE_NAME
                    + " WHERE " + ResourceURL.URL + "='" + lookupURL(inputObject) + "'";
            cursor = readable.rawQuery(__SQL, null);
            if(cursor.moveToFirst()) {
                byte[] rawBytes = cursor.getBlob(cursor.getColumnIndexOrThrow(ResourceURL.BLOB_CONTENT));
                bitmap = BitmapFactory.decodeByteArray(rawBytes, 0, rawBytes.length);
            }
        } catch (Exception e) {
            Log.i(LOG_TAG, e.getMessage());
            bitmap = null;
        } finally {
            if(cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return bitmap;
    }

    @Override
    protected String getFailureMessage() {
        return errorMsg;
    }

    @Override
    protected String getContainerIdForObjectStore() {
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if(progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
        if(imageView != null) {
            setImageBitmapToImageView(bitmap);
        }
        super.onPostExecute(bitmap);
    }

    protected void setImageBitmapToImageView(Bitmap bitmap) {
        if(bitmap != null) {
            imageView.setImageBitmap(bitmap);
            imageView.setVisibility(View.VISIBLE);
        } else {
            imageView.setVisibility(View.GONE);
        }
    }
}
