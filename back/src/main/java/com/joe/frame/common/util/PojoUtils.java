package com.joe.frame.common.util;

import java.beans.PropertyDescriptor;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.joe.frame.web.exception.BeanException;

public class PojoUtils {
	private static final Logger logger = LoggerFactory.getLogger(PojoUtils.class);
 
	/**
	 * 将source强制转换为clazz对应的类型（source必须使clazz类或者clazz的子类）
	 * 
	 * @param source
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T convert(Object source, Class<T> clazz) {
		return (T) source;
	}

	/**
	 * 将source中与targetClass同名的字段从source中复制到targetClass的实例中，使用前请对参数进行非空校验
	 * 
	 * @param source
	 *            被复制的源对象
	 * @param targetClass
	 *            要复制的目标对象的class对象
	 * @return targetClass的实例，当targetClass或者source的class为接口、抽象类或者不是public时返回null
	 */
	public static <E> E copy(Object source, Class<E> targetClass) {
		E target = null;
		try {
			// 没有权限访问该类或者该类（为接口、抽象类）不能实例化时将抛出异常
			target = targetClass.newInstance();
		} catch (Exception e) {
			logger.error("target生成失败，请检查代码；失败原因：" + e.toString());
			return null;
		}
		// 获取source中的字段说明
		PropertyDescriptor[] p1 = PropertyUtils.getPropertyDescriptors(source);
		for (PropertyDescriptor descript : p1) {
			String name = descript.getName();
			try {
				// BeanUtil没有自带日期转换器，需要自己实现
				ConvertUtils.register(new Converter() {
					@SuppressWarnings("unchecked")
					public <T> T convert(Class<T> arg0, Object arg1) {
						SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.sss");
						try {
							return (T) format.parse(String.valueOf(arg1));
						} catch (Exception e) {
							logger.info("日期转换出错，原因：" + e.toString());
							return null;
						}
					}
				}, Date.class);

				// 如果source类没有声明为public将设置失败，抛出异常
				String prop = BeanUtils.getProperty(source, name);
				BeanUtils.setProperty(target, name, prop);
				logger.debug("copy " + source.getClass().getName() + "." + name + " to " + target.getClass().getName()
						+ "." + name);
			} catch (Exception e) {
				logger.error("copy中复制" + name + "时发生错误，忽略该字段");
				logger.debug("copy中复制" + name + "时发生错误", e);
				continue;
			}
		}
		return target;
	}

	/**
	 * 批量复制，将source里的对象的字段复制到targetClass的对象中并返回
	 * 
	 * @param source
	 *            被复制的源对象
	 * @param targetClass
	 *            要复制的目标对象的class对象
	 * @return targetClass的对象的数组
	 */
	public static <E, T> List<E> copy(List<T> source, Class<E> targetClass) {
		if(source == null || targetClass == null){
			logger.error("bean复制失败，参数source为：{}，targetClass为：{}" , source , targetClass);
			throw new BeanException("bean复制失败，参数不能为空，请检查");
		}
		if(source.isEmpty()){
			return Collections.emptyList();
		}
		List<E> result = new ArrayList<E>();
		for (T entity : source) {
			E dto = PojoUtils.copy(entity, targetClass);
			if (dto == null) {
				return null;
			}
			result.add(dto);
		}
		return result;
	}
}
