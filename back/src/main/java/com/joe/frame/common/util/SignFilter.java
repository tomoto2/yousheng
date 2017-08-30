package com.joe.frame.common.util;

import java.lang.reflect.Field;

import com.joe.frame.pay.common.annotation.Sign;
import com.joe.scan.Filter;

/**
 * 过滤带有{@link com.pay.common.annotation.Sign}注解的参数
 * @author joe
 *
 */
public class SignFilter implements Filter<Field>{
	@Override
	public boolean filter(Field t) {
		return t.isAnnotationPresent(Sign.class);
	}
}
