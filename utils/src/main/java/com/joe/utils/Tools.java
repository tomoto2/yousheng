package com.joe.utils;

import java.util.UUID;

public class Tools {
	/**
	 * 生成一个32位的UUID
	 * 
	 * @return 32位的UUID
	 */
	public static String createUUID() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}

	/**
	 * 生成随机字符串（只包含数字和字母）
	 * 
	 * @param length
	 *            字符串的长度
	 * @return 返回指定长度的随机字符串，当长度小于等于0时返回空的字符串
	 */
	public static String createNonceStr(int length) {
		char[] chars = new char[length];
		for (int i = 0; i < length; i++) {
			int num = (int) (Math.random() * 75) + 48;
			while ((num > 57 && num < 65) || (num > 90 && num < 97)) {
				num = (int) (Math.random() * 75) + 48;
			}
			chars[i] = (char) num;
		}
		String nonceStr = new String(chars);
		return nonceStr;
	}
}
