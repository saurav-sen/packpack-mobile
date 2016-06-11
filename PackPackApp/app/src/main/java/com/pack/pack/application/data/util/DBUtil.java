package com.pack.pack.application.data.util;

import com.pack.pack.application.data.UserInfo;

/**
 * Created by Saurav on 11-06-2016.
 */
public class DBUtil {

    private DBUtil() {
    }

    public static UserInfo loadLastLoggedInUserInfo() {
        return new UserInfo("sourabhnits@gmail.com", "P@ckp@K#123");
        //return null;
    }
}
