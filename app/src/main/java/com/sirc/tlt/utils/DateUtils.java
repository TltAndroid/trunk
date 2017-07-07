package com.sirc.tlt.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/*
 * @author  hxc
 * 描述：解析时间为秒级，所以需要乘以1000转换为毫秒级
 */
public class DateUtils {

    private static SimpleDateFormat sf = null;

    /* 获取系统时间 格式为："yyyy/MM/dd " */
    public static String getCurrentDate() {
        Date d = new Date();
        sf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return sf.format(d);
    }

    /* 时间戳转换成字符窜 */
    public static String getDateToString(long time) {
        Date d = new Date(time * 1000);
        sf = new SimpleDateFormat("yyyy-MM-dd");
        return sf.format(d);
    }

    /* 时间戳转换成字符窜 */
    public static String getDateToWekl(long time) {
        String week = "";
        Date d = new Date(time * 1000);
        week = getWeekOfDate(d);
        return week;
    }

    public static String getDateToDay(long time){
        Date d = new Date(time * 1000);
        sf = new SimpleDateFormat("MM-dd");
        return sf.format(d);
    }

    /* 时间戳转换成字符窜 */
    public static String getDateToDetialTime(long time) {
        Date d = new Date(time * 1000);
        sf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        return sf.format(d);
    }

    /**
     * 根据日期获得星期
     * @param date
     * @return
     */
    public static String getWeekOfDate(Date date) {

        String[] weekDaysName = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
        String[] weekDaysCode = { "0", "1", "2", "3", "4", "5", "6" };
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int intWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        return weekDaysName[intWeek];
    }

    /* 时间戳转换成时分 */
    public static String getStringHourMinutes(long time) {
        Date d = new Date(time);
        sf = new SimpleDateFormat("HH:mm");
        return sf.format(d);

    }

    /* 时间戳转换成年月日 */
    public static String getStringYear(long time) {
        Date d = new Date(time);
        sf = new SimpleDateFormat("yyyy-MM-dd");
        return sf.format(d);

    }

    /* 将字符串转为时间戳 */
    public static long getStringToDate(String time) {
        sf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        try {
            date = sf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }

    /* 将字符串转为时间戳 */
    public static long getStringToDateDetail(String time) {
        sf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = new Date();
        try {
            date = sf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }


    /**
     * 以友好的方式显示时间
     *
     * @param sdate
     * @return
     */
    public static String friendlyTime(String sdate) {
        Date date = new Date();
        try {
            sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            date = sf.parse(sdate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String ftime = "";
        Calendar cal = Calendar.getInstance();
        // 判断是否是相同
        String curDate = getDateToString(cal.getTimeInMillis());
        String paramDate = getDateToString(date.getTime());
        if (curDate.equals(paramDate)) {
            int hour = (int) ((cal.getTimeInMillis() - date.getTime()) / 3600000);
            if (hour == 0) {
                ftime = Math.max(
                        (cal.getTimeInMillis() - date.getTime()) / 60000, 1)
                        + "分钟前";
            } else {
                ftime = hour + "小时前";
            }
            return ftime;
        }

        long lt = date.getTime() / 86400000;
        long ct = cal.getTimeInMillis() / 86400000;
        int days = (int) (ct - lt);
        if (days == 0) {
            int hour = (int) ((cal.getTimeInMillis() - date.getTime()) / 3600000);
            if (hour == 0)
                ftime = Math.max(
                        (cal.getTimeInMillis() - date.getTime()) / 60000, 1)
                        + "分钟前";
            else
                ftime = hour + "小时前";
        } else if (days == 1) {
            String timeStr = date.getHours() + ":" + date.getMinutes();
            ftime = "昨天 " + timeStr;
        } else if (days == 2) {
            String timeStr = date.getHours() + ":" + date.getMinutes();
            ftime = "前天 " + timeStr;
        } else if (days > 2 && days <= 10) {
            String timeStr = date.getHours() + ":" + date.getMinutes();
            ftime = days + "天前 " + timeStr;
        } else if (days > 10) {
            ftime = sdate;
        }
        return ftime;
    }

    /**
     * Java是毫秒级别的时间
     *
     * @param time 兼容IOS，乘以1000 = 秒级时间
     * @return
     */
    public static String getHourMinutes(String time) {
        Date d = new Date(Integer.valueOf(time) * 1000);
        SimpleDateFormat sf = new SimpleDateFormat("HH:mm");
        sf.setTimeZone(TimeZone.getTimeZone("GMT+0:00"));
        return sf.format(d);

    }

}
