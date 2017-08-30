package com.joe.frame.pay.wechatpay.service;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joe.concurrent.LockService;
import com.joe.frame.common.secure.Encipher;
import com.joe.frame.common.secure.IBase64;
import com.joe.frame.common.secure.MD5;
import com.joe.frame.common.util.PojoUtils;
import com.joe.frame.common.util.SignUtil;
import com.joe.frame.core.entity.Recharge;
import com.joe.frame.core.param.BuyParam;
import com.joe.frame.core.repository.RechargeRepository;
import com.joe.frame.core.service.AgentService;
import com.joe.frame.core.trade.entity.OrderStatus;
import com.joe.frame.pay.common.dto.OrderDTO;
import com.joe.frame.pay.common.service.PayService;
import com.joe.frame.pay.common.service.SupportService;
import com.joe.frame.pay.prop.WechatApiProp;
import com.joe.frame.pay.prop.WechatProp;
import com.joe.frame.pay.wechatpay.dto.DownloadBillDTO;
import com.joe.frame.pay.wechatpay.dto.H5PayOrder;
import com.joe.frame.pay.wechatpay.dto.PayOrder;
import com.joe.frame.pay.wechatpay.dto.RefundDTO;
import com.joe.frame.pay.wechatpay.dto.UnifiedOrderDTO;
import com.joe.frame.pay.wechatpay.entity.WechatOrder;
import com.joe.frame.pay.wechatpay.exception.WechatException;
import com.joe.frame.pay.wechatpay.repository.WechatOrderRepository;
import com.joe.frame.pay.wechatpay.type.WechatOrderStatus;
import com.joe.frame.web.dto.BaseDTO;
import com.joe.frame.web.dto.NormalDTO;
import com.joe.frame.web.exception.CodeException;
import com.joe.frame.web.exception.NetException;
import com.joe.frame.web.prop.SystemProp;
import com.joe.http.IHttpClientUtil;
import com.joe.parse.xml.XmlParser;
import com.joe.utils.DateUtil;
import com.joe.utils.Tools;
@Service	
@Transactional
public class WechatOrderService extends PayService {
	private static final Logger logger = LoggerFactory.getLogger(WechatOrderService.class);
	private IHttpClientUtil client = new IHttpClientUtil();
	// 退款用client，带有证书
	private IHttpClientUtil gatewayClient;
	@Autowired
	private SupportService supportService;
	@Autowired
	private WechatProp wechatPayProp;
	@Autowired
	private MD5 md5;
	@Resource(type = IBase64.class)
	private Encipher iBase64;
	@Autowired
	private WechatOrderRepository wechatOrderRepository;
	private XmlParser xmlParser = XmlParser.getInstance();
	@Autowired
	private WechatApiProp wechatApiProp;
//	@Autowired
//	private LockService lockService;
	@Autowired
	private HttpServletRequest request;
	@Autowired
	private SystemProp systemProp;
	
	@Autowired
	private RechargeRepository rechargeRepository;
	@Autowired
	private AgentService agentService;

	public WechatOrderService() {
		// 初始化httpclient
//		try {
//			// 商户id
//			String MCH_ID = wechatPayProp.getMch_id();
//			// 指定读取证书格式为PKCS12
//			KeyStore keyStore = KeyStore.getInstance("PKCS12");
//			String path = wechatPayProp.getCertPath();// 证书路径
//			// 读取本机存放的PKCS12证书文件
//			FileInputStream instream = new FileInputStream(new File(path));
//			try {
//				// 指定PKCS12的密码(商户ID)
//				keyStore.load(instream, MCH_ID.toCharArray());
//			} finally {
//				instream.close();
//			}
//			SSLContext sslcontext = SSLContexts.custom().loadKeyMaterial(keyStore, MCH_ID.toCharArray()).build();
//
//			this.gatewayClient = new IHttpClientUtil(IHttpClient.builder().sslContext(sslcontext).build());
//		} catch (Exception e) {
//			throw new RuntimeException("SSL证书加载失败", e);
//		}
	}

	public void downloadbill(String date, String type) {
		DownloadBillDTO dto = new DownloadBillDTO();
		dto.setAppid(wechatPayProp.getAppid());
		dto.setBill_date(date);
		dto.setBill_type(type);
		dto.setDevice_info("454645");
		dto.setMch_id(wechatPayProp.getMch_id());
		dto.setNonce_str(Tools.createNonceStr(10));
		dto.setSign(sign(dto));
	}

	/**
	 * 处理微信下单后的回调
	 * 
	 * @param message
	 *            微信传来的参数
	 * @return 处理结果
	 */
	public void callback(String message) {
		dealResponse(message, "微信支付回调", true);
	}

	/**
	 * 生成订单
	 * 
	 * @param orderDTO
	 *            订单DTO
	 * @return 返回prepay_id参数，用于后续支付
	 * 
	 * @throws WechatException
	 *             当微信返回错误信息时返回该异常
	 * @throws NetException
	 *             当网络异常时返回该异常
	 */
	public NormalDTO<PayOrder> createOrder(BuyParam orderDTO, String ip) throws WechatException, NetException {
		logger.info("开始生成微信订单：{}", orderDTO);
		NormalDTO<PayOrder> dto = null;
		// 生成微信订单
		WechatOrder order = new WechatOrder();
		Recharge sysOrder = rechargeRepository.find(orderDTO.getOrderId());
		if (sysOrder == null) {
			dto = new NormalDTO<PayOrder>();
			dto.error("404", "系统中没有该订单");
			return dto;
		}

		// 查看订单是否是创建状态
		if (!sysOrder.getOrderStatus().equals(OrderStatus.CREATE)) {
			dto = new NormalDTO<PayOrder>();
			dto.error("404", "订单超时，不能付款");
			return dto;
		}

		// 设置订单号
		order.setOut_trade_no(orderDTO.getOrderId());
		String time_start = DateUtil.getFormatDate("yyyyMMddHHmmss");
		String time_expire = DateUtil.getFormatDate("yyyyMMddHHmmss",
				DateUtil.add(DateUtil.MINUTE, wechatPayProp.getTimeout()));
		order.setTime_start(time_start);
		order.setTime_expire(time_expire);
		order.setStatus(WechatOrderStatus.CREATE);
		order.setSpbill_create_ip(ip);
		// 补全信息
		supportService.addWechatInfo(order);
		// 不能使用信用卡支付
		order.setLimit_pay("no_credit");

		if (wechatOrderRepository.find(order.getOut_trade_no()) == null) {
			wechatOrderRepository.persist(order);
		} else {
			logger.warn("系统中已经存在该微信订单了");
		}

		logger.info("系统订单为：" + order);
		// 此处可以加上参数校验校验必要参数是否完成

		logger.debug("系统微信订单生成完毕，订单信息：" + order);
		logger.debug("准备发送订单");
		try {
			dto = sendOrder(order);
		} catch (Exception e) {
			logger.info("订单发送失败，将订单{}从缓存删除", orderDTO);
			throw e;
		}
		logger.debug("订单发送完毕");
		return dto;
	}

	/**
	 * 生成订单
	 * 
	 * @param orderDTO
	 *            订单DTO
	 * @return
	 * @return 返回prepay_id参数，用于后续支付
	 * 
	 */
	public NormalDTO<Object> createOrder1(String id) throws WechatException, NetException {
		logger.info("开始生成微信订单：{}");
		String ip = request.getRemoteAddr();
		OrderDTO orderDTO = new OrderDTO();
		orderDTO.setOrderId(id);

		NormalDTO<Object> dto = new NormalDTO<>();
		// 生成微信订单
		WechatOrder order = new WechatOrder();
		Recharge sysOrder = rechargeRepository.find(orderDTO.getOrderId());
		if (sysOrder == null) {
			dto.error("404", "系统中没有该订单");
			return dto;
		}

		// 查看订单是否是创建状态
		if (!sysOrder.getOrderStatus().equals(OrderStatus.CREATE)) {
			dto.error("404", "订单超时，不能付款");
			return dto;
		}

		// 设置订单号
		order.setOut_trade_no(orderDTO.getOrderId());
		String time_start = DateUtil.getFormatDate("yyyyMMddHHmmss");
		String time_expire = DateUtil.getFormatDate("yyyyMMddHHmmss",
				DateUtil.add(DateUtil.MINUTE, wechatPayProp.getTimeout()));
		order.setTime_start(time_start);
		order.setTime_expire(time_expire);
		order.setStatus(WechatOrderStatus.CREATE);
		order.setSpbill_create_ip(ip);
		// 补全信息
		supportService.addWechatInfo(order);
		// 不能使用信用卡支付
		order.setLimit_pay("no_credit");

		if (wechatOrderRepository.find(order.getOut_trade_no()) == null) {
			wechatOrderRepository.persist(order);
		} else {
			logger.warn("系统中已经存在该微信订单了");
		}

		logger.info("系统订单为：" + order);
		// 此处可以加上参数校验校验必要参数是否完成

		logger.debug("系统微信订单生成完毕，订单信息：" + order);
		logger.debug("准备发送订单");

		String string = sendOrder1(order);// 向微信发送预支付订单信息
		if (string != null) {
			dto.error("200", "成功");
			dto.setData(string);
			return dto;
		}
		logger.debug("微信二维码信息生成出错");
		return dto;
	}

	public boolean gateway(Recharge order) {
		RefundDTO refundDTO = new RefundDTO();
		refundDTO.setAppid(wechatPayProp.getAppid());
		refundDTO.setMch_id(wechatPayProp.getMch_id());
		refundDTO.setNonce_str(Tools.createNonceStr(10));
		refundDTO.setOut_trade_no(order.getId());
		refundDTO.setOut_refund_no(order.getId());
		refundDTO.setTotal_fee((int) order.getMoney()/100);
		refundDTO.setRefund_fee((int) order.getMoney()/100);
		refundDTO.setOp_user_id(wechatPayProp.getMch_id());
		// 设置签名
		refundDTO.setSign(sign(refundDTO));
		logger.debug("签名为：{}", refundDTO.getSign());
		// 获取要发送到微信的数据
		String data = xmlParser.toXml(refundDTO, "xml", false);
		try {
			logger.debug("要发送到微信的数据位：{}", data);
			String result = gatewayClient.executePost(wechatApiProp.getRefund(), data);
			logger.debug("微信响应的数据为：{}", result);
			return dealResponse(result, "微信退款", false);
		} catch (Exception e) {
			logger.error("微信退款异常", e);
			return false;
		}
	}

	/**
	 * 处理微信响应结果
	 * 
	 * @param message
	 *            微信响应数据
	 * @param methodName
	 *            微信接口名（记录日志用）
	 * @param checkSign
	 *            是否需要验证签名（支付回调接口选true，其他false）
	 * @return 微信业务是否成功，true为成功
	 */
	private boolean dealResponse(String message, String methodName, boolean checkSign) {
		TreeMap<String, Object> map = xmlParser.parse(message);
		String return_code = String.valueOf(map.get("return_code"));
		if (!"success".equalsIgnoreCase(return_code)) {
			logger.error("{}失败，失败原因：return_msg={}", methodName, map.get("return_msg"));
			return false;
		} else if (!"success".equalsIgnoreCase(String.valueOf(map.get("result_code")))) {
			logger.error("{}失败，失败原因：err_code={}；err_code_des={}", methodName, map.get("err_code"),
					map.get("err_code_des"));
			return false;
		}

		if (checkSign) {
			// 订单回调接口需要验证签名
			boolean flag = false;
			// 商户订单号
			String out_trade_no = (String) map.get("out_trade_no");
			LockService.lock(out_trade_no);

			try {
//				try {
//					if (!lock.tryLock(1, TimeUnit.SECONDS)) {
//						return false;
//					}
//				} catch (Exception e) {
//					logger.error("微信响应处理异常", e);
//					return false;
//				}
				flag = true;
				WechatOrder order = wechatOrderRepository.find(out_trade_no);
				if (order == null) {
					logger.warn("微信订单为null，不处理");
					return false;
				}
				if (!(order.getStatus().equals(WechatOrderStatus.CREATE)
						|| order.getStatus().equals(WechatOrderStatus.UNPAID))) {
					logger.warn("订单{}已经处理过，无需再次处理", order);
					return false;
				}

				// 应结订单金额
				int total_fee = Integer.parseInt((String) map.get("total_fee"));
				// 微信订单号
				String transaction_id = (String) map.get("transaction_id");
				// 支付完成时间
				String time_end = (String) map.get("time_end");
				// 支付银行
				String bank_type = (String) map.get("bank_type");
				// 返回签名
				String sign = (String) map.get("sign");
				map.remove("sign");

				String mySign = signByMap(map);
				logger.debug("数据库中的order为：" + order);// 标记
				// 验证订单是否存在、金额是否正确，签名是正确
				if (order != null && order.getTotal_fee() == total_fee && mySign.equals(sign)) {
					// 支付成功
					order.setSettlement_total_fee(total_fee);
					order.setTransaction_id(transaction_id);
					order.setTime_end(time_end);
					order.setBank_type(bank_type);
					order.setStatus(WechatOrderStatus.PAID);
					// 系统订单支付事件
					agentService.payEnd("wechatpay",out_trade_no );
					return true;
				} else {
					// 支付失败
					if (order != null) {
						String time_expire = order.getTime_expire();
						String nowTime = DateUtil.getFormatDate("yyyyMMddHHmmss");
						if (Long.parseLong(nowTime) >= Long.parseLong(time_expire)) {
							// 订单过期
							order.setStatus(WechatOrderStatus.EXPIRE);
						} else {
							order.setStatus(WechatOrderStatus.FAIL);
						}
					}
					logger.error("{}失败，订单不存在、签名错误或者订单支付金额不对；微信订单号：{}；商户订单号：{}", methodName, transaction_id,
							out_trade_no);
					return false;
				}
			} finally {
				if (flag) {
					// 解开锁并销毁
					LockService.unlock(out_trade_no);
				}
			}
		} else {
			// 其他接口不需要验证签名
			return true;
		}

	}

	/**
	 * 往微信发送订单
	 * 
	 * @param order
	 *            系统订单
	 * @return
	 * 
	 * @throws WechatException
	 * @throws NetException
	 * @throws WechatException
	 */
	private NormalDTO<Object> H5SendOrder(WechatOrder order) {
		NormalDTO<Object> baseDTO = new NormalDTO<Object>();
		// 向微信发送订单数据
		UnifiedOrderDTO dto = H5InitOrder(order);

		String data = xmlParser.toXml(dto, "xml", false);
		try {
			logger.debug("开始向微信发送订单");
			logger.debug("发送往微信的订单为：" + data);
			// 当存在中文时需要用这种编码格式编码一下
			String result = client.executePost(wechatApiProp.getUnifiedorder(),
					new String(data.getBytes("UTF8"), "ISO8859-1"));
			logger.debug("微信的响应信息为：" + result);

			Map<String, Object> map = xmlParser.parse(result);
			String return_code = (String) map.get("return_code");

			if ("success".equalsIgnoreCase(return_code)) {
				String result_code = (String) map.get("result_code");
				if ("success".equalsIgnoreCase(result_code)) {
					// 订单发送成功
					String prepay_id = (String) map.get("prepay_id");
					// 前端调用需要的数据
					H5PayOrder h5PayOrder = new H5PayOrder();
					h5PayOrder.setAppId(wechatPayProp.getAppid());
					h5PayOrder.setPackages("prepay_id=" + prepay_id);
					h5PayOrder.setTimeStamp(String.valueOf((System.currentTimeMillis() / 1000)));
					h5PayOrder.setNonceStr(Tools.createNonceStr(10));
					h5PayOrder.setSignType("MD5");
					h5PayOrder.setPaySign(H5Sign(h5PayOrder));
					baseDTO.setData(h5PayOrder);
					return baseDTO;
				} else {
					String err_code = (String) map.get("err_code");
					String err_code_des = (String) map.get("err_code_des");
					throw new WechatException(err_code, err_code_des);
				}

			} else {
				throw new NetException();
			}
		} catch (IOException e) {
			logger.error("微信订单发送失败" + e);
			throw new CodeException("999", "未知原因", e);
		}
	}

	/**
	 * 初始化订单
	 * 
	 * @param 商户订单
	 * @return 要发送的订单数据
	 */
	private UnifiedOrderDTO H5InitOrder(WechatOrder order) {
		logger.debug("要签名的order为：" + order);
		// 转换DTO
		UnifiedOrderDTO dto = PojoUtils.copy(order, UnifiedOrderDTO.class);
		dto.setAppid(wechatPayProp.getAppid());
		dto.setMch_id(wechatPayProp.getMch_id());
		dto.setNonce_str(Tools.createNonceStr(10));
		dto.setNotify_url(systemProp.getServerServiceUrl() + "/ws/pay/wechat/H5wechatCallBack");
		dto.setSign(H5Sign(dto));

		return dto;
	}

	/**
	 * 往微信发送订单
	 * 
	 * @param order
	 *            系统订单
	 * @return
	 * 
	 * @throws WechatException
	 * @throws NetException
	 * @throws WechatException
	 */
	private NormalDTO<PayOrder> sendOrder(WechatOrder order) {

		NormalDTO<PayOrder> baseDTO = new NormalDTO<PayOrder>();
		// 向微信发送订单数据
		UnifiedOrderDTO dto = initOrder(order);
		String data = xmlParser.toXml(dto, "xml", false);
		try {
			logger.debug("开始向微信发送订单");
			logger.debug("发送往微信的订单为：" + data);
			// 当存在中文时需要用这种编码格式编码一下
			String result = client.executePost(wechatApiProp.getUnifiedorder(),
					new String(data.getBytes("UTF8"), "ISO8859-1"));
			logger.debug("微信的响应信息为：" + result);

			Map<String, Object> map = xmlParser.parse(result);
			String return_code = (String) map.get("return_code");

			if ("success".equalsIgnoreCase(return_code)) {

				String result_code = (String) map.get("result_code");
				if ("success".equalsIgnoreCase(result_code)) {
					// 订单发送成功
					String prepay_id = (String) map.get("prepay_id");
					// 前端调用需要的数据
					PayOrder payOrder = new PayOrder();
					// payOrder.setAppid("wx46370f57b95970e8");
					payOrder.setAppid(wechatPayProp.getAppid());
					payOrder.setPartnerid(wechatPayProp.getMch_id());
					// payOrder.setPartnerid("1480073142");
					payOrder.setPrepayid(prepay_id);
					payOrder.setNoncestr(Tools.createNonceStr(10));
					payOrder.setTimestamp((long) (System.currentTimeMillis() / 1000));
					payOrder.setPaySign(sign(payOrder));
					payOrder.setOut_trade_no(order.getOut_trade_no());
					baseDTO.setData(payOrder);
					return baseDTO;
				} else {
					String err_code = (String) map.get("err_code");
					String err_code_des = (String) map.get("err_code_des");
					throw new WechatException(err_code, err_code_des);
				}

			} else {
				throw new NetException();
			}
		} catch (IOException e) {
			logger.error("微信订单发送失败" + e);
			throw new CodeException("999", "未知原因", e);
		}
	}

	/**
	 * 往微信发送订单 二维码订单
	 * 
	 * @param order
	 *            系统订单
	 * @return
	 * @return
	 * 
	 * @throws WechatException
	 * @throws NetException
	 * @throws WechatException
	 */
	private String sendOrder1(WechatOrder order) {

		// 向微信发送订单数据
		UnifiedOrderDTO dto = initOrder1(order);
		String data = xmlParser.toXml(dto, "xml", false);
		try {
			logger.debug("开始向微信发送订单");
			logger.debug("发送往微信的订单为：" + data);
			// 当存在中文时需要用这种编码格式编码一下
			String result = client.executePost(wechatApiProp.getUnifiedorder(),
					new String(data.getBytes("UTF8"), "ISO8859-1"));
			logger.debug("微信的响应信息为：" + result);

			Map<String, Object> map = xmlParser.parse(result);
			String return_code = (String) map.get("return_code");
			if ("success".equalsIgnoreCase(return_code)) {

				String result_code = (String) map.get("result_code");
				if ("success".equalsIgnoreCase(result_code)) {
					logger.debug("生成二维码的信息{}", (String) map.get("code_url"));
					return (String) map.get("code_url");
				} else {
					String err_code = (String) map.get("err_code");
					String err_code_des = (String) map.get("err_code_des");
					throw new WechatException(err_code, err_code_des);
				}

			} else {
				throw new NetException();
			}
		} catch (IOException e) {
			logger.error("微信订单发送失败" + e);
			throw new CodeException("999", "未知原因", e);
		}
	}

	/**
	 * 初始化订单
	 * 
	 * @param 商户订单
	 * @return 要发送的订单数据
	 */
	private UnifiedOrderDTO initOrder(WechatOrder order) {
		logger.debug("要签名的order为：" + order);
		// 转换DTO
		UnifiedOrderDTO dto = PojoUtils.copy(order, UnifiedOrderDTO.class);
		dto.setAppid(wechatPayProp.getAppid());
		dto.setMch_id(wechatPayProp.getMch_id());
		dto.setNonce_str(Tools.createNonceStr(10));
		dto.setNotify_url(wechatPayProp.getNotify_url());
//		dto.setTrade_type("JSAPI");// 微信公告号内 -设置交易类型
		dto.setTrade_type("APP");// app支付-设置交易类型
		dto.setSign(sign(dto));
		return dto;
	}

	/**
	 * 初始化订单
	 * 
	 * @param 商户订单
	 * @return 要发送的订单数据 dto.setTrade_type("NATIVE"); 生成支付二维码类型
	 */
	private UnifiedOrderDTO initOrder1(WechatOrder order) {
		logger.debug("要签名的order为：" + order);
		// 转换DTO
		UnifiedOrderDTO dto = PojoUtils.copy(order, UnifiedOrderDTO.class);
		dto.setAppid(wechatPayProp.getAppid());
		dto.setMch_id(wechatPayProp.getMch_id());
		dto.setNonce_str(Tools.createNonceStr(10));
		dto.setNotify_url(wechatPayProp.getNotify_url());
		dto.setTrade_type("NATIVE");
		dto.setSpbill_create_ip("185.165.29.41");
		dto.setSign(sign(dto));
		return dto;
	}

	/**
	 * 签名
	 * 
	 * @param dto
	 *            要签名的对象
	 * @return
	 */
	private String sign(Object dto) {
		return signByMap(SignUtil.getSignFields(dto));
	}

	/**
	 * 签名
	 * 
	 * @param dto
	 *            要签名的对象
	 * @return
	 */
	private String H5Sign(Object dto) {
		return H5SignByMap(SignUtil.getSignFields(dto));
	}

	private String H5SignByMap(Map<String, Object> params) {
		logger.debug("签名方法调用参数为：{}", params);
		String data = SignUtil.getSignDataFromMap(params, 3);
//		data = data + "&key=" + "3ABB098EC42C354E9172263665984F72";//商户的key
		data = data + "&key=" + "XACjFlLfiJksbnKX5nEPC90IweenXXbK";//商户的key
		logger.debug("要签名的数据为：" + data);
		String result = md5.encrypt(data).toUpperCase();
		logger.debug("签名结果：" + result);
		return result;
	}

	/**
	 * 签名
	 * 
	 * @param params
	 * @return
	 */
	private String signByMap(Map<String, Object> params) {
		logger.debug("签名方法调用参数为：{}", params);
		String data = SignUtil.getSignDataFromMap(params, 3);
		data = data + "&key=" + wechatPayProp.getKey();

		logger.debug("要签名的数据为：" + data);
		String result = md5.encrypt(data).toUpperCase();
		logger.debug("签名结果：" + result);
		return result;
	}

	public NormalDTO<Object> H5CreateOrder(String orderId, String ip, String openId) {
		logger.info("开始生成微信订单：{}", orderId);
		OrderDTO orderDTO = new OrderDTO();
		orderDTO.setOrderId(orderId);
		NormalDTO<Object> dto = null;
		// 生成微信订单
		WechatOrder order = new WechatOrder();
		
		order.setTrade_type("JSAPI");// 设置交易类型
		order.setOpenid(openId);
		Recharge sysOrder = rechargeRepository.find(orderDTO.getOrderId());
		if (sysOrder == null) {
			dto = new NormalDTO<Object>();
			dto.error("404", "系统中没有该订单");
			return dto;
		}

		// 查看订单是否是创建状态
		if (!sysOrder.getOrderStatus().equals(OrderStatus.CREATE)) {
			dto = new NormalDTO<Object>();
			dto.error("404", "订单超时，不能付款");
			return dto;
		}

		// 设置订单号
		order.setOut_trade_no(orderDTO.getOrderId());
		String time_start = DateUtil.getFormatDate("yyyyMMddHHmmss");
		String time_expire = DateUtil.getFormatDate("yyyyMMddHHmmss",
				DateUtil.add(DateUtil.MINUTE, wechatPayProp.getTimeout()));
		order.setTime_start(time_start);
		order.setTime_expire(time_expire);
		order.setStatus(WechatOrderStatus.CREATE);
		order.setSpbill_create_ip(ip);
		// 补全信息
		supportService.addWechatInfo(order);
		// 不能使用信用卡支付
		order.setLimit_pay("no_credit");

		if (wechatOrderRepository.find(order.getOut_trade_no()) == null) {
			wechatOrderRepository.persist(order);
		} else {
			logger.warn("系统中已经存在该微信订单了");
		}

		logger.info("系统订单为：" + order);
		// 此处可以加上参数校验校验必要参数是否完成

		logger.debug("系统微信订单生成完毕，订单信息：" + order);
		logger.debug("准备发送订单");
		try {

			dto = H5SendOrder(order);

		} catch (Exception e) {
			logger.info("订单发送失败，将订单{}从缓存删除", orderDTO);
			throw e;
		}
		logger.debug("订单发送完毕");
		return dto;
	}

	public void H5callback(String message) {
		H5dealResponse(message, "微信支付回调", true);

	}

	private boolean H5dealResponse(String message, String methodName, boolean checkSign) {
		TreeMap<String, Object> map = xmlParser.parse(message);
		String return_code = String.valueOf(map.get("return_code"));
		if (!"success".equalsIgnoreCase(return_code)) {
			logger.error("{}失败，失败原因：return_msg={}", methodName, map.get("return_msg"));
			return false;
		} else if (!"success".equalsIgnoreCase(String.valueOf(map.get("result_code")))) {
			logger.error("{}失败，失败原因：err_code={}；err_code_des={}", methodName, map.get("err_code"),
					map.get("err_code_des"));
			return false;
		}

		if (checkSign) {
			// 订单回调接口需要验证签名
			boolean flag = false;
			// 商户订单号
			String out_trade_no = (String) map.get("out_trade_no");
			LockService.lock(out_trade_no);

			try {
//				try {
//					if (!lock.tryLock(1, TimeUnit.SECONDS)) {
//						return false;
//					}
//				} catch (Exception e) {
//					logger.error("微信响应处理异常", e);
//					return false;
//				}
				flag = true;
				WechatOrder order = wechatOrderRepository.find(out_trade_no);
				if (order == null) {
					logger.warn("微信订单为null，不处理");
					return false;
				}
				if (!(order.getStatus().equals(WechatOrderStatus.CREATE)
						|| order.getStatus().equals(WechatOrderStatus.UNPAID))) {
					logger.warn("订单{}已经处理过，无需再次处理", order);
					return false;
				}

				// 应结订单金额
				int total_fee = Integer.parseInt((String) map.get("total_fee"));
				// 微信订单号
				String transaction_id = (String) map.get("transaction_id");
				// 支付完成时间
				String time_end = (String) map.get("time_end");
				// 支付银行
				String bank_type = (String) map.get("bank_type");
				// 返回签名
				String sign = (String) map.get("sign");
				map.remove("sign");

				String mySign = H5SignByMap(map);
				logger.debug("数据库中的order为：" + order);// 标记
				// 验证订单是否存在、金额是否正确，签名是正确
				if (order != null && order.getTotal_fee() == total_fee && mySign.equals(sign)) {
					// 支付成功
					order.setSettlement_total_fee(total_fee);
					order.setTransaction_id(transaction_id);
					order.setTime_end(time_end);
					order.setBank_type(bank_type);
					order.setStatus(WechatOrderStatus.PAID);
					// 系统订单支付事件
					agentService.payEnd("wechatpay",out_trade_no);
					return true;
				} else {
					// 支付失败
					if (order != null) {
						String time_expire = order.getTime_expire();
						String nowTime = DateUtil.getFormatDate("yyyyMMddHHmmss");
						if (Long.parseLong(nowTime) >= Long.parseLong(time_expire)) {
							// 订单过期
							order.setStatus(WechatOrderStatus.EXPIRE);
						} else {
							order.setStatus(WechatOrderStatus.FAIL);
						}
					}
					logger.error("{}失败，订单不存在、签名错误或者订单支付金额不对；微信订单号：{}；商户订单号：{}", methodName, transaction_id,
							out_trade_no);
					return false;
				}
			} finally {
				if (flag) {
					// 解开锁并销毁
					LockService.unlock(out_trade_no);
				}
			}
		} else {
			// 其他接口不需要验证签名
			return true;
		}

	}

}
