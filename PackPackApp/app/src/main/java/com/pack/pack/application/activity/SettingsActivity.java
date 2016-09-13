package com.pack.pack.application.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.support.v7.app.ActionBar;
import android.text.InputType;
import android.util.Log;

import com.pack.pack.application.AppController;
import com.pack.pack.application.R;
import com.pack.pack.application.data.util.ApiConstants;
import com.pack.pack.application.topic.activity.model.ParcelableTopic;
import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIBuilder;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.model.web.JTopic;

import java.util.List;

/**
 * Created by Saurav on 03-09-2016.
 */
public class SettingsActivity extends AppCompatPreferenceActivity {

    private static final String PARCELABLE_TOPIC_KEY = "topic";

    private static final String DISPLAY_NAME = "display_name";
    private static final String USER_ADDRESS = "user_address";
    private static final String PROFILE_PICTURE = "profilePicPref";

    private static Preference.OnPreferenceChangeListener onPreferenceChangeListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object o) {
            String key = preference.getKey();
            String value = o.toString();
            preference.setDefaultValue(value);
            ParcelableTopic topic = (ParcelableTopic) preference.getExtras().getParcelable(PARCELABLE_TOPIC_KEY);
            if(topic != null) {
                key = key.substring(key.indexOf(".")+1);
                TopicPreferenceSettings[] topicPreferenceSettingses = new TopicPreferenceSettings[] {
                        new TopicPreferenceSettings(key, value, topic.getTopicId())};
                new UpdateTopicSettingsTask().execute(topicPreferenceSettingses);
            }
            return true;
        }
    };

    @Override
    protected boolean isValidFragment(String fragmentName) {
        return AccountsFragment.class.getName().equals(fragmentName) || TopicSettingsFragment.class.getName().equals(fragmentName);
    }

    private static void bindPreferenceSummaryToValue(Preference preference) {
        preference.setOnPreferenceChangeListener(onPreferenceChangeListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        super.onBuildHeaders(target);

        {
            Header accountsHeader = new Header();
            accountsHeader.fragment = "com.pack.pack.application.activity.SettingsActivity$AccountsFragment";
            accountsHeader.iconRes = getResources().getIdentifier("accounts_settings_icon", "drawable", this.getPackageName());
            accountsHeader.title = "Accounts";
            target.add(accountsHeader);
        }

        {
            List<JTopic> userOwnedTopics = AppController.getInstance().getUserOwnedTopics();
            if(userOwnedTopics != null && !userOwnedTopics.isEmpty()) {
                for(JTopic userOwnedTopic : userOwnedTopics) {
                    Header topicSettingsHeader = new Header();
                    topicSettingsHeader.fragment = "com.pack.pack.application.activity.SettingsActivity$TopicSettingsFragment";
                    topicSettingsHeader.iconRes = getResources().getIdentifier("topic_settings_icon", "drawable", this.getPackageName());
                    topicSettingsHeader.title = userOwnedTopic.getName();
                    Bundle extras = new Bundle();
                    extras.putParcelable(PARCELABLE_TOPIC_KEY, new ParcelableTopic(userOwnedTopic));
                    topicSettingsHeader.fragmentArguments = extras;
                    target.add(topicSettingsHeader);
                }
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class AccountsFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_accounts);

            bindPreferenceSummaryToValue(findPreference(DISPLAY_NAME));
            bindPreferenceSummaryToValue(findPreference(USER_ADDRESS));
            bindPreferenceSummaryToValue(findPreference(PROFILE_PICTURE));
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class TopicSettingsFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            //addPreferencesFromResource(R.xml.pref_topics);
            ParcelableTopic topic = getArguments().getParcelable(PARCELABLE_TOPIC_KEY);
            setPreferenceScreen(createPreferenceScreen(topic));

            Preference allow_followers_to_promote = findPreference(topic.getTopicId() + ".allow_followers_to_promote");
            allow_followers_to_promote.getExtras().putParcelable(PARCELABLE_TOPIC_KEY, topic);
            bindPreferenceSummaryToValue(allow_followers_to_promote);

            Preference number_of_promotions = findPreference(topic.getTopicId() + ".number_of_promotions");
            number_of_promotions.getExtras().putParcelable(PARCELABLE_TOPIC_KEY, topic);
            bindPreferenceSummaryToValue(number_of_promotions);

            Preference enable_live_streaming_service = findPreference(topic.getTopicId() + ".enable_live_streaming_service");
            enable_live_streaming_service.getExtras().putParcelable(PARCELABLE_TOPIC_KEY, topic);
            bindPreferenceSummaryToValue(enable_live_streaming_service);

            Preference enable_notification_service = findPreference(topic.getTopicId() + ".enable_notification_service");
            enable_notification_service.getExtras().putParcelable(PARCELABLE_TOPIC_KEY, topic);
            bindPreferenceSummaryToValue(enable_notification_service);
        }

        private PreferenceScreen createPreferenceScreen(ParcelableTopic topic) {
            Context context = this.getActivity();
            PreferenceScreen preferenceScreen = getPreferenceManager().createPreferenceScreen(context);

            SwitchPreference allow_followers_to_promote = new SwitchPreference(context);
            allow_followers_to_promote.setKey(topic.getTopicId() + ".allow_followers_to_promote");
            allow_followers_to_promote.setTitle("Allow Topic Followers To Promote");
            allow_followers_to_promote.setDefaultValue(false);
            allow_followers_to_promote.setSwitchTextOff("Disabled");
            allow_followers_to_promote.setSwitchTextOn("Enabled");
            allow_followers_to_promote.getExtras().putParcelable(PARCELABLE_TOPIC_KEY, topic);
            preferenceScreen.addPreference(allow_followers_to_promote);

            EditTextPreference number_of_promotions = new EditTextPreference(context);
            number_of_promotions.setKey(topic.getTopicId() + ".number_of_promotions");
            number_of_promotions.setTitle("Number of promotions");
            number_of_promotions.setDefaultValue("500");
            number_of_promotions.getEditText().setInputType(InputType.TYPE_NUMBER_VARIATION_NORMAL);
            number_of_promotions.getExtras().putParcelable(PARCELABLE_TOPIC_KEY, topic);
            preferenceScreen.addPreference(number_of_promotions);

            SwitchPreference enable_live_streaming_service = new SwitchPreference(context);
            enable_live_streaming_service.setKey(topic.getTopicId() + ".enable_live_streaming_service");
            enable_live_streaming_service.setTitle("Enable Live Streaming Service");
            enable_live_streaming_service.setDefaultValue(false);
            enable_live_streaming_service.getExtras().putParcelable(PARCELABLE_TOPIC_KEY, topic);
            preferenceScreen.addPreference(enable_live_streaming_service);

            SwitchPreference enable_notification_service = new SwitchPreference(context);
            enable_notification_service.setKey(topic.getTopicId() + ".enable_notification_service");
            enable_notification_service.setTitle("Enable Notification Service");
            enable_notification_service.setDefaultValue(false);
            enable_notification_service.getExtras().putParcelable(PARCELABLE_TOPIC_KEY, topic);
            preferenceScreen.addPreference(enable_notification_service);

            return preferenceScreen;
        }
    }

    private static class PreferenceSettings {

        private String key;

        private String value;

        public PreferenceSettings(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    private static class TopicPreferenceSettings extends PreferenceSettings {

        private String topicId;

        public TopicPreferenceSettings(String key, String value, String topicId) {
            super(key, value);
            this.topicId = topicId;
        }

        public String getTopicId() {
            return topicId;
        }

        public void setTopicId(String topicId) {
            this.topicId = topicId;
        }
    }

    private static class UpdateTopicSettingsTask extends AsyncTask<TopicPreferenceSettings, Void, Void> {

        private static final String LOG_TAG = "UpdateTopicSettingsTask";

        @Override
        protected Void doInBackground(TopicPreferenceSettings... topicPreferenceSettingses) {
            if(topicPreferenceSettingses == null || topicPreferenceSettingses.length == 0)
                return null;
            TopicPreferenceSettings topicPreferenceSettings = topicPreferenceSettingses[0];
            try {
                API api = APIBuilder.create(ApiConstants.BASE_URL)
                        .setAction(COMMAND.EDIT_TOPIC_SETTINGS)
                        .setOauthToken(AppController.getInstance().getoAuthToken())
                        .addApiParam(APIConstants.Topic.ID, topicPreferenceSettings.getTopicId())
                        .addApiParam(APIConstants.User.ID, AppController.getInstance().getUserId())
                        .addApiParam(APIConstants.TopicSettings.KEY, topicPreferenceSettings.getKey())
                        .addApiParam(APIConstants.TopicSettings.VALUE, topicPreferenceSettings.getValue())
                        .build();
                api.execute();
            } catch (Exception e) {
                Log.i(LOG_TAG, e.getMessage(), e);
            }
            return null;
        }
    }
}