package com.pack.pack.application.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.pack.pack.application.R;
import com.pack.pack.application.view.util.ExternalLinkShareUtil;
import com.pack.pack.application.view.util.ViewUtil;

public class FullScreenWebViewActivity extends AppCompatActivity {

    private WebView web_detail_fullscreen_view;

    private FloatingActionButton fab;

    public static final String SHARE_WEB_LINK = "SHARE_WEB_LINK";
    public static final String WEB_LINK = "WEB_LINK";

    private String shareWebLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_web_view);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        final String webLink = getIntent().getStringExtra(WEB_LINK);
        shareWebLink = getIntent().getStringExtra(SHARE_WEB_LINK);
        if(shareWebLink == null) {
            shareWebLink = webLink;
        }
        web_detail_fullscreen_view = (WebView) findViewById(R.id.web_detail_fullscreen_view);
        web_detail_fullscreen_view.getSettings().setJavaScriptEnabled(true);
        //new_detail_fullscreen_view.getSettings().setUserAgentString();
        web_detail_fullscreen_view.setWebChromeClient(new WebChromeClient());
        web_detail_fullscreen_view.setWebViewClient(new SquillWebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareUrl(shareWebLink);
            }
        });
        web_detail_fullscreen_view.loadUrl(webLink);
    }

    private void shareUrl(String url) {
        ExternalLinkShareUtil.shareDirectLink(FullScreenWebViewActivity.this, url, "");
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
            progressDialog = new ProgressDialog(FullScreenWebViewActivity.this);
            progressDialog.setMessage("Loading...");
            progressDialog.show();
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
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
