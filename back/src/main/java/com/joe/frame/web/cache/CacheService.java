package com.joe.frame.web.cache;

import java.io.Serializable;

/**
 * 缓存接口
 * 
 * @author joe
 *
 */
public interface CacheService {
	/**
	 * 向缓存中存放数据
	 * 
	 * @param key
	 *            缓存的key
	 * @param value
	 *            数据
	 */
	<T> void put(Serializable key, T value);

	/**
	 * 向缓存中存放数据
	 * 
	 * @param key
	 *            缓存的key
	 * @param value
	 *            数据
	 * @param timeToLiveSeconds
	 *            缓存有效时间（单位为秒，当值小于等于0时缓存不过期）
	 */
	<T> void put(Serializable key, T value, int timeToLiveSeconds);

	/**
	 * 从缓存中取出数据
	 * 
	 * @param key
	 *            缓存的key
	 * @param clazz
	 *            要取出的数据的类型
	 * @return
	 */
	<T> T get(Serializable key, Class<T> clazz);

	/**
	 * 从缓存中删除
	 * 
	 * @param key
	 *            缓存的key
	 */
	void remove(Serializable key);
}
