package com.joe.frame.pay.alipay.dto;


import com.joe.frame.pay.common.annotation.Sign;

import lombok.Data;

/**
 * 支付宝提现请求对象
 * @author joe
 *
 */
@Data
public class CashOut {
	/*
	 * 签约的支付宝账号对应的支付宝唯一用户号。 以2088开头的16位纯数字组成。
	 */
	@Sign
	private String partner;
	/*
	 * 签名
	 */
	private String sign;
	/*
	 * 支付宝服务器主动通知商户网站里指定的页面http路径
	 * 
	 * 可以为空
	 */
	@Sign
	private String notify_url;
	/*
	 * 付款方的支付宝账户名。
	 */
	@Sign
	private String account_name;
	/*
	 * 付款的详细数据，最多支持1000笔。
	 * 
	 * 格式为：流水号1^收款方账号1^收款账号姓名1^付款金额1^备注说明1|流水号2^收款方账号2^收款账号姓名2^付款金额2^备注说明2。
	 * 
	 * 每条记录以“|”间隔。
	 * 
	 * 流水号不能超过64字节，收款方账号小于100字节，备注不能超过200字节。当付款方为企业账户，且转账金额达到（大于等于）50000元，
	 * 
	 * 备注不能为空。
	 */
	@Sign
	private String detail_data;
	/*
	 * 批量付款批次号。
	 * 
	 * 11～32位的数字或字母或数字与字母的组合，且区分大小写。
	 * 
	 * 注意：
	 * 
	 * 批量付款批次号用作业务幂等性控制的依据，一旦提交受理，请勿直接更改批次号再次上传。
	 */
	@Sign
	private String batch_no;
	/*
	 * 批量付款笔数（最少1笔，最多1000笔）。
	 */
	@Sign
	private String batch_num;
	/*
	 * 付款文件中的总金额。单位为元
	 * 
	 * 格式：10.01，精确到分。
	 */
	@Sign
	private String batch_fee;
	/*
	 * 付款方的支付宝账号，支持邮箱和手机号2种格式。
	 */
	@Sign
	private String email;
	/*
	 * 支付时间（必须为当前日期）。 格式：YYYYMMDD。
	 */
	@Sign
	private String pay_date;
	/*
	 * 同email参数，可以使用该参数名代替email输入参数；优先级大于email。
	 * 
	 * 可以为空
	 */
	@Sign
	private String buyer_account_name;
	/*
	 * 用于商户的特定业务信息的传递，只有商户与支付宝约定了传递此参数且约定了参数含义，此参数才有效。
	 * 
	 * 可以为空
	 */
	@Sign
	private String extend_param;
	/*
	 * 接口名称。
	 */
	@Sign
	private String service = "batch_trans_notify";
	/*
	 * 商户网站使用的编码格式，如UTF-8、GBK、GB2312等。
	 */
	@Sign
	private String _input_charset = "UTF-8";
	/*
	 * DSA、RSA、MD5三个值可选，必须大写。
	 */
	private String sign_type = "MD5";
}
