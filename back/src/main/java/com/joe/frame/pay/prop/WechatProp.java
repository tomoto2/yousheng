package com.joe.frame.pay.prop;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * 微信支付配置文件
 * 
 * @author Administrator
 *
 */
@Data
@ConfigurationProperties(prefix = "pay.wechat")
public class WechatProp {
	/*
	 * APPID
	 */
//	AppID：wxcfdc760cf0cc6974
//	AppSecret：ab1728dfac58b6dd55489d700613ebff

	private String appid;
	/*
	 * 商户号
	 */
	private String mch_id;
	/*
	 * 应用秘钥
	 */
	private String secret;
	/*
	 * API密匙
	 */
	private String key;
	/*
	 * 通知地址，下单接口回调地址
	 */
	private String notify_url;
	/*
	 * 订单有效时间（单位为分钟）
	 */
	private int timeout = 30;
	/*
	 * 微信端编码
	 */
	private String charset = "UTF8";
	/*
	 * 证书路径
	 */
	private String certPath;
}
