package com.pack.pack.application.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.pack.pack.application.R;
import com.pack.pack.application.data.cache.PreferenceManager;

public class IntroMainActivity extends AbstractActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_main);

        PreferenceManager prefManager = new PreferenceManager(getApplicationContext());
        if(prefManager.isFirstTimeLaunch()) {
            prefManager.setFirstTimeLaunch(false);
            startActivity(new Intent(IntroMainActivity.this, WelcomeActivity.class));
            finish();
        } else {
            startActivity(new Intent(IntroMainActivity.this, SplashActivity.class));
            finish();
        }
    }
}
