package com.pack.pack.application.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import android.widget.Toast;

//import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.pack.pack.application.AppController;
import com.pack.pack.application.Constants;
import com.pack.pack.application.R;
import com.pack.pack.application.fragments.MyFamilyMemoriesFragment;
import com.pack.pack.application.fragments.MySocietyDiscussionsFragment;
import com.pack.pack.application.fragments.MySocietyMemoriesFragment;
import com.pack.pack.application.topic.activity.model.ParcelableTopic;
import com.pack.pack.model.web.EntityType;

import static com.pack.pack.application.AppController.TOPIC_ID_KEY;

public class MySocietyActivity extends AppCompatActivity {

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

    private FloatingActionButton mysociety_fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_society);

        topic = (ParcelableTopic) getIntent().getParcelableExtra(AppController.TOPIC_PARCELABLE_KEY);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.mysociety_container);
        mViewPager.setAdapter(mSectionsPagerAdapter);


        mysociety_fab = (FloatingActionButton) findViewById(R.id.mysociety_fab);
        mysociety_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int index = mViewPager.getCurrentItem();
                if(index == 0) {
                    Intent intent = new Intent(MySocietyActivity.this, CreatePackActivity.class);
                    intent.putExtra(TOPIC_ID_KEY, topic.getTopicId());
                    startActivityForResult(intent, Constants.PACK_CREATE_REQUEST_CODE);
                } else if(index == 1) {
                    Intent intent = new Intent(MySocietyActivity.this, DiscussionStartActivity.class);
                    intent.putExtra(Constants.DISCUSSION_IS_REPLY, false);
                    intent.putExtra(Constants.DISCUSSION_ENTITY_ID, topic.getTopicId());
                    intent.putExtra(Constants.DISCUSSION_ENTITY_TYPE, EntityType.TOPIC.name());
                    startActivityForResult(intent, Constants.DISCUSSION_CREATE_REQUEST_CODE);
                }
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

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
        } else if(requestCode == Constants.DISCUSSION_CREATE_REQUEST_CODE) {
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
                    errorMsg = "You have cancelled to start new discussion";
                }
                Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.inside_society_menu, menu);
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
                startActivityForResult(intent, Constants.INVITE_OTHERS_TO_JOIN_FAMILY);
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
            switch (position) {
                case 0:
                    MySocietyMemoriesFragment fragment0 = new MySocietyMemoriesFragment();
                    Bundle bundle0 = new Bundle();
                    bundle0.putParcelable(MySocietyMemoriesFragment.TOPIC, topic);
                    fragment0.setArguments(bundle0);
                    return fragment0;
                case 1:
                    MySocietyDiscussionsFragment fragment1 = new MySocietyDiscussionsFragment();
                    Bundle bundle1 = new Bundle();
                    bundle1.putParcelable(MySocietyDiscussionsFragment.TOPIC, topic);
                    fragment1.setArguments(bundle1);
                    return fragment1;
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Memories";
                case 1:
                    return "Forum";
                /*case 2:
                    return "Helpdesk";*/
            }
            return null;
        }
    }
}
