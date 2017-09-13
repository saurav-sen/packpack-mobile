package com.pack.pack.application.view.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.pack.pack.application.AppController;
import com.pack.pack.application.R;
import com.pack.pack.application.data.util.AbstractNetworkTask;
import com.pack.pack.application.data.util.IAsyncTaskStatusListener;
import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.model.web.JRssFeed;
import com.pack.pack.model.web.Pagination;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Saurav on 01-05-2016.
 */
public class ViewUtil {

    private ViewUtil() {
    }

    public static int getViewLayoutId(String categoryType) {
        try {
            return R.layout.class.getField(categoryType + "_topic_view").getInt(null);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public static int getListViewId(String categoryType) {
        return getViewId(categoryType, "events");
    }

    public static int getViewId(String categoryType, String viewIdSuffix) {
        try {
            return R.id.class.getField(categoryType + "_" + viewIdSuffix).getInt(null);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public static int getListViewLayoutId(String categoryType) {
        try {
            return R.layout.class.getField(categoryType + "_event_item").getInt(null);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
}
