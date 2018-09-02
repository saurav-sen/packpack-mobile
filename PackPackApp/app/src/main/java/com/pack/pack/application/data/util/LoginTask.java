package com.pack.pack.application.data.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.pack.pack.application.AppController;
import com.pack.pack.application.Mode;
import com.pack.pack.application.data.LoggedInUserInfo;
import com.pack.pack.application.db.DBUtil;
import com.pack.pack.application.db.DbObject;
import com.pack.pack.application.db.UserInfo;
import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIBuilder;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.model.web.JUser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Saurav on 11-06-2016.
 */
public class LoginTask extends AbstractNetworkTask<UserInfo, Integer, JUser> {

    private String errorMsg;

    private static final String LOG_TAG = "LoginTask";

   // private JUser user;

    private UserInfo userInfo;

    private String followedCategories;

    private boolean firstTime = false;

    /*public LoginTask(Context context) {
        super(false, true, context, false);
    }*/

    public LoginTask(Context context, IAsyncTaskStatusListener listener, boolean firstTime) {
        super(AppController.getInstance().getExecutionMode() == Mode.OFFLINE, true, context, false);
        addListener(listener);
        this.firstTime = firstTime;
    }

    @Override
    protected boolean isStoreResultsInDB() {
        return true;
    }

    @Override
    protected boolean forceStoreInDb() {
        return true;
    }

    @Override
    protected String getContainerIdForObjectStore() {
        return null;
    }

    @Override
    protected JUser executeApi(API api0) throws Exception {
        JUser user = null;
        try {
            user = (JUser) api0.execute();
            AppController.getInstance().setUser(user);

            // GET user followed categories
           /* API api = APIBuilder
                    .create(ApiConstants.BASE_URL)
                    .setAction(COMMAND.GET_USER_CATEGORIES)
                    .setUserName(AppController.getInstance().getUserEmail())
                    .addApiParam(APIConstants.User.ID, user.getId())
                    .build();
            List<String> list = (List<String>) api.execute();
            if(list != null) {
                AppController.getInstance().getFollowedCategories().clear();
                AppController.getInstance().getFollowedCategories().addAll(list);
                StringBuilder stringBuilder = new StringBuilder();
                for(String l : list) {
                    stringBuilder.append(l);
                    stringBuilder.append(":");
                }
                followedCategories = stringBuilder.toString();
            }*/

            API api = APIBuilder
                    .create(ApiConstants.BASE_URL)
                    .setAction(COMMAND.ANDROID_APK_URL)
                    .build();
            String apkUrl = (String) api.execute();
            AppController.getInstance().setApkUrl(apkUrl);
        } catch (Exception e) {
            Log.i(LOG_TAG, e.getMessage());
            errorMsg = e.getMessage();
        }
        return user;
    }

    @Override
    protected DbObject convertObjectForStore(JUser user, String containerIdForObjectStore) {
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setDisplayName(user.getDisplayName());
        return userInfo;
    }

    @Override
    protected COMMAND command() {
        return COMMAND.GET_USER_BY_USERNAME;
    }

    @Override
    protected Map<String, Object> prepareApiParams(UserInfo userInfo) {
        this.userInfo = userInfo;
        Map<String, Object> apiParams = new HashMap<String, Object>();
        /*apiParams.put(APIConstants.Login.CLIENT_KEY, ANDROID_APP_CLIENT_KEY);
        apiParams.put(APIConstants.Login.CLIENT_SECRET, ANDROID_APP_CLIENT_SECRET);*/
        /*if(!firstTime) {*/
            apiParams.put(APIConstants.User.USERNAME,
                    userInfo.getUsername());
       /* } *//*else {
            apiParams.put(APIConstants.Login.OLD_ACCESS_TOKEN, userInfo.getAccessToken());
            apiParams.put(APIConstants.Login.OLD_ACCESS_TOKEN_SECRET, userInfo.getAccessTokenSecret());
        }*/
        return apiParams;
    }

    @Override
    protected Object getSuccessResult(JUser user) {
        return new LoggedInUserInfo(user);
    }

    @Override
    protected boolean isSuccess(JUser user) {
        return user != null;
    }

    @Override
    protected String getFailureMessage() {
        return errorMsg;
    }

    @Override
    protected JUser doRetrieveFromDB(SQLiteDatabase readable, UserInfo inputObject) {
        final UserInfo userInfo = DBUtil.loadLastLoggedInUserInfo(readable);
        if(userInfo != null) {
            JUser user = DBUtil.convertUserInfo(userInfo);
            AppController.getInstance().setUser(user);
            if(AppController.getInstance().getExecutionMode() == Mode.OFFLINE) {
                AppController.getInstance().setUserEmail(userInfo.getUsername());
                return user;
            }
        }
        return null;
    }
}
