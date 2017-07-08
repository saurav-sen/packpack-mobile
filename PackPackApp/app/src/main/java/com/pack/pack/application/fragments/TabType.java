package com.pack.pack.application.fragments;

import android.support.v4.app.Fragment;

import com.pack.pack.application.R;
import com.pack.pack.application.data.util.ApiConstants;

import java.util.List;

/**
 * Created by Saurav on 08-04-2016.
 */
public enum TabType {
    //Tabs which are enabled
    SQUILL("squill", "SQUILL TEAM", R.drawable.home, SquillTeamViewFragment.class, true, new String[] {"squill"}, true),
    NEWS("news", "NEWS", R.drawable.news, NewsViewFragment.class, true, new String[] {"news"}, true),
    CUSTOM("custom", "USER", R.drawable.forum, UserBroadcastMessageViewFragment.class, false, new String[] {"custom"}, true),

    //HOME("home", "Home", R.drawable.home, HomeViewFragment.class, true, new String[] {"home"}, false),
    ART(ApiConstants.ART, "Artist", R.drawable.art, ArtViewFragment.class, true, new String[]{ApiConstants.ART, ApiConstants.MUSIC}, false),
    PHOTOGRAPHY(ApiConstants.PHOTOGRAPHY, "Photography", R.drawable.photography, PhotographyViewFragment.class, true, new String[]{ApiConstants.PHOTOGRAPHY, ApiConstants.LIFESTYLE}, false),
    EDUCATION(ApiConstants.EDUCATION, "Writer", R.drawable.education, EducationViewFragment.class, true, new String[]{ApiConstants.EDUCATION, ApiConstants.SPIRITUAL, ApiConstants.FUN}, false),
    OTHERS(ApiConstants.OTHERS, "Misc", R.drawable.others, MiscViewFragment.class, true, new String[]{ApiConstants.OTHERS, ApiConstants.LIFESTYLE}, false);

    //Tabs which are disabled
    /*LIFE_STYLE(ApiConstants.LIFESTYLE, "Lifestyle", R.drawable.lifestyle, LifestyleViewFragment.class, false, new String[]{ApiConstants.LIFESTYLE}, false),
    MUSIC(ApiConstants.MUSIC, "Music", R.drawable.music, MusicViewFragment.class, false, new String[]{ApiConstants.MUSIC}, false),
    ENTERTAINMENT_FUN(ApiConstants.FUN, "Fun", R.drawable.fun, FunViewFragment.class, false, new String[]{ApiConstants.FUN}, false),
    SPIRITUAL(ApiConstants.SPIRITUAL, "Spiritual", R.drawable.spiritual, SpiritualViewFragment.class, false, new String[]{ApiConstants.SPIRITUAL}, false);*/

    private String type;

    private String displayName;

    private int icon;

    private Class<?> fragmentClass;

    private boolean recreate = true;

    private boolean isEnabled;

    private String[] similarCategories;

    public boolean isBroadcastTab() {
        return isBroadcastTab;
    }

    private boolean isBroadcastTab;

    public void setRecreate(boolean recreate) {
        this.recreate = recreate;
    }

    public Fragment getFragment() {
        try {
            if(fragmentClass != null) {
                if(fragment == null) {
                    fragment = (Fragment) fragmentClass.newInstance();
                    recreate = false;
                } else if(recreate) {
                    fragment = (Fragment) fragmentClass.newInstance();
                    recreate = false;
                }
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        if(fragment != null && (fragment instanceof TopicViewFragment)) {
            ((TopicViewFragment)fragment).setTabType(this);
        }
        return fragment;
    }

    private Fragment fragment;

    TabType(String type, String displayName, int icon, Class<?> fragmentClass, boolean isEnabled, String[] similarCategories, boolean isBroadcastTab) {
        this.displayName = displayName;
        this.type = type;
        this.icon = icon;
        this.fragmentClass = fragmentClass;
        this.isEnabled = isEnabled;
        this.similarCategories = similarCategories;
        this.isBroadcastTab = isBroadcastTab;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getType() {
        return type;
    }

    public int getIcon() {
        return icon;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public String[] getSimilarCategories() {
        return similarCategories;
    }
}
