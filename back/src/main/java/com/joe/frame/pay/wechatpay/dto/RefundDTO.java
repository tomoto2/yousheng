package com.joe.frame.pay.wechatpay.dto;

import com.joe.frame.pay.common.annotation.Sign;

import lombok.Data;

/**
 * 申请退款
 * 
 * @author dell-
 *
 */
@Data
public class RefundDTO {
	/**
	 * 公众账号ID
	 */
	@Sign
	private String appid;
	/**
	 * 商户号
	 */
	@Sign
	private String mch_id;
	/**
	 * 随机字符串
	 */
	@Sign
	private String nonce_str;
	/**
	 * 签名
	 */
	private String sign;
	/**
	 * 商户订单号
	 */
	@Sign
	private String out_trade_no;
	/**
	 * 商户退款号
	 */
	@Sign
	private String out_refund_no;
	/**
	 * 订单金额
	 */
	@Sign
	private int total_fee;
	/**
	 * 退款金额
	 */
	@Sign
	private int refund_fee;
	/**
	 * 操作员
	 */
	@Sign
	private String op_user_id;
}
