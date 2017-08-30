package com.joe.frame.core.service;

import java.text.ParseException;
import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joe.frame.common.util.DateUtil;
import com.joe.frame.common.util.PojoUtils;
import com.joe.frame.common.util.Tools;
import com.joe.frame.core.dto.FenliDTO;
import com.joe.frame.core.dto.MonthDTO;
import com.joe.frame.core.entity.Proxy;
import com.joe.frame.core.entity.Share;
import com.joe.frame.core.repository.ProxyRepository;
import com.joe.frame.core.repository.ShareRepository;

/**
 * 
 *  计算所有用户的每月分红
 * 
 * @author lpx
 *
 * 2017年7月26日
 */
@Service
@Transactional
public class ShareService {
	private static final Logger logger = LoggerFactory.getLogger(ShareService.class);

	@Autowired
	private ProxyRepository ProxyRepository;
	@Autowired
	private ShareRepository shareRepository; 
	@Autowired
	private Tools tools;
	@Autowired
	private AgentService agentService;
	@Autowired
	private DateUtil dateUtil;

	/**
	 * 计算分红
	 * @param now yyyy-MM-dd 分红计算时间
	 * @throws ParseException 
	 */
	public void getShare(String now) throws ParseException{
		logger.info("开始计算{}的分红", now);
		List<Proxy> list  = ProxyRepository.findAll();//查询出来所有代理
		for (Proxy proxy : list) {
			giveShare(proxy, now);
		}
		logger.info("本月的分红计算完毕");
	}

	/**
	 * 计算指定用户的分红
	 * 分红给指定用户（保存该分红同时将该用户的资金账户做相应处理）
	 * 
	 * @param proxy
	 *            指定用户
	 * @param now
	 *            分红计算日期，格式：yyyy-MM-dd HH:mm:ss
	 * @throws ParseException 
	 */
	private void giveShare(Proxy proxy, String now) throws ParseException {
		logger.info("开始给用户{}分红", proxy.getPid());
		String now1 = now.substring(0,7);
		MonthDTO montshare= agentService.getMonthShares(proxy.getPid(),now1);//日期格式：yyyy-MM
		Share share = initShare(montshare,now);
		logger.info("初始化share信息",share);
		share.setDate(now);//此时的日期格式是yyyy-MM-dd HH:mm:ss
		share.setUid(proxy.getPid());//获得分红的用户
		shareRepository.merge(share);
		// 用户自己的分红
		proxy.setBalance(proxy.getBalance() + montshare.getShijiFanli()*100);
		logger.info("用户{}在{}时间，分红{}元", proxy.getPid(),now,montshare.getShijiFanli());
		return;
	}

	/**
	 * 初始化一个分红
	 * 
	 * @param stock
	 *            分红来源股份
	 * @return 分红对象，需要自己设置用户ID、分红金额和日期
	 */
	private Share initShare(MonthDTO shareCount,String now) {
		Share share = new Share();
		share.setId(tools.createUUID());
//		share.setDate(now);
		share.setAllCount(share.getAllCount() + shareCount.getShijiFanli()*100);//历来获得的所有分红金额
		share.setCount( shareCount.getShijiFanli()*100);//本月分红金额(实际返利)
		share.setAllMoney(shareCount.getAllMoney()*100);
		share.setQixiaAllMoney(shareCount.getQixiaAllMoney()*100);
		share.setAllYeji(shareCount.getAllYeji()*100);
		share.setFanliBi(shareCount.getFanliBi());
		share.setAllFanli(shareCount.getAllFanli()*100);
		share.setQixiaAllFanli(shareCount.getQixiaAllFanli()*100);
		share.setShijiFanli(shareCount.getShijiFanli()*100);
		logger.info("初始化share信息",share);
//		shareRepository.merge(share);
		return share;
	}


	/**
	 * 获取代理某月返利详情
	 * @param uid 代理id
	 * @param nowMonth 某月 格式:yyyy-MM
	 */
	public Share getMonthShare(String uid, String nowMonth) {
		Share share = shareRepository.getMonthShare(uid, nowMonth);
		logger.info("{}的{}月返利{}分",uid,nowMonth,share);
		return share;
	}


	
	/**
	 * 获取代理本月返利详情
	 * @param uid 代理id
	 * @param nowMonth 本月 格式:yyyy-MM
	 */
	public FenliDTO getMonthShare1 (String uid, String nowMonth){
		Share share = shareRepository.getMonthShare(uid, nowMonth);
		logger.info("{}的{}月返利{}",uid,share);
		FenliDTO dto = PojoUtils.copy(share, FenliDTO.class);
		return dto;
	}

}
