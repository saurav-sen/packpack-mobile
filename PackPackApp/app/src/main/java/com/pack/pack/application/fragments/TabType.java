package com.pack.pack.application.fragments;

import com.pack.pack.application.data.util.ApiConstants;

/**
 * Created by Saurav on 08-04-2016.
 */
public enum TabType {
    HOME("home", "Home"),
    LIFE_STYLE(ApiConstants.LIFESTYLE, "Lifestyle"),
    ART(ApiConstants.ART, "Art"),
    MUSIC(ApiConstants.MUSIC, "Music"),
    EDUCATION(ApiConstants.EDUCATION, "Education"),
    ENTERTAINMENT_FUN(ApiConstants.FUN, "Fun"),
    SPIRITUAL(ApiConstants.SPIRITUAL, "Spiritual"),
    OTHERS(ApiConstants.OTHERS, "Others");

    private String type;

    private String displayName;

    TabType(String type, String displayName) {
        this.displayName = displayName;
        this.type = type;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getType() {
        return type;
    }
}
