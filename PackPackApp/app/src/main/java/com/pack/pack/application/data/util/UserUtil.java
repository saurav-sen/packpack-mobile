package com.pack.pack.application.data.util;

import android.content.Context;
import android.util.Patterns;

import com.pack.pack.application.AppController;
import com.pack.pack.application.Mode;
import com.pack.pack.application.data.LoggedInUserInfo;
import com.pack.pack.application.db.DBUtil;
import com.pack.pack.application.db.SquillDbHelper;
import com.pack.pack.application.db.UserInfo;
import com.pack.pack.application.image.loader.DownloadProfilePictureTask;
import com.pack.pack.model.web.JUser;

import java.util.regex.Pattern;

/**
 * Created by Saurav on 17-05-2017.
 */
public class UserUtil {

    /*private static final String PASSWORD_PATTERN_REGEX =
            "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*]).{6,20})";*/
    private static final String PASSWORD_PATTERN_REGEX =
            "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{6,20})";
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(PASSWORD_PATTERN_REGEX);

    private UserUtil() {

    }

    public static final String resolveUserDisplayName(JUser user) {
        String displayName = user.getDisplayName();
        if(displayName == null || displayName.trim().isEmpty()) {
            return user.getName();
        }
        return displayName;
    }

    public static final boolean isValidEmailAddressFormat(CharSequence emailAddr) {
        if(emailAddr == null) {
            return false;
        }
        return Patterns.EMAIL_ADDRESS.matcher(emailAddr).matches();
    }

    public static final String applyPasswordPolicy(String passwd) {
        if(PASSWORD_PATTERN.matcher(passwd).matches()) {
            return null;
        }
        //return "6 to 20 length,at least one digit,one upper case,one lower case,anyone of !,@,#,$,%,^,&,*";
        return "6 to 20 length,at least one digit,one upper case,one lower case";
    }

    public static void verifyLogin(Context context, IAsyncTaskStatusListener listener) {

    }

    /*private static void tryLogin(Context context, IAsyncTaskStatusListener listener) {
        String userName = AppController.getInstance().getUserEmail();
        if(AppController.getInstance().getExecutionMode() == Mode.OFFLINE) {
            UserInfo userInfo = DBUtil.loadLastLoggedInUserInfo(new SquillDbHelper(context).getReadableDatabase());
            if(userInfo != null) {
                userName = userInfo.getUsername();
                if(userName != null) {
                    *//*AppController.getInstance().setoAuthToken(oAuthToken);
                    JUser user = DBUtil.convertUserInfo(userInfo);
                    AppController.getInstance().setUser(user);
                    finish();
                    startMainActivity();*//*
                    doLogin(userInfo, true, listener, context);
                } else {
                    doLogin(userInfo, false, listener, context);
                }
            }
        }
    }*/

    /*private static void doLogin(UserInfo userInfo, boolean refreshToken, IAsyncTaskStatusListener listener, Context context) {
        new LoginTask(context, new LoginTaskStatusListenerProxy(listener), refreshToken).execute(userInfo);
    }*/

    /*private static class LoginTaskStatusListenerProxy implements IAsyncTaskStatusListener {

        private IAsyncTaskStatusListener listener;

        LoginTaskStatusListenerProxy(IAsyncTaskStatusListener listener) {
            this.listener = listener;
        }

        @Override
        public void onPreStart(String taskID) {
            listener.onPreStart(taskID);
        }

        @Override
        public void onSuccess(String taskID, Object data) {
            LoggedInUserInfo userInfo = (LoggedInUserInfo) data;
           // AccessToken token = userInfo.getAccessToken();
            JUser user = userInfo.getUser();
            if(user.getProfilePictureUrl() != null && !user.getProfilePictureUrl().trim().isEmpty()) {
                new DownloadProfilePictureTask().execute(user.getProfilePictureUrl());
            }
            AppController.getInstance().setExecutionMode(Mode.ONLINE);
            listener.onSuccess(taskID, data);
        }

        @Override
        public void onFailure(String taskID, String errorMsg) {
            AppController.getInstance().setExecutionMode(Mode.OFFLINE);
            listener.onFailure(taskID, errorMsg);
        }

        @Override
        public void onPostComplete(String taskID) {
            listener.onPostComplete(taskID);
        }
    }*/
}
