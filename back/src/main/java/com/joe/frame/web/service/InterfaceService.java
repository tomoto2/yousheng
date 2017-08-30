package com.joe.frame.web.service;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joe.frame.web.bean.InterfaceInfo;
import com.joe.frame.web.prop.SystemProp;

/**
 * 接口信息服务
 * 
 * @author Administrator
 *
 */
@Service
public class InterfaceService {
	private final Logger logger = LoggerFactory.getLogger(InterfaceService.class);
	/** 
	 * 当前正在处理的所有的请求数量
	 */
	private final AtomicLong nowRequestCount = new AtomicLong(0);
	/**
	 * 历史所有请求数量
	 */
	private final AtomicLong all = new AtomicLong(0);
	/**
	 * 请求历史记录，从开机到现在的请求历史（key对应接口，value对应请求信息）
	 */
	private final TreeMap<String, Deque<InterfaceInfo>> history = new TreeMap<String, Deque<InterfaceInfo>>();
	/**
	 * 各接口历史请求数量
	 */
	private final TreeMap<String, AtomicLong> historyCount = new TreeMap<String, AtomicLong>();
	/**
	 * 当前各个接口的请求数量
	 */
	private final TreeMap<String, AtomicInteger> notComplete = new TreeMap<String, AtomicInteger>();
	@Autowired
	private SystemProp prop;
	/**
	 * 最后一次请求消耗时间，单位为毫秒
	 */
	@SuppressWarnings("unused")
	private volatile int lastConsume;
	/**
	 * 最近几次请求的时间消耗
	 */
	private Queue<Integer> queue = new LinkedList<Integer>();
	/**
	 * 当前接口请求平均时间
	 */
	private int nowAverageTime = 0;
	/**
	 * 记录请求次数，当达到50时清0一次，nowAverageTime就重新就算一次
	 */
	private int flag = 0;
	/**
	 * 请求处理锁
	 */
	private Lock requestLock = new ReentrantLock();
	/**
	 * 响应处理锁
	 */
	private Lock responseLock = new ReentrantLock();
	// /**
	// * 当前请求等级，低于这个等级的请求将被抛弃，默认为LEVEL1
	// */
	// private volatile Level nowLevel = Level.LEVEL1;
	// /**
	// * 已经使用禁止的接口
	// */
	// private final TreeMap<String, String> ban = new TreeMap<String,
	// String>();

	/**
	 * 处理接口请求
	 * 
	 * @param info
	 */
	public void process(InterfaceInfo info) {
		// 接口是否完成请求
		if (info.isFinish()) {
			// 已经完成
			processResponse(info);
		} else {
			processRequest(info);
		}
	}

	/**
	 * 响应处理
	 * 
	 * @param info
	 */
	private void processResponse(InterfaceInfo info) {
		this.lastConsume = info.getConsumeTime();
		this.all.addAndGet(1);
		
		this.responseLock.lock();
		averageTime(info);
		logger.debug("开始处理响应..............");
		// 处理限流
		// limitProcess(info);
		AtomicLong count = this.historyCount.get(info.getRealRequestAddr());
		if (count == null) {
			count = new AtomicLong(0);
			this.historyCount.put(info.getRealRequestAddr(), count);
		}
		// 增加一条历史记录
		Deque<InterfaceInfo> list = this.history.get(info.getRealRequestAddr());
		if (list == null) {
			list = new LinkedList<InterfaceInfo>();
			this.history.put(info.getRealRequestAddr(), list);
		}
		list.offer(info);
		if (list.size() > this.prop.getMaxHistory()) {
			list.removeFirst();
		}
		// 未完成数减一
		this.responseLock.unlock();
		count.addAndGet(1);
		AtomicInteger num = this.notComplete.get(info.getRealRequestAddr());
		num.addAndGet(-1);
		this.nowRequestCount.addAndGet(-1);
	}

	/**
	 * 请求处理
	 * 
	 * @param info
	 */
	private void processRequest(InterfaceInfo info) {
		this.requestLock.lock();
		logger.info("开始处理请求...............");
		// 记录请求数
		AtomicInteger num = this.notComplete.get(info.getRealRequestAddr());
		if (num == null) {
			num = new AtomicInteger(0);
			this.notComplete.put(info.getRealRequestAddr(), num);
		}
		this.requestLock.unlock();
		num.addAndGet(1);
		this.nowRequestCount.addAndGet(1);
	}
	
	/**
	 * 接口请求平均时间计算
	 * 
	 * @param info
	 */
	public void averageTime(InterfaceInfo info) {
		int consume = info.getConsumeTime();
		this.queue.offer(consume);
		int size = this.queue.size();
		this.flag ++;
		if(this.flag >= 50){
			this.flag = 0;
			this.nowAverageTime = 0;
			for(int i : this.queue){
				this.nowAverageTime += i;
			}
			this.nowAverageTime = this.nowAverageTime / size;
		}else{
			this.nowAverageTime = (this.nowAverageTime * (size - 1) + consume) / size;
		}
		
		int count = prop.getCount();
		if (size > count) {
			this.queue.poll();
		}
	}
	
	/**
	 * 获取接口最近几次的请求响应时间
	 * @return
	 */
	public Queue<Integer> getAllConsumeTime(){
		return this.queue;
	}
	
	/**
	 * 获取接口最近几次请求平均时间
	 * @return
	 */
	public int getAverageTime(){
		return this.nowAverageTime;
	}

	/**
	 * 获取所有接口的历史请求数量和
	 * 
	 * @return
	 */
	public long getAllHistoryCount() {
		return this.all.get();
	}

	/**
	 * 获取各个接口的请求数量
	 * 
	 * @return
	 */
	public TreeMap<String, AtomicLong> getHistoryCount() {
		return this.historyCount;
	}

	/**
	 * 获取当前正在处理的请求总数
	 * 
	 * @return
	 */
	public long getAllActive() {
		return this.nowRequestCount.get();
	}

	/**
	 * 获取当前正在处理的各个接口的请求数量
	 * 
	 * @return
	 */
	public TreeMap<String, AtomicInteger> getActiveInterface() {
		return this.notComplete;
	}

	/**
	 * 获取各个接口的请求历史记录
	 * 
	 * @return
	 */
	public TreeMap<String, Deque<InterfaceInfo>> getHistory() {
		return this.history;
	}

	// /**
	// * 判断当前接口是否允许请求
	// *
	// * @param resourceMethod
	// * 当前要访问的资源方法
	// * @param resourceClass
	// * 当前要访问的资源类
	// * @return
	// */
	// public boolean isAllow(Method resourceMethod, Class<?> resourceClass) {
	// if (!prop.isAllowFilterRequest())
	// return true;
	// Level resourceLevel = null;
	// if (resourceMethod.isAnnotationPresent(Limit.class)) {
	// resourceLevel = resourceMethod.getAnnotation(Limit.class).level();
	// } else if (resourceMethod.isAnnotationPresent(Limit.class)) {
	// resourceLevel = resourceMethod.getAnnotation(Limit.class).level();
	// } else {
	// resourceLevel = Level.DEFAULT;
	// }
	// logger.info("当前接口等级为：{}，当前允许访问的接口等级为：{}", resourceLevel, nowLevel);
	// if (!resourceLevel.higher(nowLevel)) {
	// logger.info("当前接口等级不允许访问");
	// return false;
	// }
	//
	// String resource = getResourcePath(resourceMethod, resourceClass);
	// return !ban.containsKey(resource);
	// }

	// /**
	// * 禁止使用某个接口
	// *
	// * @param interfaceName
	// * 接口名称，除去根路径的全部名称，如果接口名称中有PathParam，应该填写PathParam的表达式而不是实际地址，
	// * 例如应该填写delete/{user}而不是/delete/qiao
	// */
	// public void ban(String interfaceName) {
	// ban.put(interfaceName, null);
	// }
	//
	// /**
	// * 解除某个接口的限制
	// *
	// * @param interfaceName
	// * 接口名称，除去根路径的全部名称，如果接口名称中有PathParam，应该填写PathParam的表达式而不是实际地址，
	// * 例如应该填写delete/{user}而不是/delete/qiao
	// */
	// public void lift(String interfaceName) {
	// ban.remove(interfaceName);
	// }

	// /**
	// * 获取资源的路径
	// *
	// * @param resourceMethod
	// * 访问资源的方法
	// * @param resourceClass
	// * 资源所属的类
	// * @return 资源的路径，如果路径中有参数（PathParam），那么将该值替换为\S+。<br/>
	// * 示例：
	// * <li>http://localhost/项目根路径/root/test -> root/test/</li>
	// * <li>http://localhost/项目根路径/root/{user} -> root/\S+/</li>
	// */
	// public String getResourcePath(Method resourceMethod, Class<?>
	// resourceClass) {
	// StringBuilder sb = new StringBuilder();
	// if (resourceClass.isAnnotationPresent(Path.class)) {
	// processPath(sb, resourceClass.getAnnotation(Path.class).value());
	// }
	//
	// if (resourceMethod.isAnnotationPresent(Path.class)) {
	// processPath(sb, resourceMethod.getAnnotation(Path.class).value());
	// }
	//
	// return sb.toString();
	// }

	//
	// private StringBuilder processPath(StringBuilder sb, String path) {
	// if ("".equals(path.trim())) {
	// return sb;
	// }
	// String[] paths = path.split("/");
	// for (String str : paths) {
	// if ("".equals(str.trim())) {
	// continue;
	// }
	// if (str.startsWith("{") && str.endsWith("}")) {
	// sb.append("\\S+/");
	// } else {
	// sb.append(str).append("/");
	// }
	// }
	// return sb;
	// }
}
