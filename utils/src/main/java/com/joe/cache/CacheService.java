package com.joe.cache;

import java.io.Serializable;
import java.util.Map;

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
	 * 往缓存中放入数据
	 * 
	 * @param key
	 *            缓存的key
	 * @param value
	 *            缓存的value
	 * @return 如果缓存中不存在该key那么将指定键值对放入并返回true，否则不会将指定键值对放入并且返回false
	 */
	<T> boolean putIfAbsent(Serializable key, T value);

	/**
	 * 刷新缓存
	 * 
	 * @param key
	 *            要刷新的缓存的key
	 * @param value
	 *            要刷新的缓存的value
	 */
	<T> void refresh(Serializable key, T value);

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
	 * 从缓存中取出带一层简单泛型的数据
	 * 
	 * @param key
	 * @param baseClass
	 * @param keyClass
	 * @param valueClass
	 * @return
	 */
	<K, V, M extends Map<K, V>> M getMap(String key, Class<M> baseClass, Class<K> keyClass, Class<V> valueClass);

	/**
	 * 从缓存中删除
	 * 
	 * @param key
	 *            缓存的key
	 */
	void remove(Serializable key);
}
