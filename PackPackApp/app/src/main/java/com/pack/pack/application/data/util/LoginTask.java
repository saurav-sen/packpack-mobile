package com.pack.pack.application.data.util;

import android.os.AsyncTask;
import android.util.Log;

import com.pack.pack.application.AppController;
import com.pack.pack.application.data.LoggedInUserInfo;
import com.pack.pack.application.data.UserInfo;
import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIBuilder;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.model.web.JUser;
import com.pack.pack.oauth1.client.AccessToken;

import java.util.ArrayList;
import java.util.List;

import static com.pack.pack.application.AppController.ANDROID_APP_CLIENT_KEY;
import static com.pack.pack.application.AppController.ANDROID_APP_CLIENT_SECRET;

/**
 * Created by Saurav on 11-06-2016.
 */
public class LoginTask extends AbstractTask<UserInfo, Integer, AccessToken> {

    private String errorMsg;

    private static final String LOG_TAG = "LoginTask";

    private JUser user;

    public LoginTask() {
    }

    public LoginTask(IAsyncTaskStatusListener listener) {
        addListener(listener);
    }

    @Override
    protected AccessToken doInBackground(UserInfo... userInfos) {
        if(userInfos == null || userInfos.length == 0)
            throw new RuntimeException("Failed to login.");
        AccessToken accessToken = null;
        UserInfo userInfo = userInfos[0];
        try {
            API api = APIBuilder.create().setAction(COMMAND.SIGN_IN)
                    .addApiParam(APIConstants.Login.CLIENT_KEY, ANDROID_APP_CLIENT_KEY)
                    .addApiParam(APIConstants.Login.CLIENT_SECRET, ANDROID_APP_CLIENT_SECRET)
                    .addApiParam(APIConstants.Login.USERNAME, userInfo.getUsername())
                    .addApiParam(APIConstants.Login.PASSWORD, userInfo.getPassword())
                    .build();
            accessToken = (AccessToken) api.execute();
            AppController.getInstance().setoAuthToken(accessToken.getToken());

            api = APIBuilder
                    .create()
                    .setAction(COMMAND.GET_USER_BY_USERNAME)
                    .setOauthToken(AppController.getInstance().getoAuthToken())
                    .addApiParam(APIConstants.User.USERNAME,
                            userInfos[0].getUsername()).build();
            user = (JUser) api.execute();
            AppController.getInstance().setUser(user);
        } catch (Exception e) {
            Log.i(LOG_TAG, e.getMessage());
            errorMsg = e.getMessage();
        }
        return accessToken;
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
}
