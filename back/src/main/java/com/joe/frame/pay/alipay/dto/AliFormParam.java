package com.joe.frame.pay.alipay.dto;


import javax.ws.rs.FormParam;

import lombok.Data;

@Data
public class AliFormParam {

	//	
	//	gmt_create=2017-08-04+17%3A38%3A52&charset=UTF8&seller_email=m15239137715%40163.com
	//			&subject=%E5%90%8D%E9%97%A8%E4%BA%92%E5%A8%B1-%E5%A5%97%E9%A4%90%E8%B4%AD%E4%B9%B0
	//			&sign=ZUR5rq8KU2g2lu62cO5rrcjgNpHAdaR4n35u7TZwXxtoLz2tLHRmRoZTDqYcuNUwMrLSiHu6ST%2B7Dx5TRwlK0sD23lATGukbVfVtaNAVaQGl50F%2Fhg8ZkPJjELh4ChBehD2QXd947Z3jfFKmzUuzhmyuhYNDGrLJJHpEZd%2B4dMK6cVJvULYJ1tBA7Nk7gRa4GwS8QhG3GrMqLIoHQjeu7hhg2Ywd2HIg%2B%2BdrGUZ3W2pTcnvj2RdGAZsuhIxLbxmbdrfyoSeJKyi2IlBL%2FSE2Wn5C1wYImq%2Bnv0cdKUTzKf2hp33VJwEIRInCAAXcKbvKjw1kyHXKSJvZIhIiSrDuGQ%3D%3D
	//			&buyer_id=2088802134130194&invoice_amount=0.01&notify_id=a792d014cd20b7cb6f0f80477a99262hgu
	//			&fund_bill_list=%5B%7B%22amount%22%3A%220.01%22%2C%22fundChannel%22%3A%22ALIPAYACCOUNT%22%7D%5D
	//			&notify_type=trade_status_sync&trade_status=TRADE_SUCCESS&receipt_amount=0.01&app_id=2017072807929109
	//			&buyer_pay_amount=0.01&sign_type=RSA2&seller_id=2088721427511147&gmt_payment=2017-08-04+17%3A38%3A53
	//			&notify_time=2017-08-04+17%3A38%3A53&version=1.0&out_trade_no=c3bd41263846420a8f0b8bc75b7097eb
	//			&total_amount=0.01&trade_no=2017080421001004190229430256&auth_app_id=2017072807929109
	//			&buyer_logon_id=159****2551&point_amount=0.00
	//
	//
	//	
	@FormParam(value="gmt_create")
	private String gmt_create;
	@FormParam(value="charset")
	private String charset;
	@FormParam(value="seller_email")
	private String seller_email;
	@FormParam(value="subject")
	private String subject;
	@FormParam(value="sign")
	private String sign;
	@FormParam(value="fund_bill_list")
	private String fund_bill_list;
	@FormParam(value="notify_type")
	private String notify_type;
	@FormParam(value="trade_status")
	private String trade_status;
	@FormParam(value="receipt_amount")
	private String receipt_amount;
	@FormParam(value="app_id")
	private String app_id;
	@FormParam(value="buyer_pay_amount")
	private String buyer_pay_amount;
	@FormParam(value="sign_type")
	private String sign_type;
	@FormParam(value="seller_id")
	private String seller_id;
	@FormParam(value="gmt_payment")
	private String gmt_payment;
	@FormParam(value="notify_time")
	private String notify_time;
	@FormParam(value="version")
	private String version;
	@FormParam(value="out_trade_no")
	private String out_trade_no;
	@FormParam(value="total_amount")
	private String total_amount;
	@FormParam(value="trade_no")
	private String trade_no;
	@FormParam(value="auth_app_id")
	private String auth_app_id;
	@FormParam(value="buyer_logon_id")
	private String buyer_logon_id;
	@FormParam(value="point_amount")
	private String point_amount;


}
