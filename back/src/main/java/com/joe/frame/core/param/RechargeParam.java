package com.joe.frame.core.param;

import javax.persistence.Column;

import com.joe.frame.core.trade.entity.OrderStatus;

import lombok.Data;
//订单编号，套餐名称，支付方式，支付状态，支付时间
@Data
public class RechargeParam {
	/**
	 * 订单编号
	 */
	@Column(length = 32)
	private String orderNum;


	/**
	 * 套餐名称
	 */
	private String pname;

	/**
	 * （房卡数量）
	 *    单位:张
	 */
	private long number;

	/**
	 * 充值的金额
	 *   单位：分
	 */
	private long money;

	/**
	 * 充值日期
	 * 格式：yyyy-MM-dd HH:mm:ss
	 */
	private String createTime;

	/**
	 * 充值状态
	 */
	private String status;

	/**
	 * 订单状态
	 */
//	private OrderStatus orderStatus;

	/**
	 * 支付方式
	 * 1支付宝 --alipay
	 * 2微信 --wechatpay
	 */
	private String payWay;
}
