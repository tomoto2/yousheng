package com.joe.cluster;

import java.util.Map;
import java.util.TreeMap;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

/**
 * redis实现的分布式锁管理器
 * 
 * @author joe
 *
 */
public class RedisClusterLockManager implements ClusterLockManager {
	// 管理redis连接
	private static Map<String, RedissonClient> cache = new TreeMap<>();
	private static final Object lock = new Object();
	private RedissonClient redissonClient;

	private RedisClusterLockManager(RedissonClient redissonClient) {
		this.redissonClient = redissonClient;
	}

	/**
	 * 获取redis实现的分布式锁管理器
	 * 
	 * @param host
	 *            redis的主机地址，例如192.168.1.100
	 * @param port
	 *            redis的端口，例如8080
	 * @return redis实现的分布式锁管理器
	 */
	public static RedisClusterLockManager getInstance(String host, int port) {
		String add = host + port;
		if (!cache.containsKey(add)) {
			synchronized (lock) {
				if (!cache.containsKey(add)) {
					Config config = new Config();
					config.useSingleServer().setAddress(host + port);
					RedissonClient client = Redisson.create(config);
					cache.put(add, client);
				}
			}
		}
		return new RedisClusterLockManager(cache.get(add));
	}

	@Override
	public ClusterLock getLock(String name) {
		return new RedisClusterLock(redissonClient.getLock(name));
	}
}
