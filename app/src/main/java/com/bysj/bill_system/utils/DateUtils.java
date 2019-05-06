package com.bysj.bill_system.utils;

import java.util.Calendar;

public class DateUtils {
    public static boolean isSameDay(long time1, long time2) {
        Calendar current = Calendar.getInstance();
        Calendar compare = Calendar.getInstance();
        current.setTimeInMillis(time1);
        compare.setTimeInMillis(time2);
        return current.get(Calendar.ERA) == compare.get(Calendar.ERA)
                && current.get(Calendar.YEAR) == compare.get(Calendar.YEAR)
                && current.get(Calendar.DAY_OF_YEAR) == compare.get(Calendar.DAY_OF_YEAR);
    }

    public static String formatDate(long time) {
        Calendar current = Calendar.getInstance();
        Calendar compare = Calendar.getInstance();
        current.setTimeInMillis(System.currentTimeMillis());
        compare.setTimeInMillis(time);
        if (current.get(Calendar.ERA) == compare.get(Calendar.ERA)
                && current.get(Calendar.YEAR) == compare.get(Calendar.YEAR))
            return fmt(compare.get(Calendar.MONTH) + 1) + "月" + fmt(compare.get(Calendar.DAY_OF_MONTH)) + "日";
        else
            return compare.get(Calendar.YEAR) + "年" + fmt(compare.get(Calendar.MONTH) + 1) + "月" + fmt(compare.get(Calendar.DAY_OF_MONTH)) + "日";
    }

    public static String fmt(int numb) {
        return numb > 9 ? "" + numb : "0" + numb;
    }

    public static String format(long time){
        Calendar current = Calendar.getInstance();
        current.setTimeInMillis(time);
        return current.get(Calendar.YEAR) + "年" + fmt(current.get(Calendar.MONTH) + 1) + "月" + fmt(current.get(Calendar.DAY_OF_MONTH)) + "日";
    }
}
