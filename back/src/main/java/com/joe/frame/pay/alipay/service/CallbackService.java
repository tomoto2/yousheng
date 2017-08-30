package com.joe.frame.pay.alipay.service;

import java.util.Map;
import java.util.concurrent.locks.Lock;

import javax.transaction.Transactional;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joe.concurrent.LockService;
import com.joe.frame.common.util.SignUtil;
import com.joe.frame.core.service.AgentService;
import com.joe.frame.pay.alipay.dto.PayByUser;
import com.joe.frame.pay.alipay.dto.PublicParam;
import com.joe.frame.pay.prop.AliProp;
import com.joe.frame.web.cache.CacheServiceProxy;
import com.joe.secure.Encipher;
import com.joe.secure.MD5;
/**
 * 支付宝回调服务
 * 
 * @author joe
 *
 */
@Service
@Transactional
public class CallbackService {
	private static final Logger logger = LoggerFactory.getLogger(CallbackService.class);
	private static final Encipher encipher = new MD5();
	@Autowired
	private AgentService agentService;
	@Autowired
	private CacheServiceProxy cache;
	@Autowired
	private AliSignService aliSignService;
	@Autowired
	private AliProp aliProp;

	public void callback(Map<String, Object> data) {
		// 获取回调类型（具体是哪个接口的回调）
		String type = (String) data.get("notify_type");
		if (StringUtils.isEmpty(type)) {
			logger.warn("回调数据有误，不处理");
			return;
		}

//		if (type.equals("batch_trans_notify")) {
//			logger.debug("收到支付宝提现回调，回调信息为：{}", data);
//			cashoutCallback(data);
//		}

		if (type.equals("trade_status_sync")) {
			// 支付宝支付回调
			logger.debug("收到支付宝支付回调，回调信息为：{}", data);
			payCallback(data);
		}
	}

	/**
	 * 支付宝提现回调
	 * 
	 * @param data
	 *            回调数据
	 */
//	private void cashoutCallback(Map<String, Object> data) {
//		logger.debug("提现回调数据为：{}", data);
//		if (!checkMd5Sign(data)) {
//			logger.debug("签名校验失败");
//			return;
//		}
//		String success = String.valueOf(data.get("success_details"));
//		String error = String.valueOf(data.get("fail_details"));
//		logger.debug("提现成功的为：{}；提现失败的为：{}", success, error);
//
//		if (success.equals("null")) {
//			logger.debug("没有成功提现的");
//			return;
//		}
//
//		String[] all = success.split("\\|");
//		for (String cash : all) {
//			if (StringUtils.isEmpty(cash)) {
//				continue;
//			}
//			logger.debug("开始处理提现：{}", cash);
//			String id = cash.split("\\^")[0];
//			logger.debug("对应的提现历史表ID为：{}", id);
//			CashoutHistory cashout = cashoutHistoryRepository.find(id);
//			cashout.setDeal(true);
//			RongCloudMethodUtil.pushSystemMessage("提现申请成功，等待支付宝处理。提现金额:" + cashout.getAmount() / 100.0 + " 元", "123",
//					cashout.getUid(), "RC:TxtMsg", "提现成功", "null");
//			logger.debug("提现历史表更新完毕后为：{}", cashout);
//		}
//		logger.debug("提现回调处理完毕");
//	}

	/**
	 * 支付回调
	 * 
	 * @param data
	 *            回调数据
	 */
	private void payCallback(Map<String, Object> data) {
		boolean flag = false;
		String out_trade_no = null;
		try {
			logger.debug("支付宝回调信息为：{}", data);
			// 验签
			checkSign(data);
			// 获取回调中的信息
			out_trade_no = (String) data.get("out_trade_no");
			String sign_type = (String) data.get("sign_type");
			String trade_status = (String) data.get("trade_status");
			LockService.lock(out_trade_no);
			flag = true;
			// 根据订单号从缓存中获取订单
			PublicParam unifiedOrder = cache.get(out_trade_no, PublicParam.class);
			if (unifiedOrder == null) {
				logger.warn("订单{}在缓存中没有找到，不处理", data);
				return;
			}
			if (unifiedOrder.getSign_type().equals(sign_type)) {
				logger.debug("订单{}是系统订单，从缓存删除", unifiedOrder);
				cache.remove(out_trade_no);

				if (trade_status.equals("TRADE_SUCCESS") || trade_status.equals("TRADE_FINISHED")) {
					logger.debug("支付成功");
					// 系统订单设置为已经支付状态（支付成功回调）
					agentService.payEnd("alipay",out_trade_no);
				} else {
					logger.warn("支付状态为：{}，支付失败", trade_status);
				}
			} else {
				logger.warn("回调订单{}与缓存订单{}不符，不操作");
			}
		} catch (Exception e) {
			logger.error("回调过程中异常，原因：", e);
		} finally {
			if (flag) {
				// 销毁锁
				LockService.unlock(out_trade_no);
			}
		}
	}

	/**
	 * 验证MD5签名
	 * 
	 * @param data
	 *            要验证的数据（包含签名的数据）
	 * @return
	 *         <li>true：签名验证通过</li>
	 *         <li>false：签名验证不通过</li>
	 */
	private boolean checkMd5Sign(Map<String, Object> data) {
		String sign = String.valueOf(data.get("sign"));
		String sign_type = String.valueOf(data.get("sign_type"));
		if (sign.equals("null")) {
			logger.warn("支付宝回调签名为null");
			return false;
		}
		// 以下两个字段不参与签名，删除
		data.remove("sign");
		data.remove("sign_type");
		String signData = SignUtil.getSignDataFromMap(data, 3);
		logger.debug("要签名的数据为：{}", signData);

		// 拼接MD5密钥
		signData = signData + aliProp.getMd5();
		logger.debug("要签名的data为：{}", signData);
		// 签名（MD5签名）
		String selfSign = encipher.encrypt(signData);
		logger.debug("回调接口签名是：{}", sign);
		logger.debug("自签名是“{}", selfSign);
		// 重新放入，防止其他地方调用取出null
		data.put("sign", sign);
		data.put("sign_type", sign_type);
		return sign.equals(selfSign);
	}

	/**
	 * 验证签名
	 * 
	 * @param data
	 *            要验证的数据（包含签名的数据）
	 * @return
	 *         <li>true：签名验证通过</li>
	 *         <li>false：签名验证不通过</li>
	 */
	public boolean checkSign(Map<String, Object> data) {
		String sign = (String) data.get("sign");
		String sign_type = (String) data.get("sign_type");
		if (sign == null || sign.trim().isEmpty()) {
			logger.warn("支付宝回调签名为null");
			return false;
		}
		// 以下两个字段不参与签名，删除
		data.remove("sign");
		data.remove("sign_type");
		String content = SignUtil.getSignDataFromMap(data, 2);
		logger.debug("要签名的数据为：{}", content);
		String selfSign = aliSignService.rsa256Sign(content, "UTF8");
		logger.debug("回调接口签名是：{}", sign);
		logger.debug("自签名是“{}", selfSign);
		// 重新放入，防止其他地方调用取出null
		data.put("sign", sign);
		data.put("sign_type", sign_type);
		return sign.equals(selfSign);
	}

	public void webCallback(Map<String, Object> data) {

		// 获取回调类型（具体是哪个接口的回调）
		String type = (String) data.get("notify_type");
		if (type == null || type.trim().isEmpty()) {
			logger.warn("回调数据有误，不处理");
			return;
		}
		if (type.equals("trade_status_sync")) {
			// 支付宝支付回调
			logger.debug("收到支付宝支付回调，回调信息为：{}", data);
			webpayCallback(data);
		}

	}

	private void webpayCallback(Map<String, Object> data) {
		boolean flag = false;
		String out_trade_no = null;
		try {
			logger.debug("支付宝回调信息为：{}", data);
			// 验签
			checkSign(data);
			// 获取回调中的信息
			out_trade_no = (String) data.get("out_trade_no");
			String sign_type = (String) data.get("sign_type");
			String trade_status = (String) data.get("trade_status");
			LockService.lock(out_trade_no);
			flag = true;
			// 根据订单号从缓存中获取订单
			PayByUser unifiedOrder = cache.get(out_trade_no, PayByUser.class);
			if (unifiedOrder == null) {
				logger.warn("订单{}在缓存中没有找到，不处理", data);
				return;
			}
			if (unifiedOrder.getSign_type().equals(sign_type)) {
				logger.debug("订单{}是系统订单，从缓存删除", unifiedOrder);
				cache.remove(out_trade_no);

				if (trade_status.equals("TRADE_SUCCESS") || trade_status.equals("TRADE_FINISHED")) {
					logger.debug("支付成功");
					// 系统订单设置为已经支付状态（支付成功回调）
					agentService.payEnd("alipay",out_trade_no);
					
				} else {
					logger.warn("支付状态为：{}，支付失败", trade_status);
				}
			} else {
				logger.warn("回调订单{}与缓存订单{}不符，不操作");
			}
		} catch (Exception e) {
			logger.error("回调过程中异常，原因：", e);
		} finally {
			if (flag) {
				// 销毁锁
				LockService.unlock(out_trade_no);
			}
		}
	}

}
