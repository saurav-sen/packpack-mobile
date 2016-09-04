package com.pack.pack.application.activity;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.support.v7.app.ActionBar;

import com.pack.pack.application.R;

import java.util.List;

/**
 * Created by Saurav on 03-09-2016.
 */
public class SettingsActivity extends AppCompatPreferenceActivity {

    private static Preference.OnPreferenceChangeListener onPreferenceChangeListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object o) {
            String value = o.toString();
            preference.setSummary(value);
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
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        super.onBuildHeaders(target);
        Header accountsHeader = new Header();
        accountsHeader.fragment = "com.pack.pack.application.activity.SettingsActivity$AccountsFragment";
        accountsHeader.iconRes = getResources().getIdentifier("accounts_settings_icon", "drawable", this.getPackageName());
        accountsHeader.title = "Accounts";
        target.add(accountsHeader);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class AccountsFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_accounts);

            bindPreferenceSummaryToValue(findPreference("display_name"));
            bindPreferenceSummaryToValue(findPreference("user_address"));
            bindPreferenceSummaryToValue(findPreference("profilePicPref"));
        }
    }
}