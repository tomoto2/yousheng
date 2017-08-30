package com.joe.frame.common.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.joe.frame.pay.common.annotation.Sign;
import com.joe.utils.StringUtils;

public class SignUtil {
	private static final Logger logger = LoggerFactory.getLogger(SignUtil.class);

	/**
	 * 获取签名字段拼装的签名参数（将所有签名字段按ASCII升序排列，除去值为空的字段，然后将剩下的字段拼装为form格式的字符串）
	 * 
	 * @param source
	 *            要签名的对象
	 * @param code
	 *            <li>1：使用URLencode编码</li>
	 *            <li>2：使用URLdecode编码</li>
	 *            <li>3：不使用任何编码</li>
	 * @return 待签名字符串
	 */
	public static String getSignDataFromObject(Object source, int code) {
		return getSignDataFromMap(getSignFields(source), code);
	}

	/**
	 * 获取指定对象中要签名的字段的信息（不包含空字段）
	 * 
	 * @param source
	 *            指定对象
	 * @return 要签名的字段的信息
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getSignFields(Object source) {
		if (source instanceof Map) {
			logger.debug("指定的对象是map");
			return (Map<String, Object>) source;
		}
		logger.debug("开始获取{}中要签名的字段信息", source);
		Map<String, Object> map = new TreeMap<String, Object>();
		FieldScanner scanner = new FieldScanner();
		String name = null;
		try {
			List<Field> fields = scanner.scan(source);
			logger.debug("{}中要签名的字段集合为{}", source, fields);
			for (Field field : fields) {
				name = field.getName();
				logger.debug("要签名的字段名为：{}", name);
				String getMethodName = "get" + StringUtils.toFirstUpperCase(name);
				Method method = source.getClass().getMethod(getMethodName);
				Object value = method.invoke(source);
				logger.debug("要签名的字段的值为：{}", value);
				Sign sign = field.getDeclaredAnnotation(Sign.class);
				if (value == null || String.valueOf(value).trim().isEmpty()) {
					logger.debug("字段{}的值为{}，跳过字段", name, value);
					continue;
				}
				map.put(sign.keyName().isEmpty() ? name : sign.keyName(), value);
			}
			logger.debug("要签名的所有字段映射为：{}", map);
			return map;
		} catch (Exception e) {
			logger.error("获取字段{}的值时失败，跳过", name, e);
			return Collections.emptyMap();
		}
	}

	/**
	 * 将map中的数据按照升序排列然后拼为form格式的数据（忽略空元素）
	 * 
	 * @param data
	 *            map数据
	 * @param code
	 *            <li>1：使用URLencode编码</li>
	 *            <li>2：使用URLdecode编码</li>
	 *            <li>3：不使用任何编码</li>
	 * @return form格式的数据
	 */
	public static String getSignDataFromMap(Map<String, Object> data, int code) {
		logger.debug("要转换的map数据为：{}", data);
		// 如果map不是TreeMap那么将其转换为TreeMap
		data = getSignFields(data);
		StringBuilder sb = new StringBuilder();
		boolean flag = false;
		for (Map.Entry<String, Object> entry : data.entrySet()) {
			String value = String.valueOf(entry.getValue());
			if (flag) {
				sb.append("&");
			} else {
				flag = true;
			}
			sb.append(entry.getKey()).append("=");
			try {
				switch (code) {
				case 1:
					sb.append(URLEncoder.encode(value, "UTF8"));
					break;
				case 2:
					sb.append(URLDecoder.decode(value, "UTF8"));
					break;
				case 3:
					sb.append(value);
					break;
				}
			} catch (Exception e) {
				// 该异常不可能抛出
			}
		}
		String result = sb.toString();
		logger.debug("转换结果为：{}", result);
		return result;
	}

	/**
	 * 将map转换为TreeMap并且除去空元素
	 * 
	 * @param source
	 *            指定对象
	 * @return 要签名的字段的信息
	 */
	public static TreeMap<String, Object> getSignFields(Map<String, Object> source) {
		logger.debug("开始转换map{}", source);
		if (!(source instanceof TreeMap)) {
			source = new TreeMap<String, Object>(source);
		}
		TreeMap<String, Object> result = (TreeMap<String, Object>) source;
		for (Map.Entry<String, Object> entry : result.entrySet()) {
			Object value = entry.getValue();
			if (value == null || String.valueOf(value).trim().isEmpty()) {
				// 除去空元素
				result.remove(entry.getKey());
				logger.debug("key（{}）对应的value（{}）为空，忽略该元素", entry.getKey(), value);
			}
		}
		logger.debug("转换后的map为：{}", result);
		return result;
	}

	/**
	 * 把数组所有元素排序，并按照“参数=参数值”的模式用“&”字符拼接成字符串
	 * 
	 * @param params
	 *            需要排序并参与字符拼接的参数组
	 * @return 拼接后字符串
	 */
	public static String createLinkString(Map<String, Object> params) {

		List<String> keys = new ArrayList<String>(params.keySet());
		Collections.sort(keys);

		String prestr = "";

		for (int i = 0; i < keys.size(); i++) {
			String key = keys.get(i);
			String value = (String) params.get(key);

			if (i == keys.size() - 1) {// 拼接时，不包括最后一个&字符
				prestr = prestr + key + "=" + value;
			} else {
				prestr = prestr + key + "=" + value + "&";
			}
		}

		return prestr;
	}

}
