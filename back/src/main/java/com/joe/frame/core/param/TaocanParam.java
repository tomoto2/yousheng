package com.joe.frame.core.param;

import lombok.Data;

@Data
public class TaocanParam {
	/**
	 * 选择的套餐的编号（名称）
	 */
	private String selectItem;
	/**
	 * 自定义套餐的数量
	 */
	private int num;
	
	/**
	 * 支付类型
	 * alipay
	 * wechatpay
	 */
	private String payWay;
}
