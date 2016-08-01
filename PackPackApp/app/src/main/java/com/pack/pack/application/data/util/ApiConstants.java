package com.pack.pack.application.data.util;

/**
 * Created by Saurav on 23-07-2016.
 */
public interface ApiConstants {
    //public static final String BASE_URL = "http://54.169.84.61:9999/packpack/";
    public static final String BASE_URL = "http://192.168.35.12:8080/packpack/";

    public static final String LIFESTYLE = "lifestyle";
    public static final String ART = "art";
    public static final String MUSIC = "music";
    public static final String EDUCATION = "education";
    public static final String FUN = "fun";
    public static final String SPIRITUAL = "spiritual";
    public static final String OTHERS = "others";

    public static final String[] SUPPORTED_CATEGORIES = new String[] {
            LIFESTYLE, ART, MUSIC, EDUCATION, FUN,
            SPIRITUAL, OTHERS};
}
