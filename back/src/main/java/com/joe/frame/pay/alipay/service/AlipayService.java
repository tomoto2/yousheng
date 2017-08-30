package com.joe.frame.pay.alipay.service;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joe.frame.common.util.SignUtil;
import com.joe.frame.core.entity.Recharge;
import com.joe.frame.core.param.BuyParam;
import com.joe.frame.core.repository.RechargeRepository;
import com.joe.frame.core.trade.entity.OrderStatus;
import com.joe.frame.pay.alipay.dto.PayBusiness;
import com.joe.frame.pay.alipay.dto.PayByUser;
import com.joe.frame.pay.alipay.dto.PublicParam;
import com.joe.frame.pay.common.service.PayService;
import com.joe.frame.pay.prop.AliProp;
import com.joe.frame.web.cache.CacheServiceProxy;
import com.joe.frame.web.dto.NormalDTO;
import com.joe.frame.web.prop.SystemProp;
import com.joe.http.IHttpClientUtil;
import com.joe.parse.json.JsonParser;
import com.joe.secure.Encipher;
import com.joe.secure.MD5;
import com.joe.utils.DateUtil;
import com.joe.utils.Tools;

/**
 * 阿里支付service
 * 
 * @author joe
 *
 */
@Service
@Transactional
public class AlipayService extends PayService {
	private static final Logger logger = LoggerFactory.getLogger(AlipayService.class);
	private JsonParser parser = JsonParser.getInstance();
	private IHttpClientUtil client = new IHttpClientUtil();
	@Autowired
	private AliSignService aliSignService;
	@Autowired
	private AliProp aliProp;
	@Autowired
	private SystemProp systemProp;
	@Autowired
	private CacheServiceProxy cache;
	private Encipher encipher = new MD5();

	@Autowired
	private RechargeRepository rechargeRepository;

	/**
	 * 生成支付宝下单信息
	 * 
	 * @param dto
	 * @return
	 */
	public Map<String , Object> createOrder(BuyParam dto) {
		logger.debug("接收到订单请求{}", dto);
		Recharge order = rechargeRepository.find(dto.getOrderId());
		if (order == null) {
			return Collections.emptyMap();
		}
//		if (!order.getOrderStatus().equals(OrderStatus.CREATE)) {
//			return "订单超时，不能付款";
//		}

		logger.debug("支付宝支付，系统中的订单为：{}", parser.toJson(order));
		PublicParam unifiedOrder = new PublicParam();
		unifiedOrder.setApp_id(aliProp.getAppId());
		unifiedOrder.setMethod("alipay.trade.wap.pay");
		unifiedOrder.setTimestamp(DateUtil.getFormatDate("yyyy-MM-dd HH:mm:ss"));
		unifiedOrder.setNotify_url(aliProp.getNotifyUrl());
		// 业务参数
		PayBusiness business = new PayBusiness();
		business.setOut_trade_no(dto.getOrderId());
		// 金额
		business.setTotal_amount(String.valueOf((double) order.getMoney() / 100));
		// 设置超时时间，默认5分钟
		business.setTimeout_express("5m");
		// 设置标题信息
		business.setBody("购买房卡套餐");
		business.setSubject(order.getPname());//套餐名称
		// 设置商品类型为虚拟物品
		business.setGoods_type("0");
		business.setPassback_params(Tools.createNonceStr(10));
		unifiedOrder.setBiz_content(parser.toJson(business, true));
		// 放入缓存，收到回调或者超时删除，超时时间与订单超时时间相同，一定要在convert之后，如果在之前放入会缺少签名
		cache.put(dto.getOrderId(), unifiedOrder);
		// 获取要发送到支付宝的信息
		return convertToMap(unifiedOrder);
	}
	
	

//	@Override
	public boolean gateway(Recharge order) {
//		String nowTime = DateUtil.getFormatDate("yyyy-MM-dd HH:mm:ss");
//		// 公共参数
//		PublicParam param = new PublicParam();
//		param.setApp_id(aliProp.getAppId());
//		param.setMethod("alipay.trade.refund");
//		param.setTimestamp(nowTime);
//		// 业务参数
//		GatewayBusiness business = new GatewayBusiness();
//		business.setOut_trade_no(order.getId());
//		String amount = String.valueOf((double) order.getAmount() / 100);
//		business.setRefund_amount(amount);
//		param.setBiz_content(parser.toJson(business, true));
//		String data = convert(param);
//		// 要发送到支付宝的数据
//		logger.debug("要发送到支付宝的数据为：{}，开始发送请求", data);
//
//		try {
//			String result = client.executePost(aliProp.getGateway(), data, "UTF8", "UTF8",
//					"application/x-www-form-urlencoded;charset=UTF8");
//			logger.debug("支付宝响应信息为：{}", result);
//			// 暂时不对退款接口返回信息校验，默认发送成功就是退款成功
//			// 更改系统订单状态
//			logger.debug("订单{}退款", order.getId());
//			order.setGatewayTime(nowTime);
//			order.setRealGateway(nowTime);
//			return true;
//		} catch (Exception e) {
//			logger.debug("支付宝退款异常，异常原因：", e);
//			return false;
//		}
		return true;
	}

	/**
	 * 将param签名并转换为要发送到支付宝的字符串数据（FORM格式）
	 * 
	 * @param param
	 *            param（除了签名参数外的所有参数都要在调用前完善）
	 * @return
	 */
	private String convert(PublicParam param) {
		Map<String, Object> map = SignUtil.getSignFields(param);
		logger.debug("要签名的集合为：{}", map);
		// 获取要签名的数据
		String data = SignUtil.getSignDataFromMap(map, 3);
		logger.debug("要签名的数据为：{}", data);
		param.setSign(aliSignService.rsa256Sign(data, "UTF8"));
		logger.debug("订单对象为：{}", param);

		try {
			data = SignUtil.getSignDataFromMap(map, 1) + "&sign=" + URLEncoder.encode(param.getSign(), "UTF8");
			logger.debug("支付宝接口调用参数为：{}", data);
			return data;
		} catch (Exception e) {
			logger.error("支付宝付款出错", e);
			return "";
		}
	}
	
	
	private Map<String , Object> convertToMap(PublicParam param) {
		Map<String, Object> map = SignUtil.getSignFields(param);
		logger.debug("要签名的集合为：{}", map);
		// 获取要签名的数据
		String data = SignUtil.getSignDataFromMap(map, 3);
		logger.debug("要签名的数据为：{}", data);
		param.setSign(aliSignService.rsa256Sign(data, "UTF8"));
		logger.debug("订单对象为：{}", param);
		try {
			Map<String , Object> params = SignUtil.getSignFields(map);
			params.put("sign", param.getSign());
			return params;
		} catch (Exception e) {
			logger.error("支付宝付款出错", e);
			return Collections.emptyMap();
		}
	}

	/**
	 * 构建提现数据（返回form表单）
	 * 
	 * @param list
	 *            要提现的提现对象ID列表
	 * @return form表单（直接输出html）
	 */
//	public String cashout(List<String> list) {
//		// 建立提现对象
//		logger.debug("对{}进行提现", list);
//		// logger.debug("对日期进行判断");
//		// int i = dateUtil.getNow();
//		// System.out.println(i);
//		// logger.debug("今天是周{}", i);
//		// if (i != 3 || i != 6) {
//		// logger.debug("今天不能体现");
//		// return null;
//		// }
//		List<CashoutHistory> cashoutList = new ArrayList<CashoutHistory>(list.size());
//		// 遍历查询出来要提现的对象并且计算一些数据
//		long amount = 0;
//		StringBuilder sb = new StringBuilder();
//		String string = null;
//		for (String str : list) {
//			CashoutHistory cashout = cashoutHistoryRepository.find(str);
//			if (cashout != null && cashout.getWay().equalsIgnoreCase("alipay")) {
//				// 计算总金额
//				logger.debug("主播{}支付宝提现{}分到账号{}", cashout.getUid(), cashout.getAmount(), cashout.getAccount());
//				amount += cashout.getAmount();
//				cashoutList.add(cashout);
//				// 拼接详细数据
//
//				sb.append(cashout.getId()).append("^").append(cashout.getAccount()).append("^")
//				.append(cashout.getName()).append("^")
//				.append(String.valueOf((double) cashout.getAmount() / 100)).append("^").append("主播提现|");
//				string = sb.substring(0, sb.length() - 1).toString();
//			}
//		}
//		logger.debug("要体现的列表为：{}", cashoutList);
//		CashOut cashOut = new CashOut();
//		cashOut.setPartner(aliProp.getPartner());
//		cashOut.setNotify_url(aliProp.getCashOutNotifyUrl());
//		cashOut.setAccount_name("郑州撩撩科技有限公司");
//		cashOut.setEmail(aliProp.getAccount());
//		cashOut.setPay_date(DateUtil.getFormatDate("yyyyMMdd"));
//		cashOut.setBatch_no(cashOut.getPay_date() + Tools.createNonceStr(16));
//		// 提现笔数
//		cashOut.setBatch_num(String.valueOf(cashoutList.size()));
//		// 提现金额
//		cashOut.setBatch_fee(String.valueOf((double) amount / 100));
//		// 付款详细数据
//		String detail_data = string;
//		cashOut.setDetail_data(detail_data);
//
//		// 签名
//		// 获取签名数据
//		Map<String, Object> map = SignUtil.getSignFields(cashOut);
//		logger.debug("要签名的map为：{}", map);
//		String signData = SignUtil.getSignDataFromMap(map, 3);
//		// 拼接MD5密钥
//		signData = signData + aliProp.getMd5();
//		logger.debug("要签名的data为：{}", signData);
//		// 签名（MD5签名）
//		cashOut.setSign(encipher.encrypt(signData));
//		logger.debug("签名为：{}", cashOut.getSign());
//		map.put("sign", cashOut.getSign());
//		map.put("sign_type", "MD5");
//		logger.debug("要发送到支付宝的数据为：{}", map);
//		return buildRequest(map, "GET", "提交");
//	}

	/**
	 * 建立请求，以表单HTML形式构造（默认）
	 * 
	 * @param sParaTemp
	 *            请求参数数组
	 * @param strMethod
	 *            提交方式。两个值可选：post、get
	 * @param strButtonName
	 *            确认按钮显示文字
	 * @return 提交表单HTML文本
	 */
	private String buildRequest(Map<String, Object> sPara, String strMethod, String strButtonName) {
		// 待请求参数数组
		List<String> keys = new ArrayList<String>(sPara.keySet());

		StringBuffer sbHtml = new StringBuffer();

		sbHtml.append("<form id=\"alipaysubmit\" name=\"alipaysubmit\" action=\"" + aliProp.getCashOut()
		+ "?_input_charset=" + "UTF8" + "\" method=\"" + strMethod + "\">");

		for (int i = 0; i < keys.size(); i++) {
			String name = (String) keys.get(i);
			String value = (String) sPara.get(name);

			sbHtml.append("<input type=\"hidden\" name=\"" + name + "\" value=\"" + value + "\"/>");
		}

		// submit按钮控件请不要含有name属性
		sbHtml.append("<input type=\"submit\" value=\"" + strButtonName + "\" style=\"display:none;\"></form>");
		sbHtml.append("<script>document.forms['alipaysubmit'].submit();</script>");

		return sbHtml.toString();
	}

	/**
	 * 后台付款
	 * 
	 * @param dto 订单号
	 * @return
	 */
	public NormalDTO<Object> foundPay(String dto) {
		logger.debug("开始后台支付 订单号{}", dto);
		NormalDTO<Object> result = new NormalDTO<>();
		Recharge order = rechargeRepository.find(dto);
		if (order == null) {
			result.error("404", "系统中没有该订单");
			return result;
		}
		if (!order.getOrderStatus().equals(OrderStatus.CREATE)) {
			result.error("404", "订单超时，不能付款");
			return result;
		}
		PayByUser payByUser = new PayByUser();
		payByUser.setOut_trade_no(dto);// 订单ID
		payByUser.setSubject(order.getPname());// 商品名称
		payByUser.setTotal_fee(order.getMoney() / 100.0);// 交易金额
		payByUser.setBody("购买房卡套餐");// 详细说明
		payByUser.setIt_b_pay("5m");// 超时时间
		payByUser.setNotify_url(systemProp.getServerServiceUrl() + "/ws/pay/ali/callback");//回调地址（待修改----
//		dto.setNotify_url(systemProp.getServerServiceUrl() + "/ws/pay/ali/webCallback");
		Map<String, Object> map = SignUtil.getSignFields(payByUser);
		logger.debug("所有签名参数的map{}", map);
		String string = SignUtil.getSignDataFromMap(map, 3);// 获取签名参数
		logger.debug("代签名的参数{}", string);
		String sign = aliSignService.sign(string);// 获取签名
		logger.debug("签名结果{}", sign);
		map.put("sign", sign);
		map.put("sign_type", "RSA");
		List<String> keys = new ArrayList<String>(map.keySet());
		StringBuffer sbHtml = new StringBuffer();
		sbHtml.append(
				"<form id=\"alipaysubmit\" name=\"alipaysubmit\" action=\"" + "https://mapi.alipay.com/gateway.do?"
						+ "_input_charset=utf-8" + "\" method=\"" + "get" + "\" target=\"_blank\">");
		for (int i = 0; i < keys.size(); i++) {
			String name = keys.get(i);
			Object value = map.get(name);
			if (name.equals("out_trade_no")) {
				sbHtml.append("<input type=\"hidden\"  id=\"sysorId\" name=\"" + name + "\" value=\"" + value + "\"/>");
			}
			sbHtml.append("<input type=\"hidden\" name=\"" + name + "\" value=\"" + value + "\"/>");
		}
		sbHtml.append("<input type=\"submit\" value=\"" + "确认" + "\" style=\"display:none;\"></form>");
		sbHtml.append("<script>document.forms['alipaysubmit'].submit();</script>");
		cache.put(order.getId(), payByUser, 30 * 60);
		result.error("200", "成功");
		result.setData(sbHtml.toString());
		return result;

	}


}
