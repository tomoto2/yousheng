package com.joe.frame.pay.wechatpay.dto;


import com.joe.frame.pay.common.annotation.Sign;
import com.joe.parse.xml.XmlNode;

import lombok.Data;

/**
 * 统一下单 unifiedorder
 * 
 * @author dell-
 *
 */
@Data
public class UnifiedOrderDTO  {
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
	 * 设备号
	 */
	@Sign
	private String device_info;
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
	 * 商品描述
	 */
	@Sign
	private String body;
	/**
	 * 商品详情
	 */
	@Sign
	@XmlNode(isCDATA=true)
	private String detail;
	/**
	 * 附加数据
	 */
	@Sign
	private String attach;
	/**
	 * 商户订单号
	 */
	@Sign
	private String out_trade_no;
	/**
	 * 货币类型
	 */
	@Sign
	private String fee_type;
	/**
	 * 总金额
	 */
	@Sign
	private int total_fee;
	/**
	 * 终端IP
	 */
	@Sign
	private String spbill_create_ip;
	/**
	 * 交易起始时间
	 */
	@Sign
	private String time_start;
	/**
	 * 交易结束时间
	 */
	@Sign
	private String time_expire;
	/**
	 * 商品标记
	 */
	@Sign
	private String goods_tag;
	/**
	 * 通知地址
	 */
	@Sign
	private String notify_url;
	/**
	 * 交易类型
	 */
	@Sign
	private String trade_type;
	/**
	 * 商品ID
	 */
	@Sign
	private String product_id;
	/**
	 * 指定支付方式
	 */
	@Sign
	private String limit_pay;
	/**
	 * 用户标识
	 */
	@Sign
	private String openid;
}
