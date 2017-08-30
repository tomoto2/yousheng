package com.joe.frame.pay.wechatpay.dto;

import com.joe.frame.pay.common.annotation.Sign;

import lombok.Data;

/**
 * 下载对账单
 * 
 * @author dell-
 *
 */
@Data
public class DownloadBillDTO {
	/**
	 * 公众账号ID
	 */
	@Sign
	private String appid;
	/**
	 * 商户号
	 */
	@Sign
	private String mch_id;
	/**
	 * 设备号
	 */
	@Sign
	private String device_info;
	/**
	 * 随机字符串
	 */
	@Sign
	private String nonce_str;
	/**
	 * 签名
	 */
	private String sign;
	/**
	 * 对账单日期
	 */
	@Sign
	private String bill_date;
	/**
	 * 账单类型
	 */
	@Sign
	private String bill_type;
}
