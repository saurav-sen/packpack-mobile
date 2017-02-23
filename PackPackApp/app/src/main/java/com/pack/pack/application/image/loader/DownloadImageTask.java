package com.pack.pack.application.image.loader;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.pack.pack.application.AppController;
import com.pack.pack.application.data.util.AbstractNetworkTask;
import com.pack.pack.application.data.util.ApiConstants;
import com.pack.pack.application.db.DbObject;
import com.pack.pack.application.db.ResourceURL;
import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIBuilder;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

    private static final String LOG_TAG = "DownloadImageTask";

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
        super(false, false, context);
        this.imageView = imageView;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.progressBar = progressBar;
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
            return apiParams;
        }
        Map<String, Object> apiParams = new HashMap<String, Object>();
        apiParams.put(APIConstants.ProtectedResource.RESOURCE_URL, inputObject);
        apiParams.put(APIConstants.Image.WIDTH, imageWidth);
        apiParams.put(APIConstants.Image.HEIGHT, imageHeight);
        return apiParams;
    }

    protected String lookupURL(String url) {
        /*if(ApiConstants.IS_PRODUCTION_ENV) {
            return url != null ? url.trim() : url;
        }*/
        return URLEncoder.encode(url) + "?w=" + imageWidth + "&h=" + imageHeight;
    }

    @Override
    protected Bitmap executeApi(API api) throws Exception {
        InputStream stream = null;
        Bitmap bitmap = null;
        try {
            String url = getInputObject();
            bitmap = AppController.getInstance().getLruBitmapCache().getBitmap(lookupURL(url));
            if(bitmap != null)
                return bitmap;
            stream = (InputStream) api.execute();
            if(stream != null) {
                bitmap = BitmapFactory.decodeStream(stream);
                if(bitmap != null) {
                    if (imageWidth > 0 && imageHeight > 0) {
                        bitmap = Bitmap.createScaledBitmap(bitmap, imageWidth, imageHeight, false);
                    } else {
                        bitmap = Bitmap.createScaledBitmap(bitmap, 800, 800, false);
                    }
                }
                AppController.getInstance().getLruBitmapCache().putBitmap(lookupURL(url), bitmap);
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
            imageView.setImageBitmap(bitmap);
            imageView.setVisibility(View.VISIBLE);
        }
        super.onPostExecute(bitmap);
    }
}
