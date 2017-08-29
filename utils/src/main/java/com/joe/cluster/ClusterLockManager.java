package com.joe.cluster;

/**
 * 分布式锁管理器
 * @author joe
 *
 */
public interface ClusterLockManager {
	/**
	 * 
	 * @param name
	 * @return
	 */
	ClusterLock getLock(String name);
}
