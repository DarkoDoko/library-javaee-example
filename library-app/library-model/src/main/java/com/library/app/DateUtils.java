package com.library.app;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public final class DateUtils {
    private static final String FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    public static Date currentDatePlusDays(int days) {
        LocalDateTime localDateTime = LocalDateTime.now();
        return Date.from(localDateTime.plusDays(days).atZone(ZoneId.systemDefault()).toInstant());
    }
    
    private DateUtils() {    
    }
    
    public static Date getAsDateTime(String dateTime) {
        try{
            return new SimpleDateFormat(FORMAT).parse(dateTime);
        } catch(ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }
    
    public static String formatDateTime(Date date) {
        return new SimpleDateFormat(FORMAT).format(date);
    }
}
