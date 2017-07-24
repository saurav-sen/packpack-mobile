package com.pack.pack.application.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.Toast;

//import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.pack.pack.application.AppController;
import com.pack.pack.application.Constants;
import com.pack.pack.application.R;
import com.pack.pack.application.fragments.MyFamilyMemoriesFragment;
import com.pack.pack.application.topic.activity.model.ParcelableTopic;

import static com.pack.pack.application.AppController.TOPIC_ID_KEY;

public class MyFamilyActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ParcelableTopic topic;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private FloatingActionButton myfamily_fab;

    //private static final String[] TAB_NAMES = new String[] {"Memories"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_family);

        topic = (ParcelableTopic) getIntent().getParcelableExtra(AppController.TOPIC_PARCELABLE_KEY);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);*/

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.myfamily_container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(2);
        /*mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });*/

        TabLayout tabLayout = (TabLayout) findViewById(R.id.myfamily_tabs);
        //tabLayout.setSelectedTabIndicatorColor(Color.WHITE);
        tabLayout.setupWithViewPager(mViewPager);


        for(int i=0; i<2; i++) {
            //tabLayout.getTabAt(i).setIcon(value.getIcon());
            //tabLayout.getTabAt(i).setText(TAB_NAMES[i]);
            tabLayout.getTabAt(i).setText(topic.getTopicName());
            i++;
        }


        myfamily_fab = (FloatingActionButton) findViewById(R.id.myfamily_fab);
        myfamily_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*int index = mViewPager.getCurrentItem();
                if(index == 0) {
                    Intent intent = new Intent(MyFamilyActivity.this, CreatePackActivity.class);
                    intent.putExtra(TOPIC_ID_KEY, topic.getTopicId());
                    startActivityForResult(intent, Constants.PACK_CREATE_REQUEST_CODE);
                } else if(index == 1) {

                }*/
                Intent intent = new Intent(MyFamilyActivity.this, CreatePackActivity.class);
                intent.putExtra(TOPIC_ID_KEY, topic.getTopicId());
                startActivityForResult(intent, Constants.PACK_CREATE_REQUEST_CODE);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Constants.PACK_CREATE_REQUEST_CODE) {
            if(resultCode == RESULT_OK) {
                if(Build.VERSION.SDK_INT >= 11) {
                    recreate();
                } else {
                    finish();
                    startActivity(getIntent());
                }
            } else if(resultCode == RESULT_CANCELED) {
                String errorMsg = data != null ? data.getStringExtra(Constants.ERROR_MSG) : null;
                if(errorMsg == null || errorMsg.trim().isEmpty()) {
                    errorMsg = "You have cancelled to create new album";
                }
                Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
            }
        } else if(requestCode == Constants.INVITE_OTHERS_TO_JOIN_TOPIC) {
            if(resultCode == RESULT_OK) {
                Toast.makeText(this, "Invite sent successfully", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Failed sending invite", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.inside_family_menu, menu);
        /*MenuItem item0 = menu.findItem(R.id.app_settings);
        if(item0 != null) {
            item0.setVisible(true);
        }*/
        /*MenuItem item1 = menu.findItem(R.id.enter_forum);
        if(item1 != null) {
            item1.setVisible(true);
        }*/
        invalidateOptionsMenu();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            /*case R.id.enter_forum:
                Intent intent = new Intent(InsideTopicActivity.this, DiscussionViewActivity.class);
                intent.putExtra(Constants.DISCUSSION_ENTITY_ID, topic.getTopicId());
                intent.putExtra(Constants.DISCUSSION_ENTITY_TYPE, EntityType.TOPIC.name());
                startActivity(intent);
                break;*/
            case R.id.invite_others:
                Intent intent = new AppInviteInvitation.IntentBuilder(topic.getTopicName())
                        .setMessage(topic.getDescription())
                        .setDeepLink(Uri.parse(getString(R.string.invite_others_to_family_deeplink_base_url) + topic.getTopicId()))
                        .setCustomImage(Uri.parse(topic.getWallpaperUrl()))
                        .setCallToActionText("Join My Family")
                        .build();
                startActivityForResult(intent, Constants.INVITE_OTHERS_TO_JOIN_TOPIC);
                break;
            case R.id.invite_others_alt:
                Intent intent1 = new AppInviteInvitation.IntentBuilder(topic.getTopicName())
                        .setMessage(topic.getDescription())
                        .setDeepLink(Uri.parse(getString(R.string.invite_others_to_family_deeplink_base_url) + topic.getTopicId()))
                        .setCustomImage(Uri.parse(topic.getWallpaperUrl()))
                        .setCallToActionText("Join My Family")
                        .build();
                startActivityForResult(intent1, Constants.INVITE_OTHERS_TO_JOIN_TOPIC);
                break;
        }
        return true;
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            Fragment fragment = null;
            switch (position) {
                case 0:
                    MyFamilyMemoriesFragment fragment0 = new MyFamilyMemoriesFragment();
                    Bundle bundle0 = new Bundle();
                    bundle0.putParcelable(MyFamilyMemoriesFragment.TOPIC, topic);
                    fragment0.setArguments(bundle0);
                    fragment = fragment0;
                    break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String title = null;
            switch (position) {
                case 0:
                    //title = TAB_NAMES[0];
                    title = topic.getTopicName();
                    break;
            }
            return title;
        }
    }
}
