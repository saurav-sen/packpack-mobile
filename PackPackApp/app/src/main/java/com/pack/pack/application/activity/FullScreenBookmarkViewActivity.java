package com.pack.pack.application.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.pack.pack.application.R;
import com.pack.pack.application.view.util.ExternalLinkShareUtil;
import com.pack.pack.application.view.util.HtmlUtil;
import com.pack.pack.application.view.util.LogoMap;

public class FullScreenBookmarkViewActivity extends AppCompatActivity {

    private WebView bookmark_detail_fullscreen_view;

    public static final String SOURCE_LINK = "SOURCE_LINK";

    public static final String NEWS_TITLE = "NEWS_TITLE";
    public static final String NEWS_FULL_TEXT = "NEWS_FULL_TEXT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_bookmark_view);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        final String sourceLink = getIntent().getStringExtra(SOURCE_LINK);
        final String newsTitle = getIntent().getStringExtra(NEWS_TITLE);
        final String newsFullText = getIntent().getStringExtra(NEWS_FULL_TEXT);
        bookmark_detail_fullscreen_view = (WebView) findViewById(R.id.bookmark_detail_fullscreen_view);
        bookmark_detail_fullscreen_view.getSettings().setJavaScriptEnabled(true);
        //new_detail_fullscreen_view.getSettings().setUserAgentString();
        bookmark_detail_fullscreen_view.setWebChromeClient(new WebChromeClient());
        bookmark_detail_fullscreen_view.setWebViewClient(new SquillWebViewClient());
        if(sourceLink != null) {
            bookmark_detail_fullscreen_view.loadUrl(sourceLink);
        }
        else if(newsFullText != null) {
            String html = HtmlUtil.generateOfflineHtml(newsTitle, newsFullText, sourceLink, LogoMap.get(sourceLink));
            if(html != null) {
                bookmark_detail_fullscreen_view.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "UTF-8", null);
            } else {
                bookmark_detail_fullscreen_view.loadUrl(sourceLink);
            }
        }
    }

    private void shareUrl(String url) {
        ExternalLinkShareUtil.shareDirectLink(FullScreenBookmarkViewActivity.this, url, "");
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

        SquillWebViewClient() {
            progressDialog = new ProgressDialog(FullScreenBookmarkViewActivity.this);
            progressDialog.setMessage("Loading...");
            progressDialog.show();
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if(URLUtil.isNetworkUrl(url)) {
                //view.loadUrl(url);
                return false;
            }
                /*Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);*/
            return true;
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
