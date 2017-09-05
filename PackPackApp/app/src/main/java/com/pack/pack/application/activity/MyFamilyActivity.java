package com.pack.pack.application.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

//import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.pack.pack.application.AppController;
import com.pack.pack.application.Constants;
import com.pack.pack.application.R;
import com.pack.pack.application.data.util.AbstractNetworkTask;
import com.pack.pack.application.data.util.IAsyncTaskStatusListener;
import com.pack.pack.application.fragments.MyFamilyMemoriesFragment;
import com.pack.pack.application.fragments.MyFamilyTopicSharedFeedsFragment;
import com.pack.pack.application.fragments.MySocietyTopicSharedFeedsFragment;
import com.pack.pack.application.image.loader.DownloadImageTask;
import com.pack.pack.application.topic.activity.model.ParcelableTopic;
import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.model.web.JPackAttachment;
import com.pack.pack.model.web.JRssFeed;
import com.pack.pack.model.web.Pagination;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private JRssFeed selectedFeedForUpload;

    private static final String[] TAB_NAMES = new String[] {"Shared", "Memories"};

    private MyFamilyTopicSharedFeedsFragment topicSharedFeedsFragment;

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
        tabLayout.setSelectedTabIndicatorColor(Color.WHITE);
        tabLayout.setupWithViewPager(mViewPager);


        for(int i=0; i<2; i++) {
            //tabLayout.getTabAt(i).setIcon(value.getIcon());
            tabLayout.getTabAt(i).setText(TAB_NAMES[i]);
            //tabLayout.getTabAt(i).setText(topic.getTopicName());
            i++;
        }


        myfamily_fab = (FloatingActionButton) findViewById(R.id.myfamily_fab);
        myfamily_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int index = mViewPager.getCurrentItem();
                if(index == 0) {
                    selectedFeedForUpload = null;
                    final Dialog copyLinkDialog = new Dialog(MyFamilyActivity.this);
                    copyLinkDialog.setContentView(R.layout.copy_link);
                    copyLinkDialog.setTitle("Share External Link");

                    final EditText copy_link_url = (EditText) copyLinkDialog.findViewById(R.id.copy_link_url);
                    Button copy_link_ok = (Button) copyLinkDialog.findViewById(R.id.copy_link_ok);

                    final RelativeLayout feed_display = (RelativeLayout) copyLinkDialog.findViewById(R.id.feed_display);
                    final ImageView feed_image = (ImageView) copyLinkDialog.findViewById(R.id.feed_image);
                    final TextView feed_title = (TextView) copyLinkDialog.findViewById(R.id.feed_title);
                    //final TextView feed_description = (TextView) copyLinkDialog.findViewById(R.id.feed_description);

                    final Button copy_link_done = (Button) copyLinkDialog.findViewById(R.id.copy_link_done);

                    final IAsyncTaskStatusListener testLinkListener = new IAsyncTaskStatusListener() {
                        @Override
                        public void onPreStart(String taskID) {
                        }

                        @Override
                        public void onSuccess(String taskID, Object data) {
                            if(data != null) {
                                feed_display.setVisibility(View.VISIBLE);
                                copy_link_done.setVisibility(View.VISIBLE);
                                selectedFeedForUpload = (JRssFeed) data;
                                feed_display.setTag(selectedFeedForUpload.getOgUrl());
                            }
                            if(selectedFeedForUpload != null) {
                                new DownloadImageTask(feed_image, 200, 200, MyFamilyActivity.this, null, false, true, true).execute(selectedFeedForUpload.getOgImage());
                                feed_title.setText(selectedFeedForUpload.getOgTitle());
                                //feed_description.setText(selectedFeedForUpload.getOgDescription());
                            }
                        }

                        @Override
                        public void onFailure(String taskID, String errorMsg) {
                            feed_display.setVisibility(View.GONE);
                            copy_link_done.setVisibility(View.GONE);
                            feed_display.setTag(null);
                        }

                        @Override
                        public void onPostComplete(String taskID) {

                        }
                    };

                    copy_link_ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String url = copy_link_url.getText() != null ? copy_link_url.getText().toString() : null;
                            if(url != null && !url.trim().isEmpty()) {
                                new ReadCopiedLink(MyFamilyActivity.this, testLinkListener).execute(url);
                            }
                        }
                    });

                    final IAsyncTaskStatusListener uploadLinkListener = new IAsyncTaskStatusListener() {
                        @Override
                        public void onPreStart(String taskID) {

                        }

                        @Override
                        public void onSuccess(String taskID, Object data) {
                            copyLinkDialog.dismiss();
                            if(data != null && (data instanceof JPackAttachment)) {
                                JPackAttachment attachment = (JPackAttachment) data;
                                List<JPackAttachment> attachments = new ArrayList<JPackAttachment>(2);
                                attachments.add(attachment);
                                Pagination<JPackAttachment> page = new Pagination<JPackAttachment>(null, null, attachments);
                                topicSharedFeedsFragment.handleSuccess(page);
                            } else {
                                topicSharedFeedsFragment.handleFailure("Failed to upload link");
                            }
                        }

                        @Override
                        public void onFailure(String taskID, String errorMsg) {
                            copyLinkDialog.dismiss();
                            topicSharedFeedsFragment.handleFailure("Failed to upload link");
                        }

                        @Override
                        public void onPostComplete(String taskID) {

                        }
                    };

                    copy_link_done.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(selectedFeedForUpload != null) {
                                ExternalLinkAttchmentData uploadData = new ExternalLinkAttchmentData();
                                uploadData.setTopicId(topic.getTopicId());
                                uploadData.setUserId(AppController.getInstance().getUserId());
                                uploadData.setTitle(selectedFeedForUpload.getOgTitle());
                                uploadData.setDescription(selectedFeedForUpload.getOgDescription());
                                uploadData.setAttachmentUrl(selectedFeedForUpload.getOgUrl());
                                uploadData.setAttachmentThumbnailUrl(selectedFeedForUpload.getOgImage());

                                new UploadExternalLink(MyFamilyActivity.this, uploadLinkListener).execute(uploadData);
                            }
                            selectedFeedForUpload = null;
                        }
                    });

                    copyLinkDialog.show();
                } else if(index == 1) {
                    Intent intent = new Intent(MyFamilyActivity.this, CreatePackActivity.class);
                    intent.putExtra(TOPIC_ID_KEY, topic.getTopicId());
                    startActivityForResult(intent, Constants.PACK_CREATE_REQUEST_CODE);
                }
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
                    MyFamilyTopicSharedFeedsFragment fragment1 = new MyFamilyTopicSharedFeedsFragment();
                    Bundle bundle1 = new Bundle();
                    bundle1.putParcelable(MyFamilyTopicSharedFeedsFragment.TOPIC, topic);
                    fragment1.setArguments(bundle1);
                    fragment = fragment1;
                    topicSharedFeedsFragment = fragment1;
                    break;
                case 1:
                    MyFamilyMemoriesFragment fragment2 = new MyFamilyMemoriesFragment();
                    Bundle bundle2 = new Bundle();
                    bundle2.putParcelable(MyFamilyMemoriesFragment.TOPIC, topic);
                    fragment2.setArguments(bundle2);
                    fragment = fragment2;
                    break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String title = null;
            switch (position) {
                case 0:
                    //title = TAB_NAMES[0];
                    title = "Shared";//topic.getTopicName();
                    break;
                case 1:
                    title = "Memories";
                    break;
            }
            return title;
        }
    }

    private class ReadCopiedLink extends AbstractNetworkTask<String, Integer, JRssFeed> {

        private String errorMsg;

        ReadCopiedLink(Context context, IAsyncTaskStatusListener listener) {
            super(false, false, false,context, false, true);
            addListener(listener);
        }

        @Override
        protected COMMAND command() {
            return COMMAND.CRAWL_FEED;
        }

        @Override
        protected String getFailureMessage() {
            return errorMsg;
        }

        @Override
        protected JRssFeed executeApi(API api) throws Exception {
            JRssFeed feed = null;
            try {
                feed = (JRssFeed) api.execute();
            } catch (Exception e) {
                errorMsg = "Failed reading from external link";
            }
            return feed;
        }

        @Override
        protected String getContainerIdForObjectStore() {
            return null;
        }

        @Override
        protected Map<String, Object> prepareApiParams(String inputObject) {
            Map<String, Object> apiParams = new HashMap<String, Object>();
            apiParams.put(APIConstants.ExternalResource.RESOURCE_URL, inputObject);
            return apiParams;
        }
    }

    private class UploadExternalLink extends AbstractNetworkTask<ExternalLinkAttchmentData, Integer, JPackAttachment> {

        private String errorMsg;

        UploadExternalLink(Context context, IAsyncTaskStatusListener listener) {
            super(false, false, false,context, true, true);
            addListener(listener);
        }

        @Override
        protected COMMAND command() {
            return COMMAND.ADD_VIDEO_TO_PACK_EXTERNAL_LINK;
        }

        @Override
        protected String getFailureMessage() {
            return errorMsg;
        }

        @Override
        protected JPackAttachment executeApi(API api) throws Exception {
            JPackAttachment attachment = null;
            try {
                attachment = (JPackAttachment) api.execute();
            } catch (Exception e) {
                errorMsg = "Failed reading to upload new attachment";
            }
            return attachment;
        }

        @Override
        protected String getContainerIdForObjectStore() {
            return null;
        }

        @Override
        protected Map<String, Object> prepareApiParams(ExternalLinkAttchmentData inputObject) {
            Map<String, Object> apiParams = new HashMap<String, Object>();
            apiParams.put(APIConstants.Topic.ID, inputObject.getTopicId());
            //apiParams.put(APIConstants.Pack.ID, inputObject.getPackId());
            apiParams.put(APIConstants.User.ID, inputObject.getUserId());
            apiParams.put(APIConstants.Attachment.TITLE, inputObject.getTitle());
            apiParams.put(APIConstants.Attachment.DESCRIPTION, inputObject.getDescription());
            apiParams.put(APIConstants.Attachment.ATTACHMENT_URL, inputObject.getAttachmentUrl());
            apiParams.put(APIConstants.Attachment.ATTACHMENT_THUMBNAIL_URL, inputObject.getAttachmentThumbnailUrl());
            return apiParams;
        }
    }

    private class ExternalLinkAttchmentData {

        private String topicId;

        private String userId;

        private String title;

        private String description;

        private String attachmentUrl;

        private String attachmentThumbnailUrl;

        public String getTopicId() {
            return topicId;
        }

        public void setTopicId(String topicId) {
            this.topicId = topicId;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getAttachmentUrl() {
            return attachmentUrl;
        }

        public void setAttachmentUrl(String attachmentUrl) {
            this.attachmentUrl = attachmentUrl;
        }

        public String getAttachmentThumbnailUrl() {
            return attachmentThumbnailUrl;
        }

        public void setAttachmentThumbnailUrl(String attachmentThumbnailUrl) {
            this.attachmentThumbnailUrl = attachmentThumbnailUrl;
        }
    }
}
