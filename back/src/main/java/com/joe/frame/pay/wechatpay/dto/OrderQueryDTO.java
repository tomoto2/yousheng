package com.joe.frame.pay.wechatpay.dto;

import lombok.Data;

/**
 * 查詢訂單
 * 
 * @author dell-
 *
 */
@Data
public class OrderQueryDTO {
	/**
	 * 公众账号ID
	 */
	private String appid;
	/**
	 * 商户号
	 */
	private String mch_id;
	/**
	 * 微信订单号
	 */
	private String transaction_id;
	/**
	 * 商户订单号
	 */
	private String out_trade_no;
	/**
	 * 随机字符串
	 */
	private String nonce_str;
	/**
	 * 签名
	 */
	private String sign;
}
