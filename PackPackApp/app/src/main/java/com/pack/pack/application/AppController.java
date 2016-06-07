package com.pack.pack.application;

import android.app.Application;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.pack.pack.application.topic.activity.model.UserInfo;
import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIBuilder;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.model.web.JPackAttachment;
import com.pack.pack.model.web.JUser;
import com.pack.pack.oauth1.client.AccessToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class AppController extends Application {

    public static final String TAG = AppController.class.getSimpleName();

    public static final String APP_NAME = "PackPack";

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    LruBitmapCache mLruBitmapCache;

    public static final String TOPIC_PARCELABLE_KEY = "topic_parcel";

    public static final String PACK_PARCELABLE_KEY = "pack_parcel";

    public static final String PACK_ATTACHMENT_ID_KEY = "pack_attachment_id";

    public static final int CAMERA_CAPTURE_PHOTO_REQUEST_CODE = 300;

    public static final int CAMERA_RECORD_VIDEO_REQUEST_CODE = 400;

    public static final String UPLOAD_FILE_PATH = "upload_file_path";

    public static final String UPLOAD_FILE_IS_PHOTO = "upload_file_is_photo";

    public static final String UPLOAD_ENTITY_ID_KEY = "entity_id";

    public static final String UPLOAD_ENTITY_TYPE_KEY = "entity_type";

    public static final String TOPIC_ID_KEY = "topic_id";

    public String getoAuthToken() {
        return oAuthToken;
    }

    public void setoAuthToken(String oAuthToken) {
        this.oAuthToken = oAuthToken;
    }

    private String oAuthToken;

    private JUser user;

    public String getUserId() {
        return user != null ? user.getId() : null;
    }

    private static AppController mInstance;

    private static final String ANDROID_APP_CLIENT_KEY = "53e8a1f2-7568-4ac8-ab26-45738ca02599";
    private static final String ANDROID_APP_CLIENT_SECRET = "b1f6d761-dcb7-482b-a695-ab17e4a29b25";

    private static final String USERNAME = "sourabhnits@gmail.com";

    public static final int APP_EXTERNAL_STORAGE_WRITE_REQUEST_CODE = 115;
    public static final int CAMERA_ACCESS_REQUEST_CODE = 116;

    private boolean enableShareOption = true;

    private boolean cameraPermissionGranted = false;

    private Map<String, JPackAttachment> packAttachmentCache = new WeakHashMap<String, JPackAttachment>();

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    private List<JPackAttachment> packAttachments;

    @Override
    public void onCreate() {
        super.onCreate();
        new AutoLoginTask().execute(new UserInfo(USERNAME, "P@ckp@K#123"));
        mInstance = this;
    }

    public void waitForLoginSuccess() {
        while (oAuthToken == null) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
              //  e.printStackTrace();
            } finally {
            }
        }
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            getLruBitmapCache();
            mImageLoader = new ImageLoader(this.mRequestQueue, mLruBitmapCache);
        }

        return this.mImageLoader;
    }

    public LruBitmapCache getLruBitmapCache() {
        if (mLruBitmapCache == null)
            mLruBitmapCache = new LruBitmapCache();
        return this.mLruBitmapCache;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    private class AutoLoginTask extends AsyncTask<UserInfo, Integer, String> {
        @Override
        protected String doInBackground(UserInfo... userInfos) {
            try {
                API api = APIBuilder
                        .create()
                        .setAction(COMMAND.SIGN_IN)
                        .addApiParam(APIConstants.Login.CLIENT_KEY,
                                ANDROID_APP_CLIENT_KEY)
                        .addApiParam(APIConstants.Login.CLIENT_SECRET,
                                ANDROID_APP_CLIENT_SECRET)
                        .addApiParam(APIConstants.Login.USERNAME, userInfos[0].getUsername())
                        .addApiParam(APIConstants.Login.PASSWORD, userInfos[0].getPassword()).build();
                AccessToken token = (AccessToken)api.execute();
                if(token == null)
                    return null;
                oAuthToken = token.getToken();

                api = APIBuilder
                        .create()
                        .setAction(COMMAND.GET_USER_BY_USERNAME)
                        .setOauthToken(oAuthToken)
                        .addApiParam(APIConstants.User.USERNAME,
                                userInfos[0].getUsername()).build();
                user = (JUser) api.execute();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
            }
            return oAuthToken;
        }
    }

    public boolean isEnableShareOption() {
        return enableShareOption;
    }

    public void enableShareOption() {
        enableShareOption = true;
    }

    public void disableShareOption() {
        enableShareOption = false;
    }

    public boolean isCameraPermissionGranted() {
        return cameraPermissionGranted;
    }

    public void cameraPermissionGranted() {
        cameraPermissionGranted = true;
    }

    public void cameraPermisionDenied() {
        cameraPermissionGranted = false;
    }

    public void cachePackAttachments(List<JPackAttachment> attachments) {
        packAttachmentCache.clear();
        if(attachments == null || attachments.isEmpty())
            return;
        for(JPackAttachment attachment : attachments) {
            packAttachmentCache.put(attachment.getId(), attachment);
        }
    }

    public JPackAttachment getPackAttachmentFromCache(String id) {
        return packAttachmentCache.get(id);
    }

    public List<JPackAttachment> getPackAttachments() {
        if(packAttachments == null) {
            packAttachments = new ArrayList<JPackAttachment>();
        }
        return packAttachments;
    }
}