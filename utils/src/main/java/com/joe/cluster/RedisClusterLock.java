package com.joe.cluster;

import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;

/**
 * redisson实现的分布式锁
 * 
 * @author joe
 *
 */
public class RedisClusterLock implements ClusterLock {
	private RLock lock;

	RedisClusterLock(RLock lock) {
		this.lock = lock;
	}

	@Override
	public void lock() {
		lock(20, TimeUnit.MINUTES);
	}

	@Override
	public void lock(long leaseTime, TimeUnit unit) {
		lock.lock(leaseTime, unit);
	}

	@Override
	public boolean tryLock(long waitTime, long leaseTime, TimeUnit unit) throws InterruptedException {
		return lock.tryLock(waitTime, leaseTime, unit);
	}

	@Override
	public void unlock() {
		lock.unlock();
	}

	@Override
	public boolean isLocked() {
		return lock.isLocked();
	}
}
