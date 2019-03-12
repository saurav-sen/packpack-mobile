package in.squill.squilloffice.view.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebResourceResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import okio.BufferedSource;
import okio.Okio;

//import okhttp3.HttpUrl;
//import rx.Observable;
//import rx.Scheduler;

/**
 * Created by Saurav on 13-12-2018.
 */
public class AdBlocker {

    private static final String AD_HOSTS_FILE = "pgl.yoyo.org.txt";
    private static final Set<String> AD_HOSTS = new HashSet<>();

    private static final String LOG_TAG = "AdBlocker";

    public static void init(final Context context/*, Scheduler scheduler*/) {
        /*Observable.fromCallable(() -> loadFromAssets(context))
                .onErrorReturn(throwable -> null)
                .subscribeOn(scheduler)
                .subscribe();*/
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    loadFromAssets(context);
                } catch (IOException e) {
                    // noop
                }
                return null;
            }
        }.execute();
    }

    public static boolean isAd(String url) {
        try {
            URL httpUrl = new URL(url);
            return isAdHost(httpUrl != null ? httpUrl.getHost() : "");
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            return false;
        }
        //HttpUrl httpUrl = HttpUrl.parse(url);
        //return isAdHost(httpUrl != null ? httpUrl.host() : "");
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static WebResourceResponse createEmptyResource() {
        return new WebResourceResponse("text/plain", "utf-8", new ByteArrayInputStream("".getBytes()));
    }

    @WorkerThread
    private static Void loadFromAssets(Context context) throws IOException {
        InputStream stream = context.getAssets().open(AD_HOSTS_FILE);
        BufferedSource buffer = Okio.buffer(Okio.source(stream));
        String line;
        while ((line = buffer.readUtf8Line()) != null) {
            AD_HOSTS.add(line);
        }
        buffer.close();
        stream.close();
        return null;
    }

    /**
     * Recursively walking up sub domain chain until we exhaust or find a match,
     * effectively doing a longest substring matching here
     */
    private static boolean isAdHost(String host) {
        if (TextUtils.isEmpty(host)) {
            return false;
        }
        if(host.contains("adservice.google")) {
            return true;
        }
        int index = host.indexOf(".");
        return index >= 0 && (AD_HOSTS.contains(host) ||
                index + 1 < host.length() && isAdHost(host.substring(index + 1)));
    }
}
