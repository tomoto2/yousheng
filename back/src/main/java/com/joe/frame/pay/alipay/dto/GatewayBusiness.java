package com.joe.frame.pay.alipay.dto;

import lombok.Data;

/**
 * 退款业务参数
 * 
 * @author joe
 *
 */
@Data
public class GatewayBusiness implements Business {
	/*
	 * 订单支付时传入的商户订单号,不能和 trade_no同时为空。
	 */
	private String out_trade_no;
	/*
	 * 支付宝交易号，和商户订单号不能同时为空
	 */
	private String trade_no;
	/*
	 * 需要退款的金额，该金额不能大于订单金额,单位为元，支持两位小数
	 */
	private String refund_amount;
	/*
	 * 退款的原因说明 可选
	 */
	private String refund_reason;
	/*
	 * 标识一次退款请求，同一笔交易多次退款需要保证唯一，如需部分退款，则此参数必传。
	 */
	private String out_request_no;
}
