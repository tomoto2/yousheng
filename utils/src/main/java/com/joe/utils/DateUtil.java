package com.joe.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 日期工具类
 * 
 * @author joe
 *
 */
public class DateUtil {
	private final static Logger logger = LoggerFactory.getLogger(DateUtil.class);

	private static long dayStartTime = -1;
	private static long dayEndTime = -1;
	private static int day = -1;
	private static String dayStr = null;
	private static Date dayDate = null;

	public final static int YEAR = 1;

	public final static int MONTH = 2;

	public final static int WEEK_OF_YEAR = 3;

	public final static int WEEK_OF_MONTH = 4;

	public final static int DAY_OF_MONTH = 5;

	public final static int DAY_OF_YEAR = 6;

	public final static int DAY_OF_WEEK = 7;

	public final static int DAY_OF_WEEK_IN_MONTH = 8;

	public final static int AM_PM = 9;

	public final static int HOUR = 10;

	public final static int HOUR_OF_DAY = 11;

	public final static int MINUTE = 12;

	public final static int SECOND = 13;

	public final static int MILLISECOND = 14;

	private final static TreeMap<String, SimpleDateFormat> cache = new TreeMap<String, SimpleDateFormat>();

	/**
	 * 将指定日期字符串按照指定格式转换为日期对象
	 * 
	 * @param date
	 *            格式化日期字符串
	 * @param format
	 *            日期字符串的格式
	 * @return 格式化日期字符串对应的日期对象
	 * @throws DateUtilParseException
	 *             格式错误时返回该异常
	 */
	public static Date parse(String date, String format) throws DateUtilParseException {
		try {
			SimpleDateFormat dateFormat = cache.get(format);
			if (dateFormat == null) {
				dateFormat = new SimpleDateFormat(format);
			}
			cache.put(format, dateFormat);
			return dateFormat.parse(date);
		} catch (ParseException e) {
			throw new DateUtilParseException(e);
		}
	}

	/**
	 * 计算arg0-arg1的时间差（该结果不精确）
	 * 
	 * @param arg0
	 *            arg0
	 * @param arg1
	 *            arg1
	 * @param field
	 *            返回结果的单位，接受1（年）、2（月）、5、6、7（日）、10（时）、12（分）、13（秒）
	 * @return arg0-arg1的时间差，精确到指定的单位（field）
	 */
	public static int calc(Date arg0, Date arg1, int field) {
		long l0 = arg0.getTime();
		long l1 = arg1.getTime();
		long l = l0 - l1;
		int result = 0;
		switch (field) {
		case 1:
			result = (int) (l / (1000 * 60 * 60 * 24 * 30 * 365));
			break;
		case 2:
			result = (int) (l / (1000 * 60 * 60 * 24 * 30));
			break;
		case 5:
		case 6:
		case 7:
			result = (int) (l / (1000 * 60 * 60 * 24));
			break;
		case 10:
			result = (int) (l / (1000 * 60 * 60));
			break;
		case 12:
			result = (int) (l / (1000 * 60));
			break;
		default:
			result = (int) (l / 1000);
			break;
		}
		return result;
	}

	/**
	 * 计算arg0-arg1的时间差（该结果不精确）
	 * 
	 * @param arg0
	 *            日期字符串
	 * @param arg1
	 *            日期字符串
	 * @param format
	 *            日期字符串的格式
	 * @param field
	 *            返回结果的单位，接受1（年）、2（月）、5、6、7（日）、10（时）、12（分）、13（秒）
	 * @return arg0-arg1的时间差，精确到指定的单位（field），出错时返回0
	 */
	public static int calc(String arg0, String arg1, String format, int field) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		try {
			Date date0 = dateFormat.parse(arg0);
			Date date1 = dateFormat.parse(arg1);
			return calc(date0, date1, field);
		} catch (Exception e) {
			logger.error("日期计算出错", e);
			return 0;
		}
	}

	/**
	 * 将指定日期增加指定市场
	 * 
	 * @param field
	 *            单位
	 * @param amount
	 *            时长
	 * @param date
	 *            指定日期
	 * @param format
	 *            指定日期字符串的格式
	 * @return 增加后的日期
	 * @throws DateUtilParseException
	 *             当date与format不匹配时抛出该异常
	 */
	public static Date add(int field, int amount, String date, String format) throws DateUtilParseException {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(parse(date, format));
		calendar.add(field, amount);
		return calendar.getTime();

	}

	/**
	 * 将指定日期加上指定的时长
	 * 
	 * @param field
	 *            单位
	 * @param amount
	 *            时长
	 * @param date
	 *            指定的日期
	 * @return 增加指定时长后的日期
	 */
	public static Date add(int field, int amount, Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(field, amount);
		return calendar.getTime();
	}

	/**
	 * 将当前日期加上指定的时长
	 * 
	 * @param field
	 *            单位
	 * @param amount
	 *            时长
	 * @return 增加过指定时长的时间
	 */
	public static Date add(int field, int amount) {
		return add(field, amount, new Date());
	}

	/**
	 * 获取指定格式的当前日期的字符串
	 * 
	 * @param format
	 *            日期格式
	 * @return 指定格式的当前日期的字符串
	 */
	public static String getFormatDate(String format) {
		return getFormatDate(format, new Date());
	}

	/**
	 * 获取指定日期的指定格式的字符串
	 * 
	 * @param format
	 *            日期格式
	 * @param date
	 *            指定日期
	 * @return 指定日期的指定格式的字符串
	 */
	public static String getFormatDate(String format, Date date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		return dateFormat.format(date);
	}

	/**
	 * 获取今日天数
	 * 
	 * @return 获取今天的日期，格式为YYYYMMdd
	 */
	public static String getTodayStr() {
		long time = System.currentTimeMillis();

		if (day > 0 && time > dayStartTime && time < dayEndTime) {
			return dayStr;
		}

		resetDate();
		return dayStr;
	}

	/**
	 * 获取今日日期
	 * 
	 * @return 今天的日期对象
	 */
	public static Date getTodayDate() {
		long time = System.currentTimeMillis();

		if (day > 0 && time > dayStartTime && time < dayEndTime) {
			return dayDate;
		}

		resetDate();
		return dayDate;
	}

	/**
	 * 判断指定日期是否在当前时间之前
	 * 
	 * @param date
	 *            指定日期
	 * @param format
	 *            指定日期的格式
	 * @return 如果指定日期在当前时间之前返回<code>true</code>
	 * @throws DateUtilParseException
	 *             日期参数错误
	 */
	public static boolean beforeNow(String date, String format) throws DateUtilParseException {
		logger.debug("指定日期为：{}", date);
		long dateL = parse(date, format).getTime();
		logger.debug("指定日期为：{} , 当前日期为：{}", dateL, System.currentTimeMillis());
		return dateL < System.currentTimeMillis();
	}

	/**
	 * 查询时间是否在今日
	 * 
	 * @param time
	 *            时间戳
	 * @return 如果时间戳是今天的则返回<code>true</code>
	 */
	public static boolean isToday(long time) {
		if (System.currentTimeMillis() > dayEndTime) {
			resetDate();
		}

		return time > dayStartTime && time < dayEndTime;
	}

	/**
	 * 查询时间是否在今日
	 * 
	 * @param time
	 *            日期对象
	 * @return 如果指定日期对象在今天则返回<code>true</code>
	 */
	public static boolean isToday(Date time) {
		if (time == null) {
			return false;
		}

		return isToday(time.getTime());
	}

	/**
	 * 重置日期
	 */
	private static synchronized void resetDate() {
		Calendar cal = Calendar.getInstance();

		day = cal.get(Calendar.DAY_OF_YEAR);

		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		dayStartTime = cal.getTimeInMillis();

		dayDate = cal.getTime();

		dayStr = new SimpleDateFormat("yyyyMMdd").format(dayDate);

		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 999);
		dayEndTime = cal.getTimeInMillis();

		logger.info("重置今日时间：{}", dayStr);
	}

	static class DateUtilParseException extends RuntimeException {
		private static final long serialVersionUID = 474205378026735176L;

		public DateUtilParseException(Throwable cause) {
			super(cause);
		}
	}

}
