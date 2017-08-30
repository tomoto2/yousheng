package com.joe.frame.pay.wechatpay.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.joe.frame.pay.common.annotation.Sign;

import lombok.Data;

/**
 * 支付订单DTO
 * 
 * @author Administrator
 *
 */
@Data
public class PayOrder implements Serializable {
	private static final long serialVersionUID = 8779895389747029317L;
	/**
	 * 系统订单号，用来后续确认订单状态使用
	 */
	private String out_trade_no;
	/**
	 * 公众号ID
	 */
	@Sign
	private String appid;
	/**
	 * 商户号
	 */
	@Sign
	private String partnerid;
	/**
	 * prepay_id
	 */
	@Sign
	private String prepayid;
	/**
	 * 固定值
	 */
	@JsonProperty(value = "package")
	@Sign(keyName = "package")
	private String packages = "Sign=WXPay";
	/**
	 * 时间戳
	 */
	@Sign
	private long timestamp;
	/**
	 * 随机字符串
	 */
	@Sign
	private String noncestr;
	/**
	 * 签名
	 */
	private String paySign;
}
