package com.pack.pack.application;

import android.app.Application;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
//import com.flurry.android.FlurryAgent;
import com.pack.pack.application.cz.fhucho.android.util.SimpleDiskCacheInitializer;
import com.pack.pack.application.data.cache.HttpCache;
import com.pack.pack.application.data.cache.HttpCacheFactory;
import com.pack.pack.application.db.DBUtil;
import com.pack.pack.application.db.SquillDbHelper;
import com.pack.pack.application.db.UserInfo;
import com.pack.pack.application.topic.activity.model.UploadAttachmentData;
import com.pack.pack.application.view.util.AdBlocker;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.model.web.JUser;
import com.squill.feed.web.model.JRssFeedType;

import org.apache.http.entity.mime.content.ContentBody;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

//import io.branch.referral.Branch;

public class AppController extends Application {

    public static final String TAG = AppController.class.getSimpleName();

    public static final String APP_NAME = "PackPack";

    public static final String PACKAGE_NAME = "com.pack.pack.application";

    //public static final boolean SUPPORT_OFFLINE = false;

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    LruBitmapCache mLruBitmapCache;

    public static final int SIGNUP_ACTIVITY_REQUEST_CODE = 100;

    public static final int RESET_PASSWD_ACTIVITY_REQUEST_CODE = 200;

    public static final int CAMERA_CAPTURE_PHOTO_REQUEST_CODE = 300;

    public static final int CREATE_TOPIC_REQUSET_CODE = 401;

    public static final int CROP_PHOTO_REQUEST_CODE = 420;

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    private String userEmail;

    private JUser user;

    public String getApkUrl() {
        return apkUrl;
    }

    public void setApkUrl(String apkUrl) {
        this.apkUrl = apkUrl;
    }

    private String apkUrl;

    public JUser getUser() {
        return user;
    }

    public List<String> getFollowedCategories() {
        if(followedCategories == null) {
            followedCategories = new ArrayList<String>(10);
        }
        return followedCategories;
    }

    private List<String> followedCategories;

    public void setUser(JUser user) {
        this.user = user;
        if(user != null) {
            setUserEmail(user.getUsername());
        }
    }

    public String getUserId() {
        return user != null ? user.getId() : "";
    }

    private boolean landingPageActive = false;

    public void setLandingPageActive(boolean landingPageActive) {
        this.landingPageActive = landingPageActive;
    }

    public boolean isLandingPageActive() {
        return landingPageActive;
    }

    private static AppController mInstance;

    public static final String ANDROID_APP_CLIENT_KEY = "53e8a1f2-7568-4ac8-ab26-45738ca02599";
    public static final String ANDROID_APP_CLIENT_SECRET = "b1f6d761-dcb7-482b-a695-ab17e4a29b25";

    public static final int APP_EXTERNAL_STORAGE_READ_REQUEST_CODE = 114;
    public static final int APP_EXTERNAL_STORAGE_WRITE_REQUEST_CODE = 115;
    public static final int CAMERA_ACCESS_REQUEST_CODE = 116;
    public static final int LOCATION_COARSE_ACCESS_REQUEST_CODE = 117;
    public static final int READ_CONTACTS_REQUEST_CODE = 118;
    public static final int USER_ACCOUNT_REQUEST_CODE = 119;
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

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    private boolean signInProgress;

    private Map<JRssFeedType, Boolean> feedTypeLoadStatus;

    private Thread.UncaughtExceptionHandler androidDefaultUEH = null;

    private Thread.UncaughtExceptionHandler customUEH = null;

    @Override
    public void onCreate() {
        super.onCreate();
        /*UserInfo userInfo = DBUtil.loadLastLoggedInUserInfo();
        if(userInfo != null) {
            new LoginTask().execute(userInfo);
        }*/
        mInstance = this;
        SimpleDiskCacheInitializer.prepare(this);
        HttpCacheFactory.prepare(this);
        AdBlocker.init(this);
        initializeBranchIO();
        feedTypeLoadStatus = new HashMap<>();
        androidDefaultUEH = Thread.getDefaultUncaughtExceptionHandler();
        customUEH = new SquillUncaughtExceptionHandler(androidDefaultUEH, getApplicationContext());
        Thread.setDefaultUncaughtExceptionHandler(customUEH);

        /*new FlurryAgent.Builder()
                .withLogEnabled(true)
                .build(this, "854NFQXNZJCYZ9PBYJBJ");*/
    }

    public void setLoadStatus(JRssFeedType feedType, Boolean status) {
        feedTypeLoadStatus.put(feedType, status);
    }

    public boolean getLoadStatus(JRssFeedType feedType) {
        Boolean status = feedTypeLoadStatus.get(feedType);
        return status != null ? status.booleanValue() : false;
    }

    public void waitForLoginSuccess() {
        while (userEmail == null) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                //  e.printStackTrace();
            } finally {
            }
        }
    }

    public void initializeBranchIO() {
        //Branch.getAutoInstance(this);
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
        if (mLruBitmapCache == null) {
            SimpleDiskCacheInitializer.prepare(this);
            mLruBitmapCache = new LruBitmapCache();
        }
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

    private Bitmap selectedBitmapPhoto;

    public Bitmap getSelectedBitmapPhoto() {
        return selectedBitmapPhoto;
    }

    public void setSelectedBitmapPhoto(Bitmap selectedBitmapPhoto) {
        this.selectedBitmapPhoto = selectedBitmapPhoto;
    }

    private UploadAttachmentData uploadAttachmentData;

    public UploadAttachmentData getUploadAttachmentData() {
        return uploadAttachmentData;
    }

    public void setUploadAttachmentData(UploadAttachmentData uploadAttachmentData) {
        this.uploadAttachmentData = uploadAttachmentData;
    }

    private FeedReceiveState feedReceiveState;

    public FeedReceiveState getFeedReceiveState() {
        if(feedReceiveState == null) {
            feedReceiveState = new FeedReceiveState();
        }
        return feedReceiveState;
    }
}