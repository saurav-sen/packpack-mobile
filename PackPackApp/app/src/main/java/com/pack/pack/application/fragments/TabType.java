package com.pack.pack.application.fragments;

/**
 * Created by Saurav on 08-04-2016.
 */
public enum TabType {
    HOME("home", "Home"),
    LIFE_STYLE("lifestyle", "Lifestyle"),
    FASHION("fashion", "Fashion");

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
