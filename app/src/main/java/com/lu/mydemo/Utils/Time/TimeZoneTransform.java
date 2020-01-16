package com.lu.mydemo.Utils.Time;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * 创建时间: 2020/01/08 13:30 <br>
 * 作者: luyajun002 <br>
 * 描述:
 */
public class TimeZoneTransform {
    private static String dateTransformBetweenTimeZone(Date sourceDate, DateFormat formatter,
                                                       TimeZone sourceTimeZone, TimeZone targetTimeZone) {
        Long targetTime = sourceDate.getTime() - sourceTimeZone.getRawOffset() + targetTimeZone.getRawOffset();
        return getTime(new Date(targetTime), formatter);
    }

    private static String getTime(Date date, DateFormat formatter) {
        return formatter.format(date);
    }

    private static String getTimeZone() {
        Calendar cal = Calendar.getInstance();
        // getOffset will access to offset and contains DaylightTime
        int timeZone = cal.getTimeZone().getOffset(System.currentTimeMillis()) / (3600000);
        if (timeZone >= 0) {
            return "+" + timeZone;
        }
        return String.valueOf(timeZone);
    }

    public static String getGMTTime(Date date, SimpleDateFormat formatter) {
        TimeZone srcTimeZone = TimeZone.getTimeZone("GMT" + getTimeZone());
        TimeZone destTimeZone = TimeZone.getTimeZone("GMT+8");

        return dateTransformBetweenTimeZone(date, formatter, srcTimeZone, destTimeZone);
    }

    public static String gmt2Gmt_8Str(Date date, SimpleDateFormat formatter) {
        TimeZone srcTimeZone = TimeZone.getTimeZone("GMT");
        TimeZone destTimeZone = TimeZone.getTimeZone("GMT+8");
        return dateTransformBetweenTimeZone(date, formatter, srcTimeZone, destTimeZone);
    }

    public static void main(String[] args) {
        System.out.println(getGMTTime(new Date(System.currentTimeMillis()), new SimpleDateFormat()));
    }
}
