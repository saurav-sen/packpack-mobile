package com.pack.pack.application.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.text.InputType;
import android.util.Log;
import android.widget.BaseAdapter;

import com.pack.pack.application.AppController;
import com.pack.pack.application.R;
import com.pack.pack.application.data.util.ApiConstants;
import com.pack.pack.application.data.util.UserUtil;
import com.pack.pack.application.view.ProfilePicturePreference;
import com.pack.pack.client.api.API;
import com.pack.pack.client.api.APIBuilder;
import com.pack.pack.client.api.APIConstants;
import com.pack.pack.client.api.COMMAND;
import com.pack.pack.model.web.JUser;
import com.pack.pack.model.web.dto.UserSettings;

import java.util.LinkedList;
import java.util.List;

import static com.pack.pack.application.AppController.CAMERA_CAPTURE_PHOTO_REQUEST_CODE;
import static com.pack.pack.application.AppController.CROP_PHOTO_REQUEST_CODE;

/**
 * Created by Saurav on 03-09-2016.
 */
public class SettingsActivity extends AppCompatPreferenceActivity {

    public static final String PROFILE_PICTURE = "profilePicPref";

    public static final String PROFILE_PICTURE_CHANGE_KEY = "profilePictureChange";
    public static final String PROFILE_PICTURE_CROP_KEY = "profilePictureCrop";

    private List<SettingsChangeListener> listeners;

    public void addSettingsChangeListener(SettingsChangeListener listener) {
        if(listeners == null) {
            listeners = new LinkedList<SettingsChangeListener>();
        }
        if(listener == null) {
            return;
        }
        listeners.add(listener);
    }

    private static Preference.OnPreferenceChangeListener onPreferenceChangeListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object o) {
            if(preference instanceof ProfilePicturePreference) {
                return true;
            }
            String key = preference.getKey();
            String value = o.toString();
            preference.setDefaultValue(value);
            preference.setTitle(value);
            if(UserSettings.DISPLAY_NAME.equals(key) || UserSettings.USER_ADDRESS.equals(key)){
                PreferenceObj preferenceObj = new PreferenceObj(key, value);
                JUser user = AppController.getInstance().getUser();
                if(UserSettings.DISPLAY_NAME.equals(key)) {
                    user.setDisplayName(value);
                }
                new UpdateUserSettings().execute(preferenceObj);
            }
            return true;
        }
    };

    @Override
    protected boolean isValidFragment(String fragmentName) {
        return AccountsFragment.class.getName().equals(fragmentName);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == CROP_PHOTO_REQUEST_CODE) {
            if(resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                Bitmap bitmap = extras.getParcelable("data");
                String preferenceKey = PROFILE_PICTURE_CHANGE_KEY;
                fireOnPreferenceChangeListeners(preferenceKey, bitmap);
                finish();
                startActivity(getIntent());
            }
        } else if(requestCode == CAMERA_CAPTURE_PHOTO_REQUEST_CODE) {
            if(resultCode == RESULT_OK) {
                fireOnPreferenceChangeListeners(PROFILE_PICTURE_CROP_KEY, null);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void fireOnPreferenceChangeListeners(String preferenceKey, Bitmap bitmap) {
        if(listeners == null || listeners.isEmpty()) {
            return;
        }
        for(SettingsChangeListener listener : listeners) {
            listener.onChange(preferenceKey, bitmap);
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
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class AccountsFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_accounts);

            Preference userFullName = findPreference(UserSettings.USER_NAME);
            JUser user = AppController.getInstance().getUser();
            if(user != null) {
                userFullName.setDefaultValue(user.getName());
                userFullName.setTitle(user.getName());
            }

            Preference displayName = findPreference(UserSettings.DISPLAY_NAME);
            if(user != null) {
                displayName.setDefaultValue(UserUtil.resolveUserDisplayName(user));
                displayName.setTitle(UserUtil.resolveUserDisplayName(user));
            }
            bindPreferenceSummaryToValue(displayName);

            /*Preference userName = findPreference(UserSettings.USER_NAME);
            if(user != null) {
                userName.setDefaultValue(user.getName());
                userName.setTitle(user.getName());
            }
            bindPreferenceSummaryToValue(userName);*/

            Preference user_addr = findPreference(UserSettings.USER_ADDRESS);
            if(user != null) {
                String city = user.getCity();
                String country = user.getCountry();
                if(city != null && !city.trim().isEmpty() && country != null && !country.trim().isEmpty()) {
                    String addr = city + ", " + country;
                    user_addr.setDefaultValue(addr);
                    user_addr.setTitle(addr);
                }
            }
            bindPreferenceSummaryToValue(user_addr);

            ProfilePicturePreference profilePicturePreference = (ProfilePicturePreference) findPreference(PROFILE_PICTURE);
            bindPreferenceSummaryToValue(profilePicturePreference);

            ((SettingsActivity) getActivity()).addSettingsChangeListener(profilePicturePreference);
        }
    }

    private static class UpdateUserSettings extends AsyncTask<PreferenceObj, Void, Void> {

        private static final String LOG_TAG = "UpdateUserSettings";

        @Override
        protected Void doInBackground(PreferenceObj... preferences) {
            if(preferences == null || preferences.length == 0)
                return null;
            PreferenceObj preference = preferences[0];
            try {
                API api = APIBuilder.create(ApiConstants.BASE_URL)
                        .setAction(COMMAND.UPDATE_USER_SETTINGS)
                        .setUserName(AppController.getInstance().getUserEmail())
                        .addApiParam(APIConstants.User.ID, AppController.getInstance().getUserId())
                        .addApiParam(APIConstants.User.Settings.KEY, preference.getKey())
                        .addApiParam(APIConstants.User.Settings.VALUE, preference.getValue())
                        .build();
                api.execute();
            } catch (Exception e) {
                Log.i(LOG_TAG, e.getMessage(), e);
            }
            return null;
        }
    }

    private static class PreferenceObj {

        private String key;

        private String value;

        PreferenceObj(String key, String value) {
            this.key = key;
            this.value = value;
        }

        String getKey() {
            return key;
        }

        String getValue() {
            return value;
        }
    }

    public static interface SettingsChangeListener {

        public void onChange(String preferenceKey, Object data);
    }
}