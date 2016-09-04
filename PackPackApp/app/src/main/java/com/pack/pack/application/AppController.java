package com.pack.pack.application;

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.pack.pack.model.web.JPackAttachment;
import com.pack.pack.model.web.JUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class AppController extends Application {

    public static final String TAG = AppController.class.getSimpleName();

    public static final String APP_NAME = "PackPack";

    //public static final boolean SUPPORT_OFFLINE = false;

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    LruBitmapCache mLruBitmapCache;

    public static final String TOPIC_PARCELABLE_KEY = "topic_parcel";

    public static final String PACK_PARCELABLE_KEY = "pack_parcel";

    public static final String PACK_ATTACHMENT_ID_KEY = "pack_attachment_id";

    public static final int SIGNUP_ACTIVITY_REQUEST_CODE = 100;

    public static final int RESET_PASSWD_ACTIVITY_REQUEST_CODE = 200;

    public static final int CAMERA_CAPTURE_PHOTO_REQUEST_CODE = 300;

    public static final int CAMERA_RECORD_VIDEO_REQUEST_CODE = 400;

    public static final int CREATE_TOPIC_REQUSET_CODE = 401;

    public static final int IMAGE_PICK_REQUSET_CODE = 402;

    public static final String UPLOAD_FILE_PATH = "upload_file_path";

    public static final String UPLOAD_FILE_IS_PHOTO = "upload_file_is_photo";

    public static final String UPLOAD_ENTITY_ID_KEY = "entity_id";

    public static final String UPLOAD_ENTITY_TYPE_KEY = "entity_type";

    public static final String UPLOAD_ATTACHMENT_TITLE = "upload_attachment_title";

    public static final String UPLOAD_ATTACHMENT_DESCRIPTION = "upload_attachment_description";

    public static final String TOPIC_ID_KEY = "topic_id";

    public String getoAuthToken() {
        return oAuthToken;
    }

    public void setoAuthToken(String oAuthToken) {
        this.oAuthToken = oAuthToken;
    }

    private String oAuthToken;

    private JUser user;

    public List<String> getFollowedCategories() {
        if(followedCategories == null) {
            followedCategories = new ArrayList<String>(10);
        }
        return followedCategories;
    }

    private List<String> followedCategories;

    public void setUser(JUser user) {
        this.user = user;
    }

    public String getUserId() {
        return user != null ? user.getId() : null;
    }

    private static AppController mInstance;

    public static final String ANDROID_APP_CLIENT_KEY = "53e8a1f2-7568-4ac8-ab26-45738ca02599";
    public static final String ANDROID_APP_CLIENT_SECRET = "b1f6d761-dcb7-482b-a695-ab17e4a29b25";

    private static final String USERNAME = "sourabhnits@gmail.com";

    public static final int APP_EXTERNAL_STORAGE_READ_REQUEST_CODE = 114;
    public static final int APP_EXTERNAL_STORAGE_WRITE_REQUEST_CODE = 115;
    public static final int CAMERA_ACCESS_REQUEST_CODE = 116;
    public static final int LOCATION_FINE_ACCESS_REQUEST_CODE = 117;
    public static final String RESULT_RECEIVER = "resultReceiver";
    public static final String LOCATION_PARCELABLE_ADDRESS_KEY = "address";
    public static final String LOCATION_PARCELABLE_ERR_MSG_KEY = "errMsg";
    public static final int SUCCESS_RESULT = 0;
    public static final int FAILURE_RESULT = 1;

    public static final int PLACE_AUTO_COMPLETE_REQ_CODE = 118;

    private boolean enableShareOption = true;

    private boolean cameraPermissionGranted = false;

    private boolean externalReadGranted = false;

    public boolean isExternalReadGranted() {
        return externalReadGranted;
    }

    public void externalReadGranted() {
        this.externalReadGranted = true;
    }

    public void externalReadDenied() {
        this.externalReadGranted = false;
    }

    private Map<String, JPackAttachment> packAttachmentCache = new WeakHashMap<String, JPackAttachment>();

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    private List<JPackAttachment> packAttachments;

    private boolean signInProgress;

    private String userPassword;

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        /*UserInfo userInfo = DBUtil.loadLastLoggedInUserInfo();
        if(userInfo != null) {
            new LoginTask().execute(userInfo);
        }*/
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