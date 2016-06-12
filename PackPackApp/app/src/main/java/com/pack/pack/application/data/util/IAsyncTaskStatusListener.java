package com.pack.pack.application.data.util;

import com.pack.pack.model.web.JUser;
import com.pack.pack.oauth1.client.AccessToken;

/**
 * Created by Saurav on 11-06-2016.
 */
public interface IAsyncTaskStatusListener {

    public void onPreStart();

    public void onSuccess(Object data);

    public void onFailure(String errorMsg);

    public void onPostComplete();
}
