package com.joe.frame.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 日期工具类
 * 
 * @author dengjianjun
 *
 */
@Component
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

	/**
	 * 将指定日期字符串按照指定格式转换为日期对象
	 * 
	 * @param date
	 * @param format
	 * @return
	 * @throws ParseException
	 */
	public Date parse(String date, String format) throws ParseException {
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		return dateFormat.parse(date);
	}

	/**
	 * 计算arg0-arg1的时间差（该结果不精确）
	 * 
	 * @param arg0
	 * @param arg1
	 * @param field
	 *            返回结果的单位，接受1（年）、2（月）、5、6、7（日）、10（时）、12（分）、13（秒）
	 * @return arg0-arg1的时间差，精确到指定的单位（field）
	 */
	public int calc(Date arg0, Date arg1, int field) {
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
	 *            返回结果的单位，接受1（年）、2（月）、3（日）、4（时）、5（分）、6（秒）
	 * @return arg0-arg1的时间差，精确到指定的单位（field），出错时返回0
	 */
	public int calc(String arg0, String arg1, String format, int field) {
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
	 * 将指定日期加上指定的时长
	 * 
	 * @param field
	 *            单位
	 * @param amount
	 *            时长
	 * @param date
	 *            指定的日期
	 * @return
	 */
	public Date add(int field, int amount, Date date) {
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
	public Date add(int field, int amount) {
		return add(field, amount, new Date());
	}

	/**
	 * 获取指定格式的当前日期的字符串
	 * 
	 * @param format
	 *            日期格式
	 * @return 指定格式的当前日期的字符串
	 */
	public String getFormatDate(String format) {
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
	public String getFormatDate(String format, Date date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		return dateFormat.format(date);
	}

	/**
	 * 获取今日天数
	 * 
	 * @return
	 */
	public String getTodayStr() {
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
	 * @return
	 */
	public Date getTodayDate() {
		long time = System.currentTimeMillis();

		if (day > 0 && time > dayStartTime && time < dayEndTime) {
			return dayDate;
		}

		resetDate();
		return dayDate;
	}

	/**
	 * 获取今日天数
	 * 
	 * @return
	 */
	public int getTodayDay() {
		long time = System.currentTimeMillis();

		if (day > 0 && time > dayStartTime && time < dayEndTime) {
			return day;
		}

		resetDate();
		return day;
	}

	/**
	 * 查询时间是否在今日
	 * 
	 * @param time
	 * @return
	 */
	public boolean isToday(long time) {
		if (System.currentTimeMillis() > dayEndTime) {
			resetDate();
		}

		return time > dayStartTime && time < dayEndTime;
	}

	/**
	 * 查询时间是否在今日
	 * 
	 * @param time
	 * @return
	 */
	public boolean isToday(Date time) {
		if (time == null) {
			return false;
		}

		return isToday(time.getTime());
	}

	private synchronized void resetDate() {
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

	/**
	 * 获取指定日期的上一个月
	 * 
	 * @param format
	 *            指定格式 yyyy年MM月dd日 yyyyMMdd yyyyMM
	 * @param now
	 *            指定日期
	 * 
	 * @return 上月日期
	 * @throws ParseException
	 */
	public String getPreMonth(String now, String format) throws ParseException {
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		Date date = dateFormat.parse(now); // 将指定日期转为date类型
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date); // 设置为当前时间
		calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1); // 设置为上一个月
		date = calendar.getTime();
		return dateFormat.format(date);
	}

	/**
	 * 比较两个String类型的日期的大小，精确到日
	 * 
	 * @param d1
	 *            格式：yyyy-MM-dd HH:mm:ss
	 * @param d2
	 *            格式：yyyy-MM-dd HH:mm:ss
	 * @return
	 * @throws ParseException
	 */
	public boolean compareTime(String d1, String d2) throws ParseException {
		//d1 = d1.substring(0, 10);
//		d2 = d2.substring(0, 10);// 先截取日期到日，
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date dt1 = df.parse(d1);// 转换成Date类型
		Date dt2 = df.parse(d2);
		if (dt1.getTime() >= dt2.getTime()) {// 比较两个Date的大小
			// System.out.println("dt1大");
			return true;
		} else {
			// System.out.println("dt2大");
			return false;
		}
	}
	

	
	/**
	 * 比较两个String类型的日期中的月份是否相等，精确到月
	 * 
	 * @param d1
	 *            格式：yyyy-MM-dd HH:mm:ss
	 * @param d2
	 *            格式：yyyy-MM-dd HH:mm:ss
	 * @return
	 * @throws ParseException
	 */
	public boolean compareTime1(String d1, String d2) throws ParseException {
		d1 = d1.substring(0, 7);
		d2 = d2.substring(0, 7);// 先截取日期到日，
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM");
		Date dt1 = df.parse(d1);// 转换成Date类型
		Date dt2 = df.parse(d2);
		if (dt1.getTime() == dt2.getTime()) {// 比较两个Date的大小
			// System.out.println("dt1大");
			return true;
		} else {
			// System.out.println("dt2大");
			return false;
		}
	}
		

//	public static void main(String[] args) throws ParseException {
//		boolean s = compareTime1("2017-05-05 12:00:00","2017-05-01 12:00:00");
//		System.out.println(s);
//	}
	
	
	/**
	 * 判断某个时间是否在两个日期之间  精确到日，判断mou是否在d1和d2之间，且d1<d2
	 * 
	 * @param d1
	 *            格式：yyyy-MM-dd  beginTime
	 * @param d2 
	 *            格式：yyyy-MM-dd   endTime
	 * @param mou
	 *            格式：yyyy-MM-dd HH:mm:ss
	 * @return ture 在 ，false 不在
	 * @throws ParseException
	 */
	public boolean compareInTime(String d1, String d2, String mou) throws ParseException {
		mou = mou.substring(0, 10);// 先截取日期到日，
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date dt1 = df.parse(d1);// 转换成Date类型
		Date dt2 = df.parse(d2);
		Date dt3 = df.parse(mou);
		if (dt1.getTime() <= dt3.getTime() && dt3.getTime() <= dt2.getTime()) {
			return true;// 在之间
		} else {
			return false;// 不在其之间
		}
	}
	
	
	  /**
     * 
     * 功能: 判断是否是月末
     * @param 日期
     * @return true月末,false不是月末
     */
    public boolean isMonthLastDay(Date days){
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(days);
        if(calendar.get(Calendar.DATE)==calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
            return true;
        else
            return false;
    }
    
    /**
     * 功能: 主函数
     * @param args 
     */
//    public static void main(String[] args) {
//        if(isMonthLastDay(new Date()))
//            System.out.println("当前是月末!");
//        else
//            System.out.println("当前不是月末!");
//    }

}
