package com.joe.frame.pay.wechatpay.resource;

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

import com.joe.frame.core.param.BuyParam;
import com.joe.frame.pay.wechatpay.dto.NotifyDTO;
import com.joe.frame.pay.wechatpay.dto.PayOrder;
import com.joe.frame.pay.wechatpay.service.WechatOrderService;
import com.joe.frame.web.dto.NormalDTO;
import com.joe.parse.xml.XmlParser;

@Path("pay/wechat")
public class WechatOrderResource {
	private static final Logger logger = LoggerFactory.getLogger(WechatOrderResource.class);
	@Autowired
	private WechatOrderService orderService;
	private XmlParser xmlParser = XmlParser.getInstance();
	@Context
	private HttpServletRequest request;

	
	
	//支付过程中使用的是订单id--getOrderId
	/**
	 * 创建订单
	 * 
	 * @param order
	 * @return
	 */
	@POST
	@Path("createOrder")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public NormalDTO<PayOrder> createOrder(BuyParam order) {
		String ip = request.getRemoteAddr();
		logger.info("收到ip为{}的订单请求:{}", ip, order);
		NormalDTO<PayOrder> dto = orderService.createOrder(order, ip);
		return dto;
	}

	 /**
	 * 微信公众号内置购买创建订单
	 *
	 * @param order
	 * @return
	 */
	 @GET
	 @Path("H5CreateOrder")
	 @Produces(MediaType.APPLICATION_JSON)
	 public NormalDTO<Object> H5CreateOrder(@QueryParam("orderId") String orderId,@QueryParam("openId") String openId) {
	 String ip = request.getRemoteAddr();
//	 String ip = "192.168.0.110" ;
	 logger.info("收到ip为{}的订单请求:{}", ip);
	 return orderService.H5CreateOrder(orderId, ip,openId);
	 }

	/**
	 * 创建订单 生成二维码手机扫码支付
	 * 
	 * @param order
	 * @return
	 * 
	 */
	@GET
	@Path("Test")
	@Produces(MediaType.APPLICATION_JSON)
	public void createOrder(@QueryParam("id") String id) {
		logger.info("收到ip为{}的订单请求:{}", id);
		orderService.createOrder1(id);
	}

	/**
	 * 微信支付回调接口，返回支付结果
	 * 
	 * @param message
	 *            微信传来的参数
	 * @param request
	 *            请求信息
	 * @return 回复微信
	 */
	@POST
	@Path("wechatCallBack")
	@Produces(MediaType.TEXT_XML)
	public String wechatCallback(String msg) {
		NotifyDTO dto = new NotifyDTO();
		try {
			logger.info("收到微信支付回调，微信回调消息是：" + msg);
			orderService.callback(msg);
		} catch (Exception e) {
			logger.error("微信回调处理失败", e);
		}
		return xmlParser.toXml(dto, "xml", true);
	}

	/**
	 * 微信支付回调接口，返回支付结果
	 * 
	 * @param message
	 *            微信传来的参数
	 * @param request
	 *            请求信息
	 * @return 回复微信
	 */
	@POST
	@Path("H5wechatCallBack")
	@Produces(MediaType.TEXT_XML)
	public String H5wechatCallBack(String msg) {
		NotifyDTO dto = new NotifyDTO();
		try {
			logger.info("收到微信支付回调，微信回调消息是：" + msg);
			orderService.H5callback(msg);
		} catch (Exception e) {
			logger.error("微信回调处理失败", e);
		}
		return xmlParser.toXml(dto, "xml", true);
	}
}
