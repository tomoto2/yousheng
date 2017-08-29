package com.joe.collection;

import java.util.concurrent.ConcurrentHashMap;

import com.joe.cache.CacheService;

/**
 * 遍历安全的可缓存map
 * 
 * 
 * @author joe
 *
 * @param <K>
 *            key的类型，不能为接口或者抽象类
 * @param <V>
 *            value的类型，不能为接口或者抽象类
 */
public class ErgodicSafeMap<K, V> extends CacheableMap<K, V> {

	/**
	 * 构建默认的缓存map（TreeMap）
	 * 
	 * @param name
	 *            map在缓存中的名字（key）
	 * @param cacheService
	 *            缓存服务
	 */
	public ErgodicSafeMap(String name, CacheService cacheService, Class<K> keyClass, Class<V> valueClass) {
		super(name, cacheService, ConcurrentHashMap.class, keyClass, valueClass);
	}
}
