package com.joe.frame.pay.prop;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "pay.ali")
public class AliProp {
	/*
	 * 应用ID
	 */
	private String appId;
	/*
	 * 私钥
	 */
	private String privateKey = "123";
	/*
	 * 公钥
	 */
	private String publicKey = "123";
	
	/**
	 * 支付宝公钥
	 */
	private String aliPublicKey ;
	/*
	 * 签约的支付宝账号对应的支付宝唯一用户号。 以2088开头的16位纯数字组成。
	 */
	private String partner;
	/*
	 * 付款支付宝账户名，给用户打款时使用
	 */
	private String account_name;
	/*
	 * 付款方的支付宝账号，支持邮箱和手机号2种格式。
	 */
	private String account;
	/*
	 * 阿里提现接口
	 */
	private String cashOut;
	/*
	 * 回调URL（支付接口回调）
	 */
	private String notifyUrl;
	/*
	 * 回调URL（前台回调）
	 */
	private String returnUrl;
	/*
	 * 回调URL（提现接口回调）
	 */
	private String cashOutNotifyUrl;
	/*
	 * 退款接口
	 */
	private String gateway;
	private String md5;
	
	/**
	 * 统一下单地址
	 */
	private String unifiedorder;
}
