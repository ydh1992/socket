package com.socket.util;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

	public static final String today = "今天";
	public static final String tomorrow = "明天";
	public static final String[] week = { "日", "一", "二", "三", "四", "五", "六" };
	public static final String df_day_pattern = "yyyy-MM-dd";
	public static final String file_day_pattern = "yyyy/MM/dd";
	public static final String df_month_pattern = "yyyy-MM";
	public static final String df_pattern = "yyyy-MM-dd HH:mm:ss";
	public static final String df_minute_pattern = "yyyy-MM-dd HH:mm";
	public static final String df_hour_pattern = "yyyy-MM-dd HH";
	public static final String df_minute_patterns = "MM-dd HH:mm";
	private static final String df_time_pattern = "HH:mm:ss";
	public static final String df_cntime_pattern = "M月d日 HH:mm";
	public static final String df_cntime_patterns = "M月d日";
	public static final String df_cnminute_pattern = "yyyy年MM月dd日HH时mm分";
	private static final String df_ymd_pattern = "yyyy年MM月dd日";
	public static final String df_smsnotify_pattern = "HH:mm";
	public static final String df_calendar_pattern = "MM-dd";
	public static final String df_trade_no_prefix_pattern = "yyyyMMddHHmmss";
	public static final long MILLISEC_IN_DAY = 1000 * 60 * 60 * 24;

	// 获取系统当前时间
	public static String getCurrentTime() {
		return DateFormatUtils.format(new Date(), df_pattern);
	}

	// 获取系统当前时间
	public static Date nowDate() {
		return new Date();
	}

	// 获取系统当前时间
	public static String getCurrentTime(String pattern) {
		return DateFormatUtils.format(new Date(), pattern);
	}

	// 获取时间字段
	public static int getDateOrTimeField(Date date, int field) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.get(field);
	}

	// 日期格式化
	public static String formatDate(String date, String pattern, String targetPattern) throws Exception {
		return DateFormatUtils.format(DateUtils.parseDate(date, pattern), targetPattern);
	}

	// 格式化时间
	public static String formatDate(Date date) {
		return DateFormatUtils.format(date, df_pattern);
	}

	// 格式化时间
	public static String formatDateToDay(Date date) {
		return DateFormatUtils.format(date, df_day_pattern);
	}

	// 格式化时间
	public static String formatDate(long date) {
		return DateFormatUtils.format(date, df_pattern);
	}

	// 计算两个时间之间的秒数
	public static int getSecsIntervalOfTime(Date date1, Date date2) {
		try {
			long mills = date1.getTime() - date2.getTime();
			return Math.round(mills / 1000.0f);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	// 计算两个时间之间的分钟数
	public static int getMinsIntervalOfTime(String time1, String time2) {
		try {
			Date date1 = DateUtils.parseDate(time1, df_pattern);
			Date date2 = DateUtils.parseDate(time2, df_pattern);
			long mills = date1.getTime() - date2.getTime();
			return Math.round(mills / (60 * 1000.0f));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	// 计算两个时间之间的分钟数
	public static int getMinsIntervalOfTime(Date time1, Date time2) {
		try {
			long mills = time1.getTime() - time2.getTime();
			return Math.round(mills / (60 * 1000.0f));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	// 计算两个时间之间的天数
	public static int getDaysIntervalOfTime(String time1, String time2) {
		try {
			Date date1 = DateUtils.parseDate(time1, df_day_pattern);
			Date date2 = DateUtils.parseDate(time2, df_day_pattern);
			long mills = date1.getTime() - date2.getTime();
			return Math.round(mills / (24 * 60 * 60 * 1000.0f));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	// 计算两个时间之间的天数
	public static int getDaysIntervalOfTime(Date date1, Date date2) {
		try {
			long mills = date1.getTime() - date2.getTime();
			return Math.round(mills / (24 * 60 * 60 * 1000.0f));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	// 计算两个时间之间的月份
	public static int getMonthsIntervalOfTime(Date date1, Date date2) {
		Calendar start = Calendar.getInstance();
		Calendar end = Calendar.getInstance();
		start.setTime(date2);
		end.setTime(date1);
		int result = end.get(Calendar.MONTH) - start.get(Calendar.MONTH);
		int month = (end.get(Calendar.YEAR) - start.get(Calendar.YEAR)) * 12;
		return Math.abs(month + result);
	}

	// 获取最后一分钟
	public static Date lastMinuteInDay(Date date) {
		date = DateUtils.truncate(date, Calendar.DAY_OF_MONTH);
		date = DateUtils.addDays(date, 1);
		date = DateUtils.addMilliseconds(date, -1);
		return date;
	}

	// 获取最后一秒钟
	public static Date lastMinuteInHour(Date date) {
		date = DateUtils.truncate(date, Calendar.HOUR_OF_DAY);
		date = DateUtils.addHours(date, 1);
		date = DateUtils.addSeconds(date, -1);
		return date;
	}

	// 判断两个日期是否在同一个月份
	public static boolean isSameMonth(String date1, String date2, String pattern) throws Exception {
		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();
		c1.setTime(DateUtils.parseDate(date1, pattern));
		c2.setTime(DateUtils.parseDate(date2, pattern));
		return c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH);
	}

	// 判断两个日期是否是同一天
	public static boolean isSameDate(Date date1, Date date2) {
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(date1);
		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(date2);
		boolean isSameYear = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);// 是否是同一年
		boolean isSameMonth = isSameYear && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);// 是否在同一个月
		boolean isSameDate = isSameMonth && cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);// 是否在同一天
		return isSameDate;
	}

	public static Integer dayDiff(Date date1, Date date2) {
		if (null == date1 || null == date2) {
			return null;
		}
		Long ret = (DateUtils.truncate(date1, Calendar.DAY_OF_MONTH).getTime()
				- DateUtils.truncate(date2, Calendar.DAY_OF_MONTH).getTime()) / MILLISEC_IN_DAY;
		return ret.intValue();
	}

	// 将分钟转换成文字描述
	public static String convertMinsToDesc(int mins) {
		StringBuffer str = new StringBuffer();
		int days = mins / (24 * 60);
		str.append(days + "天");
		int hours = (mins % (24 * 60)) / 60;
		str.append(hours + "小时");
		mins = (mins % (24 * 60)) % 60;
		str.append(mins + "分钟");
		return str.toString();
	}

	// 日期比较
	public static int compareDate(String date1, String date2) throws Exception {
		Date d1 = DateUtils.parseDate(date1, df_day_pattern);
		Date d2 = DateUtils.parseDate(date2, df_day_pattern);
		if (d1.getTime() > d2.getTime()) {
			return 1;
		} else if (d1.getTime() < d2.getTime()) {
			return -1;
		} else {
			return 0;
		}
	}

	// 时间比较大小
	public static int compareTime(Date date1, Date date2) {
		if (date1.getTime() > date2.getTime()) {
			return 1;
		} else if (date1.getTime() < date2.getTime()) {
			return -1;
		} else {
			return 0;
		}
	}

	// 获取日期字段
	public static int getDateField(Date date, int field) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.get(field);
	}

	// 格式化时间
	public static String formatDateToString(Date date, String pattern) {
		return DateFormatUtils.format(date, pattern);
	}

	public static String date2Datetime(Date date) {
		return DateFormatUtils.format(date, df_time_pattern);
	}

	public static String dateToYMD(Date date) {
		return DateFormatUtils.format(date, df_ymd_pattern);
	}

	public static String date2Stringtime(Date date) {
		return DateFormatUtils.format(date, df_trade_no_prefix_pattern);
	}

	public static String date2CalendarString(Date date) {
		return DateFormatUtils.format(date, df_calendar_pattern);
	}

	public static boolean isBetween(Date date, Date a, Date b) {
		return (date.after(a) && date.before(b));
	}

	// 字符串转换日期
	public static Date parseDate(String str) {
		try {
			return DateUtils.parseDate(str, df_pattern);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	// 字符串转换日期
	public final static Date string2DateByMinute(String date) {
		try {
			return DateUtils.parseDate(date, df_minute_pattern);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	// 字符串转换日期
	public final static Date string2DateBySeconds(String date) {
		try {
			return DateUtils.parseDate(date, df_pattern);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	// 字符串转换日期
	public static Date parseDate(String str, String pattern) {
		try {
			return DateUtils.parseDate(str, pattern);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public final static String getTime(Date date) {
		return DateFormatUtils.format(date, df_smsnotify_pattern);
	}

	public final static String date2String(Date date) {
		return DateFormatUtils.format(date, df_pattern);
	}

	public final static String date2StringByCnminute(Date date) {
		return DateFormatUtils.format(date, df_cnminute_pattern);
	}

	public final static String date2StringByMinute(Date date) {
		return DateFormatUtils.format(date, df_minute_pattern);
	}

	public final static String date2StringByHour(Date date) {
		return DateFormatUtils.format(date, df_hour_pattern);
	}

	public final static String date2StringByMinutes(Date date) {
		return DateFormatUtils.format(date, df_minute_patterns);
	}

	public final static String date2StringByDay(Date date) {
		return DateFormatUtils.format(date, df_day_pattern);
	}

	public final static String date2StringByMonth(Date date) {
		return DateFormatUtils.format(date, df_month_pattern);
	}

	public final static String date3StringByDay(Date date) {
		return DateFormatUtils.format(date, df_cntime_patterns);
	}

	public final static Date string2DateByDay(String date) {
		try {
			return DateUtils.parseDate(date, df_day_pattern);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public final static Date string2DateByMonth(String date) {
		try {
			return DateUtils.parseDate(date, df_month_pattern);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public final static String now() {
		return DateFormatUtils.format(Calendar.getInstance().getTime(), df_pattern);
	}

	public final static String getTimeString(Date date) {
		return DateFormatUtils.format(date, df_cntime_pattern);
	}

	// 往前或往后推移时间
	public static Date addDateOfField(Date date, String pattern, int amount) {
		if (pattern.equals("yyyy")) {
			date = DateUtils.addYears(date, amount);
		} else if (pattern.equals("MM")) {
			date = DateUtils.addMonths(date, amount);
		} else if (pattern.equals("dd")) {
			date = DateUtils.addDays(date, amount);
		} else if (pattern.equals("HH")) {
			date = DateUtils.addHours(date, amount);
		} else if (pattern.equals("mm")) {
			date = DateUtils.addMinutes(date, amount);
		} else if (pattern.equals("ss")) {
			date = DateUtils.addSeconds(date, amount);
		} else if (pattern.equals("SSS")) {
			date = DateUtils.addMilliseconds(date, amount);
		}
		return date;
	}

	// 返回传入月的第一天
	public static Date getFirstDayOfMonth(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		return cal.getTime();
	}

	// 返回传入月的最后一天时间
	public static Date getLastDayOfMonth(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MONTH, 1);
		cal.set(Calendar.DAY_OF_MONTH, 0);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MINUTE, 59);
		return cal.getTime();
	}

	// 获取传入时间的最后的时间点（传入：2018-01-01 10:22:22 输出：2018-01-01 23:59:59）
	public static String getLastTimeString(Date date) {
		date = DateUtils.addDays(date, 1);
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		return DateFormatUtils.format(addDateOfField(cal.getTime(), "ss", -1), df_pattern);
	}

	// 返回传入时间是周几
	public static String getDayofWeek(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
		if (w < 0)
			w = 0;
		return week[w];
	}

	// 返回传入时间是周几
	public static int getWeekOfDate(Date dt) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(dt);
		return cal.get(Calendar.DAY_OF_WEEK) - 1 > 0 ? cal.get(Calendar.DAY_OF_WEEK) - 1 : 7;
	}

	// 结果为“0”是上午 结果为“1”是下午
	public static int getAmOrPm(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.AM_PM);
	}

	// 返回传入时间的零点
	public static Date getFirstDayMinuteOfDate(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		return cal.getTime();
	}

	// 两个时间相差距离多少小时多少分
	public static String getDistanceTimes(Date startDate, Date endDate) {
		long diff = endDate.getTime() - startDate.getTime();
		long hour = (diff / (60 * 60 * 1000));
		long min = ((diff / (60 * 1000)) - hour * 60);
		return hour + "小时" + (min < 9 ? "0" + min : min) + "分";
	}
}
