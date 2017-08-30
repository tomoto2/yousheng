package com.joe.frame.pay.wechatpay.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.joe.frame.pay.wechatpay.type.WechatOrderStatus;
import com.joe.frame.web.repository.BaseEntity;

import lombok.Data;

/**
 * 微信订单
 * 
 * @author joe
 *
 */
@Data
@Entity
@Table
public class WechatOrder implements BaseEntity<String> {
	/**
	 * 商户订单号，与系统订单号相同
	 */
	@Id
	@Column(length = 32)
	private String out_trade_no;
	/**
	 * 用户ID
	 */
	private String uid;
	/**
	 * 微信订单号
	 */
	@Column(length = 50)
	private String transaction_id;
	/**
	 * 设备号
	 */
	@Column(length = 50)
	private String device_info;
	/**
	 * 商品描述
	 */
	@Column(length = 300)
	private String body;
	/**
	 * 商品详情
	 */
	@Column(length = 300)
	private String detail;
	/**
	 * 附加信息
	 */
	@Column(length = 300)
	private String attach;
	/**
	 * 货币类型CNY，人民币
	 */
	@Column(length = 10)
	private String fee_type = "CNY";
	/**
	 * 总金额，单位为分
	 */
	private Integer total_fee;
	/**
	 * 应结金额（实际付款金额）
	 */
	private Integer settlement_total_fee;
	/**
	 * 终端IP
	 */
	@Column(length = 20)
	private String spbill_create_ip;
	/**
	 * 交易起始时间，yyyyMMddHHmmss
	 */
	@Column(length = 14)
	private String time_start;
	/**
	 * 交易结束时间，yyyyMMddHHmmss
	 */
	@Column(length = 14)
	private String time_expire;
	/**
	 * 支付完成时间，yyyyMMddHHmmss
	 */
	@Column(length = 14)
	private String time_end;
	/**
	 * 交易类型
	 */
	@Column(length = 10)
//	private String trade_type = "APP";
	private String trade_type = "JSAPI";
	/**
	 * 指定支付方式
	 */
	@Column(length = 15)
	private String limit_pay;
	/**
	 * 订单状态
	 */
	private WechatOrderStatus status;
	/**
	 * 付款银行
	 */
	@Column(length = 50)
	private String bank_type;

	/**
	 * 用户标识
	 */
	@Column(length = 50)
	private String openid;
}
