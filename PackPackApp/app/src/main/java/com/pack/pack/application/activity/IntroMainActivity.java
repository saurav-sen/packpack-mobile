package com.pack.pack.application.activity;

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

import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.appinvite.AppInviteInvitationResult;
import com.google.android.gms.appinvite.AppInviteReferral;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.appinvite.FirebaseAppInvite;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.pack.pack.application.R;
import com.pack.pack.application.data.cache.PreferenceManager;

public class IntroMainActivity extends AbstractAppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final String LOG_TAG = "IntroMainActivity";

    //private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_main);

        FirebaseDynamicLinks.getInstance().getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        if (pendingDynamicLinkData == null) {
                            Log.d(LOG_TAG, "getInvitation: No data");
                            return;
                        }

                        FirebaseAppInvite appInvite = FirebaseAppInvite.getInvitation(pendingDynamicLinkData);
                        if(appInvite != null) {
                            String appInviteId = appInvite.getInvitationId();
                            Log.d(LOG_TAG, "getInvitation: appInviteID=" + appInviteId);
                        }

                        Uri deepLinkUri = pendingDynamicLinkData.getLink();
                        if(deepLinkUri != null) {
                            Log.d(LOG_TAG, "getInvitation: deepLinkUri=" + deepLinkUri.toString());
                            handleDeepLinkBasedRouting(deepLinkUri.toString());
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
                                    // Extract deep link from Intent
                                    Intent intent = result.getInvitationIntent();
                                    String deepLink = AppInviteReferral.getDeepLink(intent);

                                    // Handle the deep link. For example, open the linked
                                    // content, or apply promotional credit to the user's
                                    // account.

                                    // [START_EXCLUDE]
                                    // Display deep link in the UI
                                    //((TextView) findViewById(R.id.link_view_receive)).setText(deepLink);
                                    // [END_EXCLUDE]
                                } else {
                                    Log.d(LOG_TAG, "getInvitation: no deep link found.");
                                }
                            }
                        });*/

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

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.w(LOG_TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services Error: " + connectionResult.getErrorCode(),
                Toast.LENGTH_SHORT).show();
    }

    private void handleDeepLinkBasedRouting(String deepLink) {

    }
}
