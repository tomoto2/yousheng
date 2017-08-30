package com.joe.frame.pay.common.service;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joe.frame.core.entity.Recharge;
import com.joe.frame.core.repository.RechargeRepository;
import com.joe.frame.pay.wechatpay.entity.WechatOrder;

/**
 * 订单支持服务
 * 
 * @author joe
 *
 */
@Service
@Transactional
public class SupportService {
	private static final Logger logger = LoggerFactory.getLogger(SupportService.class);
//	@Autowired
//	private SysOrderRespository sysOrderRespository;
//	@Autowired
//	private GoodsRepository goodsRepository;
//	@Autowired
//	private OrderGoodsRepository orderGoodsRepository;
//	@Autowired
//	private ApriceDaoImpl apriceDao;

	@Autowired
	private RechargeRepository rechargeRepository;

	/**
	 * 将微信订单补全
	 * 
	 * @param wechatOrder
	 *            微信订单
	 */
	public void addWechatInfo(WechatOrder wechatOrder) {
		logger.debug("开始补全微信订单信息，需要补全的信息为：{}	", wechatOrder);
		Recharge order = rechargeRepository.find(wechatOrder.getOut_trade_no());
		logger.debug("系统中的订单为：{}", order);
		// 设置金额，单位为分
		wechatOrder.setTotal_fee((int) order.getMoney());
		wechatOrder.setSettlement_total_fee((int) order.getMoney());
//		wechatOrder.setTotal_fee(1);
//		wechatOrder.setSettlement_total_fee(1);
		wechatOrder.setBody("名门互娱-套餐购买");
		logger.debug("信息补全之后为：{}", wechatOrder);
	}

	/**
	 * 补充商品信息（系统订单），只补全价格信息
	 * 
	 * @param orderGoods
	 *            商品
	 */
//	public void addInfo(OrderGoods orderGoods) {
//		logger.debug("开始完善商品信息{}", orderGoods);
//		Goods goods = goodsRepository.find(orderGoods.getGoodsId());
//		logger.debug("数据库中的商品为：{}", goods);
//		orderGoods.setPrice(goods.getPrice());
//		if (goods.getGoodsType().equals(GoodsType.SERVICE)) {
//			String beginTime = orderGoods.getBeginTime();
//			String endTime = orderGoods.getEndTime();
//			String format = "yyyy-MM-dd HH:mm:ss";
//			//安卓凌晨传过来的时间有可能是2017-05-13 1:00::00，所以要用这个时间格式
//			String errFormat = "yyyy-MM-dd H:mm:ss";
//			int time = 0;
//			try {
//				logger.debug("开始计算服务时长，开始时间是：{}；结束时间是：{}", beginTime, endTime);
//				long begin = 0;
//				long end = 0;
//				try {
//					begin = DateUtil.parse(beginTime, format).getTime();
//					end = DateUtil.parse(endTime, format).getTime();
//				} catch (Exception e) {
//					logger.warn("前台是在凌晨下的单，格式可能是{}，用这种格式重试");
//					begin = DateUtil.parse(beginTime, errFormat).getTime();
//					end = DateUtil.parse(endTime, errFormat).getTime();
//				}
//				time = (int) ((end - begin) / (1000 * 60 * 30));
//
//				String apriceId = goods.getApriceId();
//				if (apriceId != null && apriceId.length() > 0) {
//					Aprice aprice = apriceDao.find(apriceId);
//					if (aprice.getProjects_projects_id().equals("6")) {
//						time = 2;
//					}
//				}
//
//				logger.debug("用户购买服务{}个小时", time / 2);
//			} catch (Exception e) {
//				logger.debug("计算服务时长出错", e);
//			}
//			orderGoods.setRealPrice(goods.getPrice() * time / 2);
//		} else {
//			orderGoods.setRealPrice(goods.getPrice());
//		}
//		logger.debug("商品信息完善后是：{}", orderGoods);
//	}
}
