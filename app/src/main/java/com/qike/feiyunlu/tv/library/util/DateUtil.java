package com.qike.feiyunlu.tv.library.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * <p>时间工具类<p/>
 * @author zhulingjun
 */
public class DateUtil {

	private static DateFormat yyyy_MM_dd = new SimpleDateFormat("yyyy-MM-dd");

	/**
	 * <p>判断当前时间是否在该时间段内<p/>
	 * @return true/false
     * @author zhulingjun
	 */
	public static boolean isTimeIn_YYYY_MM_DD(String starttime, String endtime) {
		Date startDate = null;
		Date endDate = null;
		try {
			startDate = yyyy_MM_dd.parse(starttime);
			endDate = yyyy_MM_dd.parse(endtime);

			if (System.currentTimeMillis() >= startDate.getTime() && System.currentTimeMillis() <= endDate.getTime()) {
				return true;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return false;
	}

	private static DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	private static DateFormat sf = new SimpleDateFormat("yyyyMMddhhmmss");
	private static DateFormat ysf = new SimpleDateFormat("yyMMddhhmmss");
	private static DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");

	private static DateFormat sdfs = new SimpleDateFormat("yyyy-MM-dd");
	private static DateFormat sdfsd = new SimpleDateFormat("yyyy.MM.dd");

	/** 格式化时间 yyyy.MM.dd */
	public static String converTime4(long time) {
		return sdfsd.format(new Date(time));
	}
	
	/** 格式化时间 yyMMddhhmmss */
	public static String converTime7(long time) {
		return ysf.format(new Date(time));
	}

	/** 格式化时间 yyyy-MM-dd hh:mm(12小时制) */
	public static String converTime5(long time) {
		return sdf.format(new Date(time));
	}

	/** 格式化时间 yyyy-MM-dd HH:mm(24小时制) */
	public static String converTime6(long time) {
		return df.format(new Date(time));
	}

	/** 格式化时间 yyyy-MM-dd */
	public static String converTime3(long time) {
		return sdfs.format(new Date(time));
	}

	/**-1 从前，0现在，1将来 单位（天）  */
	public static int isfutrueDay(long time) {
		String day = sdfs.format(new Date());
		try {
			Date date = sdfs.parse(day);
			long now = date.getTime();
			if (time < now) {
				return -1;
			} else if (time - now >= 24 * 60 * 60 * 1000) {
				return 1;
			} else {
				return 0;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static long getTime(String dateTime) {

		try {
			Date date = sdf.parse(dateTime);
			return date.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 返回年月日
	 * 
	 * @param c
	 * @param separator
	 *            如: "-"
	 * @return 如: 2012-05-06
	 */
	public static String getDate(Calendar c, String separator) {
		int month = c.get(Calendar.MONTH) + 1;
		int day = c.get(Calendar.DAY_OF_MONTH);
		StringBuffer sb = new StringBuffer();
		sb.append(c.get(Calendar.YEAR));
		sb.append(separator);
		if (month < 10) {
			sb.append(0);// 添0补位
		}
		sb.append(month);// 月份从0开始
		sb.append(separator);
		if (day < 10) {
			sb.append(0);// 添0补位
		}
		sb.append(day);
		return sb.toString();
	}

	/**
	 * 返回格式化的时间, 带毫秒
	 * 
	 * @param c
	 * @param seprator
	 *            如:":"
	 * @return 如: 23:01:15:998
	 */
	public static String getTimeWithMillSec(Calendar c, String seprator) {
		return getTime(c, true, seprator);
	}

	public static String getTime(Calendar c, boolean withMillSec, String separator) {
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);
		int second = c.get(Calendar.SECOND);
		int millSec = c.get(Calendar.MILLISECOND);
		StringBuffer sb = new StringBuffer();
		if (hour < 10) {
			sb.append(0);// 添0补位
		}
		sb.append(hour);

		sb.append(separator);
		if (minute < 10) {
			sb.append(0);// 添0补位
		}
		sb.append(minute);

		sb.append(separator);
		if (second < 10) {
			sb.append(0);// 添0补位
		}
		sb.append(second);
		if (withMillSec) {
			sb.append(separator);
			if (millSec < 10) {
				sb.append("00");
			} else if (millSec < 100) {
				sb.append("0");
			}
			sb.append(millSec);
		}
		return sb.toString();
	}

	/**
	 * 列表 中的发时间
	 * 
	 * @param Published
	 * @return
	 */
	public static String converTime(long data) {
		long timeSpan = (long) (System.currentTimeMillis() - data) / (1000 * 60);

		if (timeSpan < 1) {
			return "刚刚";
		}

		if (timeSpan >= 1 && timeSpan < 60) {
			return String.format("%1$s分钟前", String.valueOf(timeSpan));
		}
		if (timeSpan >= 60 && timeSpan < 60 * 24) {
			return String.format("%1$s小时前", String.valueOf(timeSpan / 60));
		}

		if (timeSpan >= 60 * 24 && timeSpan < 60 * 24 * 1000) {
			return String.format("%1$s天前", String.valueOf(timeSpan / (60 * 24)));
		}

		return df.format(data);
	}

	public static String converTime2(long data) {
		long timeSpan = (long) (System.currentTimeMillis() - data) / (1000 * 60);

		if (timeSpan < 1) {
			return "刚刚";
		}
		if (timeSpan >= 1 && timeSpan < 60) {
			return String.format("%1$s分钟前", String.valueOf(timeSpan));
		}
		if (timeSpan >= 60 && timeSpan < 60 * 24) {
			return String.format("%1$s小时前", String.valueOf(timeSpan / 60));
		}
		return df.format(data);
	}

	/**
	 * 列表 中的发时间
	 * 
	 * @param Published
	 * @return
	 */
	public static String converTime(String data) {

		try {
			Date date = sf.parse(data);
			if (date == null) {
				return "未知";
			}
			long timeZone = date.getTime();

			int timeSpan = (int) (System.currentTimeMillis() - timeZone) / (1000 * 60);

			if (timeSpan < 1) {
				return "刚刚";
			}

			if (timeSpan >= 1 && timeSpan < 60) {
				return String.format("%1$s分钟前", String.valueOf(timeSpan));
			}
			if (timeSpan >= 60 && timeSpan < 60 * 24) {
				return String.format("%1$s小时前", String.valueOf(timeSpan / 60));
			}

			if (timeSpan >= 60 * 24 && timeSpan < 60 * 24 * 1000) {
				return String.format("%1$s天前", String.valueOf(timeSpan / (60 * 24)));
			}
		} catch (ParseException e) {
			e.printStackTrace();
			return data;
		}
		return sf.format(data);
	}
}
