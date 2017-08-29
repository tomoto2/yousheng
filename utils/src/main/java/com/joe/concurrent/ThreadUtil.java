package com.joe.concurrent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 线程操作类
 *
 * @author joe
 */
public class ThreadUtil {
	private static final Logger logger = LoggerFactory.getLogger(ThreadUtil.class);
	private static final Map<PoolType, ExecutorService> cache = new HashMap<>();

	/**
	 * 当前线程睡眠一段时间
	 *
	 * @param time
	 *            时长
	 * @param unit
	 *            单位
	 */
	public static void sleep(long time, TimeUnit unit) {
		try {
			if (time <= 0 || unit == null) {
				return;
			}

			Thread.sleep(unit.toMillis(time));
		} catch (InterruptedException e) {
			logger.warn("时间参数不正确，线程将不会睡眠");
		}
	}

	/**
	 * 当前线程睡眠一段时间（单位为秒）
	 *
	 * @param time
	 *            时长
	 */
	public static void sleep(long time) {
		sleep(time, TimeUnit.SECONDS);
	}

	/**
	 * 从缓存中查找指定类型的线程池，如果存在那么直接返回，如果不存在那么创建返回
	 *
	 * @param type
	 *            线程池类型
	 * @return 指定类型的线程池
	 */
	public static ExecutorService getOrCreatePool(PoolType type) {
		ExecutorService service = cache.get(type);
		if (service == null || service.isTerminated() || service.isShutdown()) {
			synchronized (cache) {
				if (service == null || service.isTerminated() || service.isShutdown()) {
					switch (type) {
					case Singleton:
						service = Executors.newSingleThreadExecutor();
					case IO:
						service = new ThreadPoolExecutor(30, 100, 30, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
					case Calc:
						service = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
					}
				}
				cache.put(type, service);
			}
		}
		return service;
	}

	/**
	 * 创建指定类型的线程池
	 *
	 * @param type
	 *            线程池类型
	 * @return 返回指定类型的线程池
	 */
	public static ExecutorService createPool(PoolType type) {
		ExecutorService service = null;
		switch (type) {
		case Singleton:
			service = Executors.newSingleThreadExecutor();
			break;
		case IO:
			service = new ThreadPoolExecutor(Math.max(Runtime.getRuntime().availableProcessors() * 50, 80),
					Math.max(Runtime.getRuntime().availableProcessors() * 150, 260), 30, TimeUnit.SECONDS,
					new LinkedBlockingQueue<>());
			break;
		case Calc:
			service = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
			break;
		default:
			throw new IllegalArgumentException(String.format("当前参数为：%s；请使用正确的参数", type.toString()));
		}
		return service;
	}

	public static enum PoolType {
		Singleton, IO, Calc
	}
}
