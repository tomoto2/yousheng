package com.joe.frame.pay.common.service;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.stereotype.Service;

/**
 * 锁服务，为全局业务提供锁服务，该类所有方法都是线程安全的
 * 
 * @author joe
 *
 */
@Service
public class LockService {
	private static final Map<String, CustomLock> container = new TreeMap<String, CustomLock>();
	// 销毁锁与获取锁（从缓存获取现有的锁）时需要加锁
	private static final Lock destroyLock = new ReentrantLock();

	/**
	 * 根据锁名字获取指定锁（最后必须通过unlockAndDestroy方法释放并销毁锁）
	 * 
	 * @param key
	 *            锁的名字
	 * @return 对应的锁，如果没有则创建
	 */
	public Lock getLock(String key) {
		try {
			// 加上销毁锁，防止获取出来锁后锁被销毁
			destroyLock.lock();

			// 从缓存获取
			CustomLock lock = container.get(key);
			// 因为此时有锁，所以不会出现并发情况
			if (lock == null) {
				// 缓存中不存在指定锁，创建
				lock = new CustomLock();
				container.put(key, lock);
			} else {
				// 缓存中有锁，将锁的引用加1
				lock.add();
			}
			return lock;
		} finally {
			// 释放销毁锁
			destroyLock.unlock();
		}
	}

	/**
	 * 将指定的锁立即解锁并移除（如果能移除的话）
	 * 
	 * @param key
	 */
	public void unlockAndDestroy(String key) {
		try {
			destroyLock.lock();
			CustomLock lock = container.get(key);
			if (lock.destroyable()) {
				// 锁可以销毁，将该锁移除
				container.remove(key);
			}
			// 解锁
			lock.unlock();
		} finally {
			destroyLock.unlock();
		}
	}

	/**
	 * 自定义锁，加上了一个引用计数
	 * 
	 * @author joe
	 *
	 */
	public static class CustomLock implements Lock {
		// 锁
		private ReentrantLock lock;
		// 锁获取的引用计数，当前有几个地方持有该锁
		private AtomicInteger count;

		public CustomLock() {
			lock = new ReentrantLock();
			count = new AtomicInteger(1);
		}

		@Override
		public void lock() {
			lock.lock();
		}

		@Override
		public void unlock() {
			lock.unlock();
		}

		/**
		 * 将锁的引用加1
		 */
		public void add() {
			count.addAndGet(1);
		}

		/**
		 * 获取当前锁是否可销毁（当锁的引用计数为0时可以销毁）
		 * 
		 * @return
		 *         <li>true：可以销毁</li>
		 *         <li>false：不可以销毁</li>
		 */
		public boolean destroyable() {
			if (count.addAndGet(-1) <= 0) {
				return true && lock.getHoldCount() == 0;
			} else {
				return false;
			}
		}

		@Override
		public void lockInterruptibly() throws InterruptedException {
			lock.lockInterruptibly();
		}

		@Override
		public boolean tryLock() {
			return lock.tryLock();
		}

		@Override
		public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
			return lock.tryLock(time, unit);
		}

		@Override
		public Condition newCondition() {
			return lock.newCondition();
		}
	}
}
