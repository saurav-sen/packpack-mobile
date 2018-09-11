package com.pack.pack.application.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.pack.pack.application.AppController;
import com.pack.pack.application.R;
import com.pack.pack.application.data.LoggedInUserInfo;
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
import com.pack.pack.model.web.Pagination;
import com.squill.feed.web.model.JRssFeed;
import com.squill.feed.web.model.JRssFeedType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

        startNetworkChecker();
        startNTPService();
        startAddBookmarkService();

        if(!NetworkUtil.checkConnectivity(this)) {
            routeToTargetActivity();
        } else {
            loadUserInfo0();
            tryFetchingFeeds();
        }
    }

    private void tryFetchingFeeds() {
        NewsFeedTask newsFeedTask = new NewsFeedTask(SplashActivity.this, 0);
        SportsFeedTask sportsFeedTask = new SportsFeedTask(SplashActivity.this, 0);
        ScienceNewsFeedTask scienceNewsFeedTask = new ScienceNewsFeedTask(SplashActivity.this, 0);
        ArticlesFeedTask artilcesFeedTask = new ArticlesFeedTask(SplashActivity.this, 0);

        IAsyncTaskStatusListener listener = new FeedReceiveTaskStatusListener()
                .addTaskID(newsFeedTask.getTaskID(), JRssFeedType.NEWS)
                .addTaskID(sportsFeedTask.getTaskID(), JRssFeedType.NEWS_SPORTS)
                .addTaskID(scienceNewsFeedTask.getTaskID(), JRssFeedType.NEWS_SCIENCE_TECHNOLOGY)
                .addTaskID(artilcesFeedTask.getTaskID(), JRssFeedType.ARTICLE);

        newsFeedTask.addListener(listener);
        sportsFeedTask.addListener(listener);
        scienceNewsFeedTask.addListener(listener);
        artilcesFeedTask.addListener(listener);

        newsFeedTask.execute(String.valueOf(0));
        sportsFeedTask.execute(String.valueOf(0));
        scienceNewsFeedTask.execute(String.valueOf(0));
        artilcesFeedTask.execute(String.valueOf(0));
    }

    private class FeedReceiveTaskStatusListener implements IAsyncTaskStatusListener {

        private Set<String> taskIDs = new HashSet<>();

        private Map<String, JRssFeedType> taskIdVsFeedType = new HashMap<>();

        private FeedReceiveTaskStatusListener addTaskID(String taskID, JRssFeedType feedType) {
            this.taskIDs.add(taskID);
            this.taskIdVsFeedType.put(taskID, feedType);
            return this;
        }

        @Override
        public void onPreStart(String taskID) {
            // Do nothing
        }

        @Override
        public void onFailure(String taskID, String errorMsg) {
            // Do nothing
            if(this.taskIDs.remove(taskID)) {
                JRssFeedType feedType = this.taskIdVsFeedType.get(taskID);
                if(feedType != null) {
                    switch (feedType) {
                        case NEWS:
                            AppController.getInstance().getFeedReceiveState().setIsFirstPageNewsReceived(false);
                            AppController.getInstance().getFeedReceiveState().setNewsNextPageNo(0);
                            break;
                        case NEWS_SPORTS:
                            AppController.getInstance().getFeedReceiveState().setIsFirstPageSportsNewsReceived(false);
                            AppController.getInstance().getFeedReceiveState().setSportsNewsNextPageNo(0);
                            break;
                        case NEWS_SCIENCE_TECHNOLOGY:
                            AppController.getInstance().getFeedReceiveState().setIsFirstPageScienceNewsReceived(false);
                            AppController.getInstance().getFeedReceiveState().setScienceNewsNextPageNo(0);
                            break;
                        case ARTICLE:
                            AppController.getInstance().getFeedReceiveState().setIsFirstPageArticlesReceived(false);
                            AppController.getInstance().getFeedReceiveState().setArticlesNextPageNo(0);
                            break;
                    }
                }
            }
        }

        @Override
        public void onSuccess(String taskID, Object data) {
            if(this.taskIDs.remove(taskID) && data != null) {
                Pagination<JRssFeed> page = (Pagination<JRssFeed>) data;
                int nextPageNo = page.getNextPageNo();
                List<JRssFeed> list = page.getResult();
                if(list == null || list.isEmpty())
                    return;
                JRssFeedType feedType = this.taskIdVsFeedType.get(taskID);
                if(feedType != null) {
                    switch (feedType) {
                        case NEWS:
                            AppController.getInstance().getFeedReceiveState().setIsFirstPageNewsReceived(true);
                            AppController.getInstance().getFeedReceiveState().setNewsNextPageNo(nextPageNo);
                            break;
                        case NEWS_SPORTS:
                            AppController.getInstance().getFeedReceiveState().setIsFirstPageSportsNewsReceived(true);
                            AppController.getInstance().getFeedReceiveState().setSportsNewsNextPageNo(nextPageNo);
                            break;
                        case NEWS_SCIENCE_TECHNOLOGY:
                            AppController.getInstance().getFeedReceiveState().setIsFirstPageScienceNewsReceived(true);
                            AppController.getInstance().getFeedReceiveState().setScienceNewsNextPageNo(nextPageNo);
                            break;
                        case ARTICLE:
                            AppController.getInstance().getFeedReceiveState().setIsFirstPageArticlesReceived(true);
                            AppController.getInstance().getFeedReceiveState().setArticlesNextPageNo(nextPageNo);
                            break;
                    }
                }
            }
        }

        @Override
        public void onPostComplete(String taskID) {
            if(this.taskIDs.isEmpty()) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                    }
                }, 2000);
                loadUserInfoAndRouteToTargetActivity();
            }
        }
    }

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
                   bookmark.setSourceUrl(sharedText.trim());
                   bookmark.setTimeOfAdd(System.currentTimeMillis());
                   bookmark = DBUtil.storeNewBookmark(bookmark, this);
                   if(bookmark == null || bookmark.getEntityId() == null || bookmark.getEntityId().trim().isEmpty()) {
                       openLandingPageActivity("Failed to process link");
                   } else {
                       submitNewLinkToAddBookmarkService(bookmark.getEntityId());
                       startBookmarkActivity();
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