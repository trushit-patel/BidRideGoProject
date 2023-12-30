package com.bidridego.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateTimeUtils {

    public static Date getDateFromTimeStamp(String timeStamp) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.getDefault());
        Date filterDate = dateFormat.parse(timeStamp);
        return filterDate;
    }

    public static String getTimeStampFromDate(Date date) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String timeStamp = dateFormat.format(date);
        return timeStamp;
    }

    public static String formatDate(Date date, String outputFormat) {
        if (date != null) {
            SimpleDateFormat sdf = new SimpleDateFormat(outputFormat, Locale.getDefault());
            return sdf.format(date);
        } else {
            return null;
        }
    }
}
