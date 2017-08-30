package com.joe.frame.pay.alipay.resource;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.joe.frame.core.entity.Recharge;
import com.joe.frame.core.param.BuyParam;
import com.joe.frame.core.service.AgentService;
import com.joe.frame.core.trade.entity.OrderStatus;
import com.joe.frame.pay.alipay.service.AlipayService;
import com.joe.frame.pay.alipay.service.CallbackService;
import com.joe.frame.pay.prop.AliProp;
import com.joe.frame.web.dto.BaseDTO;
import com.joe.frame.web.dto.NormalDTO;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Path("pay/ali")
public class AliOrderResource {
	private static final Logger logger = LoggerFactory.getLogger(AliOrderResource.class);
	@Autowired
	private AlipayService service;
	@Autowired
	private CallbackService callbackService;
	@Context
	private HttpServletRequest request;
	@Autowired
	private AliProp aliProp;
	@Autowired
	private AgentService agentService;

//支付时候使用的订单id
	
	
	/**
	 * 创建支付宝订单，并封装form数据
	 * @param order 本地订单
	 * @return form数据
	 */

	@POST
	@Path("createOrder1")
	@Produces(MediaType.TEXT_HTML)
	@Consumes(MediaType.APPLICATION_JSON)
	public String createOrder1(BuyParam order){
		//先判断系统中是否有该订单
		logger.debug("接收到订单请求{}", order);
		Recharge hasOrder = agentService.judgeOrder(order);
		if(hasOrder == null){
			logger.info("系统中没有该订单{}",order.getOrderNum());
			return "系统中没有该订单";
		}
		
		AlipayClient alipayClient =  new DefaultAlipayClient(aliProp.getUnifiedorder(), aliProp.getAppId(), aliProp.getPrivateKey(), "json", "UTF8", aliProp.getPublicKey(), "RSA2");//获得初始化的AlipayClient
		AlipayTradeWapPayRequest alipayRequest = new AlipayTradeWapPayRequest();//创建API对应的request
		alipayRequest.setReturnUrl(aliProp.getReturnUrl());
		alipayRequest.setNotifyUrl(aliProp.getNotifyUrl());//在公共参数中设置回跳和通知地址
		//		alipayRequest.setBizContent("{" +
		//				"    \"out_trade_no\":"+order.getOrderId()+"," +
		//				"    \"total_amount\":"+order.getMoney()+"," +
		//				"    \"subject\":\"代理套餐购买\"," +
		//				"    \"seller_id\":"+aliProp.getPartner()+"," +
		//				"    \"product_code\":\"QUICK_WAP_WAY\"" +
		//				"  }");//填充业务参数、
		//		
		String amount = String.valueOf((double) order.getMoney());//单位元
		String params = "{\"out_trade_no\":\""+order.getOrderId()+"\","
				+ "\"total_amount\":\""+amount+"\","
				+ "\"subject\":\"名门互娱-套餐购买\","
				+ "\"seller_id\":\""+aliProp.getPartner()+"\","
				+ "\"product_code\":\"QUICK_WAP_PAY\"}";
		alipayRequest.setBizContent(params);
		logger.info("封装请求参数setBizContent{}，向支付宝发送请求",alipayRequest.getBizContent());
		String form = null;
		try {
			form = alipayClient.pageExecute(alipayRequest).getBody();
		} catch (AlipayApiException e) {
			logger.error("支付宝异常{},{}",e,e.getMessage());
			System.out.println("支付宝异常"+e);
		}
		logger.info("封装的form向支付宝请求数据{}",form);
		return form;
	}



	/**
	 * 支付宝回调，验签以及更新本地订单
	 * @param params 支付宝返回的参数
	 * @return 向支付宝发送支付结果
	 */
	@POST
	@Path("callback")
	@Produces(MediaType.TEXT_PLAIN)
	public String call(String param){
		logger.info("收到支付宝回调");
		logger.info("收到支付宝回调形式参数{}",param);
		String substring1 = param.substring(param.indexOf("trade_status="));
		String trade_status=substring1.substring(substring1.indexOf("=")+1,substring1.indexOf("receipt_amount")-1);
		logger.info("trade_status===",trade_status);

		String substring = param.substring(param.indexOf("out_trade_no="));
		String trade_no_And_out=substring.substring(substring.indexOf("=")+1,substring.indexOf("auth_app_id")-1);
		String out_trade_no=trade_no_And_out.substring(0,trade_no_And_out.indexOf("total_amount")-1);
		logger.info("收到支付宝回调参数中的out_trade_no===",out_trade_no);

		String trade_no_And_out1 = trade_no_And_out.substring(trade_no_And_out.indexOf("trade_no="));
		String trade_no=trade_no_And_out1.substring(trade_no_And_out1.indexOf("=")+1);
		logger.info("收到支付宝回调参数中的trade_no===",trade_no);

		
		logger.info("out_trade_no=={},out_trade=={},trade_status={}",out_trade_no,trade_no,trade_status);
		boolean verify_result =  judgeSuccess(out_trade_no,trade_no);
//		getstatus(out_trade_no, trade_no);
		logger.info("verify_result统一订单查询结果{}",verify_result);
		if(verify_result){//验证成功
			logger.info("验签成功");
			if (trade_status.equals("TRADE_SUCCESS") || trade_status.equals("TRADE_FINISHED")){
				logger.debug("支付成功");
				// 系统订单设置为已经支付状态（支付成功回调）
				agentService.payEnd("alipay",out_trade_no);
				logger.info("支付成功之后，执行本地修改操作也成功");
			} else {
				logger.info("支付状态为：{}，支付失败", trade_status);
				logger.warn("支付状态为：{}，支付失败", trade_status);
				//				return "fail";//请不要修改或删除
			}
			return "success";//请不要修改或删除
		}else{
			//验证失败
			logger.info("验签失败");
			logger.info("支付宝回调完毕fail");
			return "fail";
		}
	}


	//		Map<String, String[]> map = request.getParameterMap();
	//		logger.debug("支付宝回调map为：{}", map);
	//		// 解析支付宝回调
	//		Map<String, Object> data = new HashMap<String, Object>();
	//		for (Map.Entry<String, String[]> entry : map.entrySet()) {
	//			data.put(entry.getKey(), entry.getValue()[0]);
	//		}
	//		callbackService.checkSign(data);//验签


	//获取支付宝POST过来反馈信息
	//				Map<String,String> params = new HashMap<String,String>();
	//				Map requestParams = request.getParameterMap();
	//				logger.info("支付宝回调收到的map{}",requestParams);
	//				for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
	//					String name = (String) iter.next();
	//					String[] values = (String[]) requestParams.get(name);
	//					logger.info("收到的回调{}参数{}",name,values);
	//					String valueStr = "";
	//					for (int i = 0; i < values.length; i++) {
	//						valueStr = (i == values.length - 1) ? valueStr + values[i]
	//								: valueStr + values[i] + ",";
	//						logger.info("实际参数{}",valueStr);
	//					}
	//					//乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
	//					//valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
	//					params.put(name, valueStr);
	//				}
	//				//获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以下仅供参考)//
	//boolean AlipaySignature.rsaCheckV1(Map<String, String> params, String publicKey, String charset, String sign_type)
	//					boolean verify_result = AlipaySignature.rsaCheckV1(params, aliProp.getPublicKey(),"UTF-8", "RSA2");
	//					logger.info("验签结果{}",verify_result);
	//					if(verify_result){//验证成功
	//						logger.info("验签成功");
	//						//请在这里加上商户的业务逻辑程序代码
	//						if (trade_status.equals("TRADE_SUCCESS") || trade_status.equals("TRADE_FINISHED")) {
	//							logger.debug("支付成功");
	//							// 系统订单设置为已经支付状态（支付成功回调）
	//							agentService.payEnd("alipay",out_trade_no);
	//							logger.info("支付成功之后，执行本地修改操作也成功");
	//						} else {
	//							logger.info("支付状态为：{}，支付失败", trade_status);
	//							logger.warn("支付状态为：{}，支付失败", trade_status);
	//						}
	//		
	//						//——请根据您的业务逻辑来编写程序（以上代码仅作参考）——
	//						logger.info("支付宝回调完毕success");
	//						return "success";//请不要修改或删除
	//		
	//					}else{//验证失败
	//						logger.info("验签失败");
	//						logger.info("支付宝回调完毕fail");
	//						return "fail";
	//					}




	/**
	 * 判断订单是否支付成功
	 * 根据支付宝返回的参数，获取订单号，判断本地订单状态是否修改
	 * 
	 * @param out_trade_no 订单号
	 * @return 是否支付成功
	 */
	@POST
	@Path("justStatus")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public NormalDTO<Object> getOrderstatus(String out_trade_no){
		NormalDTO<Object> dto = new NormalDTO<Object>();
		OrderStatus orderStatus = agentService.getOrderstatus(out_trade_no);
		if(!orderStatus.equals(OrderStatus.PAID)){
			dto.setErrorMessage("订单支付失败");
			dto.setStatus("201");
			return dto;
		}
		return dto;
	}




	//	public static void main(String[] args) {
	//		String out_trade_no = "e00b878a84bd4db79b208e42e49e0931";
	//		String trade_no ="2017080521001004190231189765";
	//
	//		Map<String, String> getstatus = getstatus(out_trade_no, trade_no);
	//		logger.info("统一订单查询码getstatusMap为{}",getstatus);
	//		String code = getstatus.get("code");
	//		logger.info("统一订单查询码code为{}",code);
	//		//交易是否完成
	//		if("10000".equals(code)){
	//			logger.info("充值成功： 返回信息"+getstatus);
	//		}else{
	//			logger.info("充值失败： 返回信息"+getstatus);
	//		}
	//	}

	/**
	 * 统一收单线下交易查询
	 * @param out_trade_no 订单号
	 * @param trade_no 商户号
	 * @return 返回结果
	 */
	public Map<String, String>  getstatus(String out_trade_no, String trade_no){
		System.out.println("out_trade_no"+out_trade_no);
		System.out.println("trade_no"+trade_no);
		logger.info("getstatus方法中的out_trade_no{},trade_no{}", out_trade_no,trade_no);
		Map<String, String> analysis = null;
		try {
			System.out.println("out_trade_no"+out_trade_no);
			System.out.println("trade_no"+trade_no);
			//签名并发送请求
			AlipayClient client = new DefaultAlipayClient(aliProp.getUnifiedorder(), aliProp.getAppId(), aliProp.getPrivateKey(), "json", "UTF8", aliProp.getAliPublicKey(), "RSA2"); //获得初始化的AlipayClient
			AlipayTradeQueryRequest alipay_request = new AlipayTradeQueryRequest();
			AlipayTradeQueryModel model= new AlipayTradeQueryModel();
			model.setOutTradeNo(out_trade_no);
			model.setTradeNo(trade_no);
			alipay_request.setBizModel(model);
			System.out.println(model.getOutTradeNo());
			System.out.println(model.getTradeNo());
			AlipayTradeQueryResponse alipay_response =client.execute(alipay_request);

			if(alipay_response.isSuccess()){
				logger.info("response.getBody()统一订单查询获取参数{}",alipay_response.getBody());
				logger.info("订单查询调用成功");
				analysis = analysis(alipay_response.getBody());
				logger.info("充值 订单  查询状态 ： 订单编号"+out_trade_no+" 返回信息："+analysis);
				System.out.println("调用成功");
			} else {
				logger.info("订单查询调用失败");
			}
		} catch (Exception e) {
			logger.error("交易状态 获取失败"+e);
		}
		return analysis;
	}


	public Map<String, String> analysis(String params){
		params = "["+params+"]";
		Map<String, String> map = new HashMap<String, String>();
		JSONArray arry_1 = JSONArray.fromObject(params);
		String alipay_trade_query_response="";
		for (int i = 0; i < arry_1.size(); i++){
			JSONObject jsonObject = arry_1.getJSONObject(i);
			for (Iterator<?> iter = jsonObject.keys(); iter.hasNext();){
				String key = (String) iter.next();
				if("alipay_trade_query_response".equals(key)){
					alipay_trade_query_response = "["+jsonObject.get(key).toString()+"]";
					break;
				}
			}
		}

		JSONArray arry_2 = JSONArray.fromObject(alipay_trade_query_response);
		for (int i = 0; i < arry_2.size(); i++){
			JSONObject jsonObject = arry_2.getJSONObject(i);
			for (Iterator<?> iter = jsonObject.keys(); iter.hasNext();){
				String key = (String) iter.next();

				if("code".equals(key)){
					String value = jsonObject.get(key).toString();
					map.put(key, value);
				}else if("trade_status".equals(key)){
					String value = jsonObject.get(key).toString();
					map.put(key, value);
				}
			}
		}
		map.put("msg", params);
		return map;
	}





	/**
	 * 创建订单
	 * 
	 * @param order
	 * @return
	 */
	//	@POST
	//	@Path("createOrder")
	//	@Produces(MediaType.TEXT_HTML)
	//	@Consumes(MediaType.APPLICATION_JSON)
	//	public String createOrder(BuyParam order){
	//		logger.info("收到订单请求:" + order);
	//		String form = "";
	//		Map<String, Object> data = service.createOrder(order);
	//		java.lang.StringBuffer sb = new StringBuffer();
	//		sb.append("<form name=\"punchout_form\" method=\"post\" action=\"");
	//		sb.append(aliProp.getUnifiedorder());
	//		sb.append("\">\n");
	//		sb.append(buildHiddenFields(data));
	//
	//		sb.append("<input type=\"submit\" value=\"提交\" style=\"display:none\" >\n");
	//		sb.append("</form>\n");
	//		sb.append("<script>document.forms[0].submit();</script>");
	//		form = sb.toString();
	//		return form;
	//	}

	//	"    \"seller_id\":"+aliProp.getPartner()+"," +
	//	private String buildHiddenFields(Map<String, Object> parameters) {
	//		if (parameters == null || parameters.isEmpty()) {
	//			return "";
	//		}
	//		java.lang.StringBuffer sb = new StringBuffer();
	//		Set<String> keys = parameters.keySet();
	//		for (String key : keys) {
	//			String value = String.valueOf(parameters.get(key));
	//			sb.append(buildHiddenField(key, value));
	//		}
	//		String result = sb.toString();
	//		return result;
	//
	//	}
	//
	//	private static String buildHiddenField(String key, String value) {
	//		java.lang.StringBuffer sb = new StringBuffer();
	//		sb.append("<input type=\"hidden\" name=\"");
	//		sb.append(key);
	//
	//		sb.append("\" value=\"");
	//		// 杞箟鍙屽紩鍙�
	//		String a = value.replace("\"", "&quot;");
	//		sb.append(a).append("\">\n");
	//		return sb.toString();
	//	}


	/**
	 * 支付宝回调 手机网站
	 * 
	 * @return
	 */
	//		@POST
	//		@Path("callback0")
	//		@Produces(MediaType.TEXT_PLAIN)
	//		public String callbackPhone() {
	//			logger.info("收到支付宝回调");
	//			// 支付宝回调格式是form格式
	//			Map<String, String[]> map = request.getParameterMap();
	//			logger.debug("支付宝回调map为：{}", map);
	//			// 解析支付宝回调
	//			Map<String, Object> data = new HashMap<String, Object>();
	//			for (Map.Entry<String, String[]> entry : map.entrySet()) {
	//				data.put(entry.getKey(), entry.getValue()[0]);
	//			}
	//			callbackService.callback(data);
	//			logger.info("支付宝回调完毕");
	//			return "success";
	//		}



	//				String out_trade_no1 = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"),"UTF-8");
	//				//支付宝交易号
	//				logger.info("支付宝交易号out_trade_no1是{}",out_trade_no1);
	//				
	//				//获取支付宝POST过来反馈信息
	//				Map<String,String> params = new HashMap<String,String>();
	//				Map requestParams = request.getParameterMap();
	//				logger.info("支付宝回调收到的map{}",requestParams);
	//				for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
	//					String name = (String) iter.next();
	//					String[] values = (String[]) requestParams.get(name);
	//					logger.info("收到的回调{}参数{}",name,values);
	//					String valueStr = "";
	//					for (int i = 0; i < values.length; i++) {
	//						valueStr = (i == values.length - 1) ? valueStr + values[i]
	//								: valueStr + values[i] + ",";
	//						logger.info("实际参数{}",valueStr);
	//					}
	//					//乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
	//					//valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
	//					params.put(name, valueStr);
	//				}
	//				//获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以下仅供参考)//
	//					//商户订单号
	//		
	//					String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"),"UTF-8");
	//					//支付宝交易号
	//					logger.info("支付宝交易号out_trade_no是{}",out_trade_no);
	//					String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"),"UTF-8");
	//					logger.info("商户号吗trade_no是{}",trade_no);
	//					//交易状态
	//					String trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"),"UTF-8");
	//					logger.info("交易状态trade_status是{}",trade_status);
	//					//获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以上仅供参考)//
	//					//计算得出通知验证结果
	//					//boolean AlipaySignature.rsaCheckV1(Map<String, String> params, String publicKey, String charset, String sign_type)
	//					boolean verify_result = AlipaySignature.rsaCheckV1(params, aliProp.getPublicKey(),"UTF-8", "RSA2");
	//					logger.info("验签结果{}",verify_result);
	//					if(verify_result){//验证成功
	//						logger.info("验签成功");
	//						//请在这里加上商户的业务逻辑程序代码
	//						if (trade_status.equals("TRADE_SUCCESS") || trade_status.equals("TRADE_FINISHED")) {
	//							logger.debug("支付成功");
	//							// 系统订单设置为已经支付状态（支付成功回调）
	//							agentService.payEnd("alipay",out_trade_no);
	//							logger.info("支付成功之后，执行本地修改操作也成功");
	//						} else {
	//							logger.info("支付状态为：{}，支付失败", trade_status);
	//							logger.warn("支付状态为：{}，支付失败", trade_status);
	//						}
	//		
	//						//——请根据您的业务逻辑来编写程序（以上代码仅作参考）——
	//						logger.info("支付宝回调完毕success");
	//						return "success";//请不要修改或删除
	//		
	//					}else{//验证失败
	//						logger.info("验签失败");
	//						logger.info("支付宝回调完毕fail");
	//						return "fail";
	//					}




	//	@POST
	//	@Path("callback")
	//	@Produces(MediaType.TEXT_PLAIN)
	//	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	//	public String call1(AliFormParam aliFormParam){
	//		logger.info("aliFormParam==",aliFormParam);
	//		return "1";
	//	}
	//	





	/**
	 * 统一收单线下交易查询
	 * 通知页面 判断是否支付成功
	 * @param out_trade_no	订单号
	 * @param trade_no		支付宝交易号
	 * @return
	 */
	public boolean judgeSuccess(String out_trade_no, String trade_no){
		//交易状态
		Map<String, String> getstatus = getstatus(out_trade_no, trade_no);
		logger.info("统一订单查询码getstatusMap为{}",getstatus);
		String code = getstatus.get("code");
		logger.info("统一订单查询码code为{}",code);
		//交易是否完成
		if("10000".equals(code)){
			logger.info("充值成功： 返回信息"+getstatus);
			return true;
		}else{
			logger.info("充值失败： 返回信息"+getstatus);
			return false;
		}
	}




//		public static void main(String[] args) {
	//		//		String param="params  == total_amount=0.01&buyer_id=2088812992784624&trade_no=2017041321001004620284941823&notify_time=2017-04-13+23%3A01%3A47&subject=%E5%85%85%E5%80%BC&sign_type=RSA2&buyer_logon_id=138****8143&auth_app_id=2017030906136143&charset=UTF-8&notify_type=trade_status_sync&invoice_amount=0.01&out_trade_no=20170413225453490040&trade_status=TRADE_SUCCESS&gmt_payment=2017-04-13+22%3A54%3A57&version=1.0&point_amount=0.00&sign=c51tfGYbrIOZqPBVm0kVCFP%2B16U1fe%2FNB4lXA3KDwccM2uTiFj8kz2ozzVcpzbl7va7T247EpdwHZTcPQ18O2pPRUjc8dElKnAZ%2BrF9pbbyqI5iu5WNMcOOsgq5COZgnrOC4AYwgfh8PDbB6PDaZabtVXh%2FvgWMhnKI%2BS9Ul1lln9bIapztLf3Tw5bzY4eEtA8n%2B%2FUzDKp91RXnlx5K%2F5ETpePSIVjm1LirnPaV1wokztjlZAuEGVeOMDn483h9XP9MoLfg5BukQkqb4DcWxmUVTEzGQix9AevQmJxcgQlJIRklLn06SnPwTO7wmHy5zi3uBxx9%2B%2B94OIpgGeRl1fg%3D%3D&gmt_create=2017-04-13+22%3A54%3A56&buyer_pay_amount=0.01&receipt_amount=0.01&fund_bill_list=%5B%7B%22amount%22%3A%220.01%22%2C%22fundChannel%22%3A%22ALIPAYACCOUNT%22%7D%5D&app_id=2017030906136143&seller_id=2088621498193175&notify_id=e875efcdeac99686fae6538edbb12e7ksa&seller_email=henanshanhui%40163.com";
	//		String param ="gmt_create=2017-08-05+11%3A48%3A00&charset=UTF8&seller_email=m15239137715%40163.com&subject=%E5%90%8D%E9%97%A8%E4%BA%92%E5%A8%B1-%E5%A5%97%E9%A4%90%E8%B4%AD%E4%B9%B0&sign=UaEONJk6RNd2pMknM4CeSgUj27P061GIqOXghnN3c0MrfX0S07B95YiKWDQOFviap58tHy%2BY3C8LAJckBcPhK1WzzcfCvsSogTEHkyhCW0meht8cHygeJYX%2BZeHKlK%2FkLJEr7rNcLRUw3JVWdM0vjvHwgJk5BS0QicMf9QgMCQnbiSQks4PEtOe40Pn5qouEHDXy%2BID9qex2P2Mvr5vP7Jm1%2BdG3qMp9oRpXmsKCnvXpQUifUYPbzvA%2F7yVpspcfkVaCar0y6pVljsdlqHb7iUF3yjWH2MLTXKlPiJGPNMEdhqsGD7awifaWspxza1%2FJh6jlYGYk9K4kSTzDrLbVyg%3D%3D&buyer_id=2088802134130194&invoice_amount=0.01&notify_id=882bb8cdeecf3774e3594024bc97825hgu&fund_bill_list=%5B%7B%22amount%22%3A%220.01%22%2C%22fundChannel%22%3A%22ALIPAYACCOUNT%22%7D%5D&notify_type=trade_status_sync&trade_status=TRADE_SUCCESS&receipt_amount=0.01&app_id=2017072807929109&buyer_pay_amount=0.01&sign_type=RSA2&seller_id=2088721427511147&gmt_payment=2017-08-05+11%3A48%3A01&notify_time=2017-08-05+11%3A48%3A01&version=1.0&out_trade_no=26a5b3e807964f3d9618c43e2fcd9b45&total_amount=0.01&trade_no=2017080521001004190230740832&auth_app_id=2017072807929109&buyer_logon_id=159****2551&point_amount=0.00";
	//		//		out_trade_no=c3bd41263846420a8f0b8bc75b7097eb&total_amount=0.01&trade_no=2017080421001004190229430256&auth_app_id=
//	 String param = "gmt_create=2017-08-07+00%3A03%3A46&charset=UTF8&"
//	 		+ "seller_email=m15239137715%40163.com&notify_time=2017-08-22+00%3A08%3A35&"
//	 		+ "subject=%E5%90%8D%E9%97%A8%E4%BA%92%E5%A8%B1-%E5%A5%97%E9%A4%90%E8%B4%AD%E4%B9%B0"
//	 		+ "&sign=jBzxiEuLXZHFZYnWL4BHSdKL3nmarvbMn%2FT9myGKRvKgOmfU7ynipO2%2FOmltKFmgwks5eVSNCK6VOC5O4YDKuzhGxnKZoHPgouqAynY8nTVIBd2gkW8%2BTXerDjJos2Y7rMPEpf8d0xv1748Qqr7PNYq3YRft5kP8Ji4izxBaAuQwQ94HHlS1XBoBkaKbgnEDg3PC3d4WcFKDOO3WpA9%2BaUYyjAgFTDoesPJshq%2F2pbKU9DzCCHZDqS76h4qQ6QlYysA0C8fxIh0adbropskX3leZlsz6HdUAuLv33otRB5692zAJwp88%2BLsFm%2B3IhHd2ed%2BMJvxeKGG6TPkK7KdJNw%3D%3D&b"
//	 		+ "uyer_id=2088722438021180&version=1.0&notify_id=677d12c9e85731c5c3ba40b986d02c0he2&notify_type=trade_status_sync"
//	 		+ "&out_trade_no=8f62bc04936445f5873e123e7cd8cbec&total_amount=0.01&"
//	 		+ "trade_status=TRADE_CLOSED&refund_fee=0.00&"
//	 		+ "trade_no=2017080721001004180297524031&auth_app_id=2017072807929109&gmt_close=2017-08-22+00%3A04%3A29&"
//	 		+ "buyer_logon_id=188****9147&app_id=2017072807929109&sign_type=RSA2&seller_id=2088721427511147";
//	 logger.info("out_trade_no=={},out_trade=={},trade_status={}",out_trade_no,trade_no,trade_status);
	 
	//		String substring1 = param.substring(param.indexOf("trade_status="));
	//		String trade_status=substring1.substring(substring1.indexOf("=")+1,substring1.indexOf("receipt_amount")-1);
	//		System.out.println("trade_status==="+trade_status);
	//
	//		String substring = param.substring(param.indexOf("out_trade_no="));
	//		String trade_no_And_out=substring.substring(substring.indexOf("=")+1,substring.indexOf("auth_app_id")-1);
	//		System.out.println(trade_no_And_out);
	//
	//		String out_trade_no=trade_no_And_out.substring(0,trade_no_And_out.indexOf("total_amount")-1);
	//		System.out.println("out_trade_no==="+out_trade_no);
	//
	//		String trade_no_And_out1 = trade_no_And_out.substring(trade_no_And_out.indexOf("trade_no="));
	//		String out_trade=trade_no_And_out1.substring(trade_no_And_out1.indexOf("=")+1);
	//		System.out.println("out_trade==="+out_trade);
	//
	//		//		&notify_type=trade_status_sync&trade_status=TRADE_SUCCESS&receipt_amount=0.01
	//		//		String substring2 = substring.substring(substring.indexOf("out_trade_no="));
	//		//		String out_trade_no=substring2.substring(substring2.indexOf("=")+1,substring2.indexOf("trade_status")-1);
	//		//		System.out.println(out_trade_no);
	//
//		}



	/**
	 * 支付宝回调 app
	 * 
	 * @return
	 */
	@POST
	@Path("appcallback")
	@Produces(MediaType.TEXT_PLAIN)
	public String callback() {
		// 支付宝回调格式是form格式
		Map<String, String[]> map = request.getParameterMap();
		logger.debug("支付宝回调map为：{}", map);
		// 解析支付宝回调
		Map<String, Object> data = new HashMap<String, Object>();
		for (Map.Entry<String, String[]> entry : map.entrySet()) {
			data.put(entry.getKey(), entry.getValue()[0]);
		}
		callbackService.callback(data);
		return "success";
	}

	/**
	 * 支付宝回调 web
	 * 
	 * @return
	 */
	@POST
	@Path("webCallback")
	@Produces(MediaType.TEXT_PLAIN)
	public String webCallback() {
		// 支付宝回调格式是form格式
		Map<String, String[]> map = request.getParameterMap();
		logger.debug("支付宝回调map为：{}", map);
		// 解析支付宝回调
		Map<String, Object> data = new HashMap<String, Object>();
		for (Map.Entry<String, String[]> entry : map.entrySet()) {
			data.put(entry.getKey(), entry.getValue()[0]);
		}
		callbackService.webCallback(data);
		return "success";
	}

	@GET
	@Path("test")
	@Produces(MediaType.APPLICATION_JSON)
	public BaseDTO<Object> test(@QueryParam("id") String id) {
		return service.foundPay(id);
	}

}
