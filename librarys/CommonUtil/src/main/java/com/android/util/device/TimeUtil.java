package com.android.util.device;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimeUtil {

    /**
     * 获取每个月最后一天
     */
    public static int getLastDayOfMonth(int year, int month) {
        if (month == 0) {
            month = 12;
        }
        if (month == 13) {
            month = 1;
        }
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);// 设置为下个月
        cal.set(Calendar.DATE, 1); // 设置为该月第一天
        cal.add(Calendar.DATE, -1); // 再减一天即为上个月(这个月)最后一天
        return cal.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 判断前后年份
     *
     * @param isPreMonth true判断上个月年份 false判断下个月年份
     */
    public static int getTrueYear(int year, int month, boolean isPreMonth) {
        if (isPreMonth) {
            if (month <= 1) {
                year--;
            }
        } else {
            if (month >= 12) {
                year++;
            }
        }
        return year;
    }

    /**
     * 判断前后月份
     *
     * @param isPreMonth true判断上个月 false判断下个月
     */
    public static int getTrueMonth(int month, boolean isPreMonth) {
        if (isPreMonth) {
            month--;
            if (month < 1) {
                month = 12;
            }
        } else {
            month++;
            if (month > 12) {
                month = 1;
            }
        }
        return month;
    }

    public static String getCurrentDate() {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
        int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        return year + "-" + (month < 10 ? "0" + month : month) + "-"
                + (day < 10 ? "0" + day : day);
    }

    public static String getYesterDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
        int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH) - 1;
        return year + "-" + (month < 10 ? "0" + month : month) + "-"
                + (day < 10 ? "0" + day : day);
    }

    public static String getCurrentTime() {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
        int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int minute = Calendar.getInstance().get(Calendar.MINUTE);
        int second = Calendar.getInstance().get(Calendar.SECOND);
        return year + "-" + (month < 10 ? "0" + month : month) + "-"
                + (day < 10 ? "0" + day : day) + " " + (hour < 10 ? "0" + hour : hour) + ":" + (minute < 10 ? "0" + minute : minute) + ":" + (second < 10 ? "0" + second : second);
    }

    public static int getCurrentYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    public static int getCurrentMonth() {
        return Calendar.getInstance().get(Calendar.MONTH) + 1;
    }

    public static int getCurrentDay() {
        return Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取两个日期之间的间隔天数
     *
     * @return
     */
    public static int getGapCount(Date startDate, Date endDate) {
        Calendar fromCalendar = Calendar.getInstance();
        fromCalendar.setTime(startDate);
        fromCalendar.set(Calendar.HOUR_OF_DAY, 0);
        fromCalendar.set(Calendar.MINUTE, 0);
        fromCalendar.set(Calendar.SECOND, 0);
        fromCalendar.set(Calendar.MILLISECOND, 0);

        Calendar toCalendar = Calendar.getInstance();
        toCalendar.setTime(endDate);
        toCalendar.set(Calendar.HOUR_OF_DAY, 0);
        toCalendar.set(Calendar.MINUTE, 0);
        toCalendar.set(Calendar.SECOND, 0);
        toCalendar.set(Calendar.MILLISECOND, 0);

        return (int) ((toCalendar.getTime().getTime() - fromCalendar.getTime()
                .getTime()) / (1000 * 60 * 60 * 24));
    }


    public static String timeFormat(long timeMillis, String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.CHINA);
        return format.format(new Date(timeMillis));
    }

    public static String formatPhotoDate(long time) {
        return timeFormat(time, "yyyy-MM-dd");
    }

    public static String formatPhotoDate(String path) {
        File file = new File(path);
        if (file.exists()) {
            long time = file.lastModified();
            return formatPhotoDate(time);
        }
        return "1970-01-01";
    }
}
