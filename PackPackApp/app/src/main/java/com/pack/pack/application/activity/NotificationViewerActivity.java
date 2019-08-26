package com.pack.pack.application.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.pack.pack.application.Constants;
import com.pack.pack.application.R;
import com.pack.pack.application.view.util.AdBlocker;
import com.pack.pack.application.view.util.ExternalLinkShareUtil;
import com.pack.pack.application.view.util.HtmlUtil;
import com.pack.pack.application.view.util.LogoMap;

import java.util.HashMap;
import java.util.Map;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class NotificationViewerActivity extends AppCompatActivity {

    private WebView notification_fullscreen_view;

    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_viewer);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        final String ogTitle = getIntent().getStringExtra(Constants.OG_TITLE);
        final String ogImage = getIntent().getStringExtra(Constants.OG_IMAGE);
        final String ogUrl = getIntent().getStringExtra(Constants.OG_URL);
        final String summaryText = getIntent().getStringExtra(Constants.SUMMARY_TEXT);
        final String shareableUrl = getIntent().getStringExtra(Constants.SHAREABLE_URL);
        notification_fullscreen_view = (WebView) findViewById(R.id.notification_fullscreen_view);
        notification_fullscreen_view.getSettings().setJavaScriptEnabled(true);
        //notification_fullscreen_view.getSettings().setUserAgentString();
        notification_fullscreen_view.setWebChromeClient(new WebChromeClient());
        notification_fullscreen_view.setWebViewClient(new SquillWebViewClient());
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareUrl(shareableUrl);
            }
        });

        if (ogTitle != null && !ogTitle.trim().isEmpty() && summaryText != null && !summaryText.trim().isEmpty()) {
            String html = HtmlUtil.generateNotificationViewerHtml(ogTitle, summaryText, ogUrl, ogImage, LogoMap.get(ogUrl));
            notification_fullscreen_view.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "UTF-8", null);
        } else if (ogUrl != null && !ogUrl.trim().isEmpty()) {
            notification_fullscreen_view.loadUrl(ogUrl);
        }
    }

    private void shareUrl(String url) {
        ExternalLinkShareUtil.shareDirectLink(NotificationViewerActivity.this, url, "");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class SquillWebViewClient extends WebViewClient {

        private ProgressDialog progressDialog;

        private Map<String, Boolean> loadedUrls = new HashMap<>();

        SquillWebViewClient() {
            progressDialog = new ProgressDialog(NotificationViewerActivity.this);
            progressDialog.setMessage("Loading...");
            progressDialog.show();
        }

        /*@Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if(URLUtil.isNetworkUrl(url)) {
                //view.loadUrl(url);
                return false;
            }
                *//*Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);*//*
            return true;
        }*/

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            boolean ad;
            String url = request.getUrl().toString();
            if (!loadedUrls.containsKey(url)) {
                ad = AdBlocker.isAd(url);
                loadedUrls.put(url, ad);
            } else {
                ad = loadedUrls.get(url);
            }
            return ad ? AdBlocker.createEmptyResource() :
                    super.shouldInterceptRequest(view, request);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            hideProgressDialog();
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            /*view.loadUrl(
                    "javascript:(function() { " +
                            "var imgArr = document.getElementsByTagName('img');"
                            + "for(i = 0;i < imgArr.length; i++) {"
                            + "var srcUrl = imgArr[i].getAttribute(\"src\");"
                            + "}"
                            + "element.parentNode.removeChild(element);" +
                            "})()");*/
            hideProgressDialog();
        }

        private void hideProgressDialog() {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }
}
