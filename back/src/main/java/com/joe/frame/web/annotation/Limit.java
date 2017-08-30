package com.joe.frame.web.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.joe.frame.web.bean.Level;

/**
 * 接口限制（方法上的注解将覆盖类上边的注解）
 * @author Administrator
 *
 */
@Documented
@Retention(RUNTIME)
@Target({ElementType.TYPE , ElementType.METHOD})
public @interface Limit {
	/**
	 * 接口等级
	 * @return
	 */
	Level level() default Level.LEVEL5;
}
