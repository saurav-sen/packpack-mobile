package com.pack.pack.application.view.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.pack.pack.application.data.util.AbstractNetworkTask;
import com.pack.pack.application.data.util.IAsyncTaskStatusListener;
import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.model.web.JRssFeed;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Saurav on 13-09-2017.
 */
public final class ExternalLinkShareUtil {

    private ExternalLinkShareUtil() {
    }

    public static void shareUrl(Context context, JRssFeed feed) {
        shareUrl(context, feed, null);
    }

    public static void shareUrl(Context context, JRssFeed feed, String url) {
        if(url == null) {
            url = feed.getShareableUrl();
            if(url == null || url.trim().isEmpty()) {
                url = feed.getHrefSource();
            }
            if(url == null || url.trim().isEmpty()) {
                url = feed.getOgUrl();
            }
        }

        shareDirectLink(context, url, feed.getOgTitle());
    }

    public static void shareDirectLink(Context context, String url, String titleText) {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

        share.putExtra(Intent.EXTRA_SUBJECT, "[Shared @ SQUILL] " + titleText);
        share.putExtra(Intent.EXTRA_TEXT, url);

        context.startActivity(Intent.createChooser(share, "Shared @ SQUILL"));
    }
}
