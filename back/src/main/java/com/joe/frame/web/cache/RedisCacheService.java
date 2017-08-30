package com.joe.frame.web.cache;


import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Component;

/**
 * redis缓存(redisCacheService与已有的spring中的bean冲突，所以需要改名)
 * @author joe
 *
 */
@Component("redisService")
public class RedisCacheService  implements CacheService{
	@Resource(name = "redisTemplate")
	private ValueOperations<Serializable, byte[]> valueOps;
	@Resource(name = "redisTemplate")
	private RedisOperations<Serializable, byte[]> redisOps;

	public <T> void put(Serializable key, T value) {
		putCache(key, value , -1);
	}
	
	public <T> void put(Serializable key, T value , int timeToLiveSeconds) {
		putCache(key, value , timeToLiveSeconds);
	}
	
	/**
	 * 向缓存中存放数据
	 * @param key
	 * 缓存的key
	 * @param value
	 * 数据
	 * @param timeToLiveSeconds
	 * 过期时间（单位为秒，小于等于0表示不过期）
	 */
	@SuppressWarnings("unchecked")
	public <T> void putCache(Serializable key, T value , int timeToLiveSeconds) {
		Class<T> clazz = (Class<T>) value.getClass();
		Jackson2JsonRedisSerializer<T> serializer = new Jackson2JsonRedisSerializer<T>(clazz);
		byte[] data = serializer.serialize(value);
		if(timeToLiveSeconds > 0){
			valueOps.set(key, data , timeToLiveSeconds , TimeUnit.SECONDS);
		}else{
			valueOps.set(key, data);
		}
	}
	

	public <T> T get(Serializable key, Class<T> type) {
		if(key == null){
			return null;
		}
		Jackson2JsonRedisSerializer<T> serializer = new Jackson2JsonRedisSerializer<T>(type);
		byte[] data = valueOps.get(key);
		T t = serializer.deserialize(data);
		return t;
	}
	
	public void remove(Serializable key){
		if(key != null){
			redisOps.delete(key);
		}
	}
}
