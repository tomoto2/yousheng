package com.joe.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串的常用操作
 * 
 * @author joe
 *
 */
public class StringUtils {
	private static Pattern numberPattern = Pattern.compile("-?[0-9]{1,}\\.?[0-9]{1,}");

	/**
	 * 判断字符串是否为空（为null时也是空，字符串全是空白符时也是空）
	 * 
	 * @param arg
	 *            指定字符串
	 * @return 如果为空则返回true
	 */
	public static boolean isEmpty(String arg) {
		return arg == null || arg.trim().isEmpty();
	}

	/**
	 * 判断字符串参数是否是数字
	 * 
	 * @param arg
	 *            数字参数
	 * @return 如果参数是数字则返回<code>true</code>
	 */
	public static boolean isNumber(String arg) {
		Matcher matcher = numberPattern.matcher(arg);
		return matcher.matches();
	}

	/**
	 * 将首字母大写
	 * 
	 * @param arg
	 *            指定字符串
	 * @return 首字母大写后的字符串
	 */
	public static String toFirstUpperCase(String arg) {
		return arg.substring(0, 1).toUpperCase() + arg.substring(1);
	}

	/**
	 * 求两个字符串的最大公共子序列的长度
	 * 
	 * @param arg0
	 *            字符串1
	 * @param arg1
	 *            字符串2
	 * @return 两个字符串的最大公共子序列的长度
	 */
	
	public static long lcs(String arg0, String arg1) {
		if (arg0 == null || arg1 == null) {
			return 0;
		}
		return lcs(arg0, arg1, 0, 0);
	}

	/**
	 * 求两个字符串的最大公共子序列的长度
	 * 
	 * @param arg0
	 *            字符串1
	 * @param arg1
	 *            字符串2
	 * @param i
	 *            字符串1的当前位置指针
	 * @param arg1
	 *            字符串2的当前位置指针
	 * @return 两个字符串的最大公共子序列的长度
	 */
	private static long lcs(String arg0, String arg1, int i, int j) {
		if (arg0.length() == i || arg1.length() == j) {
			return 0;
		}

		if (arg0.charAt(i) == arg1.charAt(j)) {
			return 1 + lcs(arg0, arg1, ++i, ++j);
		} else {
			return Math.max(lcs(arg0, arg1, ++i, j), lcs(arg0, arg1, i, ++j));
		}
	}
}
