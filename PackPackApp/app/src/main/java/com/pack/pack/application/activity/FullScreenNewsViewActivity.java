package com.pack.pack.application.activity;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.pack.pack.application.AppController;
import com.pack.pack.application.Mode;
import com.pack.pack.application.R;
import com.pack.pack.application.service.NetworkUtil;
import com.pack.pack.application.view.util.AdBlocker;
import com.pack.pack.application.view.util.ExternalLinkShareUtil;
import com.pack.pack.application.view.util.HtmlUtil;
import com.pack.pack.application.view.util.LogoMap;
import com.pack.pack.application.view.util.ViewUtil;

import java.util.HashMap;
import java.util.Map;

public class FullScreenNewsViewActivity extends AppCompatActivity {

    private WebView new_detail_fullscreen_view;

    private FloatingActionButton fab;

    public static final String WEB_SHARE_LINK = "WEB_SHARE_LINK";
    public static final String NEWS_LINK = "NEWS_LINK";
    public static final String SOURCE_LINK = "SOURCE_LINK";

    public static final String NEWS_TITLE = "NEWS_TITLE";
    public static final String NEWS_FULL_TEXT = "NEWS_FULL_TEXT";

    public static final String NEWS_HTML_CONTENT = "NEWS_HTML_CONTENT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_news_view);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        final String newsLink = getIntent().getStringExtra(NEWS_LINK);
        final String webShareLink = getIntent().getStringExtra(WEB_SHARE_LINK);
        final String sourceLink = getIntent().getStringExtra(SOURCE_LINK);
        final String newsTitle = getIntent().getStringExtra(NEWS_TITLE);
        final String newsFullText = getIntent().getStringExtra(NEWS_FULL_TEXT);
        final String newsHtmlContent = getIntent().getStringExtra(NEWS_HTML_CONTENT);
        new_detail_fullscreen_view = (WebView) findViewById(R.id.new_detail_fullscreen_view);
        new_detail_fullscreen_view.getSettings().setJavaScriptEnabled(true);
        //new_detail_fullscreen_view.getSettings().setUserAgentString();
        new_detail_fullscreen_view.setWebChromeClient(new WebChromeClient());
        new_detail_fullscreen_view.setWebViewClient(new SquillWebViewClient());
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareUrl(webShareLink);
            }
        });
        /*if(sourceLink != null && !sourceLink.trim().isEmpty() && NetworkUtil.checkConnectivity(this)) {
            new_detail_fullscreen_view.loadUrl(sourceLink);
        } else {
            String html = HtmlUtil.generateOfflineHtml(newsTitle, newsFullText, sourceLink, LogoMap.get(sourceLink));
            new_detail_fullscreen_view.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "UTF-8", null);
        }*/
        if(newsHtmlContent != null && !newsHtmlContent.trim().isEmpty()) {
            String html = HtmlUtil.generateOfflineHtmlFromHtmlSnippet(newsTitle, newsHtmlContent, sourceLink, LogoMap.get(sourceLink));
            new_detail_fullscreen_view.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "UTF-8", null);
        } else if((newsFullText != null && !newsFullText.trim().isEmpty())) {
            String html = HtmlUtil.generateOfflineHtml(newsTitle, newsFullText, sourceLink, LogoMap.get(sourceLink));
            new_detail_fullscreen_view.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "UTF-8", null);
        }
    }

    private void shareUrl(String url) {
        ExternalLinkShareUtil.shareDirectLink(FullScreenNewsViewActivity.this, url, "");
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
            progressDialog = new ProgressDialog(FullScreenNewsViewActivity.this);
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
            hideProgressDialog();
        }

        private void hideProgressDialog() {
            if(progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }
}
