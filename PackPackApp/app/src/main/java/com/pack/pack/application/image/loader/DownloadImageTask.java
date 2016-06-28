package com.pack.pack.application.image.loader;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.pack.pack.application.AppController;
import com.pack.pack.application.data.util.AbstractNetworkTask;
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
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Saurav on 21-04-2016.
 */
public class DownloadImageTask extends AbstractNetworkTask<String, Void, Bitmap> {

    private ImageView imageView;

    private int imageWidth;

    private int imageHeight;

    private String errorMsg;

    public DownloadImageTask(ImageView imageView) {
        this(imageView, -1, -1);
    }

    public DownloadImageTask(ImageView imageView, int imageWidth, int imageHeight) {
        super(true, true);
        this.imageView = imageView;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
    }

    @Override
    protected COMMAND command() {
        return COMMAND.LOAD_RESOURCE;
    }

    @Override
    protected Map<String, Object> prepareApiParams(String inputObject) {
        Map<String, Object> apiParams = new HashMap<String, Object>();
        apiParams.put(APIConstants.ProtectedResource.RESOURCE_URL, inputObject);
        return apiParams;
    }

    @Override
    protected Bitmap executeApi(API api) throws Exception {
        InputStream stream = null;
        Bitmap bitmap = null;
        try {
            String url = getInputObject();
            bitmap = AppController.getInstance().getLruBitmapCache().getBitmap(url);
            if(bitmap != null)
                return bitmap;
            stream = (InputStream) api.execute();
            if(stream != null) {
                bitmap = BitmapFactory.decodeStream(stream);
                if(imageWidth > 0 && imageHeight > 0 && bitmap != null) {
                    bitmap = Bitmap.createScaledBitmap(bitmap, imageWidth, imageHeight, false);
                }
                AppController.getInstance().getLruBitmapCache().putBitmap(url, bitmap);
            }
        } catch (Exception e) {
            errorMsg = e.getMessage();
        } finally {
            try {
                if(stream != null) {
                    stream.close();
                }
            } catch (IOException e) {
                // e.printStackTrace();
            }
        }
        return bitmap;
    }

    @Override
    protected DbObject convertObjectForStore(Bitmap successResult, String containerIdForObjectStore) {
        String url = getInputObject();
        byte[] rawBytes = readRawBytes(successResult);
        ResourceURL resourceURL = new ResourceURL();
        resourceURL.setUrl(url);
        resourceURL.setBytes(rawBytes);
        return resourceURL;
    }

    private byte[] readRawBytes(Bitmap bitmap) {
        byte[] rawBytes = null;
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 0, byteArrayOutputStream);
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
                    + " WHERE " + ResourceURL.URL + "='" + inputObject + "'";
            cursor = readable.rawQuery(__SQL, null);
            if(cursor.moveToFirst()) {
                byte[] rawBytes = cursor.getBlob(cursor.getColumnIndexOrThrow(ResourceURL.BLOB_CONTENT));
                bitmap = BitmapFactory.decodeByteArray(rawBytes, 0, rawBytes.length);
            }
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
       imageView.setImageBitmap(bitmap);
    }
}
