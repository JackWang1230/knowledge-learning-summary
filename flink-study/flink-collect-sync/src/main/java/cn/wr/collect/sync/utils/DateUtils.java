package cn.wr.collect.sync.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * 日期工具类
 */
public class DateUtils {
    private static final Logger LOG = LoggerFactory.getLogger(DateUtils.class);

    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static final String YYYY_MM_DD_HH_MM_SS_SSS = "yyyy-MM-dd HH:mm:ss.SSS";

    /**
     * 格式化
     * @param date
     * @return
     */
    public static String format(Date date) {
        SimpleDateFormat df = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS);
        return df.format(date);
    }

    /**
     * 格式化成指定格式字符串
     * @param date
     * @param format
     * @return
     */
    public static String format(Date date, String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(date);
    }

    public static LocalDateTime parse(String dateStr) {
        return parse(dateStr, YYYY_MM_DD_HH_MM_SS);
    }

    public static LocalDateTime parse(String dateStr, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return LocalDateTime.parse(dateStr, formatter);
    }

    public static Date parseDate(String dateStr) {
        SimpleDateFormat df = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS);
        Date parse = null;
        try {
            parse = df.parse(dateStr);
        } catch (ParseException e) {
            LOG.error("DateUtils.parseDate({}) e:{}", dateStr, e);
        }
        return parse;
    }


    /**
     * 格式化
     * @param date
     * @return
     */
    public static String format(LocalDateTime date) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS);
        return df.format(date);
    }

    /**
     * 格式化成指定格式字符串
     * @param date
     * @param format
     * @return
     */
    public static String format(LocalDateTime date, String format) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern(format);
        return df.format(date);
    }

    /**
     * 计算两个时间差 毫秒数
     * @param date1
     * @param date2
     * @return
     */
    public static long diffMillis(LocalDateTime date1, LocalDateTime date2) {
        Duration duration = Duration.between(date1, date2);
        return duration.toMillis();
    }

    /**
     * 计算当前时间距离下一个固定时间点剩余毫秒数
     * @param hour
     * @param minute
     * @param second
     * @return
     */
    public static long calculateDiffTime(int hour, int minute, int second) {
        LocalDateTime syncDateTime = LocalDateTime.now().withHour(hour).withMinute(minute).withSecond(second);
        LocalDateTime currentDateTime = LocalDateTime.now();
        if (syncDateTime.isBefore(currentDateTime)) {
            syncDateTime = syncDateTime.plusDays(1);
            return DateUtils.diffMillis(currentDateTime, syncDateTime);
        }
        else {
            return DateUtils.diffMillis(currentDateTime, syncDateTime);
        }
    }

    /**
     * 获取当前时间
     * @return
     */
    public static Date now() {
        Calendar now = Calendar.getInstance();
        now.setTimeZone(TimeZone.getTimeZone("GMT+8"));//格式差8小时
        return now.getTime();
    }

    public static void main(String[] args) {

        System.out.println(new Date());
        System.out.println(now());
    }
}
