package com.joe.frame.core.param;

import lombok.Data;
/**
 * 提现申请
 * @author lpx
 *
 * 2017年7月21日
 */
@Data
public class CashOutParam {

	/**
	 * 体现记录id
	 */
	private String cid;
	
	/**
	 *申请人id
	 */
	private String applyId;
	/**
	 * 提现金额
	 */
	private long money;

	/**
	 * 申请状态(0:已申请，1：已提现，2：已返还)
	 */
	private String status;

	/**
	 * 提现申请日期
	 */
	private String datetime;
	
	/**
	 * 提现方式，提现申请的类型（alipay，wechatpay）
	 */
	private String type;
	
	/**
	 * 提现申请的支付宝账号或者微信号
	 */
	private String cashAliOrwechat;

	
	/**
	 * 操作详情
	 */
	private String details;


}
