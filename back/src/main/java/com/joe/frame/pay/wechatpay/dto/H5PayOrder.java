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
public class H5PayOrder implements Serializable {
	private static final long serialVersionUID = 8779895389747029317L;

	/**
	 * 公众号ID
	 */
	@Sign
	private String appId;

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
	private String timeStamp;
	/**
	 * 随机字符串
	 */
	@Sign
	private String nonceStr;
	/**
	 * 签名
	 */
	private String paySign;
	/**
	 * 签名方式
	 */
	@Sign
	private String signType = "MD5";
}
