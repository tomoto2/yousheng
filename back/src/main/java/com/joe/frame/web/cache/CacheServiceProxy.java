package com.joe.frame.web.cache;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.joe.frame.web.prop.SystemProp;

/**
 * 缓存代理类
 * 
 * @author qiao9
 *
 */
@Component
public class CacheServiceProxy implements CacheService {
	private static Logger logger = LoggerFactory.getLogger(CacheServiceProxy.class);
	private CacheService virtualCache;

	public CacheServiceProxy(@Autowired SystemProp systemProp, @Autowired(required=false) EhcacheService ehcacheService,
			@Autowired(required=false) RedisCacheService redisCacheService) {
		if(ehcacheService == null && redisCacheService == null){
			throw new NullPointerException("没有可用cache");
		}
		if (CacheNaming.Redis.equalsIgnoreCase(systemProp.getCacheService())) {
			if(redisCacheService != null){
				this.virtualCache = redisCacheService;
			}else if(ehcacheService != null){
				logger.warn("未找到redisCacheService，使用ehcacheService");
				this.virtualCache = ehcacheService;
			}
		} else {
			if(ehcacheService != null){
				this.virtualCache = ehcacheService;
			}else if(redisCacheService != null){
				logger.warn("未找到ehcacheService，使用redisCacheService");
				this.virtualCache = redisCacheService;
			}
		}
	}

	public <T> void put(Serializable key, T value) {
		virtualCache.put(key, value);
	}

	public <T> void put(Serializable key, T value, int timeToLiveSeconds) {
		virtualCache.put(key, value, timeToLiveSeconds);
	}

	public <T> T get(Serializable key, Class<T> clazz) {
		return virtualCache.get(key, clazz);
	}

	public void remove(Serializable key) {
		virtualCache.remove(key);
	}
}
