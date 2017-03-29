package com.pack.pack.application.fragments;

import android.support.v4.app.Fragment;

import com.pack.pack.application.R;
import com.pack.pack.application.data.util.ApiConstants;

/**
 * Created by Saurav on 08-04-2016.
 */
public enum TabType {
    HOME("home", "Home", R.drawable.home, HomeViewFragment.class),
    LIFE_STYLE(ApiConstants.LIFESTYLE, "Lifestyle", R.drawable.lifestyle, LifestyleViewFragment.class),
    ART(ApiConstants.ART, "Art", R.drawable.art, ArtViewFragment.class),
    PHOTOGRAPHY(ApiConstants.PHOTOGRAPHY, "Photography", R.drawable.photography, PhotographyViewFragment.class),
    MUSIC(ApiConstants.MUSIC, "Music", R.drawable.music, MusicViewFragment.class),
    EDUCATION(ApiConstants.EDUCATION, "Education", R.drawable.education, EducationViewFragment.class),
    ENTERTAINMENT_FUN(ApiConstants.FUN, "Fun", R.drawable.fun, FunViewFragment.class),
    SPIRITUAL(ApiConstants.SPIRITUAL, "Spiritual", R.drawable.spiritual, SpiritualViewFragment.class),
    OTHERS(ApiConstants.OTHERS, "Others", R.drawable.others, MiscViewFragment.class);

    private String type;

    private String displayName;

    private int icon;

    private Class<?> fragmentClass;

    private boolean recreate = true;

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

    TabType(String type, String displayName, int icon, Class<?> fragmentClass) {
        this.displayName = displayName;
        this.type = type;
        this.icon = icon;
        this.fragmentClass = fragmentClass;
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
}
