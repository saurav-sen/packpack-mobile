package com.pack.pack.application.image.loader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.pack.pack.application.AppController;
import com.pack.pack.application.data.util.ApiConstants;
import com.pack.pack.application.view.ProfilePicturePreference;
import com.pack.pack.application.view.util.ProfilePicturePreferenceCache;
import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIBuilder;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Saurav on 19-03-2017.
 */
public class DownloadProfilePictureTask extends AsyncTask<String, Void, Bitmap> {

    private static final String LOG_TAG = "DP_Download";

    public DownloadProfilePictureTask() {
        super();
    }

    private COMMAND command() {
        if(ApiConstants.IS_PRODUCTION_ENV) {
            return COMMAND.LOAD_EXTERNAL_RESOURCE;
        }
        return COMMAND.LOAD_RESOURCE;
    }

    private String lookupURL(String url) {
        //if(ApiConstants.IS_PRODUCTION_ENV) {
        url = url != null ? url.trim() : url;
        //url = url + "?w=" + imageWidth + "&h=" + imageHeight;
        return url;// + "?w=" + imageWidth + "&h=" + imageHeight;
    }

    private Map<String, Object> prepareApiParams(String url) {
        if(ApiConstants.IS_PRODUCTION_ENV) {
            Map<String, Object> apiParams = new HashMap<String, Object>();
            apiParams.put(APIConstants.ExternalResource.RESOURCE_URL, url);
            apiParams.put(APIConstants.ExternalResource.INCLUDE_OAUTH_TOKEN,
                    String.valueOf(AppController.getInstance().getoAuthToken()));
            return apiParams;
        }
        Map<String, Object> apiParams = new HashMap<String, Object>();
        apiParams.put(APIConstants.ProtectedResource.RESOURCE_URL, url);
        return apiParams;
    }

    private Bitmap executeApi(API api, String url) throws Exception {
        InputStream stream = null;
        Bitmap bitmap = null;
        try {
            bitmap = AppController.getInstance().getLruBitmapCache().getBitmap(lookupURL(url));
            if(bitmap != null) {
                /*int ht = bitmap.getHeight();
                int wd = bitmap.getWidth();
                if(ht != imageHeight || wd != imageWidth) {
                    if (imageWidth > 0 && imageHeight > 0) {
                        bitmap = Bitmap.createScaledBitmap(bitmap, imageWidth, imageHeight, false);
                    } else {
                        bitmap = Bitmap.createScaledBitmap(bitmap, 900, 700, false);
                    }
                }*/
                ProfilePicturePreferenceCache.INSTANCE.setProfilePicture(bitmap);
                return bitmap;
            }
            stream = (InputStream) api.execute();
            if(stream != null) {
                bitmap = BitmapFactory.decodeStream(stream);
                if(bitmap != null) {
                   /* if (imageWidth > 0 && imageHeight > 0) {
                        bitmap = Bitmap.createScaledBitmap(bitmap, imageWidth, imageHeight, false);
                    } else {
                        bitmap = Bitmap.createScaledBitmap(bitmap, 900, 700, false);
                    }*/
                    AppController.getInstance().getLruBitmapCache().putBitmap(lookupURL(url), bitmap);
                    ProfilePicturePreferenceCache.INSTANCE.setProfilePicture(bitmap);
                }
            }
        } catch (Exception e) {
            Log.d(LOG_TAG, e.getMessage(), e);
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
    protected Bitmap doInBackground(String... params) {
        Bitmap bitmap = ProfilePicturePreferenceCache.INSTANCE.getProfilePicture();
        if(bitmap != null) {
            return bitmap;
        }
        if(params == null || params.length == 0) {
            return null;
        }

        String url = params[0];
        try {
            String oAuthToken = AppController.getInstance().getoAuthToken();
            APIBuilder builder = APIBuilder.create(ApiConstants.BASE_URL).setAction(command())
                    .setOauthToken(oAuthToken);
            Map<String, Object> apiParams = prepareApiParams(url);
            if(apiParams != null && !apiParams.isEmpty()) {
                Iterator<String> itr = apiParams.keySet().iterator();
                while (itr.hasNext()) {
                    String paramName = itr.next();
                    Object paramValue = apiParams.get(paramName);
                    builder.addApiParam(paramName, paramValue);
                }
            }
            API api = builder.build();
            bitmap = executeApi(api, url);
            if(bitmap != null) {
                ProfilePicturePreferenceCache.INSTANCE.setProfilePicture(bitmap);
            }
        } catch (Exception e) {
            e.printStackTrace();;
        }

        return bitmap;
    }
}
