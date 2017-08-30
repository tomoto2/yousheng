package com.joe.frame.pay.alipay.resource;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.joe.frame.pay.prop.AliProp;

public class AliSDkResource {
	@Autowired
	private AliProp aliProp;

	public void doPost(HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws ServletException, IOException, AlipayApiException {
		AlipayClient alipayClient =  new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", aliProp.getAppId(), aliProp.getPrivateKey(), "json", "utf-8", aliProp.getPublicKey(), "RSA2");//获得初始化的AlipayClient
				AlipayTradeWapPayRequest alipayRequest = new AlipayTradeWapPayRequest();//创建API对应的request
		alipayRequest.setReturnUrl("http://domain.com/CallBack/return_url.jsp");
		alipayRequest.setNotifyUrl("http://domain.com/CallBack/notify_url.jsp");//在公共参数中设置回跳和通知地址
		alipayRequest.setBizContent("{" +
				"    \"out_trade_no\":\"20150320010101002\"," +
				"    \"total_amount\":88.88," +
				"    \"subject\":\"Iphone6 16G\"," +
				"    \"seller_id\":\"2088123456789012\"," +
				"    \"product_code\":\"QUICK_WAP_WAY\"" +
				"  }");//填充业务参数
		String form = alipayClient.pageExecute(alipayRequest).getBody(); //调用SDK生成表单
		httpResponse.setContentType("text/html;charset=" + "utf-8");
		httpResponse.getWriter().write(form);//直接将完整的表单html输出到页面
		httpResponse.getWriter().flush();
	}
}
