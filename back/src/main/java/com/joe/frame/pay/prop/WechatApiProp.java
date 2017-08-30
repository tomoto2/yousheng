package com.joe.frame.pay.prop;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * 微信支付API
 * @author Administrator
 *
 */
@Data
@ConfigurationProperties(prefix = "pay.api.wechat")
public class WechatApiProp {
	/**
	 * 获取token的url
	 */
	private String token;
	/**
	 * 统一下单
	 */
	private String unifiedorder;
	/**
	 * 查询订单
	 */
	private String orderquery;
	/**
	 * 关闭订单
	 */
	private String closeorder;
	/**
	 * 申请退款
	 */
	private String refund;
	/**
	 * 查询退款
	 */
	private String refundquery;
	/**
	 * 下载对账单
	 */
	private String downloadbill;
	/**
	 * 交易保障
	 */
	private String report;
}
