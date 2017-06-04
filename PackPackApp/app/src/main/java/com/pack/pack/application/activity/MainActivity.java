package com.pack.pack.application.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.pack.pack.application.AppController;
import com.pack.pack.application.R;
import com.pack.pack.application.adapters.MainActivityAdapter;
import com.pack.pack.application.fragments.TabType;
import com.pack.pack.application.topic.activity.model.ParcelableTopic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.pack.pack.application.AppController.CREATE_TOPIC_REQUSET_CODE;

/**
 *
 */
public class MainActivity extends AbstractAppCompatActivity {

    private ViewPager pager;

    private int pageCurrentItemIndex;

    public static final String PAGE_CURRENT_INDEX = "pageCurrentItemIndex";
    public static final String RECREATE = "RECREATE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton FAB = (FloatingActionButton) findViewById(R.id.topic_create);
        FAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, TopicCreateActivity.class);
                startActivityForResult(intent, CREATE_TOPIC_REQUSET_CODE);
            }
        });

        List<String> list = AppController.getInstance().getFollowedCategories();
        TabType[] values = TabType.values();
        Object __OBJECT = new Object();
        Map<String, Object> map = new HashMap<String, Object>();
        for(String l : list) {
            map.put(l, __OBJECT);
        }

        boolean recreate = getIntent().getBooleanExtra(RECREATE, false);

        List<TabType> types = new ArrayList<TabType>();
        //types.add(TabType.HOME);
        for(TabType value : values) {
            /*if(map.get(value.getType()) == null)
                continue;*/
            if(!value.isEnabled()) {
                continue;
            }
            value.setRecreate(recreate);
            types.add(value);
        }

        pager = (ViewPager)findViewById(R.id.pager);
        pager.setAdapter(new MainActivityAdapter(getSupportFragmentManager(), types.toArray(new TabType[types.size()])));
        int itemIndex = getIntent().getIntExtra(PAGE_CURRENT_INDEX, -1);
        if(itemIndex >= 0 && itemIndex < list.size()) {
            pageCurrentItemIndex = itemIndex;
            pager.setCurrentItem(pageCurrentItemIndex);
        }

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setSelectedTabIndicatorColor(Color.WHITE);
        tabLayout.setupWithViewPager(pager);

        int i=0;
        for(TabType value : types) {
            tabLayout.getTabAt(i).setIcon(value.getIcon());
            tabLayout.getTabAt(i).setText(value.getDisplayName());
            i++;
        }

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_menu, menu);
        MenuItem item0 = menu.findItem(R.id.app_settings);
        if(item0 != null) {
            item0.setVisible(true);
        }
        MenuItem item1 = menu.findItem(R.id.enter_forum);
        if(item1 != null) {
            item1.setVisible(false);
        }
        invalidateOptionsMenu();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.app_settings:
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        pageCurrentItemIndex = pager.getCurrentItem();
        outState.putInt(PAGE_CURRENT_INDEX, pageCurrentItemIndex);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        pageCurrentItemIndex = savedInstanceState.getInt(PAGE_CURRENT_INDEX);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case AppController.CAMERA_ACCESS_REQUEST_CODE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    AppController.getInstance().cameraPermissionGranted();
                    /*if(Build.VERSION.SDK_INT >= 11) {
                        recreate();
                    } else {
                        finish();
                        startActivity(getIntent());
                    }*/
                } else {
                    AppController.getInstance().cameraPermisionDenied();
                }
                break;
            case AppController.APP_EXTERNAL_STORAGE_READ_REQUEST_CODE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    AppController.getInstance().externalReadGranted();
                    /*if(Build.VERSION.SDK_INT >= 11) {
                        recreate();
                    } else {
                        finish();
                        startActivity(getIntent());
                    }*/
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
                ParcelableTopic pTopic = (ParcelableTopic) data.getParcelableExtra(TopicCreateActivity.RESULT_KEY);
                if(Build.VERSION.SDK_INT >= 11) {
                    getIntent().putExtra(PAGE_CURRENT_INDEX, pageCurrentItemIndex);
                    getIntent().putExtra(RECREATE, true);
                    recreate();
                } else {
                    finish();
                    getIntent().putExtra(PAGE_CURRENT_INDEX, pageCurrentItemIndex);
                    getIntent().putExtra(RECREATE, true);
                    startActivity(getIntent());
                }
            }
            else {
                Toast.makeText(MainActivity.this, "Sorry!! Failed creating new vision",
                        Toast.LENGTH_LONG).show();;
            }
        }
    }
}
