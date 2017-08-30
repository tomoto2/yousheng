//package com.joe.frame.pay.common.scan;
//
//import java.lang.reflect.Field;
//import java.util.ArrayList;
//import java.util.List;
//
//import com.joe.scan.Scanner;
//
///**
// * 扫描指定对象中含有{@link com.pay.common.annotation.Sign}注解的参数
// * 
// * @author joe
// *
// */
//public class FieldScanner implements Scanner<Field, SignFilter> {
//	public List<Field> scan(Object... args) {
//		Field[] fields = args[0].getClass().getDeclaredFields();
//		SignFilter filter = new SignFilter();
//		List<Field> result = new ArrayList<Field>(fields.length);
//		for (Field field : fields) {
//			if (filter.filter(field)) {
//				result.add(field);
//			}
//		}
//		return result;
//	}
//
//	public List<Field> scan(List<SignFilter> filters, Object... args) {
//		Field[] fields = args[0].getClass().getDeclaredFields();
//		SignFilter filter = filters.get(0);
//		List<Field> result = new ArrayList<Field>(fields.length);
//		for (Field field : fields) {
//			if (filter.filter(field)) {
//				result.add(field);
//			}
//		}
//		return result;
//	}
//
//}
