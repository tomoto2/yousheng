package com.joe.frame.core.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 提现记录
 * @author lpx
 *
 * 2017年7月21日
 */
@Setter
@Getter
@ToString
public class CashOutDTO extends PageDTO{

	/**
	 *  用户ID
	 */
	private String uid;

	
	/**
	 * 昵称
	 */
	private String nikeName;
	
	/**
	 * 微信号
	 */
	private String weChat;
	/**
	 * 支付宝 账号
	 */
	private String alipay;

	/**
	 * 充值的金额（充卡数量）
	 * 提现的金额
	 *   单位：元
	 */
	private long money;
	
	/**
	 * 操作状态
	 * 已到账 ---1
	 * 已申请 ---2
	 */
	private String status;
	
	/**
	 * 操作详情
	 */
	private String details;
	/**
	 * 打回理由
	 */
	private String backReason;
	/**
	 * 操作时间
	 * 格式 ：yyyyMMddHHmmss
	 */
	private String dateTime;
	
	/**
	 * 提现到的账号（支付宝账号 或者微信账号）
	 */
	private String cashAliOrwechat;
	
	/**
	 * 提现方式支付宝 或者微信
	 */
	private String type;
	
}
