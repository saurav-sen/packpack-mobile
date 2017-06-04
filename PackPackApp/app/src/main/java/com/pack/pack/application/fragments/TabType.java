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
    HOME("home", "Home", R.drawable.home, HomeViewFragment.class, true, new String[] {"home"}),
    ART(ApiConstants.ART, "Art", R.drawable.art, ArtViewFragment.class, true, new String[]{ApiConstants.ART, ApiConstants.MUSIC}),
    PHOTOGRAPHY(ApiConstants.PHOTOGRAPHY, "Photo", R.drawable.photography, PhotographyViewFragment.class, true, new String[]{ApiConstants.PHOTOGRAPHY, ApiConstants.LIFESTYLE}),
    EDUCATION(ApiConstants.EDUCATION, "Writer", R.drawable.education, EducationViewFragment.class, true, new String[]{ApiConstants.EDUCATION, ApiConstants.SPIRITUAL, ApiConstants.FUN}),
    OTHERS(ApiConstants.OTHERS, "Misc", R.drawable.others, MiscViewFragment.class, true, new String[]{ApiConstants.OTHERS, ApiConstants.LIFESTYLE}),

    //Tabs which are disabled
    LIFE_STYLE(ApiConstants.LIFESTYLE, "Lifestyle", R.drawable.lifestyle, LifestyleViewFragment.class, false, new String[]{ApiConstants.LIFESTYLE}),
    MUSIC(ApiConstants.MUSIC, "Music", R.drawable.music, MusicViewFragment.class, false, new String[]{ApiConstants.MUSIC}),
    ENTERTAINMENT_FUN(ApiConstants.FUN, "Fun", R.drawable.fun, FunViewFragment.class, false, new String[]{ApiConstants.FUN}),
    SPIRITUAL(ApiConstants.SPIRITUAL, "Spiritual", R.drawable.spiritual, SpiritualViewFragment.class, false, new String[]{ApiConstants.SPIRITUAL});

    private String type;

    private String displayName;

    private int icon;

    private Class<?> fragmentClass;

    private boolean recreate = true;

    private boolean isEnabled;

    private String[] similarCategories;

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

    TabType(String type, String displayName, int icon, Class<?> fragmentClass, boolean isEnabled, String[] similarCategories) {
        this.displayName = displayName;
        this.type = type;
        this.icon = icon;
        this.fragmentClass = fragmentClass;
        this.isEnabled = isEnabled;
        this.similarCategories = similarCategories;
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
