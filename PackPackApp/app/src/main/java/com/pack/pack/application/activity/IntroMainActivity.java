package com.pack.pack.application.activity;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

/*import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.appinvite.AppInviteInvitationResult;
import com.google.android.gms.appinvite.AppInviteReferral;*/
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.appinvite.FirebaseAppInvite;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.pack.pack.application.AppController;
import com.pack.pack.application.R;
import com.pack.pack.application.data.cache.PreferenceManager;
import com.pack.pack.application.data.util.AbstractNetworkTask;
import com.pack.pack.application.data.util.IAsyncTaskStatusListener;
import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.model.web.JStatus;

import java.util.HashMap;
import java.util.Map;

public class IntroMainActivity extends AbstractActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final String LOG_TAG = "IntroMainActivity";

    //private GoogleApiClient mGoogleApiClient;

    private boolean deepLinkFound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_main);

        //forward();

        FirebaseDynamicLinks.getInstance().getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        if (pendingDynamicLinkData == null) {
                            Log.d(LOG_TAG, "getInvitation: No data");
                            return;
                        }

                        FirebaseAppInvite appInvite = FirebaseAppInvite.getInvitation(pendingDynamicLinkData);
                        if (appInvite != null) {
                            String appInviteId = appInvite.getInvitationId();
                            Log.d(LOG_TAG, "getInvitation: appInviteID=" + appInviteId);
                        }

                        Uri deepLinkUri = pendingDynamicLinkData.getLink();
                        if (deepLinkUri != null) {
                            String linkUrl = deepLinkUri.toString();
                            deepLinkFound = true;
                            handleDeepLinkBasedRouting(linkUrl);
                        }
                    }
                }).addOnCompleteListener(this, new OnCompleteListener<PendingDynamicLinkData>() {
            @Override
            public void onComplete(@NonNull Task<PendingDynamicLinkData> task) {
                if (!deepLinkFound) {
                    forward();
                }
            }
        });

      /* mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(AppInvite.API)
                .build();

        boolean autoLaunchDeepLink = false;
        AppInvite.AppInviteApi.getInvitation(mGoogleApiClient, this, autoLaunchDeepLink)
                .setResultCallback(
                        new ResultCallback<AppInviteInvitationResult>() {
                            @Override
                            public void onResult(@NonNull AppInviteInvitationResult result) {
                                if (result.getStatus().isSuccess()) {
                                    Intent intent = result.getInvitationIntent();
                                    String deepLink = AppInviteReferral.getDeepLink(intent);

                                    handleDeepLinkBasedRouting(deepLink);
                                } else {
                                    Log.d(LOG_TAG, "getInvitation: no deep link found.");
                                }
                            }
                        });*/


    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.w(LOG_TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services Error: " + connectionResult.getErrorCode(),
                Toast.LENGTH_SHORT).show();
    }

    private void handleDeepLinkBasedRouting(String deepLink) {
        String linkUrlPrefix0 = getString(R.string.invite_others_to_family_deeplink_base_url);
        String linkUrlPrefix1 = getString(R.string.invite_others_to_society_deeplink_base_url);
        if(deepLink.indexOf(linkUrlPrefix0) >= 0) {
            String topicID = deepLink.substring(linkUrlPrefix0.length());
            Log.d(LOG_TAG, "getInvitation: deepLinkUri=" + deepLink);
            //new AcceptTopicInviteTask(this).addListener(new AcceptTopicInviteTaskListener()).execute(topicID);
        } else if(deepLink.indexOf(linkUrlPrefix1) >= 0) {
            String topicID = deepLink.substring(linkUrlPrefix1.length());
            Log.d(LOG_TAG, "getInvitation: deepLinkUri=" + deepLink);
            //new AcceptTopicInviteTask(this).addListener(new AcceptTopicInviteTaskListener()).execute(topicID);
        }
    }

    private void forward() {
        PreferenceManager prefManager = new PreferenceManager(getApplicationContext());
        if(prefManager.isFirstTimeLaunch()) {
            //prefManager.setFirstTimeLaunch(false);
            startActivity(new Intent(IntroMainActivity.this, WelcomeActivity.class));
            finish();
        } else {
            startActivity(new Intent(IntroMainActivity.this, SplashActivity.class));
            finish();
        }
    }

   private class AcceptTopicInviteTaskListener implements IAsyncTaskStatusListener {
       @Override
       public void onSuccess(String taskID, Object data) {
           forward();
       }

       @Override
       public void onFailure(String taskID, String errorMsg) {
           forward();
       }

       @Override
       public void onPostComplete(String taskID) {

       }

       @Override
       public void onPreStart(String taskID) {

       }
   }
}
