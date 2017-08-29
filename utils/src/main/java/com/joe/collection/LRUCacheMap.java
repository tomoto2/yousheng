package com.joe.collection;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 简单的LRU实现（如果map的size超过初始化时指定的大小，那么将会清除最少使用或者最早放入的。
 * 注：首先根据使用次数清除，使用次数相同的根据放入顺序清除）
 * 
 * @author joe
 *
 * @param <K>
 * @param <V>
 */
public class LRUCacheMap<K, V> extends LinkedHashMap<K, V> {
	private static final long serialVersionUID = -1576619020580779936L;
	private int max;

	LRUCacheMap(Map<? extends K, ? extends V> m) {
		super(m);
	}

	LRUCacheMap(int initialCapacity, float loadFactor, boolean accessOrder) {
		super(initialCapacity, loadFactor, accessOrder);
	}

	public LRUCacheMap() {
		this(1000);
	}

	public LRUCacheMap(int initialCapacity) {
		this(initialCapacity, 0.75f);
	}

	public LRUCacheMap(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor, true);
		this.max = initialCapacity;
	}

	@Override
	protected boolean removeEldestEntry(java.util.Map.Entry<K, V> eldest) {
		return size() > this.max;
	}
}
