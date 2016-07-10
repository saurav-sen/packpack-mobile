package com.pack.pack.application.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.pack.pack.application.AppController;
import com.pack.pack.application.R;
import com.pack.pack.application.adapters.MainActivityAdapter;
import com.pack.pack.application.topic.activity.model.ParcelableTopic;

import static com.pack.pack.application.AppController.CREATE_TOPIC_REQUSET_CODE;

/**
 *
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*Toolbar toolbar = (Toolbar) findViewById(R.id.topic_toolbar);
        setSupportActionBar(toolbar);*/

        FloatingActionButton FAB = (FloatingActionButton) findViewById(R.id.topic_create);
        FAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, TopicCreateActivity.class);
                startActivityForResult(intent, CREATE_TOPIC_REQUSET_CODE);
            }
        });

        ViewPager pager = (ViewPager)findViewById(R.id.pager);
        pager.setAdapter(new MainActivityAdapter(getSupportFragmentManager()));

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(pager);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    AppController.CAMERA_ACCESS_REQUEST_CODE);
        } else {
            AppController.getInstance().cameraPermissionGranted();
        }

        if(!AppController.getInstance().isCameraPermissionGranted()
                && ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    AppController.APP_EXTERNAL_STORAGE_READ_REQUEST_CODE);
        } else {

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case AppController.CAMERA_ACCESS_REQUEST_CODE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    AppController.getInstance().cameraPermissionGranted();
                    finish();
                    startActivity(getIntent());
                } else {
                    AppController.getInstance().cameraPermisionDenied();
                }
                break;
            case AppController.APP_EXTERNAL_STORAGE_READ_REQUEST_CODE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    AppController.getInstance().externalReadGranted();
                    finish();
                    startActivity(getIntent());
                } else {
                    AppController.getInstance().externalReadDenied();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CREATE_TOPIC_REQUSET_CODE) {
            if(resultCode == RESULT_OK && data != null) {
                // TODO -- Refresh the list with newly created topic.
                ParcelableTopic pTopic = (ParcelableTopic) data.getParcelableExtra(TopicCreateActivity.RESULT_KEY);
                finish();
                startActivity(getIntent());
            }
            else {
                Toast.makeText(MainActivity.this, "Sorry!! Failed creating new topic",
                        Toast.LENGTH_LONG).show();;
            }
        }
    }
}
