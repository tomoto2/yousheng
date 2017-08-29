package com.joe.cache;

import java.util.Map;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;

public class RedisCacheService {
	public static void main(String[] args) {
		Config config = new Config();
		SingleServerConfig singleServerConfig = config.useSingleServer();
		singleServerConfig.setAddress("115.28.179.228:9997");

		RedissonClient redisson = Redisson.create(config);
		Map<String , String> map1= redisson.getMap("aa");
		System.out.println(map1.get("1"));
//		System.out.println("0");
//		
//		new Thread(() -> {
//			System.out.println("lock1");
//			RLock lock1 = redisson.getLock("myLock");
//			lock1.lock(5 , TimeUnit.SECONDS);
//			System.out.println("lock1");
//		}).start();
//		new Thread(() -> {
//			System.out.println("lock2");
//			RLock lock2 = redisson.getLock("myLock");
//			lock2.lock(1 , TimeUnit.SECONDS);
//			System.out.println("lock2");
//		}).start();
	}
}
