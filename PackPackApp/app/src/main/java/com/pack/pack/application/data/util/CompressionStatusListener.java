package com.pack.pack.application.data.util;

/**
 * Created by Saurav on 18-02-2017.
 */
public interface CompressionStatusListener {

    public void onSuccess(String message);

    public void onFailure(String message);

    public void onFinish();
}
