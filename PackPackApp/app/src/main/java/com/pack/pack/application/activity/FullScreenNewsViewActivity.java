package com.pack.pack.application.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
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
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.pack.pack.application.R;
import com.pack.pack.application.view.util.ExternalLinkShareUtil;
import com.pack.pack.application.view.util.ViewUtil;

public class FullScreenNewsViewActivity extends AppCompatActivity {

    private WebView new_detail_fullscreen_view;

    private FloatingActionButton fab;

    public static final String WEB_SHARE_LINK = "WEB_SHARE_LINK";
    public static final String NEWS_LINK = "NEWS_LINK";

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
        new_detail_fullscreen_view.loadUrl(newsLink);
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

        SquillWebViewClient() {
            progressDialog = new ProgressDialog(FullScreenNewsViewActivity.this);
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
