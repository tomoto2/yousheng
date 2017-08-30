package com.joe.frame.pay.alipay.dto;

import com.joe.frame.pay.common.annotation.Sign;

import lombok.Data;

@Data
public class PayByUser {

	@Sign
	private String service = "create_direct_pay_by_user";// 接口名
	@Sign
	private String partner = "2088421947284221";// 合作者身份ID 2088开头
	@Sign
	private String _input_charset = "UTF-8";// 商户网站使用的编码格式
	private String sign_type = "RSA";// 签名方式
	private String sign;// 签名
	@Sign
	private String notify_url;// 支付宝的回掉函数
	@Sign
	private String return_url;
	// 以上基本参数
	@Sign
	private String out_trade_no;// 商户网站唯一订单号
	@Sign
	private String subject;// 商品名称
	@Sign
	private String payment_type = "1";// 只支持取值为1（商品购买）。
	@Sign
	private double total_fee;// 交易金额 该笔订单的资金总额，单位为RMB-Yuan。
	@Sign
	private String seller_id = "2088421947284221";// 当签约账号就是收款账号时，请务必使用参数seller_id
	// @Sign
	// private double price;// 商品单价 元
	// @Sign
	// private int quantity;// 购买数量
	@Sign
	private String body;// 商品描述
	@Sign
	private String it_b_pay;// 超时时间5分钟
	@Sign
	private String qr_pay_mode = "1"; // 扫码支付的方式，支持前置模式和跳转模式。
	@Sign
	private String goods_type = "0";// 商品类型

}
