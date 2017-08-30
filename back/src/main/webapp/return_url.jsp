<%@page import="com.alipay.api.internal.util.AlipaySignature"%>
<%
	/* *
	 功能：支付宝页面跳转同步通知页面
	 版本：3.2
	 日期：2011-03-17
	 说明：
	 以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
	 该代码仅供学习和研究支付宝接口使用，只是提供一个参考。
	
	 //***********页面功能说明***********
	 该页面可在本机电脑测试
	 可放入HTML等美化页面的代码、商户业务逻辑程序代码
	 TRADE_FINISHED(表示交易已经成功结束，并不能再对该交易做后续操作);
	 TRADE_SUCCESS(表示交易已经成功结束，可以对该交易做后续操作，如：分润、退款等);
	 //********************************
	 * */
%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="java.util.Map"%>
<%@ page import="com.joe.frame.pay.alipay.resource.*"%>
<%@ page import="com.alipay.api.*"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>支付宝页面跳转同步通知页面</title>
</head>
<body>
	<%
	//AliOrderResource aliRec = new AliOrderResource();
	//获取支付宝GET过来反馈信息
	Map<String,String> params = new HashMap<String,String>();
	Map requestParams = request.getParameterMap();
	for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
		String name = (String) iter.next();
		String[] values = (String[]) requestParams.get(name);
		String valueStr = "";
		for (int i = 0; i < values.length; i++) {
			valueStr = (i == values.length - 1) ? valueStr + values[i]
					: valueStr + values[i] + ",";
		}
		//乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
		valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
		params.put(name, valueStr);
	} 
	//商户订单号
	String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"),"UTF-8");
	//支付宝交易号
	String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"),"UTF-8");
	
		//out.println("支付out_trade_no =" + out_trade_no + "支付trade_no = " + trade_no );
		//out.println("支付<br />");
		
	 if(out_trade_no!= null){
			 out.println("支付成功");
			 
			}
		/* boolean verify_result = AliOrderResource.getOrderstatus(out_trade_no);
		out.println(verify_result);
		if(verify_result){//验证成功
			//请在这里加上商户的业务逻辑程序代码
			//该页面可做页面美工编辑
			out.clear();
			out.println("验证成功<br />");
			//——请根据您的业务逻辑来编写程序（以上代码仅作参考）——
		}else{
			//该页面可做页面美工编辑
			out.clear();
			out.println("验证失败");
		}  */
		
		
		//获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以上仅供参考)//
		//计算得出通知验证结果
	/* 	String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAs6ud/Jz0TWoM7yxbF8xkRxJ/nuiC+ZK5n6sFoUr0xKju31ymNqZ+K6mOemZS6omi6KY/KbYUXO5yuEgKeiqS/rN6+kzi89RVHl+hyx3KyBFwUrEfZxFFavApAoIum2HPu0N05FpK4XOTLGquQDTMW7xRUsGWnVumWTdo4b5Tx42lyt1UuRf8LFc/uTn0Q7JyVVaLx7FHknMza0dflTruL0+3qVjElH5E+7fbPWShLipO+cIFLR+Uv4D47MBR9ZVTpzP6oe/nO6Sf/w3LsgsN0oJaUSKB947giJ/KvhjkUl4HwMNlPi6CELCZvsjQdy0Yu5lbB7SdPuU3oqQcqaBmSwIDAQAB";
		//boolean AlipaySignature.rsaCheckV1(Map<String, String> params, String publicKey, String charset, String sign_type)
		boolean verify_result = AlipaySignature.rsaCheckV1(params, publicKey, "UTF-8", "RSA2");
		out.println(verify_result);
		if(verify_result){//验证成功
			//////////////////////////////////////////////////////////////////////////////////////////
			//请在这里加上商户的业务逻辑程序代码
			//该页面可做页面美工编辑
			out.clear();
			out.println("验证成功<br />");
			//——请根据您的业务逻辑来编写程序（以上代码仅作参考）——

			//////////////////////////////////////////////////////////////////////////////////////////
		}else{
			//该页面可做页面美工编辑
			out.clear();
			out.println("验证失败");
		} */

	
		
	%>
</body>
<script type="text/javascript" src="js/jquery.min.js"></script>
<script type="text/javascript">
$(function() {
	setTimeout(window.location.href="http://47.92.115.31/back/success.html?"+out_trade_no,10000);
	;});
</script>
</html>