package com.joe.parse.xml;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * CDATA标签，可以注解在字段或者字段的get方法上，如果字段和get方法同时注解那么优先读取字段的注解
 * 
 * @author Administrator
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
@Documented
public @interface XmlNode {
	/**
	 * 标记该字段是否用CDATA包裹
	 * 
	 * @return 该字段需要CDATA则返回true
	 */
	boolean isCDATA() default true;

	/**
	 * 该节点的名称，默认为字段的名称
	 * 
	 * @return
	 */
	String name() default "";

	/**
	 * 忽略该字段
	 * 
	 * @return 如果为true则为忽略
	 */
	boolean ignore() default false;
}
