package com.joe.type;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 基本java类型
 * @author joe
 *
 */
@Getter
@Setter
@ToString
public class BaseType extends JavaType{
	/*
	 * 该类型的基本类型
	 */
	private Class<?> type;
	/*
	 * 该类型的泛型，如果没有则为null
	 */
	private JavaType[] generics;
	/*
	 * 该类型包含的类型（当该类型不是java自带类型及其子类时该参数有值，例如用户创建的User类不属于java自带类型），其中key为参数名，value为参数类型
	 */
	private Map<String , JavaType> includes;
}
