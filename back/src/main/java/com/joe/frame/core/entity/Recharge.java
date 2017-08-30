package com.joe.frame.core.entity;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.joe.frame.core.database.BaseEntity;
import com.joe.frame.core.trade.entity.OrderStatus;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * 订单
 * 代理商个人购买房卡记录
 * 	
 * @author lpx
 */
@Setter
@Getter
@ToString
@Table
@Entity
public class Recharge extends BaseEntity<String>{
	/**
	 * 记录ID 订单id
	 */
	@Id
	@Column(length = 32)
	private String id;
	
	/**
	 * 订单编号--out_trade_no
	 */
	@Column(length = 32)
	private String orderNum;
	
	/**
	 * 代理商ID
	 */
	@Column(length = 32)
	private String agentUid;
	
	/**
	 * 玩家id
	 */
	@Column(length = 6)
	private String uid;
	
	/**
	 * 套餐名称
	 * 
	 * 200张 = 300元  1.5元/张   -----1
	 * 500张 = 750元  1.5元/张	 -----2
	 * 1000张 = 1475元  1.45元/张  -----3
	 * 2000张 = 2800元  1.4元/张	 -----4
	 * 5000张 = 6750元  1.35元/张 -----5
	 * 10000张 = 13000元  1.3元/张	 -----6
	 * 
	 * 自定义的数量必须大于200张，1.5元/张 -----1
	 */
	private String pname;
	
	/**
	 * （房卡数量）
	 *    单位:张
	 */
	private int number = 0;
	
	/**
	 * 充值的金额
	 *   单位：分
	 */
	private long money = 0;
	
	/*
	 * 订单状态
	 */
	@Column(length = 50, nullable = false)
	private OrderStatus orderStatus;
	/*
	 * 订单创建时间，格式yyyy-MM-dd HH:mm:ss
	 * 
	 * 订单创建时间，格式yyyyMMddHHmmss(使用)
	 */
	@Column(length = 19, nullable = false)
	private String createTime;
	/*
	 * 订单超时失效时间，格式yyyy-MM-dd HH:mm:ss
	 */
	@Column(length = 19, nullable = false)
	private String expire;
	/*
	 * 支付时间，格式yyyy-MM-dd HH:mm:ss
	 */
	@Column(length = 19)
	private String payTime;
		
	/**
	 * 充值日期订单创建时间
	 * 格式：yyyy-MM-dd HH:mm:ss
	 */
//	@Column(length = 20)
//	private String datetime;

	/**
	 * 充值状态
	 * 1 未支付
	 * 2已完成
	 * 3支付失败
	 */	
	/*
	 * 订单状态
	 * 1 订单创建  未支付
	 * 2 已支付
	 */
	@Column(length = 20)
	private String status;

	/**
	 * 支付方式
	 * 1支付宝 --alipay
	 * 2微信 --wechatpay
	 */
	private String payWay;
	
}
