package com.pack.pack.application;

import android.content.Context;

import com.google.android.youtube.player.internal.a;
import com.pack.pack.application.db.DBUtil;
import com.pack.pack.application.db.SquillDbHelper;
import com.pack.pack.application.db.UserInfo;
import com.pack.pack.model.web.JUser;

/**
 * Created by Saurav on 25-09-2018.
 */
public class SquillUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

    private Thread.UncaughtExceptionHandler androidDefaultUEH;

    private Context context;

    public SquillUncaughtExceptionHandler(Thread.UncaughtExceptionHandler androidDefaultUEH, Context context) {
        this.androidDefaultUEH = androidDefaultUEH;
        this.context = context;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        androidDefaultUEH.uncaughtException(thread, ex);
        UserInfo userInfo = DBUtil.loadLastLoggedInUserInfo(new SquillDbHelper(context).getReadableDatabase());
        if(userInfo != null) {
            String userName = userInfo.getUsername();
            if (userName != null) {
                JUser user = DBUtil.convertUserInfo(userInfo);
                AppController.getInstance().setUser(user);
            }
        }
    }
}
