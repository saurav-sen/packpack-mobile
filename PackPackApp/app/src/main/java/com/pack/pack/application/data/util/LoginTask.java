package com.pack.pack.application.data.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.pack.pack.application.AppController;
import com.pack.pack.application.data.LoggedInUserInfo;
import com.pack.pack.application.db.DBUtil;
import com.pack.pack.application.db.DbObject;
import com.pack.pack.application.db.UserInfo;
import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIBuilder;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.model.web.JUser;
import com.pack.pack.oauth1.client.AccessToken;

import java.util.HashMap;
import java.util.Map;

import static com.pack.pack.application.AppController.ANDROID_APP_CLIENT_KEY;
import static com.pack.pack.application.AppController.ANDROID_APP_CLIENT_SECRET;

/**
 * Created by Saurav on 11-06-2016.
 */
public class LoginTask extends AbstractNetworkTask<UserInfo, Integer, AccessToken> {

    private String errorMsg;

    private static final String LOG_TAG = "LoginTask";

    private JUser user;

    private UserInfo userInfo;

    public LoginTask(Context context) {
        super(false, true, context);
    }

    public LoginTask(Context context, IAsyncTaskStatusListener listener) {
        super(false, true, context);
        addListener(listener);
    }

    @Override
    protected boolean isStoreResultsInDB() {
        return true;
    }

    @Override
    protected String getContainerIdForObjectStore() {
        return null;
    }

    @Override
    protected AccessToken executeApi(API api0) throws Exception {
        AccessToken accessToken = null;
        try {
            accessToken = (AccessToken) api0.execute();
            AppController.getInstance().setoAuthToken(accessToken.getToken());

            API api = APIBuilder
                    .create(ApiConstants.BASE_URL)
                    .setAction(COMMAND.GET_USER_BY_USERNAME)
                    .setOauthToken(AppController.getInstance().getoAuthToken())
                    .addApiParam(APIConstants.User.USERNAME,
                            userInfo.getUsername()).build();
            user = (JUser) api.execute();
            AppController.getInstance().setUser(user);
        } catch (Exception e) {
            Log.i(LOG_TAG, e.getMessage());
            errorMsg = e.getMessage();
        }
        return accessToken;
    }

    @Override
    protected DbObject convertObjectForStore(AccessToken successResult, String containerIdForObjectStore) {
        UserInfo userInfo = new UserInfo();
        userInfo.setAccessToken(successResult.getToken());
        userInfo.setAccessTokenSecret(successResult.getTokenSecret());
        userInfo.setUserId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setPassword(AppController.getInstance().getUserPassword());
        return userInfo;
    }

    @Override
    protected COMMAND command() {
        return COMMAND.SIGN_IN;
    }

    @Override
    protected Map<String, Object> prepareApiParams(UserInfo userInfo) {
        this.userInfo = userInfo;
        Map<String, Object> apiParams = new HashMap<String, Object>();
        apiParams.put(APIConstants.Login.CLIENT_KEY, ANDROID_APP_CLIENT_KEY);
        apiParams.put(APIConstants.Login.CLIENT_SECRET, ANDROID_APP_CLIENT_SECRET);
        apiParams.put(APIConstants.Login.USERNAME, userInfo.getUsername());
        apiParams.put(APIConstants.Login.PASSWORD, userInfo.getPassword());
        AppController.getInstance().setUserPassword(userInfo.getPassword());
        return apiParams;
    }

    @Override
    protected Object getSuccessResult(AccessToken accessToken) {
        return new LoggedInUserInfo(accessToken, user);
    }

    @Override
    protected boolean isSuccess(AccessToken accessToken) {
        return accessToken != null && accessToken.getToken() != null;
    }

    @Override
    protected String getFailureMessage() {
        return errorMsg;
    }

    @Override
    protected AccessToken doRetrieveFromDB(SQLiteDatabase readable, UserInfo inputObject) {
        final UserInfo userInfo = DBUtil.loadLastLoggedInUserInfo(readable);
        if(userInfo != null && userInfo.getAccessToken() != null && userInfo.getAccessTokenSecret() != null) {
            AppController.getInstance().setoAuthToken(userInfo.getAccessToken());
            AppController.getInstance().setUserPassword(userInfo.getPassword());
            user = DBUtil.convertUserInfo(userInfo);
            AppController.getInstance().setUser(user);
            return  new AccessToken() {
                @Override
                public String getToken() {
                    return userInfo.getAccessToken();
                }

                @Override
                public String getTokenSecret() {
                    return userInfo.getAccessTokenSecret();
                }
            };
        }
        return null;
    }
}
