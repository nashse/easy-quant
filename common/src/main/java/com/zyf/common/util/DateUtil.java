package com.zyf.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * @author yuanfeng.z
 * @description 时间操作类
 * @date 2019/2/21 18:46
 */
public class DateUtil {
    /**
     * 天（毫秒）
     */
    public static Double MS_DAY = 86400000d;

    /**
     * 时间格式
     */
    public static String yyyyMMddHHmmss = "yyyy-MM-dd HH:mm:ss";

    /**
     * 时间格式
     */
    public static String yyyyMMddTHHmmssSSSZ = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    /**
     * 时间格式
     */
    public static String yyyyMMddHHmmssSSS = "yyyy-MM-dd HH:mm:ss:SSS";

    /**
     * 时间格式
     */
    public static String yyyyMMdd = "yyyyMMdd";

    /**
     * 时间间隔时间戳
     *
     * @param begin 开始时间
     * @param end   结束时间
     * @return
     */
    public static Double timeInterval(Date begin, Date end) {
        return ((end.getTime() - begin.getTime()) / MS_DAY);
    }

    /**
     * java.util.Date --> java.time.LocalDateTime
     *
     * @param date
     * @return
     */
    public static LocalDateTime dateToLocalDateTime(Date date) {
        Instant instant = date.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
        return localDateTime;
    }

    /**
     * java.util.Date --> java.time.LocalDate
     *
     * @param date
     * @return
     */
    public static LocalDate dateToLocalDate(Date date) {
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDate localDate = instant.atZone(zoneId).toLocalDate();
        return localDate;
    }

    /**
     * string to date
     *
     * @param str
     * @return
     */
    public static Date str2Date(String str) {
        Date date;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(yyyyMMddHHmmssSSS);
            date = sdf.parse(str);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            return null;
        }
        return date;
    }

    /**
     * string to date
     *
     * @param str
     * @return
     */
    public static Date str2Date(String str, String format) {
        Date date;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            date = sdf.parse(str);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            return null;
        }
        return date;
    }

    /**
     * string to timestamp
     *
     * @param str
     * @return
     */
    public static Long str2TimeStamp(String str) {
        return DateUtil.str2Date(str).getTime();
    }

    /**
     * string to timestamp
     *
     * @param str
     * @param format
     * @return
     */
    public static Long str2TimeStamp(String str, String format) {
        return DateUtil.str2Date(str, format).getTime();
    }

    /**
     * timestamp to date
     *
     * @param time 时间戳
     * @return
     */
    public static Date timestamp2Date(long time) {
        Date date;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(yyyyMMddHHmmssSSS);
            String str = sdf.format(time);
            date = sdf.parse(str);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            return null;
        }
        return date;
    }

    /**
     * timestamp to string
     *
     * @param time 时间戳
     * @return
     */
    public static String timestamp2Str(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat(yyyyMMddHHmmssSSS);
        String str = sdf.format(time);
        return str;
    }

    /**
     * 获取时间的例子
     */
    public static void example() {
        LocalDateTime localDateTime = LocalDateTime.now();
        // 获取年
        localDateTime.getYear();
        // 获取月份
        localDateTime.getMonth();
        // 获取周里的第几天
        localDateTime.getDayOfWeek();
        // 获取月里面的第几天
        localDateTime.getDayOfMonth();
        // 获取年里面的第几天
        localDateTime.getDayOfYear();
    }
}
