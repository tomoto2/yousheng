package com.joe.frame.pay.wechatpay.dto;

import com.joe.parse.xml.XmlNode;

import lombok.Data;

@Data
public class NotifyDTO {
	/**
	 * 商户处理通知状态，SUCCESS为成功，FAIL为失败
	 */
	@XmlNode(isCDATA = true, name = "return_code")
	private String code = "SUCCESS";
	/**
	 * 当return_code为FAIL时需要有该参数，错误原因
	 */
	@XmlNode(isCDATA = true, name = "return_msg")
	private String msg;
}
