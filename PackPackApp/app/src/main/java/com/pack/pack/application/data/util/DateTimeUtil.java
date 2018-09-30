package com.pack.pack.application.data.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by Saurav on 25-09-2016.
 */
public class DateTimeUtil {

    /*public static String sentencify(long t1, long t2) {
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
    }*/

    public static String today() {
        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH) + 1;
        int year = c.get(Calendar.YEAR);
        String dd = day < 10 ? "0" + String.valueOf(day) : String.valueOf(day);
        String mm = month < 10 ? "0" + String.valueOf(month) : String.valueOf(month);
        String yyyy = String.valueOf(year);
        return dd + "-" + mm + "-" + yyyy;
    }

    public static long fromDateText(String dateText) throws Exception {
        String[] splits = dateText.trim().split("_");
        int dd = Integer.parseInt(splits[0].trim());
        int mm = Integer.parseInt(splits[1].trim());
        int yyyy = Integer.parseInt(splits[2].trim());
        return fromDate(dd, mm, yyyy);
    }

    public static long fromDate(int dd, int mm, int yyyy) throws Exception {
        String dtString = dd + "/" + mm + "/" + yyyy;
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date date = formatter.parse(dtString);
        return date.getTime();
    }

    public static int calculateTimeDifference(long t0, long t1, TimeUnit unit) {
        long diffInMilliseconds = Math.abs(t1 - t0);
        long diffInSeconds = (diffInMilliseconds / 1000);
        int diff = 0;
        switch (unit) {
            case MICROSECONDS:
            case MILLISECONDS:
                throw new UnsupportedOperationException(TimeUnit.MICROSECONDS + " or " + TimeUnit.MILLISECONDS + " are NOT supported.");
            case SECONDS:
                diff = (int)diffInSeconds;
                break;
            case MINUTES:
                diff = (int)(diffInSeconds / 60);
                break;
            case HOURS:
                diff = (int)((diffInSeconds / 60) / 60);
                break;
            case DAYS:
                diff = (int)(((diffInSeconds / 60) / 60) / 24);
                break;
        }
        return diff;
    }
}
