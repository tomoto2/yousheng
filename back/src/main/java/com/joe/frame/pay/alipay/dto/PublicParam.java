package com.joe.frame.pay.alipay.dto;

import com.joe.frame.pay.common.annotation.Sign;

import lombok.Data;

/**
 * 公共参数
 * 
 * @author joe
 *
 */
@Data
public class PublicParam {
	/*
	 * 支付宝分配给开发者的应用ID
	 */
	@Sign
	private String app_id;
	private String sign;
	/*
	 * 发送请求的时间，格式"yyyy-MM-dd HH:mm:ss"
	 */
	@Sign
	private String timestamp;
	/*
	 * 支付宝服务器主动通知商户服务器里指定的页面http/https路径。建议商户使用https
	 */
	@Sign
	private String notify_url;
	/*
	 * 业务请求参数的集合，最大长度不限，除公共参数外所有请求参数都必须放在这个参数中传递，具体参照各产品快速接入文档
	 */
	@Sign
	private String biz_content;
	@Sign
	private String method;//接口名称
	@Sign
	private String format = "JSON";
	@Sign
	private String charset = "utf-8";
	@Sign
	private String sign_type = "RSA2";
	@Sign
	private String version = "1.0";
	
	
	
}