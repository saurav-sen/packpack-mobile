package com.pack.pack.application.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

import com.pack.pack.application.AppController;
import com.pack.pack.application.Constants;
import com.pack.pack.application.R;
import com.pack.pack.application.data.LoggedInUserInfo;
import com.pack.pack.application.data.util.ImageUtil;
import com.pack.pack.application.db.SquillDbHelper;
import com.pack.pack.application.db.UserInfo;
import com.pack.pack.application.db.DBUtil;
import com.pack.pack.application.data.util.IAsyncTaskStatusListener;
import com.pack.pack.application.data.util.LoginTask;
import com.pack.pack.application.image.loader.DownloadProfilePictureTask;
import com.pack.pack.application.service.CheckNetworkService;
import com.pack.pack.application.service.NetworkUtil;
import com.pack.pack.application.service.NotificationReaderService;
import com.pack.pack.application.service.SquillNTPService;
import com.pack.pack.model.web.JUser;
import com.pack.pack.oauth1.client.AccessToken;

/**
 *
 * @author Saurav
 *
 */
public class SplashActivity extends AbstractActivity implements IAsyncTaskStatusListener {

    //private ImageView splash_image;

    private ProgressDialog progressDialog;

    private int ntpJobId = 1;

    private String action;
    private String actionContentType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if(!NetworkUtil.checkConnectivity(this)) {
            startNoConnectionEmptyActivity();
            return;
        }

        JUser user = AppController.getInstance().getUser();
        if(user == null) {
            startNetworkChecker();
            startNTPService();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                }
            }, 5000);

            ImageUtil.loadFFMpeg(this);
        }

        action = getIntent().getAction();
        actionContentType = getIntent().getType();
        verify();
    }

    /*private void startNTPService() {
        ComponentName serviceComponent = new ComponentName(this, SquillNTPService.class);
        int oneMinute = 60 * 60 * 1000;
        JobInfo jobInfo = new JobInfo.Builder(ntpJobId++, serviceComponent)
                .setMinimumLatency(oneMinute).setOverrideDeadline(5 * oneMinute)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY).setRequiresDeviceIdle(false)
                .setRequiresCharging(false).build();
        JobScheduler jobScheduler = (JobScheduler) getApplication()
                .getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(jobInfo);
    }*/

    private void startNoConnectionEmptyActivity() {
        Intent intent = new Intent(this, NoConnectionEmptyActivity.class);
        startActivity(intent);
        finish();
    }

    private void startNTPService() {
        Intent intent = new Intent(this, SquillNTPService.class);
        startService(intent);
    }

    private void startNetworkChecker() {
        Intent intent = new Intent(this, CheckNetworkService.class);
        startService(intent);
    }

    private void verify() {
        String oAuthToken = AppController.getInstance().getoAuthToken();
        if(oAuthToken != null) {
            finish();
            routeToTargetActivity();
        } else {
            UserInfo userInfo = DBUtil.loadLastLoggedInUserInfo(new SquillDbHelper(this).getReadableDatabase());
            if(userInfo != null) {
                oAuthToken = userInfo.getAccessToken();
                if(oAuthToken != null) {
                    /*AppController.getInstance().setoAuthToken(oAuthToken);
                    JUser user = DBUtil.convertUserInfo(userInfo);
                    AppController.getInstance().setUser(user);
                    finish();
                    startMainActivity();*/
                    doLogin(userInfo, true);
                } else {
                    doLogin(userInfo, false);
                }
            } else {
                startLoginActivity();
            }
        }
    }

    @Override
    public void onPreStart(String taskID) {
        showProgressDialog();
    }

    @Override
    public void onSuccess(String taskID, Object data) {
        LoggedInUserInfo userInfo = (LoggedInUserInfo) data;
        AccessToken token = userInfo.getAccessToken();
        JUser user = userInfo.getUser();
        if(user.getProfilePictureUrl() != null && !user.getProfilePictureUrl().trim().isEmpty()) {
            new DownloadProfilePictureTask().execute(user.getProfilePictureUrl());
        }
        getIntent().putExtra("loginStatus", true);
        finish();
        routeToTargetActivity();
    }

    @Override
    public void onPostComplete(String taskID) {

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

    @Override
    public void onFailure(String taskID, String errorMsg) {
        hideProgressDialog();
        startLoginActivity();
    }

    private void startLoginActivity() {
        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
        finish();
        startActivity(intent);
    }

    private void routeToTargetActivity() {
        if(Intent.ACTION_SEND.equals(action) && actionContentType != null){
            if("image/*".equals(actionContentType)) {
                Uri imageUri = (Uri) getIntent().getParcelableExtra(Intent.EXTRA_STREAM);
                Intent intent = new Intent(SplashActivity.this, ImageVideoShareReceiveActivity.class);
                intent.putExtra(Constants.SHARED_IMAGE_URI_KEY, imageUri);
                finish();
                startActivity(intent);
            } else if("text/plain".equals(actionContentType)) {
                finish();
            } else {
                startMainActivity();
            }
        } else {
            startMainActivity();
        }
    }

    private void startMainActivity() {
        Intent intent = new Intent(SplashActivity.this, LandingPageActivity.class);
        finish();
        startActivity(intent);
    }

    private void doLogin(UserInfo userInfo, boolean refreshToken) {
        new LoginTask(this, this, refreshToken).execute(userInfo);
    }
}