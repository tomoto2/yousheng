package com.joe.frame.web.cache;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

/**
 * Ehcache缓存
 * @author joe
 *
 */
@Component("ehcacheService")
public class EhcacheService implements CacheService {
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private Ehcache ehcache;

	public <T> void put(Serializable key, T value) {
		putCache(key, value, -1);
	}

	public <T> void put(Serializable key, T value, int timeToLiveSeconds) {
		putCache(key, value, timeToLiveSeconds);
	}

	/**
	 * 向缓存中存放数据
	 * 
	 * @param key
	 *            缓存的key
	 * @param value
	 *            数据
	 * @param timeToLiveSeconds
	 *            过期时间（单位为秒，小于等于0表示不过期）
	 */
	private <T> void putCache(Serializable key, T value, int timeToLiveSeconds) {
		Element element = new Element(key, value);
		if (timeToLiveSeconds > 0) {
			element.setTimeToLive(timeToLiveSeconds);
		}
		ehcache.put(element);
	}

	@SuppressWarnings("unchecked")
	public <T> T get(Serializable key, Class<T> type) {
		if(key == null){
			return null;
		}
		Element element = ehcache.get(key);
		if (element != null) {
			return (T) element.getObjectValue();
		}
		return null;
	}

	public void remove(Serializable key) {
		if(key != null){
			ehcache.remove(key);
		}
	}
}
