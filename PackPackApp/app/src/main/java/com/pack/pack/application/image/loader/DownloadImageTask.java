package com.pack.pack.application.image.loader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.pack.pack.application.AppController;
import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIBuilder;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Saurav on 21-04-2016.
 */
public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

    private ImageView imageView;

    private int imageWidth;

    private int imageHeight;

    public DownloadImageTask(ImageView imageView) {
        this(imageView, -1, -1);
    }

    public DownloadImageTask(ImageView imageView, int imageWidth, int imageHeight) {
        this.imageView = imageView;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
    }

    @Override
    protected Bitmap doInBackground(String... urls) {
        InputStream stream = null;
        Bitmap bitmap = null;
        try {
            String url = urls[0];
            bitmap = AppController.getInstance().getLruBitmapCache().getBitmap(url);
            if(bitmap != null)
                return bitmap;
            API api = APIBuilder.create()
                    .setAction(COMMAND.LOAD_RESOURCE)
                    .setOauthToken(AppController.getInstance().getoAuthToken())
                    .addApiParam(APIConstants.ProtectedResource.RESOURCE_URL, url)
                    .build();
            stream = (InputStream) api.execute();
            if(stream != null) {
                bitmap = BitmapFactory.decodeStream(stream);
                if(imageWidth > 0 && imageHeight > 0 && bitmap != null) {
                    bitmap = Bitmap.createScaledBitmap(bitmap, imageWidth, imageHeight, false);
                }
                AppController.getInstance().getLruBitmapCache().putBitmap(url, bitmap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(stream != null) {
                    stream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
       imageView.setImageBitmap(bitmap);
    }
}
