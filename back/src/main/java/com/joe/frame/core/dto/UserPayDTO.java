package com.joe.frame.core.dto;

import lombok.Data;

@Data
public class UserPayDTO {

	/**
	 * 记录ID 订单id
	 */
	private String id;

	/**
	 * 订单编号--out_trade_no
	 */
	private String orderNum;

	/**
	 * 充值的金额
	 *   单位：分
	 */
	private long money = 0;
	/**
	 * 充值金额 （元）用于前台显示
	 */
	private String smoney;

	/**
	 * （房卡数量）
	 *    单位:张
	 */
	private int number = 0;
	private int numbers = 0;//用于前台接收显示

	/**
	 * 玩家编号
	 * 注册玩家ID为6位数，首位数字从3开始31***
	 */
	private long uid;
	
	/**
	 * 玩家微信昵称
	 */
	private String name;
	
	/**
	 * 玩家的openid(微信支付时候使用)
	 */
	private String openId;


}
