package com.wlb.agent.util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.TextUtils;

import com.android.util.log.LogUtil;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

    /**
     * 求出某年某月某日的这一天是星期几
     *
     * @param y
     * @param m
     * @param d
     * @return
     */
    public static String day(int y, int m, int d) {
        if (m == 1 || m == 2) {
            m += 12;
            y--;
        }
        int week = d + 2 * m + 3 * (m + 1) / 5 + y + y / 4 - y / 100 + y / 400
                + 1;
        week = week % 7;
        String w = "日一二三四五六".substring(week, week + 1);
        return w;
    }

    /**
     * 求星期组的下标
     *
     * @param y
     * @param m
     * @param d
     * @return
     */
    public static int day1(int y, int m, int d) {
        if (m == 1 || m == 2) {
            m += 12;
            y--;
        }
        int week = d + 2 * m + 3 * (m + 1) / 5 + y + y / 4 - y / 100 + y / 400
                + 1;
        week = week % 7;
        return week;
    }

    /**
     * 判断字符是否是数字
     *
     * @param str 要判断的字符
     */
    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    /**
     * 计算指定年份的某个月有多少天
     *
     * @param y
     * @param m
     * @return
     */
    public static int month(int y, int m) {
        System.out.println(m);
        int days = 0;
        String leap = year(y);
        if (m > 7) {
            if (m % 2 == 0) {
                days = 31;
            } else {
                days = 30;
            }
        } else {
            if (m % 2 == 0) {
                days = 30;
            } else {
                days = 31;
            }
            if (m == 2 && leap.equals("闰年")) {
                days = 29;
            } else if (m == 2 && leap.equals("平年")) {
                days = 28;
            }
        }
        return days;
    }

    /**
     * 判断该年是不是闰年
     *
     * @param y
     * @return
     */
    public static String year(int y) {
        String leap = (y % 400 == 0 || (y % 4 == 0 && y % 100 != 0)) ? "闰年"
                : "平年";
        return leap;
    }

    /**
     * 判断该年是不是闰年
     *
     * @param y
     * @return
     */
    public static boolean isLeapYear(int y) {
        boolean leap = (y % 400 == 0 || y % 4 == 0 && y % 100 != 0) ? true
                : false;
        return leap;
    }

    /**
     * 根据时间格式转换为字符串
     *
     * @param pattern
     * @param date
     * @return
     */
    public static String getFormatTime(String pattern, Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
        return sdf.format(date == null ? new Date() : date);
    }

    /**
     * 根据时间格式转换为字符串
     *
     * @param pattern
     * @param date
     * @return
     */
    public static String getFormatTime(String pattern, long date) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
        Date date2 = new Date();
        date2.setTime(date);
        return sdf.format(date2);
    }

    /**
     * 根据格式转换
     *
     * @param pattern
     * @param date
     * @return
     */
    public static String getFormatTime(String pattern, String date) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
        try {
            return sdf.format(sdf.parse(date).getTime());
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 根据时间格式转换为时间戳
     *
     * @param pattern
     * @param time
     * @return
     */
    public static long getFormatTimeToLong(String pattern, String time) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
        try {
            long newTime = sdf.parse(
                    "".equals(time) ? System.currentTimeMillis() + "" : time)
                    .getTime();
            return newTime;
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 根据时间格式转换为时间戳
     *
     * @param pattern
     * @param time
     * @return
     */
    public static long checkFormatTimeToLong(String pattern, String time) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
        long newTime = sdf.parse(
                "".equals(time) ? System.currentTimeMillis() + "" : time)
                .getTime();
        return newTime;
    }

    /**
     * 根据时间格式转换为字符串
     *
     * @param pattern
     * @param date
     * @return
     */
    public static String getFormatTimeNoZone(String pattern, Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
        return sdf.format(date == null ? new Date() : date);
    }

    /**
     * 设置保留2个小数位，四舍五入
     */
    public static String fomatScale(float num) {
        String[] nums = (num + "").split("\\.");
        if (nums.length > 1) {
            if (nums[1].length() == 1) {
                return num + "0";
            }
        }
        DecimalFormat fnum = new DecimalFormat("##0.00");
        String result = fnum.format(num);
        return result;
    }

    /**
     * 设置保留1个小数位，四舍五入
     */
    public static String fomatScale1(float num) {
        String[] nums = (num + "").split("\\.");
        if (nums.length > 1) {
            if (nums[1].length() == 1) {
                return num + "0";
            }
        }
        DecimalFormat fnum = new DecimalFormat("##0.0");
        String result = fnum.format(num);
        return result;
    }

    /**
     * 设置保留2个小数位，四舍五入
     */
    public static float fomatFloat(float num) {
        LogUtil.e("准备转换", num + "");
        BigDecimal b = new BigDecimal(num);
        float f1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
        LogUtil.e("转换结果", f1 + "");
        return f1;
    }

    /**
     * 设置保留2个小数位，四舍五入
     */
    public static double fomatDouble(double num) {
        LogUtil.e("准备转换", num + "");
        BigDecimal b = new BigDecimal(num);
        double f1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        LogUtil.e("转换结果", f1 + "");
        return f1;
    }

    /**
     * 获取资源文件的id
     */
    public static int getRes(Context context, String name, String type) {
        return context.getResources().getIdentifier(name, type,
                context.getPackageName());
    }

    /**
     * 根据时间格式转换为时间戳
     *
     * @param pattern
     * @param time
     * @return
     */
    public static Date getFormatTimeString(String pattern, String time) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        try {
            sdf.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
            return sdf.parse("".equals(time) ? System.currentTimeMillis() + ""
                    : time);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return new Date();
    }

    public static String[] mBigNums = new String[]{"一", "二", "三", "四", "五",
            "六", "七", "八", "九", "十", "十一", "十二"};

    /**
     * 获取中文月份
     *
     * @param month
     */
    public static String getBigMonth(int month) {
        if (mBigNums.length > month - 1) {
            return mBigNums[month - 1];
        }
        return "";
    }

    public static String fixUnreadTime(long timestamp) {
        try {
            Calendar now = Calendar.getInstance();
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(timestamp);
            long gapSeconds = (System.currentTimeMillis() - timestamp) / 1000;
            int gapMinutes = (int) (gapSeconds / 60);
            int gapHours = gapMinutes / 60;
            int gapDays = gapHours / 24;
            if (gapSeconds < 60) {//小于一分钟
                return "刚刚";
            } else if (gapSeconds < 60 * 60) {//小于一小时
                return (gapSeconds / 60)
                        + "分钟前";
            } else {
                if (now.get(Calendar.YEAR) == c.get(Calendar.YEAR)) {//同年
                    if (now.get(Calendar.MONTH) == c.get(Calendar.MONTH)) {//同月
                        if (now.get(Calendar.DATE) == c.get(Calendar.DATE)) {//同日
                            if (now.get(Calendar.MINUTE) > c.get(Calendar.MINUTE)) {
                                return now.get(Calendar.HOUR_OF_DAY)
                                        - c.get(Calendar.HOUR_OF_DAY) + "小时前";
                            } else {
                                return now.get(Calendar.HOUR_OF_DAY)
                                        - c.get(Calendar.HOUR_OF_DAY) - 1 + "小时前";
                            }

                        } else {//不同日
                            if (c.get(Calendar.DATE) == now.get(Calendar.DATE) - 1) {//昨天
                                SimpleDateFormat sdf = new SimpleDateFormat("昨天 HH:mm");
                                return sdf.format(c.getTime());
                            } else {//几天前
                                return new SimpleDateFormat("yyyy-MM-dd HH:mm")
                                        .format(c.getTime());
                            }
                        }
                    } else {//不同月

                        return new SimpleDateFormat("yyyy-MM-dd HH:mm")
                                .format(c.getTime());
                    }
                } else {//不同年
                    return new SimpleDateFormat("yyyy-MM-dd HH:mm")
                            .format(c.getTime());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }

    /**
     * 处理时间(毫秒格式 转换为n天前)
     *
     * @param timestamp
     * @return
     */
    public static String fixTime(long timestamp) {
        try {
            if (System.currentTimeMillis() - timestamp < 1 * 60 * 1000) {//小于一分钟
                return "刚刚";
            } else if (System.currentTimeMillis() - timestamp < 60 * 60 * 1000) {//小于一小时
                return ((System.currentTimeMillis() - timestamp) / 1000 / 60)
                        + "分钟前";
            } else {
                Calendar now = Calendar.getInstance();
                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(timestamp);
                if (c.get(Calendar.YEAR) == now.get(Calendar.YEAR)
                        && c.get(Calendar.MONTH) == now.get(Calendar.MONTH)
                        && c.get(Calendar.DATE) == now.get(Calendar.DATE)) {//
                    return now.get(Calendar.HOUR_OF_DAY)
                            - c.get(Calendar.HOUR_OF_DAY) + "小时前";
                }
                if (c.get(Calendar.YEAR) == now.get(Calendar.YEAR)
                        && c.get(Calendar.MONTH) == now.get(Calendar.MONTH)
                        && c.get(Calendar.DATE) == now.get(Calendar.DATE) - 1) {
                    SimpleDateFormat sdf = new SimpleDateFormat("昨天 HH:mm");
                    return sdf.format(c.getTime());
                } else if (c.get(Calendar.YEAR) == now.get(Calendar.YEAR)
                        && c.get(Calendar.MONTH) == now.get(Calendar.MONTH)) {
                    return now.get(Calendar.DAY_OF_MONTH)
                            - c.get(Calendar.DAY_OF_MONTH) + "天前";
                } else if (c.get(Calendar.YEAR) == now.get(Calendar.YEAR)) {
                    return now.get(Calendar.MONTH) - c.get(Calendar.MONTH)
                            + "月前";
                } else {
                    return new SimpleDateFormat("yyyy年M月d日")
                            .format(c.getTime());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getStarMobile(String mobile) {
        if (!TextUtils.isEmpty(mobile)) {
            if (mobile.length() >= 11)
                return mobile.substring(0, 3) + "****" + mobile.substring(7, mobile.length());
        } else {
            return "";
        }
        return mobile;
    }

    public static String getStarText(String text, int start, int end) {
        if (!TextUtils.isEmpty(text)) {
            if (text.length() > end)
                return text.substring(0, start) + "****" + text.substring(end, text.length());
        }
        return text;
    }

    public static String getSplitString(String text, String split) {
        String[] districts = text.split(split);
        int length;
        if (districts.length <= 1) {
            districts = text.split(" ");
        }
        length = districts.length;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(districts[i]);
        }
        return sb.toString();
    }

    public static List<String> getSplitStringList(String text, String split) {
        List<String> splits = new ArrayList<String>();
        String[] districts = text.split(split);
        int length;
        if (districts.length <= 1) {
            districts = text.split(" ");
        }
        length = districts.length;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            splits.add(districts[i]);
        }
        return splits;
    }

    public static void copy(Context context, String text) {
        ClipData clip = ClipData.newPlainText("simple text", text);
        try {
            ((ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE)).setPrimaryClip(clip);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String subZeroAndDot(double num) {
        java.text.DecimalFormat myformat=new java.text.DecimalFormat("0.00");
        String str = myformat.format(num);
        if (str.indexOf(".") > 0) {
            str = str.replaceAll("0+?$", "");//去掉多余的0
            str = str.replaceAll("[.]$", "");//如最后一位是.则去掉
        }
        return str;
    }


}
