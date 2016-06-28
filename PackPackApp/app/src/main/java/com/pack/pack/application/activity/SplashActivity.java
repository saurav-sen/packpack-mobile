package com.pack.pack.application.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.pack.pack.application.AppController;
import com.pack.pack.application.R;
import com.pack.pack.application.data.LoggedInUserInfo;
import com.pack.pack.application.db.UserInfo;
import com.pack.pack.application.db.DBUtil;
import com.pack.pack.application.data.util.IAsyncTaskStatusListener;
import com.pack.pack.application.data.util.LoginTask;
import com.pack.pack.model.web.JUser;
import com.pack.pack.oauth1.client.AccessToken;

/**
 *
 * @author Saurav
 *
 */
public class SplashActivity extends Activity implements IAsyncTaskStatusListener {

    //private ImageView splash_image;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        /*splash_image = (ImageView) findViewById(R.id.splash_image);
        Snackbar.make(splash_image, "Zxuluk from DryDock", Snackbar.LENGTH_LONG);*/

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

            }
        }, 50000);

        verify();
    }

    private void verify() {
        String oAuthToken = AppController.getInstance().getoAuthToken();
        if(oAuthToken != null) {
            finish();
            startMainActivity();
        } else {
            UserInfo userInfo = DBUtil.loadLastLoggedInUserInfo();
            if(userInfo != null) {
                doLogin(userInfo);
            }
        }
    }

    @Override
    public void onPreStart() {
        showProgressDialog();
    }

    @Override
    public void onSuccess(Object data) {
        LoggedInUserInfo userInfo = (LoggedInUserInfo)data;
        AccessToken token = userInfo.getAccessToken();
        JUser user = userInfo.getUser();
        getIntent().putExtra("loginStatus", true);
        finish();
        startMainActivity();
    }

    @Override
    public void onPostComplete() {

    }

    private void showProgressDialog() {
        if(progressDialog != null) {
            progressDialog.dismiss();
        }
        progressDialog = new ProgressDialog(SplashActivity.this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();
    }

    private void hideProgressDialog() {
        if(progressDialog == null)
            return;
        progressDialog.dismiss();
        progressDialog = null;
    }

    @Override
    protected void onDestroy() {
        hideProgressDialog();
        super.onDestroy();
    }

    public void onFailure(String errorMsg) {
        hideProgressDialog();
        startLoginActivity();
    }

    private void startLoginActivity() {
        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    private void startMainActivity() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private void doLogin(UserInfo userInfo) {
        new LoginTask(this).execute(userInfo);
    }
}