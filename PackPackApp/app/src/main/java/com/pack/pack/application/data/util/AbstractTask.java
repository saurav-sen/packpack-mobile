package com.pack.pack.application.data.util;

import android.os.AsyncTask;

import com.pack.pack.application.data.LoggedInUserInfo;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Saurav on 12-06-2016.
 */
public abstract class AbstractTask<X, Y, Z> extends AsyncTask<X, Y, Z> {

    private List<IAsyncTaskStatusListener> listeners = new LinkedList<IAsyncTaskStatusListener>();

    public void addListener(IAsyncTaskStatusListener listener) {
        if(listener == null)
            return;
        listeners.add(listener);
    }

    @Override
    protected void onPreExecute() {
        fireOnPreStart();
        super.onPreExecute();
    }

    protected void fireOnPreStart() {
        for(IAsyncTaskStatusListener listener : listeners) {
            listener.onPreStart();
        }
    }

    protected void fireOnSuccess(Object data) {
        for(IAsyncTaskStatusListener listener : listeners) {
            listener.onSuccess(data);
        }
    }

    protected void fireOnFailure(String errorMsg) {
        for(IAsyncTaskStatusListener listener : listeners) {
            listener.onFailure(errorMsg);
        }
    }

    protected void fireOnPostComplete() {
        for(IAsyncTaskStatusListener listener : listeners) {
            listener.onPostComplete();
        }
    }

    protected abstract Object getSuccessResult(Z result);

    protected abstract boolean isSuccess(Z result);

    protected abstract String getFailureMessage();

    @Override
    protected void onPostExecute(Z z) {
        super.onPostExecute(z);
        if(isSuccess(z)) {
            Object data = getSuccessResult(z);
            fireOnSuccess(data);
        }
        else {
            fireOnFailure(getFailureMessage());
        }
        fireOnPostComplete();
    }
}
