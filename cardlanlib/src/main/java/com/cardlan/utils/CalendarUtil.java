package com.cardlan.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * 日历工具类 Created by zhoushenghua on 18-6-21.
 *
 * @author zhoushenghua
 */

public class CalendarUtil {

    /**
     * 年(yyyy)
     */
    private static final String S_YYYY      = "yyyy";
    /**
     * 月(MM)
     */
    private static final String S_MM_MONTH  = "MM";
    /**
     * 日(dd)
     */
    private static final String S_DD        = "dd";
    /**
     * 时(HH)
     */
    private static final String S_HH        = "HH";
    /**
     * 分(mm)
     */
    private static final String S_MM_MINUTE = "mm";
    /**
     * 秒(ss)
     */
    private static final String S_SS        = "ss";

    /**
     * 获取当前时间的毫秒数
     *
     * @return long 毫秒数
     */
    public static long getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTimeInMillis();
    }

    /**
     * 获取当前时间的年份
     *
     * @return int 年份
     */
    public static int getCurrentTimeYear() {
        Calendar calendar = Calendar.getInstance();
        int      year     = calendar.get(Calendar.YEAR);
        return year;
    }

    /**
     * 获取当前时间的月份
     *
     * @return int 月份
     */
    public static int getCurrentTimeMonth() {
        Calendar calendar = Calendar.getInstance();
        int      month    = calendar.get(Calendar.MONTH) + 1;
        return month;
    }

    /**
     * 获取当前时间的当月所在日
     *
     * @return int 当月所在日
     */
    public static int getCurrentTimeDayOfMonth() {
        Calendar calendar   = Calendar.getInstance();
        int      dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        return dayOfMonth;
    }

    /**
     * 获取当前时间的年份
     *
     * @return int 年份
     */
    public static int getTimeYear(long timeMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeMillis);
        int year = calendar.get(Calendar.YEAR);
        return year;
    }

    /**
     * 获取当前时间的月份
     *
     * @return int 月份
     */
    public static int getTimeMonth(long timeMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeMillis);
        int month = calendar.get(Calendar.MONTH) + 1;
        return month;
    }

    /**
     * 获取当前时间的当月所在日
     *
     * @return int 当月所在日
     */
    public static int getTimeDayOfMonth(long timeMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeMillis);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        return dayOfMonth;
    }

    /**
     * 格式化日期
     *
     * @param format 　格式化格式
     * @param date   　日期，如果为null,那么就创建当前时间日期
     * @return String 字符串日期
     */
    public static String formatDate(String format, Date date) {
        if (ByteUtil.notNull(format)) {
            if (date == null) {
                date = new Date();
            }
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
            String           returnValue      = simpleDateFormat.format(date);
            return returnValue;
        }
        return null;
    }

    /**
     * 格式化日期字符串，返回日期时间
     *
     * @param format  　格式化格式
     * @param dateStr 　日期字符串
     * @return Date 日期时间
     */
    public static Date formatDateStr(String format, String dateStr) {
        if (ByteUtil.notNull(format)) {
            if (ByteUtil.notNull(dateStr)) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
                try {
                    Date returnValue = simpleDateFormat.parse(dateStr);
                    return returnValue;
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
        }
        return null;
    }

    /**
     * 通过毫秒数得到日期类型
     *
     * @param timeMillis 　毫秒数
     * @return Date 日期类型
     */
    public static Date getDate(long timeMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getDefault());
        calendar.setTimeInMillis(timeMillis);
        return calendar.getTime();
    }

    /**
     * 通过　calendar 得到日期类型
     *
     * @param calendar 日历
     * @return Date 日期类型
     */
    public static Date getDate(Calendar calendar) {
        calendar.setTimeZone(TimeZone.getDefault());
        return calendar.getTime();
    }

    /**
     * 通过日期类型得到日历
     *
     * @param date 　日期类型
     * @return Calendar 日历
     */
    public static Calendar getCalendar(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.setTimeZone(TimeZone.getDefault());
        return calendar;
    }

    /**
     * 格式化当前时间
     *
     * @param format 　格式化格式
     * @return String 字符类型日期
     */
    public static String formatCurrentDate(String format) {
        return formatDate(format, null);
    }

    /**
     * 获得 格式为　＇yyyyy-MM-dd  HH:mm＇　的日期
     *
     * @return String 字符类型日期
     */
    public static String getDefaultYYYY_MM_ddHH_mm() {
        return formatCurrentDate("yyyyy-MM-dd  HH:mm").substring(1);
    }

    /**
     * 获得 格式为　＇yyyyy-MM-dd HH:mm:ss＇　的日期
     *
     * @return String 字符类型日期
     */
    public static String getDefaultYYYY_MM_ddHH_mm_ss() {
        return formatCurrentDate("yyyyy-MM-dd HH:mm:ss").substring(1);
    }

    /**
     * 获得 格式为　＇yyyyyMMddHHmmss＇　的日期
     *
     * @return String 字符类型日期
     */
    public static String getDefaultYYYYMMddHHmmss() {
        return formatCurrentDate("yyyyyMMddHHmmss").substring(1);
    }

    /**
     * 获得当前时间　距离下一分钟所剩的秒数
     *
     * @return long　秒数
     */
    public static long getRemainSeconds() {
        long timeMillis = getCurrentTime();
        long over = (timeMillis / 1000) % 60;
        return 60 - over;
    }

    /***
     * 得到时间字符串．
     * @param yMdOperator 年月日拼接符
     * @param blankSpace　中间空格拼接符
     * @param hmsOperator　时分秒拼接符
     * @param time　1970 以后的时间戳
     * @return String 返回日期格式的字符串
     */
    public static String getTimeString(String yMdOperator, String blankSpace, String hmsOperator,
                                       long time) {
        return formatDate(getFormatString(yMdOperator, blankSpace, hmsOperator), getDate(time));
    }

    /***
     * 得到时间字符串．
     * @param yMdOperator 年月日拼接符
     * @param blankSpace　中间空格拼接符
     * @param hmsOperator　时分秒拼接符
     * @param time　日期时间
     * @return String 返回日期格式的字符串
     */
    public static String getTimeString(String yMdOperator, String blankSpace, String hmsOperator,
                                       Date time) {
        return formatDate(getFormatString(yMdOperator, blankSpace, hmsOperator), time);
    }

    /***
     * 得到当前时间字符串．
     * @param yMdOperator 年月日拼接符
     * @param blankSpace　中间空格拼接符
     * @param hmsOperator　时分秒拼接符
     * @return String 返回日期格式的字符串
     */
    public static String getCurrentTimeString(String yMdOperator, String blankSpace, String
            hmsOperator) {
        return formatDate(getFormatString(yMdOperator, blankSpace, hmsOperator), new Date());
    }

    public static Date addDay(Date date, int day) {
        long longTime = date.getTime();
        long addLong = day * (24l * 60l * 60l);
        date.setTime(longTime + addLong);
        return date;
    }

    public static Calendar addDay(Calendar calendar, int day) {
        long longTime = calendar.getTimeInMillis();
        long addLong = day * (24l * 60l * 60l);
        calendar.setTimeInMillis(longTime + addLong);
        return calendar;
    }

    public static Calendar addTimeMillis(Calendar calendar, long timeMillis) {
        long longTime = calendar.getTimeInMillis();
        calendar.setTimeInMillis(longTime + timeMillis);
        return calendar;
    }

    public static Calendar getFormatYYYYMMdd(String yyyymmdd) {
        Date date = formatDateStr(S_YYYY + S_MM_MONTH + S_DD, yyyymmdd);
        return getCalendar(date);
    }

    public static long getFormatYYYYMMddTime(String yyyymmdd) {
        Date date = formatDateStr(S_YYYY + S_MM_MONTH + S_DD, yyyymmdd);
        return date.getTime();
    }

    /***
     * 得到时间字符串．
     * @param yMdOperator 年月日拼接符
     * @param blankSpace　中间空格拼接符
     * @param hmsOperator　时分秒拼接符
     * @return String 返回日期格式化字符串
     */
    private static String getFormatString(String yMdOperator, String blankSpace, String
            hmsOperator) {
        StringBuilder formatSB = new StringBuilder();
        //添加年月日
        formatSB.append(S_YYYY).append(yMdOperator).append(S_MM_MONTH).append(yMdOperator).append
                (S_DD);
        //添加空格
        formatSB.append(blankSpace);
        //添加时分秒
        formatSB.append(S_HH).append(hmsOperator).append(S_MM_MINUTE).append(hmsOperator).append
                (S_SS);

        return formatSB.toString();
    }

    /**
     * 获取精确到秒的时间戳
     *
     * @param date
     * @return
     */
    public static long getSecondTimestampTwo(Date date) {
        if (null == date) {
            return 0;
        }
        String timestamp = String.valueOf(date.getTime());
        return Long.valueOf(timestamp);
    }

    /**
     * 把字符串转为日期
     *
     * @param strDate
     * @return
     */
    public static Date ConverToDate(String strDate) throws Exception {
        DateFormat df = new SimpleDateFormat("yyyyyMMddHHmmss");
        return df.parse(strDate);
    }

    /**
     * 日期转换成秒数
     */
    public static long getSecondsFromDate(String expireDate) {
        if (expireDate == null || expireDate.trim().equals(""))
            return 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyyMMddHHmmss");
        Date             date;
        try {
            date = sdf.parse(expireDate);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0L;
        }
    }

    public static Calendar minusCurrentMonth(int addMonth) {
        Calendar calendar     = Calendar.getInstance();
        int      currentMonth = calendar.get(2);
        calendar.set(2, currentMonth - addMonth);
        return calendar;
    }

    public static String formatCalendarYYYYMM(Calendar calendar, String separator) {
        String formatYYYYMM = null;
        if(ObjectNotNull.notNull(separator)) {
            formatYYYYMM = "yyyy" + separator + "MM";
        } else {
            formatYYYYMM = "yyyyMM";
        }

        return formatCalendar(formatYYYYMM, calendar);
    }

    public static String formatCalendar(String format, Calendar calendar) {
        if(ObjectNotNull.notNull(format)) {
            if(calendar == null) {
                calendar = Calendar.getInstance();
            }

            Date             date             = calendar.getTime();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
            String           returnValue      = simpleDateFormat.format(date);
            return returnValue;
        } else {
            return null;
        }
    }

    // 字符串转时间戳 2017-08-29 15:26:52 ==> 1503991612952
    public static long stringToTime(String s){
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmss");
        Date             date;
        try {
            date = sdf.parse(s);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return -1;
    }
    // 字符串转时间戳 2017-08-29 ==> 1503991612952
    public static long stringToDate(String s){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date             date;
        try {
            date = sdf.parse(s);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return -1;
    }
    // 时间戳转字符串  1503991612952 ==> 2017-08-29 15:26:52  时间戳 是long 类型
    public static String timeToString(long s){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date             date;
        try {
            date = sdf.parse(sdf.format(new Date(s)));
            //Date date = sdf.parse(sdf.format(new Long(s)));// 等价于
            return sdf.format(date);
        } catch (NumberFormatException e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();
        } catch (ParseException e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();
        }
        return null;
    }
}
