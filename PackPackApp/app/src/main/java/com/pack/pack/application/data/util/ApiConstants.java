package com.pack.pack.application.data.util;

/**
 * Created by Saurav on 23-07-2016.
 */
public interface ApiConstants {

    //public static final String BASE_URL = "http://192.168.35.12:8080/packpack/"; //DEV Environment
    //public static final String PUBLIC_ENDPOINT_BASE_URL = "http://192.168.35.12:8080/RSS/";//DEV Environment
    public static final String BASE_URL = "http://api.squill.co.in/packpack/"; //PRODUCTION Environment
    public static final String PUBLIC_ENDPOINT_BASE_URL = "http://api.squill.co.in/RSS/";//PRODUCTION Environment

    public static final boolean IS_PRODUCTION_ENV = true;

    public static final String APP_NAME = "Squill";

    public static final String LIFESTYLE = "lifestyle";
    public static final String ART = "art";
    public static final String PHOTOGRAPHY = "photography";
    public static final String MUSIC = "music";
    public static final String EDUCATION = "education";
    public static final String FUN = "fun";
    public static final String SPIRITUAL = "spiritual";
    public static final String OTHERS = "others";

    public static final int MIN_DESC_FIELD_LENGTH = 5;

    public static final String YOUTUBE_API_KEY = "AIzaSyBSnFkEf4FvMUq0jPIewO-t3J2EyLBAplA";

    public static final int UPLOAD_SIZE_LIMIT_IN_MB = 20;

    /*public static final String[] SUPPORTED_CATEGORIES = new String[] {
            LIFESTYLE, ART, PHOTOGRAPHY, MUSIC, EDUCATION, FUN,
            SPIRITUAL};*/
}
