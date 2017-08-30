package com.joe.frame.pay.wechatpay.type;

public enum WechatOrderStatus {
	/**
	 * 订单生成
	 */
	CREATE,
	/**
	 * 订单过期
	 */
	EXPIRE,
	/**
	 * 已完成
	 */
	FINISH,
	/**
	 * 未付款
	 */
	UNPAID,
	/**
	 * 订单撤销
	 */
	ABOLISH,
	/**
	 * 订单支付失败
	 */
	FAIL,
	/**
	 * 已付款未发货
	 */
	PAID;
}
