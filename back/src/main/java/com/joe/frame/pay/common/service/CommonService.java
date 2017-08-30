//package com.joe.frame.pay.common.service;
//
//import javax.transaction.Transactional;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import com.joe.frame.pay.alipay.service.AlipayService;
//import com.joe.frame.pay.wechatpay.service.WechatOrderService;
//import com.joe.utils.DateUtil;
//
///**
// * 支付通用service
// * 
// * @author joe
// *
// */
//@Service
//@Transactional
//public class CommonService {
//	private static final Logger logger = LoggerFactory.getLogger(CommonService.class);
//	@Autowired
//	private AlipayService alipayService;
//	@Autowired
//	private WechatOrderService wechatOrderService;
//	@Autowired
//	private WalletService walletService;
//	@Autowired
//	private UserCouponRepository userCouponRepository;
//	@Autowired
//	private RongyunDaoImpl rongyunDao;
//	@Autowired
//	private SysOrderRespository orderRespository;
////	
//	/**
//	 * 订单退款
//	 * 
//	 * @param order
//	 *            要退款的订单
//	 * @return 退款状态，true为退款成功
//	 */
//	public boolean gateway(SysOrder order) {
//		String couponId = order.getCouponId();
//		if (couponId != null) {
//			logger.debug("优惠券ID{}", couponId);
//			UserCoupon coupon = userCouponRepository.find(couponId);
//			if (coupon != null) {
//				coupon.setState(1);
//			}
//		}
//		Anchor anchor = orderRespository.getAnchor(order.getId());
//		if (anchor != null) {
//			Rongyun rongyun = rongyunDao.findRongYun(order.getUid(), anchor.getAnchorId(),
//					DateUtil.getFormatDate("yyyy-MM-dd"));
//			if (rongyun != null) {
//				logger.debug("查询出来的权限是{}", rongyun.toString());
//				rongyunDao.remove(rongyun);
//				logger.debug("删除实体{}", rongyun.toString());
//			}
//		}
//		
//		if (order.getWay().equalsIgnoreCase("wechat")) {
//			logger.debug("微信订单{}进行退款", order.getId());
//			return wechatOrderService.gateway(order);
//		} else if (order.getWay().equalsIgnoreCase("alipay")) {
//			logger.debug("支付宝订单{}进行退款", order.getId());
//			return alipayService.gateway(order);
//		} else if (order.getWay().equals("wallet")) {
//			return walletService.gateway(order);
//		} else {
//			logger.error("用户的付款方式为：{}，没有相应的退款接口", order.getWay());
//			return false;
//		}
//	}
//}
