package com.pack.pack.application.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.pack.pack.application.AppController;
import com.pack.pack.application.R;
import com.pack.pack.application.data.LoggedInUserInfo;
import com.pack.pack.application.data.util.IAsyncTaskStatusListener;
import com.pack.pack.application.data.util.LoginTask;
import com.pack.pack.application.db.DBUtil;
import com.pack.pack.application.db.SquillDbHelper;
import com.pack.pack.application.db.UserInfo;
import com.pack.pack.application.image.loader.DownloadProfilePictureTask;
import com.pack.pack.application.service.NetworkUtil;
import com.pack.pack.model.web.JUser;
import com.pack.pack.oauth1.client.AccessToken;

/**
 * Created by Saurav on 14-04-2017.
 */
public class NoConnectionEmptyActivity extends AbstractActivity implements IAsyncTaskStatusListener {

    private Button try_connect_again;

    private ProgressDialog progressDialog;

    private boolean trying = false;

    private void verify() {
        UserInfo userInfo = DBUtil.loadLastLoggedInUserInfo(new SquillDbHelper(this).getReadableDatabase());
        if(userInfo != null) {
            String oAuthToken = userInfo.getAccessToken();
            if(oAuthToken != null) {
                doLogin(userInfo, true);
            } else {
                doLogin(userInfo, false);
            }
        } else {
            hideProgressDialog();
        }
    }

    @Override
    public void onPreStart(String taskID) {
        showProgressDialog();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

            }
        }, 2000);
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
        //finish();
        startMainActivity();
    }

    @Override
    public void onPostComplete(String taskID) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_connection_empty);

        try_connect_again = (Button) findViewById(R.id.try_connect_again);
        try_connect_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryAgain();
            }
        });

        onNetworkDisconnect();
    }

    private void tryAgain() {
        try {
            trying = true;
            showProgressDialog();
            onNetworkDisconnect();
            if(NetworkUtil.checkConnectivity(NoConnectionEmptyActivity.this)) {
                onNetworkConnect();
                verify();
            } else {
                onNetworkDisconnect();
            }
        } finally {
            hideProgressDialog();
            trying = false;
        }
    }

    private void showProgressDialog() {
        if(progressDialog != null) {
            progressDialog.dismiss();
        }
        progressDialog = new ProgressDialog(NoConnectionEmptyActivity.this);
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
    public void onNetworkConnect() {
        super.onNetworkConnect();
        if(!trying) {
            tryAgain();
        }
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
        Intent intent = new Intent(NoConnectionEmptyActivity.this, LoginActivity.class);
        finish();
        startActivity(intent);
    }

    private void startMainActivity() {
        Intent intent = new Intent(NoConnectionEmptyActivity.this, LandingPageActivity.class);
        finish();
        startActivity(intent);
    }

    private void doLogin(UserInfo userInfo, boolean refreshToken) {
        new LoginTask(NoConnectionEmptyActivity.this, NoConnectionEmptyActivity.this, refreshToken).execute(userInfo);
    }
}
