package com.joe.frame.pay.alipay.dto;

import lombok.Data;
/**
 * 支付宝支付接口业务参数，如果参数中有""字符串将签名错误
 * 
 * @author joe
 *
 */
@Data
public class PayBusiness implements Business{
	/*
	 * 对一笔交易的具体描述信息。如果是多种商品，请将商品描述字符串累加传给body。 非必填
	 */
	private String body;
	/*
	 * 商品的标题/交易标题/订单标题/订单关键字等。
	 */
	private String subject;
	/*
	 * 商户网站唯一订单号
	 */
	private String out_trade_no;
	/*
	 * 该笔订单允许的最晚付款时间，逾期将关闭交易。取值范围：1m～15d。m-分钟，h-小时，d-天，1c-当天（1c-当天的情况下，无论交易何时创建，
	 * 都在0点关闭）。 该参数数值不接受小数点， 如 1.5h，可转换为 90m。
	 */
	private String timeout_express;
	/*
	 * 订单总金额，单位为元，精确到小数点后两位，取值范围[0.01,100000000]
	 */
	private String total_amount;
	/*
	 * 收款支付宝用户ID。 如果该值为空，则默认为商户签约账号对应的支付宝用户ID
	 */
	private String seller_id;
	/*
	 * 商品主类型：0—虚拟类商品，1—实物类商品.注：虚拟类商品不支持使用花呗渠道
	 */
	private String goods_type;
	/*
	 * 公用回传参数，如果请求时传递了该参数，则返回给商户时会回传该参数。支付宝会在异步通知时将该参数原样返回。
	 * 本参数必须进行UrlEncode之后才可以发送给支付宝
	 */
	private String passback_params;
	/*
	 * 可用渠道，用户只能在指定渠道范围内支付 当有多个渠道时用“,”分隔 注：与disable_pay_channels互斥
	 */
	private String enable_pay_channels;
	private String disable_pay_channels;
	/**
	 * 销售产品码
	 */
	private String product_code = "QUICK_MSECURITY_PAY";
}
