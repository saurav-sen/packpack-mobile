package com.pack.pack.application.activity;

import android.support.annotation.NonNull;
import android.os.Bundle;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.pack.pack.application.AppController;
import com.pack.pack.application.Constants;
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
            if(getIntent().getExtras() != null && Constants.NOTIFICATION_DATA_MSG_TYPE.equals(getIntent().getExtras().getString(Constants.MSG_TYPE))) {
                intent.putExtra(Constants.OG_TITLE, getIntent().getExtras().getString(Constants.OG_TITLE));
                intent.putExtra(Constants.OG_IMAGE, getIntent().getExtras().getString(Constants.OG_IMAGE));
                intent.putExtra(Constants.OG_URL, getIntent().getExtras().getString(Constants.OG_URL));
                intent.putExtra(Constants.SUMMARY_TEXT, getIntent().getExtras().getString(Constants.SUMMARY_TEXT));
                intent.putExtra(Constants.SHAREABLE_URL, getIntent().getExtras().getString(Constants.SHAREABLE_URL));
                intent.putExtra(Constants.MSG_TYPE, getIntent().getExtras().getString(Constants.MSG_TYPE));
            }
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(IntroMainActivity.this, SplashActivity.class);
            if(getIntent().getExtras() != null && Constants.NOTIFICATION_DATA_MSG_TYPE.equals(getIntent().getExtras().getString(Constants.MSG_TYPE))) {
                intent.putExtra(Constants.OG_TITLE, getIntent().getExtras().getString(Constants.OG_TITLE));
                intent.putExtra(Constants.OG_IMAGE, getIntent().getExtras().getString(Constants.OG_IMAGE));
                intent.putExtra(Constants.OG_URL, getIntent().getExtras().getString(Constants.OG_URL));
                intent.putExtra(Constants.SUMMARY_TEXT, getIntent().getExtras().getString(Constants.SUMMARY_TEXT));
                intent.putExtra(Constants.SHAREABLE_URL, getIntent().getExtras().getString(Constants.SHAREABLE_URL));
                intent.putExtra(Constants.MSG_TYPE, getIntent().getExtras().getString(Constants.MSG_TYPE));
            }

            /*intent.putExtra(Constants.OG_TITLE, "Lok Sabha elections 2019: 0% voting in Anantnag: 'Give anger a voice,' appeals Omar Abdullah");
            intent.putExtra(Constants.OG_IMAGE, "https://img-s-msn-com.akamaized.net/tenant/amp/entityid/BBWcjX3.img?h=740&w=799&m=6&q=60&o=f&l=f&x=1660&y=882");
            intent.putExtra(Constants.OG_URL, "https://www.msn.com/en-in/news/jammu-and-kashmir/lok-sabha-elections-2019%E2%80%890percent-voting-in-anantnag-give-anger-a-voice-appeals-omar-abdullah/ar-BBWco6B?li=AAggbRN");
            intent.putExtra(Constants.SUMMARY_TEXT, "The voting percentage figures released by the Election Commission showed 0% voting in Anantnag at 9 am as voters stayed indoors and "
                    + "its 1,842 polling booths wore a deserted look.. Considered a PDP bastion, Mehbooba Mufti had won from here in 2014 defeating NC "
                    + "candidate Mehboob Beg by more than 65,000 votes.. When Mehbooba became J K chief minister in 2016 after her father's demise, "
                    + "she got elected from her father's assembly seat and vacated her Lok Sabha seat with an intention to field her younger brother "
                    + "Tasaduq Mufti from there..");
            intent.putExtra(Constants.SHAREABLE_URL, "http://squill.in/sh/bGKHvx");*/

            startActivity(intent);
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
