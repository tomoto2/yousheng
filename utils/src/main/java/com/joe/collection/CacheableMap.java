package com.joe.collection;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.joe.cache.CacheService;

/**
 * 可缓存的map，可自动缓存，默认是TreeMap实例（不能强转操作，否则可能与缓存不同步，目前只对put、putAll、remove方法进行了同步）
 * 泛型只能为一层简单泛型，多层泛型可能会出错，非线程安全
 * 
 * @author joe
 *
 * @param <K>
 *            map的key的类型
 * @param <V>
 *            map的value的类型
 */
public class CacheableMap<K, V> implements Map<K, V> {
	private static final Logger logger = LoggerFactory.getLogger(CacheableMap.class);
	// 缓存服务
	private CacheService cacheService;
	// map容器
	protected final Map<K, V> container;
	// map在缓存中的名字（缓存的key）
	private String name;

	/**
	 * 构建默认的缓存map（TreeMap）
	 * 
	 * @param name
	 *            map在缓存中的名字（key）
	 * @param cacheService
	 *            缓存服务
	 */
	public CacheableMap(String name, CacheService cacheService, Class<K> keyClass, Class<V> valueClass) {
		this(name, cacheService, TreeMap.class, keyClass, valueClass);
	}

	/**
	 * 
	 * @param name
	 *            map在缓存中的名字（key）
	 * @param cacheService
	 *            缓存服务
	 * @param baseClass
	 *            要构建的map的类型
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public CacheableMap(String name, CacheService cacheService, Class<? extends Map> baseClass, Class<K> keyClass,
			Class<V> valueClass) {
		this.name = name;
		if (cacheService == null) {
			logger.debug("未设置缓存服务代理，map将不会被缓存");
			this.cacheService = new CacheService() {
				@Override
				public void remove(Serializable key) {
				}

				@Override
				public <T> void refresh(Serializable key, T value) {
				}

				@Override
				public <T> void put(Serializable key, T value, int timeToLiveSeconds) {
				}

				@Override
				public <T> void put(Serializable key, T value) {
				}

				@Override
				public <Key, Value, M extends Map<Key, Value>> M getMap(String key, Class<M> baseClass,
						Class<Key> keyClass, Class<Value> valueClass) {
					return null;
				}

				@Override
				public <T> T get(Serializable key, Class<T> clazz) {
					return null;
				}

				@Override
				public <T> boolean putIfAbsent(Serializable key, T value) {
					return false;
				}
			};
		} else {
			this.cacheService = cacheService;
		}

		Map<K, V> map = cacheService.getMap(name, baseClass, keyClass, valueClass);
		if (map == null) {
			try {
				map = baseClass.newInstance();
				cacheService.put(name, map);
			} catch (InstantiationException | IllegalAccessException e) {
				// 创建异常
				throw new RuntimeException("Map类对象为：" + baseClass + "；创建该类的实例时出错", e);
			}
		}
		this.container = map;
	}

	@Override
	public int size() {
		return container.size();
	}

	@Override
	public boolean isEmpty() {
		return container.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return container.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return container.containsValue(value);
	}

	@Override
	public V get(Object key) {
		return container.get(key);
	}

	@Override
	public V put(K key, V value) {
		container.put(key, value);
		cacheService.refresh(name, container);
		return value;
	}

	@Override
	public V remove(Object key) {
		V v = container.remove(key);
		cacheService.refresh(name, container);
		return v;
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		container.putAll(m);
		cacheService.refresh(name, container);
	}

	@Override
	public void clear() {
		container.clear();
	}

	@Override
	public Set<K> keySet() {
		return container.keySet();
	}

	@Override
	public Collection<V> values() {
		return container.values();
	}

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		return container.entrySet();
	}

	/**
	 * 将数据从缓存中移除，但是不清空本地数据
	 */
	public void removeFromCache() {
		cacheService.remove(name);
	}
}