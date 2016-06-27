package com.pack.pack.application.image.loader;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.pack.pack.application.AppController;
import com.pack.pack.application.data.util.AbstractNetworkTask;
import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIBuilder;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;

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
    protected Bitmap doRetrieveFromDB(SQLiteDatabase readable, String inputObject) {
        return super.doRetrieveFromDB(readable, inputObject);
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
