package com.pack.pack.application.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.pack.pack.application.AppController;
import com.pack.pack.application.Mode;
import com.pack.pack.application.R;
import com.pack.pack.application.data.LoggedInUserInfo;
import com.pack.pack.application.data.util.IAsyncTaskStatusListener;
import com.pack.pack.application.data.util.MediaUtil;
import com.pack.pack.application.data.util.LoginTask;
import com.pack.pack.application.db.Bookmark;
import com.pack.pack.application.db.DBUtil;
import com.pack.pack.application.db.SquillDbHelper;
import com.pack.pack.application.db.UserInfo;
import com.pack.pack.application.image.loader.DownloadProfilePictureTask;
import com.pack.pack.application.service.AddBookmarkService;
import com.pack.pack.application.service.CheckNetworkService;
import com.pack.pack.application.service.NetworkUtil;
import com.pack.pack.application.service.SquillNTPService;
import com.pack.pack.model.web.JUser;

/**
 *
 * @author Saurav
 *
 */
public class SplashActivity extends AbstractActivity implements IAsyncTaskStatusListener {

    //private ImageView splash_image;

    private ProgressDialog progressDialog;

    private int ntpJobId = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if(!NetworkUtil.checkConnectivity(this)) {
            //startNoConnectionEmptyActivity();
            //return;
            AppController.getInstance().setExecutionMode(Mode.OFFLINE);
            routeToTargetActivity();
        } else {
            AppController.getInstance().setExecutionMode(Mode.ONLINE);
        }

       /* JUser user = AppController.getInstance().getUser();
        if(user == null) {*/
        startNetworkChecker();
        startNTPService();
        startAddBookmarkService();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

            }
        }, 5000);

        //MediaUtil.loadFFMpeg(this);
        /*}*/

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

    /*private void startNoConnectionEmptyActivity() {
        Intent intent = new Intent(this, NoConnectionEmptyActivity.class);
        startActivity(intent);
        finish();
    }*/

    private void startAddBookmarkService() {
        Intent intent = new Intent(this, AddBookmarkService.class);
        startService(intent);
    }

    private void submitNewLinkToAddBookmarkService(String entityId) {
        Intent intent = new Intent(this, AddBookmarkService.class);
        intent.putExtra(AddBookmarkService.BOOKMARK_ENTITY_ID, entityId);
        startService(intent);
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
        String userName = AppController.getInstance().getUserEmail();
        if(userName != null) {
            finish();
            routeToTargetActivity();
        } else {
            UserInfo userInfo = DBUtil.loadLastLoggedInUserInfo(new SquillDbHelper(this).getReadableDatabase());
            if(userInfo != null) {
                userName = userInfo.getUsername();
                if(userName != null) {
                    //AppController.getInstance().setoAuthToken(oAuthToken);
                    JUser user = DBUtil.convertUserInfo(userInfo);
                    AppController.getInstance().setUser(user);
                    finish();
                    startMainActivity();
                    //doLogin(userInfo, true);
                } else {
                    //doLogin(userInfo, false);
                    startSignupActivity();
                }
            } else {
                //startLoginActivity();
                startSignupActivity();
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
        //AccessToken token = userInfo.getAccessToken();
        JUser user = userInfo.getUser();
        if(user.getProfilePictureUrl() != null && !user.getProfilePictureUrl().trim().isEmpty()) {
            new DownloadProfilePictureTask().execute(user.getProfilePictureUrl());
        }
        AppController.getInstance().setExecutionMode(Mode.ONLINE);
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
       // startLoginActivity();
        startSignupActivity();
    }

    private void startSignupActivity() {
        Intent intent = new Intent(this, SignupActivity.class);
        finish();
        startActivity(intent);
    }

    /*private void startLoginActivity() {
        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
        finish();
        startActivity(intent);
    }*/

    private void routeToTargetActivity() {
        String action = getIntent().getAction();
        String type = getIntent().getType();
        if(Intent.ACTION_SEND.equals(action) && type != null){
           if("text/plain".equals(type)) {
                String sharedText = getIntent().getStringExtra(Intent.EXTRA_TEXT);
               if(sharedText == null || sharedText.trim().isEmpty()) {
                   startMainActivity("Invalid Link Found");
               } else {
                   Bookmark bookmark = new Bookmark();
                   bookmark.setProcessed(false);
                   bookmark.setSourceUrl(sharedText.trim());
                   bookmark.setTimeOfAdd(System.currentTimeMillis());
                   bookmark = DBUtil.storeNewBookmark(bookmark, this);
                   if(bookmark == null || bookmark.getEntityId() == null || bookmark.getEntityId().trim().isEmpty()) {
                       startMainActivity("Failed to process link");
                   } else {
                       submitNewLinkToAddBookmarkService(bookmark.getEntityId());
                       startBookmarkActivity();
                   }
               }


               /* Intent intent = new Intent(SplashActivity.this, ImageVideoShareReceiveActivity.class);
                intent.putExtra(Constants.SHARED_TEXT_OR_URL_KEY, sharedText);
                finish();
                startActivity(intent);*/
            } else {
                startMainActivity();
            }
        } else {
            startMainActivity();
        }
    }

    private void startMainActivity() {
        startMainActivity(null);
    }

    private void startMainActivity(String messageToDisplayIfAny) {
        Intent intent = new Intent(SplashActivity.this, LandingPageActivity.class);
        if(messageToDisplayIfAny != null && !messageToDisplayIfAny.trim().isEmpty()) {
            intent.putExtra(LandingPageActivity.MESSAGE_IF_ANY, messageToDisplayIfAny);
        }
        finish();
        startActivity(intent);
    }

    private void startBookmarkActivity() {
        Intent intent = new Intent(SplashActivity.this, BookmarkActivity.class);
        finish();
        startActivity(intent);
    }

    /*private void doLogin(UserInfo userInfo, boolean refreshToken) {
        new LoginTask(this, this, refreshToken).execute(userInfo);
    }*/
}