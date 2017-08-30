package com.joe.frame.core.dto;

import lombok.Data;

//总后台代理架构-页面显示数据
@Data
public class ProxySetDTO {

	//	 俱乐部id，代理的昵称，代理的id，上级手机号（159***），个人购卡金额，个人购卡数量，
	//	 * 旗下代理总数，本月旗下代理购卡总数，本月旗下代理购卡总金额，关系等级：一级代理为1；二级代理为2；三级代理为3

	/**
	 * 邀请码(代理编号)
	 */
	private String inviteCode;
	/**
	 *  昵称
	 */
	private String nikeName;
	/**
	 * 微信号（查询个人信息使用）
	 */
	private String wx;
	/**
	 * 代理人手机号码
	 */
	private String phone;

	/**
	 *  房卡数量
	 */
	private long card;
	/**
	 * 账户余额
	 */
	private long balance;

	/**
	 * 总充值金额
	 */
	private long Allchongzhi;
	/**
	 * 总购卡量
	 */
	private long allgoumai;

	/**
	 * 旗下代理总数
	 */
	private long childSum;
	
	/**
	 * 旗下代理购卡总数
	 */
	private long childBuySum;
	
	/**
	 * 旗下代理购卡总金额
	 */
	private long childMoneySum;
	
	/**
	 * 代理上级手机号
	 */
	private String parentPhone;
	
	/**
	 * 上级代理编号
	 */
	private String parentNumId;
	
	/**
	 * 关系等级
	 */
	private int level;
	
	/**
	 * 账号是否可用
	 */
	private String canuse;

	/**
	 * 代理id
	 */
	private String pid;
}
