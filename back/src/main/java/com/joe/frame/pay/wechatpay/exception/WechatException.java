package com.joe.frame.pay.wechatpay.exception;

import com.joe.frame.web.exception.CodeException;

/**
 * 微信订单异常
 * 
 * @author Administrator
 *
 */
public class WechatException extends CodeException {
	private static final long serialVersionUID = -3343266106990897821L;

	public WechatException(String code, String message) {
		super(code, message);
	}
}
