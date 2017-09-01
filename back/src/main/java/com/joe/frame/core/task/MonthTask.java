package com.joe.frame.core.task;

import java.text.ParseException;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.joe.frame.common.util.DateUtil;
import com.joe.frame.core.service.ShareService;

/**
 * 按月定时计算推广奖金
 * 
 * @author lpx
 *
 */
@Component
public class MonthTask {
	private static final Logger logger = LoggerFactory.getLogger(MonthTask.class);
	@Autowired
	private DateUtil dateUtil;
	@Autowired
	private ShareService shareService;
	private static ReentrantLock lock = new ReentrantLock();

	/**
	 * 每月一次，计算分红,月初计算上一个月的
	 * @throws ParseException 
	 */

	//* *  *  *  *  *
	//秒 分    时      日     月     周
	//	@Scheduled(cron = "0 0 23 31 * *")//每月最后一日的23:59触发、
	//	@Scheduled(cron = "0 0 23 L * ?")//每月最后一日的23:59触发
	//	@Scheduled(cron = "0 0 1 1 * ?")// 每月1号凌晨1点执行一次：
	//	@Scheduled(fixedDelay=800 * 1000)
	
//	@Scheduled(cron = "0 10 6 1 * ?")// 每月1日上午6:10触发
	@Scheduled(cron = "0 25 13 1 * ?")// 每月1日上午6:15触发
	public void cacl() throws ParseException{
		lock.lock();
		logger.info("开始计算分红");
		String now = dateUtil.getFormatDate("yyyy-MM-dd HH:mm:ss");
		String pre = dateUtil.getPreMonth(now, "yyyy-MM-dd HH:mm:ss");//本月1号执行上月分红
		logger.info("开始计算{}的分红",pre);
		try {
			shareService.getShare(pre);
		} catch (java.text.ParseException e) {
			logger.error("每月分红计算失败",e);
		}
		logger.info("计算{}的分红结束",pre);
		lock.unlock();
	}

	
	@Scheduled(cron = "0 */1 * * * ?")//每分钟
	public void run(){
 		System.out.println("执行开始.....................................");
		
		String now = dateUtil.getFormatDate("yyyy-MM-dd HH:mm:ss");
		logger.info("计算{}的分红结束",now);
		
		String now1 = dateUtil.getFormatDate("yyyy-MM-dd HH:mm:ss");
		logger.info("计算{}的分红结束",now1);
		
		
		String now2 = dateUtil.getFormatDate("yyyy-MM-dd HH:mm:ss");
		logger.info("计算{}的分红结束",now2);
		
		
		String now3 = dateUtil.getFormatDate("yyyy-MM-dd HH:mm:ss");
		logger.info("计算{}的分红结束",now3);
		
		
		System.out.println("执行完毕.....................................");
	}
	
	
	
	
	
	
}
