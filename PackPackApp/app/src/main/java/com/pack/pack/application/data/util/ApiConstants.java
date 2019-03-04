package com.pack.pack.application.data.util;

/**
 * Created by Saurav on 23-07-2016.
 */
public interface ApiConstants {

    public static final String LOGO_BASE_URL = "http://api.squill.in/logo/"; //DEV Environment
    public static final String BASE_URL = "http://api.squill.in/api/"; //DEV Environment
    public static final String ML_BASE_URL = "http://api.squill.in/mlapi/"; //DEV Environment

    //public static final String BASE_URL = "http://api.squill.co.in/api/"; //PRODUCTION Environment

    public static final boolean IS_PRODUCTION_ENV = false;//true;
    //public static final boolean IS_PRODUCTION_ENV = true;//false;

    //public static final boolean IS_AWS_S3_LINK_FOR_IMAGE = true;//false; //PRODUCTION Environment
    //public static final boolean IS_AWS_S3_LINK_FOR_IMAGE = false;//true; //DEV Environment

    public static final String APP_NAME = "Squill";

    public static final String LIFESTYLE = "lifestyle";
    public static final String ART = "art";
    public static final String PHOTOGRAPHY = "photography";
    public static final String MUSIC = "music";
    public static final String EDUCATION = "education";
    public static final String FUN = "fun";
    public static final String SPIRITUAL = "spiritual";
    public static final String OTHERS = "others";

    public static final String FAMILY = "family";
    public static final String SOCIETY = "society";

    public static final int MIN_VISION_DESC_FIELD_LENGTH = 50;
    public static final int MAX_VISION_DESC_FIELD_LENGTH = 1000;

    public static final int MIN_GALLERY_DESC_FIELD_LENGTH = 10;
    public static final int MAX_GALLERY_DESC_FIELD_LENGTH = 600;

    public static final int MIN_ATTACHMENT_DESC_FIELD_LENGTH = 5;
    public static final int MAX_ATTACHMENT_DESC_FIELD_LENGTH = 600;

    public static final String YOUTUBE_API_KEY = "AIzaSyCEBFeUbTeBMtL4mNfpgANER7Da0Z_Gq5Q";

    public static final int UPLOAD_SIZE_LIMIT_IN_MB = 20;

    public static final String AMQP_URI = "amqp://bbjoaswu:k8hcZHx9zv60vwKu3rLXjzKjH63lyc1s@sidewinder.rmq.cloudamqp.com/bbjoaswu";

    /*public static final String[] SUPPORTED_CATEGORIES = new String[] {
            LIFESTYLE, ART, PHOTOGRAPHY, MUSIC, EDUCATION, FUN,
            SPIRITUAL};*/
}
