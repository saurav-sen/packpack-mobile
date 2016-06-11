package com.pack.pack.application.data.util;

import com.pack.pack.model.web.JUser;
import com.pack.pack.oauth1.client.AccessToken;

/**
 * Created by Saurav on 11-06-2016.
 */
public interface ILoginStatusListener {

    public void onPreStart();

    public void onLoginSuccess(AccessToken token, JUser user);

    public void onLoginFailure(String errorMsg);

    public void onPostComplete();
}
