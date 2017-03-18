package com.pack.pack.application.data.util;

/**
 * Created by Saurav on 25-09-2016.
 */
public class DateTimeUtil {

    public static String sentencify(long t1, long t2) {
        if(t1 > 0 && t2 > 0 && t2 >= t1) {
            long diff = t2 - t1;

            long diffSeconds = diff / 1000;
            if (diffSeconds < 60) {
                return "Just Now";
            } else {
                long diffMinutes = diffSeconds / 60;
                if (diffMinutes == 1) {
                    return diffMinutes + " minute ago";
                } else if (diffMinutes < 60) {
                    return diffMinutes + " minutes ago";
                } else {
                    long diffHours = diffMinutes / 60;
                    if (diffHours == 1) {
                        return diffHours + " hour ago";
                    } else if (diffHours < 24) {
                        return diffHours + " hours ago";
                    } else {
                        long diffDays = diffHours / 24;
                        if (diffDays == 1) {
                            return diffDays + " day ago";
                        } else {
                            return diffDays + " days ago";
                        }
                    }
                }
            }
        }
        return "";
    }
}
