package com.pack.pack.application.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.pack.pack.application.AppController;
import com.pack.pack.application.R;
import com.pack.pack.application.async.FeedReceiveCallback;
import com.pack.pack.application.async.FeedReceiveTaskStatusListener;
import com.pack.pack.application.async.FeedsDownloadUtil;
import com.pack.pack.application.data.util.ArticlesFeedTask;
import com.pack.pack.application.data.util.IAsyncTaskStatusListener;
import com.pack.pack.application.data.util.NewsFeedTask;
import com.pack.pack.application.data.util.ScienceNewsFeedTask;
import com.pack.pack.application.data.util.SportsFeedTask;
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
import com.squill.feed.web.model.JRssFeedType;

/**
 *
 * @author Saurav
 *
 */
public class SplashActivity extends AbstractActivity /*implements IAsyncTaskStatusListener*/ {

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        JUser user = AppController.getInstance().getUser();
        String action = getIntent().getAction();
        String type = getIntent().getType();
        if(user != null && Intent.ACTION_SEND.equals(action) && type != null){ // Share from external flow
            routeToTargetActivity();
        } else {
            startNetworkChecker();
            startNTPService();
            startAddBookmarkService();

            if(!NetworkUtil.checkConnectivity(this)) {
                routeToTargetActivity();
            } else {
                loadUserInfo0();
                FeedReceiveCallback callback = new FeedReceiveCallback() {
                    @Override
                    public void handleTaskComplete() {
                        loadUserInfoAndRouteToTargetActivity();
                    }
                };
                FeedsDownloadUtil.downloadLatestFeedsFromOrigin(SplashActivity.this, callback, false);
            }
        }
    }

    private void startAddBookmarkService() {
        Intent intent = new Intent(this, AddBookmarkService.class);
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

    private void loadUserInfo0() {
        String userName = AppController.getInstance().getUserEmail();
        if(userName == null || userName.trim().isEmpty()) {
            UserInfo userInfo = DBUtil.loadLastLoggedInUserInfo(new SquillDbHelper(this).getReadableDatabase());
            if(userInfo == null) {
                openSignupActivity();
            } else {
                userName = userInfo.getUsername();
                if(userName == null || userName.trim().isEmpty()) {
                    openSignupActivity();
                } else {
                    JUser user = DBUtil.convertUserInfo(userInfo);
                    AppController.getInstance().setUser(user);
                    if(user.getProfilePictureUrl() != null && !user.getProfilePictureUrl().trim().isEmpty()) {
                        new DownloadProfilePictureTask().execute(user.getProfilePictureUrl());
                    }
                }
            }
        }
    }

   private void loadUserInfoAndRouteToTargetActivity() {
        String userName = AppController.getInstance().getUserEmail();
        if(userName != null) {
            finish();
            routeToTargetActivity();
        } else {
            UserInfo userInfo = DBUtil.loadLastLoggedInUserInfo(new SquillDbHelper(this).getReadableDatabase());
            if(userInfo != null) {
                userName = userInfo.getUsername();
                if(userName != null) {
                    JUser user = DBUtil.convertUserInfo(userInfo);
                    AppController.getInstance().setUser(user);
                    finish();
                    openLandingPageActivity();
                } else {
                    openSignupActivity();
                }
            } else {
                openSignupActivity();
            }
        }
    }

    /*@Override
    public void onPreStart(String taskID) {
        showProgressDialog();
    }

    @Override
    public void onSuccess(String taskID, Object data) {
        LoggedInUserInfo userInfo = (LoggedInUserInfo) data;
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

    }*/

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

    /*@Override
    public void onFailure(String taskID, String errorMsg) {
        hideProgressDialog();
        openSignupActivity();
    }*/

    private void openSignupActivity() {
        Intent intent = new Intent(this, SignupActivity.class);
        finish();
        startActivity(intent);
    }

    private void routeToTargetActivity() {
        String action = getIntent().getAction();
        String type = getIntent().getType();
        if(Intent.ACTION_SEND.equals(action) && type != null){
           if("text/plain".equals(type)) {
                String sharedText = getIntent().getStringExtra(Intent.EXTRA_TEXT);
               if(sharedText == null || sharedText.trim().isEmpty()) {
                   openLandingPageActivity("Invalid Link Found");
               } else {
                   Bookmark bookmark = new Bookmark();
                   bookmark.setProcessed(false);
                   bookmark.setTitle(sharedText);
                   bookmark.setSourceUrl(sharedText.trim());
                   bookmark.setEntityId(bookmark.getSourceUrl());
                   bookmark.setTimeOfAdd(System.currentTimeMillis());
                   bookmark = DBUtil.storeNewBookmark(bookmark, this);
                   if(bookmark == null || bookmark.getEntityId() == null || bookmark.getEntityId().trim().isEmpty()) {
                       openLandingPageActivity("Failed to process link");
                   } else {
                       startAddBookmarkService();
                      // startBookmarkActivity();
                       openLandingPageActivity();
                   }
               }
            } else {
               openLandingPageActivity();
            }
        } else {
            openLandingPageActivity();
        }
    }

    private void openLandingPageActivity() {
        openLandingPageActivity(null);
    }

    private void openLandingPageActivity(String messageToDisplayIfAny) {
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
}