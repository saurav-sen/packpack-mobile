package com.pack.pack.application.activity;

import android.support.annotation.NonNull;
import android.os.Bundle;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.pack.pack.application.AppController;
import com.pack.pack.application.R;
import com.pack.pack.application.data.cache.PreferenceManager;

public class IntroMainActivity extends AbstractActivity {

    private static final String LOG_TAG = "IntroMainActivity";

    //private GoogleApiClient mGoogleApiClient;

   // private boolean deepLinkFound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_main);

        forward();

        /*FirebaseDynamicLinks.getInstance().getDynamicLink(getIntent())
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
        });*/
    }

   /* @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.w(LOG_TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services Error: " + connectionResult.getErrorCode(),
                Toast.LENGTH_SHORT).show();
    }*/

    /*private void handleDeepLinkBasedRouting(String deepLink) {
        String linkUrlPrefix0 = getString(R.string.invite_others_to_family_deeplink_base_url);
        String linkUrlPrefix1 = getString(R.string.invite_others_to_society_deeplink_base_url);
        if(deepLink.indexOf(linkUrlPrefix0) >= 0) {
            String topicID = deepLink.substring(linkUrlPrefix0.length());
            Log.d(LOG_TAG, "getInvitation: deepLinkUri=" + deepLink);
        } else if(deepLink.indexOf(linkUrlPrefix1) >= 0) {
            String topicID = deepLink.substring(linkUrlPrefix1.length());
            Log.d(LOG_TAG, "getInvitation: deepLinkUri=" + deepLink);
        }
    }*/

    private void forward() {
        PreferenceManager prefManager = new PreferenceManager(getApplicationContext());
        if(prefManager.isFirstTimeLaunch()) {
            startActivity(new Intent(IntroMainActivity.this, WelcomeActivity.class));
            finish();
        } else if(AppController.getInstance().isLandingPageActive()){
            Intent intent = new Intent(IntroMainActivity.this, LandingPageActivity.class);
            startActivity(intent);
            finish();
        } else {
            startActivity(new Intent(IntroMainActivity.this, SplashActivity.class));
            finish();
        }
    }

  /* private class AcceptTopicInviteTaskListener implements IAsyncTaskStatusListener {
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
   }*/
}
