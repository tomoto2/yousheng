package com.joe.frame.core.param;

import lombok.Data;
/**
 * 代理商自己的充值记录 --Recharge
 * @author lpx
 *
 * 2017年7月21日
 */
@Data
public class BuyParam {

	/**
	 * 套餐名称
	 */
	private String pname;
	/**
	 * 房卡数量
	 */
	private int number;
	/**
	 * 金额
	 */
	private long money;

	/**
	 * 代理商id
	 */
	private String agentUid;

	/**
	 * 代理商手机号
	 */
	private String agentPhone;

	/**
	 * 订单id  Recharge id记录（使用）
	 */
	private String orderId;
	
	/**
	 * 订单编号
	 */
	private String orderNum;
	
	/**
	 * 支付方式
	 * 1支付宝 alipay
	 * 2微信 wechatpay
	 */
	private String payWay;


}
