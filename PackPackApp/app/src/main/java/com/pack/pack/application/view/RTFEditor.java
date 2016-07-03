package com.pack.pack.application.view;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.apache.commons.lang3.StringEscapeUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Created by Saurav on 02-07-2016.
 */
public class RTFEditor extends WebView {

    private static final String SETUP_HTML = "file:///android_asset/editor.html";
    private static final String CALLBACK_SCHEME = "re-callback://";
    private static final String STATE_SCHEME = "re-state://";

    private Activity activity;

    public RTFEditor(Activity activity) {
        this(activity, null);
    }

    public RTFEditor(Activity activity, AttributeSet attrs) {
        this(activity, attrs, android.R.attr.webViewStyle);
    }

    //@SuppressLint("SetJavaScriptEnabled")
    public RTFEditor(Activity activity, AttributeSet attrs, int defStyleAttr) {
        super(activity, attrs, defStyleAttr);

        setVerticalScrollBarEnabled(false);
        setHorizontalScrollBarEnabled(false);
        getSettings().setJavaScriptEnabled(true);
        setWebChromeClient(new WebChromeClient());
        setWebViewClient(createWebviewClient());
        getSettings().setJavaScriptEnabled(true);
        loadUrl(SETUP_HTML);

        //applyAttributes(context, attrs);
        addJavascriptInterface(new WebAppInterface(), "Squill");
        this.activity = activity;
    }

    private RTFListener rtfListener;

    public void setOnSaveListener(RTFListener rtfListener) {
        this.rtfListener = rtfListener;
    }

    protected RTFListener getRtfListener() {
        return rtfListener;
    }

    private void close() {
        if(activity != null) {
            activity.finish();
        }
    }

    protected void onSave(String rtfText) {
        RTFListener rtfListener = getRtfListener();
        if(rtfListener != null) {
            rtfListener.onSave(rtfText);
        }
        close();
    }

    private class WebAppInterface {

        public WebAppInterface() {
        }

        @JavascriptInterface
        public void doSaveContent(String htmlContent) {
            onSave(StringEscapeUtils.escapeHtml4(htmlContent));
        }
    }

    public void load() {
        loadUrl(SETUP_HTML);
    }

    protected EditorWebViewClient createWebviewClient() {
        return new EditorWebViewClient();
    }

    protected class EditorWebViewClient extends WebViewClient {
        @Override public void onPageFinished(WebView view, String url) {
            /*isReady = url.equalsIgnoreCase(SETUP_HTML);
            if (mLoadListener != null) {
                mLoadListener.onAfterInitialLoad(isReady);
            }*/
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            String decode;
            try {
                decode = URLDecoder.decode(url, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                // No handling
                return false;
            }

            if (TextUtils.indexOf(url, CALLBACK_SCHEME) == 0) {
                //callback(decode);
                return true;
            } else if (TextUtils.indexOf(url, STATE_SCHEME) == 0) {
                //stateCheck(decode);
                return true;
            }

            return super.shouldOverrideUrlLoading(view, url);
        }
    }
}
