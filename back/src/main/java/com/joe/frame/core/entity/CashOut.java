package com.joe.frame.core.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.joe.frame.core.database.BaseEntity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 代理商个人钱包的提现操作记录
 * 
 * @author lpx 
 *
 */
@Setter
@Getter
@ToString
@Table
@Entity
public class CashOut extends BaseEntity<String> {
	/**
	 *  id
	 */
	@Id
	@Column(length = 32)
	private String id;
	
	/**
	 *  用户ID(代理商id)
	 */
	@Column(length = 32)
	private String uid;
	
//	
//	/**
//	 * 类型
//	 * 1: 充值 
//	 * 2: 提现
//	 *     
//	 */
//	@Column(length = 2)
//	private String type;
	
	/**
	 * 充值的金额（充卡数量）
	 * 提现的金额
	 *   单位 : 分
	 */
	private long money = 0;
	
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
	 * 处理时间 yyyy-MM-dd HH:mm:ss
	 */
	private String dealTime;
	
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
	 * 格式 ：yyyy-MM-dd HH:mm:ss
	 */
	@Column(length = 32)
	private String dateTime;
	
	/**
	 * 提现方式（1  支付宝
	 *       2 微信
	 */
	private String type;
	
	/**
	 * 提现申请的支付宝账号或者微信号
	 */
	private String cashAliOrwechat;
	
	/**
	 * 处理人 adminId
	 */
	private String dealId;
	

	
}
