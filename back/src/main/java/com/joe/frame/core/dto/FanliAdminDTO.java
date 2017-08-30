package com.joe.frame.core.dto;

import lombok.Data;

@Data
public class FanliAdminDTO {

	/**
	 * 代理id
	 */
	private String pid;
	/**
	 * 昵称
	 */
	private String nikeName;

	String alipay ;//支付宝 账号
	String wx;//微信账号
	String name;//真实姓名
	
	/**
	 * id=邀请码
	 */
	private String pnumId;
	//	金额：代理一个月内的购卡总金额  
	private long oneMoney;
	//	数量：代理一个月内的购卡总数量
	private long sum;
	//	某月旗下代理购卡：旗下代理购卡总金额
	private long qixiaAllMoney;
	//	个人购卡金额对应的  个人返利比
	private long onerate;
	//总返利金额 = (个人购卡总金额+旗下代理购卡总金额）   *   对应的返利比
	private long AllFanli;
	//旗下代理返利：直属A*对应的返利比  + 直属B * 对应的返利比
	private long qixiaAndDirect;
	//代理实际返利金额
	private long shijiFanli;
	
	/**
	 * 操作状态
	 *  提现状态，0：未提现（已申请）；1：已提现；2：已返回（返还用户账户）
	 *  
	 */
	private String status;
	
	/**
	 * 0：未处理   1：已处理
	 */
	private String dealWith;


	/**
	 * 提现记录id
	 */
	private String cashOutId;

}
