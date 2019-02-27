package com.huatu.teacheronline.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by kinndann on 2017/7/12.
 * description:
 */

public class DateTimeUtil {
    public static final DateFormat yMd_Hms_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final DateFormat yMd_format = new SimpleDateFormat("yyyy年MM月dd日");


    public static String getNowTime(DateFormat format) {

        return format.format(new Date());

    }

    public static String getTime(long time, DateFormat format) {

        return format.format(new Date(time));

    }


    public static long parseTime(String str, DateFormat format) {
        Date date = null;
        try {
            date = format.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date == null ? 0 : date.getTime();
    }


}
