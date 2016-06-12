package com.pack.pack.application.data;

import com.pack.pack.model.web.JUser;
import com.pack.pack.oauth1.client.AccessToken;

/**
 * Created by Saurav on 12-06-2016.
 */
public class LoggedInUserInfo {

    private AccessToken accessToken;

    private JUser user;

    public LoggedInUserInfo(AccessToken accessToken, JUser user) {
        setAccessToken(accessToken);
        setUser(user);
    }

    public AccessToken getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(AccessToken accessToken) {
        this.accessToken = accessToken;
    }

    public JUser getUser() {
        return user;
    }

    public void setUser(JUser user) {
        this.user = user;
    }
}
