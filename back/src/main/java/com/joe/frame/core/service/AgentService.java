package com.joe.frame.core.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;
import javax.persistence.LockModeType;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.ws.rs.core.Context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joe.frame.common.secure.Encipher;
import com.joe.frame.common.secure.MD5;
import com.joe.frame.common.util.DateUtil;
import com.joe.frame.common.util.OrderNum;
import com.joe.frame.common.util.PojoUtils;
import com.joe.frame.common.util.Tools;
import com.joe.frame.core.dto.CashOutDTO;
import com.joe.frame.core.dto.ChildYejiDTO;
import com.joe.frame.core.dto.ClubDTO;
import com.joe.frame.core.dto.MonthDTO;
import com.joe.frame.core.entity.Account;
import com.joe.frame.core.entity.CashOut;
import com.joe.frame.core.entity.Club;
import com.joe.frame.core.entity.ClubNotice;
import com.joe.frame.core.entity.PlayerRecharge;
import com.joe.frame.core.entity.Proxy;
import com.joe.frame.core.entity.Recharge;
import com.joe.frame.core.entity.User;
import com.joe.frame.core.param.AgentParam;
import com.joe.frame.core.param.BuyParam;
import com.joe.frame.core.param.CashOutParam;
import com.joe.frame.core.param.ChongZhiParam;
import com.joe.frame.core.param.ClubNoticeParam;
import com.joe.frame.core.param.PageParam;
import com.joe.frame.core.param.RechargeParam;
import com.joe.frame.core.param.SearchParam;
import com.joe.frame.core.param.UserInfoParam;
import com.joe.frame.core.repository.AccountRepository;
import com.joe.frame.core.repository.CashOutRepository;
import com.joe.frame.core.repository.ClubNoticeRepository;
import com.joe.frame.core.repository.ClubRepository;
import com.joe.frame.core.repository.PlayerRechargeRepository;
import com.joe.frame.core.repository.ProxyRepository;
import com.joe.frame.core.repository.RechargeRepository;
import com.joe.frame.core.repository.UsersRepository;
import com.joe.frame.core.trade.entity.OrderStatus;
import com.joe.frame.pay.prop.WechatProp;
import com.joe.frame.pay.wechatpay.dto.PayOrder;
import com.joe.frame.pay.wechatpay.service.WechatOrderService;
import com.joe.frame.web.dto.NormalDTO;
import com.lpx.wx.send.gettoken.WeiXinAccessTokenUtil;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

@Service
@Transactional
public class AgentService {
	private static final Logger logger = LoggerFactory.getLogger(AgentService.class);
	@Autowired
	private Tools tools;
	@Autowired
	private DateUtil dateUtil;
	private Encipher encipher = new MD5();
	@Autowired
	private RechargeRepository rechargeRepository;
	@Autowired
	private PlayerRechargeRepository playerRechargeRepository;
	@Autowired
	private UsersRepository usersRepository;// 玩家
	@Autowired
	private ProxyRepository proxyRepository;// 代理商
	@Autowired
	private CashOutRepository cashOutRepository;// 代理商钱包
	@Autowired
	private AccountRepository accountRepository;
	@Autowired
	private ClubNoticeRepository clubNoticeRepository;
	@Autowired
	private ClubRepository clubRepository;
	@Autowired
	private WechatOrderService orderService;
	@Context
	private HttpServletRequest request;
	@Autowired
	private WechatProp wechatPayProp;

	public int saveUser(User u) {
		usersRepository.merge(u);
		return 0;
	}
	
	//测试发送消息
	public void senmsg(){
		/**
		 * 发送消息
		 * @param pid 代理32位id
		 * @param money 支付金额 单位元
		 * @param goodsMsg 商品信息(商品名字，商品数量)
		 * @param proxyNum 代理编号()
		 * @param dateTime 订单支付时间
		 * @param orderNum 订单编号
		 */
		Recharge order = new Recharge();
		order.setId("1");
		order.setMoney(20000);
		order.setCreateTime("2017-9-10");
		order.setNumber(10);
		order.setOrderNum("2017010101");
		order.setAgentUid("d4f0828cf03845c7b93c0ce437d2a45c");
		
		String orderMoney = String.valueOf(order.getMoney()/100);
		String ordernum = String.valueOf(order.getNumber());
	    sentAll(order.getAgentUid(),orderMoney,order.getPname(),ordernum,order.getCreateTime(),order.getOrderNum());
	}

	/**
	 * 获取所有代理
	 * 
	 * @return
	 */
	public List<Proxy> getAllproxy() {
		List<Proxy> list = proxyRepository.findAll();
		return list;
	}

	/**
	 * 查询代理个人信息
	 * 
	 * @param uid
	 *            代理id
	 * @return
	 */
	public Proxy getProxy(String uid) {
		logger.info("根据id查询查询代理{}信息", uid);
		return proxyRepository.fingByPid(uid);
	}

	/**
	 * 注册用户
	 * 
	 * @param parentId/邀请码
	 *            推荐人ID
	 * @param username
	 *            用户昵称
	 * @param password
	 *            登陆密码
	 * @param phone
	 *            登录账号（手机号码）
	 * @param isSpecial
	 *            (是否是特殊账号）
	 * @return
	 */
	public byte register(String parentId, String username, String password, String phone, String isSpecial) {
		Account account = accountRepository.find(phone);
		Proxy parent = null;
		if (parentId == null) {// 必填字段
			return 3;
		}
		// 通过邀请码，自己注册
		if (parentId.length() == 6) {
			logger.info("通过邀请码{}查询代理信息", parentId);
			parent = proxyRepository.fingByCode(parentId);
		} else {
			// 代理内部发展
			logger.info("注册代理内部发展，父级为{}", parentId);
			parent = proxyRepository.find(parentId);
		}
		if (account != null || parent == null) {
			return 2;
		}

		// 初始化登陆账号
		String uid = tools.createUUID();
		account = new Account();
		account.setId(phone);
		account.setDatetime(dateUtil.getFormatDate("yyyy-MM-dd HH:mm:ss"));
		account.setPassword(encipher.encrypt(password));
		account.setUid(uid);
		account.setCanUse("true");// 开启账户
		accountRepository.persist(account);
		logger.info("添加用户登陆账号为：{}", account);

		// 初始化一个俱乐部
		String cid = tools.createUUID();// club ID
		String invateCode = OrderNum.getSix();// 邀请码=俱乐部编号=代理编号
		Club club = new Club();
		club.setCid(cid);// id
		club.setNumId(invateCode);
		logger.info("邀请码=俱乐部编号=代理编号 {}", club.getNumId());
		club.setOwner(username);// 拥有者昵称
		club.setName(username + "的俱乐部");
		logger.info("个俱乐部名称：{}", club.getName());
		club.setOwnerId(uid);// 拥有者id
		clubRepository.persist(club);
		logger.info("初始化一个俱乐部：{}", club);

		// 初始化个人信息
		Proxy info = new Proxy();
		info.setPid(uid);
		info.setCid(cid);// 俱乐部id
		info.setNikeName(username);// 昵称
		// info.setName("");
		info.setPhone(phone);
		info.setClubName(username + "的俱乐部");
		info.setInviteTime(account.getDatetime());
		info.setFather(parent.getPid());// 上级id
		info.setParent(parent);
		info.setLevel(parent.getLevel() + 1);// 设置关系等级，
		info.setInviteCode(invateCode);// 邀请码
		info.setNumId(invateCode);// 编号
		parent.getChilds().add(info);// 设置直属下级
		if (isSpecial == null) {
			info.setIsSpecial("false");// 是否是特殊账号 默认不是
		} else {
			info.setIsSpecial(isSpecial);// 设置特殊账号
		}
		proxyRepository.persist(info);
		logger.info("初始化个人信息为：{}", info);
		return 0;
	}

	/**
	 * 代理商给玩家充值房卡
	 * 
	 * @param id
	 *            代理商id
	 * @param param
	 *            房卡信息
	 */
	public int chongzhi(ChongZhiParam param, String id) {
		Proxy p = proxyRepository.find(id);// 代理id
		if (param.getCartSum() > p.getCard()) {
			logger.info("无法充值代理商{}的房卡不足", id);
			return 1;
		}
		// 修改玩家房卡数量
		User u = usersRepository.getUserByMunber(param.getUid(), LockModeType.PESSIMISTIC_WRITE);
		if (u != null) {
			u.setCard(param.getCartSum() + u.getCard());
			playerRecharge(param, id, param.getUid());// 生成记录
			logger.info("充值成功，生成充值记录");
			logger.info("玩家充值房卡，修改代理商房卡数量");
			p.setCard(p.getCard() - param.getCartSum());// 修改代理房卡数量
		}
		return 2;
	}

	/**
	 * 初始化记录，生成新充值房卡纪录
	 * 
	 * @param agentid
	 *            代理商id
	 * @param playerid
	 *            玩家id
	 */
	public void playerRecharge(ChongZhiParam param, String agentid, long playerid) {
		PlayerRecharge recharge = new PlayerRecharge();
		String prId = tools.createUUID();
		recharge.setId(prId);
		recharge.setCartSum(param.getCartSum());
		// recharge.setDatetime(dateUtil.getFormatDate("yyyy-MM-dd HH:mm:ss"));
		recharge.setDatetime(dateUtil.getFormatDate("yyyyMMddHHmmss"));
		recharge.setAgentUid(agentid);// 代理商id
		recharge.setUNumId(playerid);// 玩家编号
		playerRechargeRepository.persist(recharge);
		logger.info("代理商{}为玩家{}购买房卡", agentid, playerid);
	}

	/**
	 * 
	 * 代理商个人购买房卡
	 * 
	 * @param param
	 *            购买房卡信息
	 * @param id
	 *            代理商id
	 * @return
	 */
	public String buy(BuyParam param, String id) {
		logger.info("{}开始购买房卡", id);
		String datas = "";
		// 调起支付
		param.getOrderId();// 订单编号
		param.getMoney();// 支付金额
		if ("alipay".equals(param.getPayWay())) {
			// datas = alipayService.createOrder(param);
		} else {
			String ip = request.getRemoteAddr();
			logger.info("收到ip为{}的订单请求:{}", ip, param);
			NormalDTO<PayOrder> dto = orderService.createOrder(param, ip);
		}
		return datas;

		// // 支付成功-- 修改代理商房卡数量
		// Proxy agentUser = proxyRepository.find(id);
		// if (agentUser != null) {
		// agentUser.setCard(agentUser.getCard() + param.getNumber());
		// }
	}

	/**
	 * 支付成功之后，修改用户资金，修改订单状态（ali和微信都会回调该函数）
	 * 
	 * @param uid
	 *            代理id(支付人)
	 * @param orderNum
	 *            订单id
	 * @param param
	 *            订单详情
	 */
	public void payEnd(String payWay, String orderNum) {
		logger.debug("支付成功，更改订单{}状态", orderNum);
		// 修改订单状态信息
		Recharge order = rechargeRepository.find(orderNum);
		if (order == null) {
			throw new NullPointerException("系统订单" + orderNum + "为null");
		}
		// 更新订单状态
		order.setOrderStatus(OrderStatus.PAID);
		order.setStatus("2");// 已支付
		order.setPayWay(payWay);
		order.setPayTime(dateUtil.getFormatDate("yyyy-MM-dd HH:mm:ss"));
		// 支付成功-- 修改代理商房卡数量
		logger.debug("支付成功，修改代理商房卡数量");
		if (order.getUid() == null || "".equals(order.getUid())) {// 玩家不存在，
			// 代理商个人充值
			if (order.getAgentUid() != null) {
				Proxy agentUser = proxyRepository.fingByPid(order.getAgentUid());
				if (agentUser != null) {
					agentUser.setCard(agentUser.getCard() + order.getNumber());
				}
				logger.debug("支付成功，修改代理商房卡数量成功");
			}
		} else {
			// 玩家自己充值 //1存在于俱乐部 //2是自由玩家
			// 为玩家添加上房卡
			User user = usersRepository.getUserByMunber(Long.parseLong(order.getUid()), LockModeType.PESSIMISTIC_WRITE);
			user.setCard(user.getCard() + order.getNumber());
			logger.debug("支付成功，修改玩家房卡数量成功");
		}
		
		
		/**
		 * 发送消息
		 * @param pid 代理32位id
		 * @param money 支付金额 单位元
		 * @param goodsMsg 商品信息(商品名字，商品数量)
		 * @param proxyNum 代理编号()
		 * @param dateTime 订单支付时间
		 * @param orderNum 订单编号
		 */
		String orderMoney = String.valueOf(order.getMoney()/100);
		String ordernum = String.valueOf(order.getNumber());
	    sentAll(order.getAgentUid(),orderMoney,order.getPname(),ordernum,order.getCreateTime(),order.getOrderNum());

	}

//	-------------------------消息推送开始

	/**
	 * 发送消息
	 * @param pid 代理32位id
	 * @param money 支付金额
	 * @param goodsMsg 商品信息
	 * @param remark 备注
	 * @param dateTime 订单支付时间
	 * @param orderNum 订单编号
	 * @return
	 */
	public String sentAll(String pid,String money,String goodsName,String goodsNum,String dateTime,String orderNum){
		//		String templat_id = "";//模板id
		String clickurl="";//点击链接跳转的URl
		String topcolor="#000";//主题颜色
		//获取代理体系列表
		ArrayList<Proxy> users = getProxySet(pid);
		if(users!=null){
			Proxy thisProxy = users.get(0);//购买者
			String result = null;
			for(Proxy proxy:users){
				String open_id =  proxy.getOpenId();//用户openID、
				JSONObject data=null;
				if(open_id != null){
					//创建交易提醒json包;
					String firstMsg = "[名门斗牛]: 您有新的订单消息";
					String remark = "代理编号："+thisProxy.getNumId() +"\n" + "订单编号:"+orderNum +"\n下单时间:"+ dateTime;
					String goodsMsg  = "商品名称："+goodsName  + "\n" + " 商品数量："+goodsNum;
					data = packJsonmsg(firstMsg, money, goodsMsg, remark);
					//发送交易提醒模板消息;
					result = sendWechatmsgToUser(open_id,wechatPayProp.getTemplat_id(),clickurl,topcolor,data);
					if(result.equals("success")){
						System.out.println("推送成功!");
					}
				}
			}
			return result;
		}
		return null;
	}
	
	
	/**
	 * 获取当前购买套餐的代理体系，要推送的代理信息
	 * @param pid 当前代理的32位ids
	 * @return 整个代理体系的详细信息
	 */
	public ArrayList<Proxy> getProxySet(String pid){
		ArrayList<Proxy> list = new ArrayList<Proxy>();
		try {
			Proxy p = proxyRepository.find(pid, LockModeType.PESSIMISTIC_WRITE);//获取该代理
			logger.info("获取购买套餐的代理信息{}",p);
			int level = p.getLevel();
			//		循环出该代理体系所有代理   
			for(int j= 1;j<=level;j++){
				list.add(p);
				p = p.getParent();//获取该代理的上级
			}
		}catch(Exception e){
			logger.info("获取当前购买套餐的代理体系，要推送的代理信息异常{}",e);
			System.out.println(e);
		}
		logger.info("查找该代理体系所有上级,便于推送消息",list);
		return list ;
	}

	/**
	 * @描述: TODO(封装微信模板:订单支付成功) 
	 * @参数@param first  头部
	 * @参数@param orderMoneySum  总金额
	 * @参数@param orderProductName  商品信息
	 * @参数@param remark  说明
	 * @参数@return
	 * @返回类型：JSONObject
	 * @作者：***
	 */
	public static JSONObject packJsonmsg(String first, String orderMoneySum, String orderProductName, String remark){
		JSONObject json = new JSONObject();
		try {
			JSONObject jsonFirst = new JSONObject();
			jsonFirst.put("value", first);
			jsonFirst.put("color", "#173177");
			json.put("first", jsonFirst);
			JSONObject jsonOrderMoneySum = new JSONObject();
			jsonOrderMoneySum.put("value", orderMoneySum);
			jsonOrderMoneySum.put("color", "#173177");
			json.put("orderMoneySum", jsonOrderMoneySum);
			JSONObject jsonOrderProductName = new JSONObject();
			jsonOrderProductName.put("value", orderProductName);
			jsonOrderProductName.put("color", "#173177");
			json.put("orderProductName", jsonOrderProductName);
			JSONObject jsonRemark = new JSONObject();
			jsonRemark.put("value", remark);
			jsonRemark.put("color", "#173177");
			json.put("Remark", jsonRemark);
			logger.info("封装消息模板为{}",json);
		} catch (JSONException e) {
			logger.info("封装消息模板异常{}",e);
			e.printStackTrace();
		}
		return json;
	}



	/**
	 * @描述: TODO(发送模板信息给用户) 
	 * @参数@param touser  用户的openid
	 * @参数@param templat_id  信息模板id
	 * @参数@param url  用户点击详情时跳转的url
	 * @参数@param topcolor  模板字体的颜色
	 * @参数@param data  模板详情变量 Json格式packJsonmsg()
	 * @参数@return
	 * @返回类型：String
	 * @作者：***
	 */
	public String sendWechatmsgToUser(String touser, String templat_id, String clickurl, String topcolor, JSONObject data){
		String tmpurl = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=ACCESS_TOKEN";
		String token = WeiXinAccessTokenUtil.getAccessToken(wechatPayProp.getAppid(), wechatPayProp.getSecret());  //微信凭证，access_token
		//String token = "bm_D7meDjNHT0hcg09erM2ULKg5jqzWZGvHpFvDnJpSfmym-8_470wBqFkZfvB1sJR07xlQjMmDYIm6P2rMEyg5rXHD2F_h1qgrSoUDwTPcH8Pfu-IKssQmzNITjAf4UKXPcAIAANF";
		logger.info("获取token为{}",token);
		String url = tmpurl.replace("ACCESS_TOKEN", token);
		JSONObject json = new JSONObject();
		try {
			json.put("touser", touser);
			json.put("template_id", templat_id);
			json.put("url", clickurl);
			json.put("topcolor", topcolor);
			json.put("data", data);
			logger.info("推送模板消息{}");
		} catch (JSONException e) {
			logger.info("推送模板消息异常{}",e);
			e.printStackTrace();
		}
		String result = httpsRequest(url, "POST", json.toString());
		logger.info("推送消息结果{}",result);
		try {
			JSONObject resultJson =  JSONObject.fromObject(result);
			String errmsg = (String) resultJson.get("errmsg");
			if(!"ok".equals(errmsg)){  //如果为errmsg为ok，则代表发送成功，公众号推送信息给用户了。
				return "error";
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return "success";
	}

	/**
	 * 发送请求推送消息
	 * @param requestUrl 向微信发送请求的地址
	 * @param requestMethod POST方法
	 * @param outputStr 消息数据
	 * @return
	 */
	public String httpsRequest(String requestUrl, String requestMethod, String outputStr){
		try {
			URL url = new URL(requestUrl);
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			// 设置请求方式（GET/POST）
			conn.setRequestMethod(requestMethod);
			conn.setRequestProperty("content-type", "application/x-www-form-urlencoded");
			// 当outputStr不为null时向输出流写数据
			if (null != outputStr) {
				OutputStream outputStream = conn.getOutputStream();
				// 注意编码格式
				outputStream.write(outputStr.getBytes("UTF-8"));
				outputStream.close();
			}
			// 从输入流读取返回内容
			InputStream inputStream = conn.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			String str = null;
			StringBuffer buffer = new StringBuffer();
			while ((str = bufferedReader.readLine()) != null) {
				buffer.append(str);
			}
			// 释放资源
			bufferedReader.close();
			inputStreamReader.close();
			inputStream.close();
			inputStream = null;
			conn.disconnect();
			return buffer.toString();
		} catch (ConnectException ce) {
			System.out.println("连接超时：{}");
			logger.info("连接超时，消息推送失败",ce);
		} catch (Exception e) {
			System.out.println("https请求异常：{}");
			logger.info("https请求异常，消息推送失败,{}",e);
		}
		return null;
	}

//	-----------------------消息推送结束
	
	
	/**
	 * 获取代理商给用户的充值记录 分页查询 page从1开始
	 * 
	 * @param uid
	 *            代理商id
	 * @param pageparam
	 *            分页数据
	 * @return
	 */
	public List<ChongZhiParam> getPalyerRechargeList(String uid, PageParam pageParam) {
		List<PlayerRecharge> list = playerRechargeRepository.findList(uid, pageParam.getPageNo(), pageParam.getSize());
		List<ChongZhiParam> list1 = PojoUtils.copy(list, ChongZhiParam.class);
		for (ChongZhiParam p : list1) {
			User u = usersRepository.getUserByMunber(p.getUNumId(), LockModeType.PESSIMISTIC_WRITE);// 玩家信息
			p.setNickName(u.getName());// 玩家昵称
			// p.setNumId(u.getNumId());//玩家编号
			p.setCard(u.getCard());// 剩余房卡数量
		}
		logger.info("{}给用户的充值记录{}", uid, list1);
		return list1;
	}

	/**
	 * 获取代理商给用户的充值记录总数
	 * 
	 * @param uid
	 *            代理id
	 * @return
	 */
	public long getgetPalyerRechargeListSize(String uid) {
		long size = playerRechargeRepository.findListSize(uid);
		logger.info(" 获取代理商给用户的充值记录总数", size);
		return size;
	}

	/**
	 * 获取代理商个人的充值记录 分页查询 page从1开始
	 * 
	 * @param uid
	 *            代理商id
	 * @param pageparam
	 *            分页数据
	 * @return 订单编号，套餐名称，支付方式，支付状态，支付时间
	 */
	public List<RechargeParam> getAgentRechargeList(String uid, PageParam pageParam) {
		List<Recharge> list = rechargeRepository.findList(uid, pageParam.getPageNo(), pageParam.getSize());
		// List<RechargeParam> list1 = PojoUtils.copy(list,
		// RechargeParam.class);
		List<RechargeParam> list1 = new ArrayList<RechargeParam>();
		for (Recharge recharge : list) {
			RechargeParam param = new RechargeParam();
			OrderStatus orderstatus = recharge.getOrderStatus();
			String orderstate = "订单状态";
			switch (orderstatus) {
			case CREATE:
				orderstate = "订单已创建，未支付";
				break;
			case PAID:
				orderstate = "支付成功";
				break;
			default:
				orderstate = "订单状态";
				break;
			}
			// 订单编号，套餐名称，金额/张数，日期，支付状态，支付方式
			param.setStatus(orderstate);// 设置订单状态
			param.setPname(recharge.getPname());
			param.setPayWay(recharge.getPayWay());
			param.setOrderNum(recharge.getOrderNum());
			param.setCreateTime(recharge.getCreateTime());
			param.setMoney(recharge.getMoney() / 100);// 转为元
			param.setNumber(recharge.getNumber());
			list1.add(param);
		}

		logger.info("{}的充值记录{}", uid, list1);
		return list1;
	}

	/**
	 * 获取代理商个人的充值记录总数
	 * 
	 * @param uid
	 *            代理商id
	 */
	public long getAgentRechargeListSum(String uid) {
		long sum = rechargeRepository.getAgentRechargeListSum(uid);
		return sum;
	}

	/**
	 * 获取代理商个人某个日期的充值记录 分页查询 page从1开始
	 * 
	 * @param uid
	 *            代理商id
	 * @param pageparam
	 *            分页数据
	 * @return 订单编号，套餐名称，支付方式，支付状态，支付时间
	 */
	public List<RechargeParam> getCZDayList(String uid, SearchParam searchParam) {
		List<Recharge> list = rechargeRepository.findDayList(uid, searchParam.getSearchTime(), searchParam.getPageNo(),
				searchParam.getSize());
		// List<RechargeParam> list1 = PojoUtils.copy(list,
		// RechargeParam.class);
		List<RechargeParam> list1 = new ArrayList<RechargeParam>();
		for (Recharge recharge : list) {
			RechargeParam param = new RechargeParam();
			OrderStatus orderstatus = recharge.getOrderStatus();
			String orderstate = "订单状态";
			switch (orderstatus) {
			case CREATE:
				orderstate = "订单已创建，未支付";
				break;
			case PAID:
				orderstate = "支付成功";
				break;
			default:
				orderstate = "订单状态";
				break;
			}
			// 订单编号，套餐名称，金额/张数，日期，支付状态，支付方式
			param.setStatus(orderstate);// 设置订单状态
			param.setPname(recharge.getPname());
			param.setPayWay(recharge.getPayWay());
			param.setOrderNum(recharge.getOrderNum());
			param.setCreateTime(recharge.getCreateTime());
			param.setMoney(recharge.getMoney() / 100);// 转为元
			param.setNumber(recharge.getNumber());
			list1.add(param);
		}
		logger.info("获取{},在{}时间的充值记录{}", uid, searchParam, list1);
		return list1;
	}

	/**
	 * 获取代理商个人某个日期的充值记录 总数
	 * 
	 * @param uid
	 *            代理商id
	 */
	public long getCZDayListSum(String uid, String searchTime) {
		long sum = rechargeRepository.findDayListSum(uid, searchTime);
		logger.info("获取{},在{}时间的充值记录条数{}", uid, searchTime);
		return sum;
	}

	/**
	 * 判断能否提现，若能则添加申请提现记录
	 * 
	 * @param param
	 * @param uid
	 * @return 1 提现金额超出账户余额，0提现申请成功
	 */
	public byte applyCash(CashOutParam param, String uid) {
		Proxy p = proxyRepository.find(uid);
		if ((param.getMoney() * 100) > p.getBalance()) {
			logger.info("提现金额超出账户余额");
			return 1;// 提现金额超出账户余额
		} else {
			// 提现操作---重新设置用户资金
			addApplyCashOut(param, uid);
			return 0;
		}
	}

	/**
	 * 查询本月是否已经进行了提现；
	 * 
	 * @param uid
	 *            代理id（提现人id）
	 * @return true 本月已经提现，，false本月未提现
	 */
	public boolean isCashOutThisMonth(String uid) {
		CashOut cashOut = cashOutRepository.isCashOutThisMonth(uid, dateUtil.getFormatDate("yyyy-MM"));// 查询本月是否进行提现
		logger.info("查询本月是否已经进行了提现 记录为", cashOut);
		if (cashOut != null) {
			// 本月是否已经进行过提现，已经提现，不可以再提现
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 添加提现记录
	 * 
	 * @param param
	 *            提现申请参数
	 * @param uid
	 *            代理id
	 */
	private void addApplyCashOut(CashOutParam param, String uid) {
		logger.info("开始添加{}的提现记录", uid);
		CashOut cashOut = new CashOut();
		cashOut.setId(tools.createUUID());
		cashOut.setDateTime(dateUtil.getFormatDate("yyyy-MM-dd HH:mm:ss"));
		cashOut.setUid(uid);// 申请提现的人
		cashOut.setStatus("0");// 已申请
		cashOut.setDealWith("0");// 未处理
		cashOut.setMoney(param.getMoney() * 100);// 提现金额
		cashOut.setType(param.getType());// 交易类型）ali wechat
		cashOut.setCashAliOrwechat(param.getCashAliOrwechat());// 提现到的账号
		cashOutRepository.merge(cashOut);
		logger.info("添加{}的提现记录成功{}", uid, cashOut);
		Proxy p = proxyRepository.find(uid);
		p.setBalance(p.getBalance() - param.getMoney() * 100);
		logger.info("提现申请成功，重新设置用户{}资金账号余额为{}}", uid, p.getAgentLevel());

	}

	/**
	 * 查询代理商的钱包操作记录（提现） page从1开始
	 * 
	 * @param uid
	 *            代理商id
	 * @param pageNo
	 *            当前页
	 * @param size
	 *            每页大小
	 * @return 所有记录
	 */
	public List<CashOutDTO> getAgentCashOut(String uid, PageParam pageParam) {
		List<CashOut> list = cashOutRepository.getAgentCashOut(uid, pageParam.getPageNo(), pageParam.getSize());
		List<CashOutDTO> list1 = PojoUtils.copy(list, CashOutDTO.class);
		logger.info("{}的提现记录{}", uid, list);
		Proxy p = proxyRepository.fingByPid(uid);
		for (CashOutDTO dto : list1) {
			dto.setMoney(dto.getMoney() / 100);
			dto.setNikeName(p.getNikeName());// 昵称
			dto.setWeChat(p.getWx());// 微信号
			dto.setAlipay(p.getAlipay());
			dto.setUid(uid);
		}
		return list1;
	}

	/**
	 * 查询代理商的钱包操作记录（提现） page从1开始
	 * 
	 * @param uid
	 *            代理商id
	 * @param pageNo
	 *            当前页
	 * @param size
	 *            每页大小
	 * @return 所有记录
	 */
	public long getAgentCashOutSum(String uid) {
		long sum = cashOutRepository.getAgentCashOutSum(uid);
		logger.info("查询代理商的提现总条数{}", sum);
		return sum;
	}

	/**
	 * 获取uid下的直属下级
	 * 
	 * @param uid
	 *            代理商id
	 * @return
	 */
	public List<UserInfoParam> getDirectChild(String uid) {
		List<Proxy> list = proxyRepository.getDirectChild(uid);
		logger.info("获取{}下的直属下级{}", uid, list);
		List<UserInfoParam> list1 = new ArrayList<UserInfoParam>();
		for (Proxy p : list) {
			UserInfoParam userparam = new UserInfoParam();
			userparam.setChildPhone(p.getPhone());
			userparam.setInviteTime(p.getInviteTime());
			userparam.setLocation(p.getLocation());
			userparam.setPnumId(p.getNumId());// 代理编号
			userparam.setNikeName(p.getNikeName());
			list1.add(userparam);
		}
		return list1;
	}

	/**
	 * 添加/修改 俱乐部公告
	 * 
	 * @param clubNoticeParam
	 *            公告详情
	 * @param uid
	 *            代理商id
	 */
	public void addNotice(ClubNoticeParam clubNoticeParam, String uid) {
		// if(clubNoticeParam.getCnid() != null){
		// ClubNotice clubNotice =
		// clubNoticeRepository.find(clubNoticeParam.getCnid());
		// clubNotice.setDateTime(dateUtil.getFormatDate("yyyy-MM-dd
		// HH:mm:ss"));
		// clubNotice.setText(clubNoticeParam.getText());
		// logger.info("开始为代理{}的俱乐部修改公告",uid);
		// }else{
		// logger.info("开始为代理{}的俱乐部添加公告",uid);
		// ClubNotice clubNotice = new ClubNotice();
		// clubNotice.setCnid(tools.createUUID());// 公告id
		// clubNotice.setDateTime(dateUtil.getFormatDate("yyyy-MM-dd
		// HH:mm:ss"));
		// clubNotice.setText(clubNoticeParam.getText());
		// String clubID = proxyRepository.find(uid).getCid();// 该代理所在的俱乐部id
		// if (clubID != null || !("".equals(clubID)))
		// clubNotice.setCid(clubID);// 俱乐部id
		// clubNoticeRepository.merge(clubNotice);
		// logger.info("代理{}的俱乐部添加公告成功",uid);
		// }
		logger.info("开始为代理{}的俱乐部添加公告", uid);
		ClubNotice clubNotice = new ClubNotice();
		clubNotice.setCnid(tools.createUUID());// 公告id
		clubNotice.setDateTime(dateUtil.getFormatDate("yyyy-MM-dd HH:mm:ss"));
		clubNotice.setText(clubNoticeParam.getText());
		String clubID = proxyRepository.find(uid).getCid();// 该代理所在的俱乐部id
		if (clubID != null || !("".equals(clubID)))
			clubNotice.setCid(clubID);// 俱乐部id

		// 根据代理id查询俱乐部
		Club club = clubRepository.findByAgentUid(uid);
		// Club club = clubRepository.find(clubID);
		if (club != null) {
			club.setCnid(clubNotice.getCnid());
		}

		clubNoticeRepository.merge(clubNotice);
		logger.info("代理{}的俱乐部添加公告成功", uid);
	}

	/**
	 * 查询某代理旗下的 俱乐部公告
	 * 
	 * @param cnid
	 *            公告id
	 * @return
	 */
	public ClubNotice getNotice(String uid) {
		// 查询uid是在哪个俱乐部，--俱乐部id。eq(notice。getcid)
		Club club = clubRepository.findByAgentUid(uid);
		if (club != null) {
			ClubNotice notice = clubNoticeRepository.getNotice(club.getCid());
			return notice;
		}
		return null;
	}

	/**
	 * 获取所有下级的数量
	 * 
	 * @param uid
	 * @return
	 */
	public long getTeamSum(String uid) {
		long allSum = proxyRepository.getAllSum(uid);
		logger.info("{}旗下的代理总数{}", uid, allSum);
		return allSum;
	}

	/**
	 * 获取某个日期之前的旗下代理总数
	 * 
	 * @param uid
	 * @param searchTime
	 *            格式：yyyy-MM-dd HH:mm:ss
	 * @return
	 * @throws ParseException
	 */
	public long getTeamSumByTime(String uid, String searchTime) throws ParseException {
		long allSum = proxyRepository.getAllNewSum(uid, searchTime);
		logger.info("{}之前的{}旗下的代理总数{}", searchTime, uid, allSum);
		return allSum;
	}

	/**
	 * 获取某个日期段区域内新增的旗下代理数
	 * 
	 * @param uid
	 *            父级id
	 * @param beginTime
	 *            日期开始 yyyy-MM-dd HH:mm:ss
	 * @param endTime
	 *            日期结束 yyyy-MM-dd HH:mm:ss
	 * @return
	 * @throws ParseException
	 */
	public long getTeamSumByRegion(String uid, String beginTime, String endTime) throws ParseException {
		long allSum = proxyRepository.getTeamSumByRegion(uid, beginTime, endTime);
		logger.info("{}到{}之间{}旗下新增的代理数为{}", beginTime, endTime, uid, allSum);
		return allSum;
	}

	/**
	 * 查询某人是否是某用户的直推代理吗,若是则返回该直推下级的信息
	 * 
	 * @param uid
	 *            某用户id
	 * @param phone
	 *            某人手机号
	 * @return
	 */
	public Proxy getoneDirectChild(String uid, String phone) {
		Proxy p = proxyRepository.getUserByPhone(phone);// 下级信息
		logger.info("根据手机号查询的代理信息为{}", p, "以下判断是否是本用户下级");
		if (p != null) {
			// String ppdid = p.getFather();
			String ppid = p.getParent().getPid();
			logger.info("该用户{}的直属下级信息为{}", uid, p);
			logger.info("用户{}的上级id是{}", p.getPid(), p.getParent().getPid());
			if (uid.equals(ppid)) {
				return p;
			} else {

				return null;
			}
		}
		return null;
	}

	/**
	 * 根据创建人查询俱乐部信息（创建人=代理）
	 * 
	 * @param uid
	 *            代理id
	 * @return
	 */
	public ClubDTO getClubMsg(String uid) {
		Proxy proxy = proxyRepository.fingByPid(uid);
		Club club = clubRepository.findByAgentUid(uid);
		// 查询俱乐部人数uid= ownerid
		// long sum = usersRepository.findClubSum(club.getCid());
		logger.info("查询俱乐部信息为{}", club);
		ClubDTO dto = PojoUtils.copy(club, ClubDTO.class);
		dto.setCRoomSum(proxy.getCard());// 剩余房卡数量、
		dto.setNumId(club.getNumId());// 俱乐部编号
		// dto.setNow((int)sum);//当前人数
		return dto;
	}

	/**
	 * 生成预订单，（套餐详情，订单编号）
	 * 
	 * @param selectItem
	 *            选择套餐的标示
	 * @param agentId
	 *            代理商id
	 * @param phone
	 *            代理商手机号
	 * @param num
	 *            选择的套餐数量（自定义套餐 1.5/张）
	 * @return
	 */
	public BuyParam addOrder(String payWay, int num, String selectItem, String agentId, String phone) {
		String orderNum = OrderNum.getOrderNo();// 订单编号
		BuyParam buyParam = new BuyParam();
		logger.info("计算具体套餐详情");
		// 计算斗牛具体套餐详情
//		switch (selectItem) {
//		case "1":
//			buyParam.setPname("套餐1");// 套餐名
//			buyParam.setMoney(300);// 金额：单位：元
//			buyParam.setNumber(200);// 房卡数量
//			break;
//		case "2":
//			buyParam.setPname("套餐2");
//			buyParam.setMoney(750);// 金额
//			buyParam.setNumber(500);// 房卡数量
//			break;
//		case "3":
//			buyParam.setPname("套餐3");
//			buyParam.setMoney(1475);// 金额
//			buyParam.setNumber(1000);// 房卡数量
//			break;
//		case "4":
//			buyParam.setPname("套餐4");
//			buyParam.setMoney(2800);// 金额
//			buyParam.setNumber(2000);// 房卡数量
//			break;
//		case "5":
//			buyParam.setPname("套餐5");
//			buyParam.setMoney(6750);// 金额
//			buyParam.setNumber(5000);// 房卡数量
//			break;
//		case "6":
//			buyParam.setPname("套餐6");
//			buyParam.setMoney(13000);// 金额
//			buyParam.setNumber(10000);// 房卡数量
//			break;
//		case "7":
//			buyParam.setPname("自定义套餐");
//			buyParam.setMoney((long) (num * 1.5));// 金额
//			buyParam.setNumber(num);// 房卡数量
//			break;
//		}

		//麻将
		switch (selectItem) {
		case "1":
			buyParam.setPname("套餐1");// 套餐名
			buyParam.setMoney(300);// 金额：单位：元
			buyParam.setNumber(600);// 600钻石
			break;
		case "2":
			buyParam.setPname("套餐2");
			buyParam.setMoney(500);// 金额
			buyParam.setNumber(1000);// 1000颗钻石
			break;
		case "3":
			buyParam.setPname("套餐3");
			buyParam.setMoney(1000);// 金额
			buyParam.setNumber(2200);// 颗钻石
			break;
		case "4":
			buyParam.setPname("套餐4");
			buyParam.setMoney(5000);// 金额
			buyParam.setNumber(12500);// 颗钻石
			break;
		}
		buyParam.setAgentUid(agentId);// 代理id
		buyParam.setAgentPhone(phone);
		buyParam.setOrderNum(orderNum);
		buyParam.setPayWay(payWay);// 支付方式
		// 1. 生成记录（生成订单）
		addRecharge(orderNum, buyParam, agentId);
		// String orderId = addRecharge(orderNum, buyParam, agentId);
		logger.info("代理商{}，所选择的购买套餐详情{}，生成订单编号为{}", agentId, buyParam, orderNum);
		// buyParam.setOrderId(orderId);
		return buyParam;
	}

	/**
	 * 生成预订单 初始化记录，生成新充值房卡纪录
	 * 
	 * @param agentId
	 *            代理商id
	 * @param param
	 *            购买详情
	 * @param orderNum
	 *            订单编号
	 */
	public void addRecharge(String orderNum, BuyParam param, String agentId) {
		Recharge recharge = new Recharge();
		logger.info("开始生成订单信息");
		String id = tools.createUUID();
		recharge.setId(id);
		recharge.setOrderNum(orderNum);// 订单编号
		recharge.setAgentUid(agentId);
		recharge.setOrderStatus(OrderStatus.CREATE);// 未支付状态(订单创建)
		recharge.setStatus("1");// 订单创建 未支付
		recharge.setPname(param.getPname());
		recharge.setNumber(param.getNumber());
		recharge.setMoney(param.getMoney() * 100);// 数据库单位为分
		// recharge.setCreateTime(dateUtil.getFormatDate("yyyy-MM-dd
		// HH:mm:ss"));
		recharge.setCreateTime(dateUtil.getFormatDate("yyyyMMddHHmmss"));
		recharge.setPayWay(param.getPayWay());// 支付方式
		recharge.setExpire("");
		recharge.setPayTime("");
		// recharge = PojoUtils.copy(param, Recharge.class);
		param.setOrderId(id);
		rechargeRepository.persist(recharge);
		logger.info("生成订单{}", recharge);
		// return id;
	}

	/**
	 * 获取某代理某段时间内，卖掉的房卡的数量
	 * 
	 * @param uid
	 *            代理id
	 * @param beginTime
	 *            开始日期 yyyy-MM-dd HH:mm:ss
	 * @param endTime
	 *            截止日期 yyyy-MM-dd HH:mm:ss
	 * @return
	 * @throws ParseException
	 */
	public long getYeji(String uid, String beginTime, String endTime) {
		long sum = playerRechargeRepository.getYeji(uid, beginTime, endTime);
		logger.info("代理商{}在{}和{}这段时间卖掉的房卡数量为{}", uid, beginTime, endTime, sum);
		return sum;
	}

	/**
	 * 获取某段时间内，代理自己购买的房卡数量
	 * 
	 * @param uid
	 *            代理id
	 * @param beginTime
	 *            开始时间 yyyy-MM-dd HH:mm:ss
	 * @param endTime
	 *            结束时间 yyyy-MM-dd HH:mm:ss
	 * @return
	 */
	public long getBuy(String uid, String beginTime, String endTime) {
		long sum = rechargeRepository.getBuy(uid, beginTime, endTime);
		logger.info("代理商{}在{}和{}这段时间自己购买的房卡数量为{}", uid, beginTime, endTime, sum);
		return sum;
	}

	/**
	 * 
	 * 某段时间内的实际返利
	 * 
	 * @param uid
	 *            代理id
	 * @param beginTime
	 *            yyyy-MM-dd HH:mm:ss
	 * @param endTime
	 *            yyyy-MM-dd HH:mm:ss
	 * @return
	 * @throws ParseException
	 */
	public long getfanli1(String uid, String beginTime, String endTime) throws ParseException {
		long shangjiShiji = getFanli(uid, beginTime, endTime);
		long allChildsShiji = getDirectChildYejis(uid, beginTime, endTime);
		logger.info("某段时间从{}到{}期间，代理{}的实际返利{}元", beginTime, endTime, uid, (shangjiShiji - allChildsShiji));
		return shangjiShiji - allChildsShiji;
	}

	/**
	 * 
	 * 某段时间内的实际返利(未减去下级代理业绩之前的)
	 * 
	 * @param uid
	 *            代理id
	 * @param beginTime
	 *            yyyy-MM-dd HH:mm:ss
	 * @param endTime
	 *            yyyy-MM-dd HH:mm:ss
	 * @return
	 * @throws ParseException
	 */
	public long getFanli(String uid, String beginTime, String endTime) throws ParseException {
		// 总业绩 = 计算某段时间内个人购卡总金额+旗下代理购卡总金额
		// 您的实际返利：您总业绩 * 对应提成比例 - 直属下级A的业绩 * 对应提成比例- 直属下级B的业绩 * 对应提成比例
		long allMoney = getBuyByBegin(uid, beginTime, endTime);// 某段时间内代理个人购卡总金额
		long childAllMoney = getChildBegin(uid, beginTime, endTime);// 某段时间内旗下代理购卡总金额
		long allYeji = allMoney + childAllMoney;// 总业绩
		long allYejiFanliBi = getRate(allYeji);// 总业绩对应的提成比例分
		// long directChildyejiAdd = getDirectChildYeji(uid,
		// beginTime,endTime);//获取所有直属下级的业绩*对应提成比例之后 的总和（总金额）
		long shijiFanlipre = (allYeji * allYejiFanliBi) / 100 /*- directChildyejiAdd*/;// 您的实际返利分
		return shijiFanlipre;
	}

	/**
	 * 查询某段时间内，代理个人购卡的总金额
	 * 
	 * @param uid
	 *            代理id
	 * @param beginTime
	 *            开始时间 格式：yyyy-MM
	 * @param endTime
	 * @return
	 */
	public long getBuyByBegin(String uid, String beginTime, String endTime) {
		long allMoney = rechargeRepository.getBuyByBegin(uid, beginTime, endTime);
		logger.info("代理商{}在{}到{}之间，自己购买的房卡的总金额为{}", uid, beginTime, endTime, allMoney);
		return allMoney;
	}

	/**
	 * 某段时间内代理旗下代理购卡总金额
	 * 
	 * @param uid
	 *            代理id
	 * @param beginTime
	 *            开始时间
	 * @param endTime
	 *            结束时间
	 * @return
	 */
	public long getChildBegin(String uid, String beginTime, String endTime) {
		long allMoney = 0;
		List<Proxy> list = proxyRepository.getAll(uid);// 获取所有代理
		for (Proxy p : list) {
			long oneMoney = rechargeRepository.getBuyByBegin(p.getPid(), beginTime, endTime);// 某段时间内代理旗下代理购卡总金额
			logger.info("代理商{},在{}到{}之间，旗下的代理{}购买的房卡的金额为{}元", uid, beginTime, endTime, p.getPhone(), allMoney);
			allMoney += oneMoney;// 累计金额
		}
		logger.info("代理商{},在{}到{}之间，旗下的所有代理购买的房卡的总金额为{}元", uid, beginTime, endTime, allMoney);
		return allMoney;
	}

	/**
	 * 获取所有 直属下级 某段时间内业绩总额 * 对应的返利比 总和
	 * 
	 * @param uid
	 *            代理id
	 * @param beginTime
	 *            开始时间 yyyy-MM-dd HH:mm:ss
	 * @param endTime
	 *            截止时间yyyy-MM-dd HH:mm:ss
	 * @return
	 * @throws ParseException
	 */
	// public long getDirectChildYeji(String uid, String beginTime,String
	// endTime) throws ParseException {
	// // long oneMoney = 0;//一个直属下级在某短时间的业绩总额
	// long allMoney = 0;//所有直属下级 某段时间业绩总额 * 对应的返利比 总和
	// List<Proxy> list = proxyRepository.getDirectChild(uid);//获取所有直属下级
	// for(Proxy p:list){
	// long oneMoney = rechargeRepository.getBuyByBegin(p.getPid(),
	// beginTime,endTime);//某段时间内代理旗下代理购卡总金额
	// long rate = getRate(oneMoney);//返利比
	// allMoney += oneMoney*rate;
	// }
	// return allMoney;
	// }

	/**
	 * 获取所有直属代理的所有下级包括该直属代理 某段时间内业绩总额 * 对应的返利比 总和
	 * 
	 * @param uid
	 *            代理id
	 * @param beginTime
	 *            开始时间 yyyy-MM-dd HH:mm:ss
	 * @param endTime
	 *            截止时间yyyy-MM-dd HH:mm:ss
	 * @return
	 * @throws ParseException
	 */
	public long getDirectChildYejis(String uid, String beginTime, String endTime) throws ParseException {
		// 获取所有的直属下级
		List<Proxy> list = proxyRepository.getDirectChild(uid);// 获取所有直属下级
		// long oneAllMoney = 0;//一个直属下级+ 该直属下面所有下级，某短时间的的总金额
		long allchildMoneys = 0;// 所有直属下级各自总业绩金额*对应的比例 相加的结果
		for (Proxy p : list) {
			long oneAllMoney = getFanli(p.getPid(), beginTime, endTime);
			logger.info(" 获取所有直属代理的所有下级包括该直属代理   某段时间内业绩总额 * 对应的返利比  {}元", oneAllMoney);
			allchildMoneys += oneAllMoney;
		}
		logger.info(" 获取所有直属代理的所有下级包括该直属代理   某段时间内业绩总额 * 对应的返利比  总和{}元", allchildMoneys);
		return allchildMoneys;
	}

	/**
	 * 查询某月，代理个人购卡的总金额
	 * 
	 * @param uid
	 *            代理id
	 * @param monthDate
	 *            具体月份 格式：yyyy-MM
	 * @return
	 */
	public long getBuyByMonth(String uid, String monthDate) {
		long allMoney = rechargeRepository.getBuyByMonthOrDay(uid, monthDate);
		logger.info("代理商{}在{}月份自己购买的房卡的总金额为{}(元)", uid, monthDate, allMoney);
		return allMoney;
	}

	/**
	 * 查询某月，代理的，旗下代理某月购卡总金额
	 * 
	 * @param uid
	 *            代理id
	 * @param monthDate
	 *            具体月份 格式：yyyy-MM
	 * @return
	 */
	public long getChildBuyByMonth(String uid, String monthDate) throws ParseException {
		long allMoney = 0;
		// List<Proxy> list =
		// proxyRepository.getChildBuyByMonth(uid,monthDate);//获取某月新增的旗下代理
		List<Proxy> list = proxyRepository.getAll(uid);// 获取所有代理
		for (Proxy p : list) {
			long oneMoney = rechargeRepository.getBuyByMonthOrDay(p.getPid(), monthDate);// 查询某月，代理个人购卡的总金额
			logger.info("代理商{},在{}月份旗下的代理{}购买的房卡的个人总金额为{}", uid, monthDate, p.getPid(), allMoney);
			allMoney += oneMoney;// 累计金额
		}
		logger.info("代理商{},在{}月份旗下的代理{}购买的房卡的所有总金额为{}", uid, monthDate, list, allMoney);
		return allMoney;
	}

	/**
	 * 查询某月，代理的，旗下代理某月购卡总数量
	 * 
	 * @param uid
	 *            代理id
	 * @param monthDate
	 *            具体月份 格式：yyyy-MM
	 * @return
	 */
	public long getSumByMonthOrDay(String uid, String monthDate) throws ParseException {
		long allSum = 0;
		// List<Proxy> list =
		// proxyRepository.getChildBuyByMonth(uid,monthDate);//获取某月新增的旗下代理
		List<Proxy> list = proxyRepository.getAll(uid);// 获取所有代理
		for (Proxy p : list) {
			long oneSum = rechargeRepository.getSumByMonthOrDay(p.getPid(), monthDate);// 查询某月，代理个人购卡的总金额
			logger.info("代理商{},在{}月份旗下的代理{}购买的房卡的个人总数为{}", uid, monthDate, p.getPid(), allSum);
			allSum += oneSum;// 累计金额
		}
		logger.info("代理商{},在{}月份旗下的代理{}购买的房卡的所有总数为{}", uid, monthDate, list, allSum);
		return allSum;
	}

	/**
	 * 获取所有 直属下级 某月业绩总额 * 对应的返利比 总和
	 * 
	 * @param uid
	 *            代理id
	 * @param monthDate
	 *            某月
	 * @return
	 * @throws ParseException
	 */
	public long getDirectChildYeji(String uid, String monthDate) throws ParseException {
		// List<Proxy> list= proxyRepository.getDirectChildBymonth(uid,
		// monthDate);//某月新增的直属下级
		long allMoney = 0;// 所有直属下级 某月业绩总额 * 对应的返利比 总和
		List<Proxy> list = proxyRepository.getDirectChild(uid);// 获取所有直属下级
		for (Proxy p : list) {
			long oneMoney = rechargeRepository.getBuyByMonthOrDay(p.getPid(), monthDate);// 查询某月，个人购卡的总金额//一个直属下级在某月的业绩总额
			long rate = getRate(oneMoney);// 返利比分
			logger.info("代理商{},的直属下级{}，在{}月份，的业绩总金额为{}，对应的返利比（分）是{}", uid, p.getPid(), monthDate, list, oneMoney, rate);
			allMoney += ((oneMoney * rate) / 100);
		}
		return allMoney;
	}

	/**
	 * 获取所有 直属下级的总业绩额， 某月业绩总额 * 对应的返利比 总和
	 * 
	 * @param uid
	 *            代理id
	 * @param monthDate
	 *            某月
	 * @return
	 * @throws ParseException
	 */
	public long getDirectChildAllYeji(String uid, String monthDate) throws ParseException {
		// List<Proxy> list= proxyRepository.getDirectChildBymonth(uid,
		// monthDate);//某月新增的直属下级
		// long oneMoney = 0;//一个直属下级在某月的业绩总额
		long allMoney = 0;// 所有直属下级 某月业绩总额 * 对应的返利比 总和
		List<Proxy> list = proxyRepository.getDirectChild(uid);// 获取所有直属下级
		for (Proxy p : list) {
			long oneMoney = rechargeRepository.getBuyByMonthOrDay(p.getPid(), monthDate);// 查询某月，个人购卡的总金额
			long rate = getRate(oneMoney);// 返利比
			logger.info("代理商{},的直属下级{}，在{}月份，的业绩总金额为{}，对应的返利比分是{}", uid, p.getPid(), monthDate, list, oneMoney, rate);
			allMoney += ((oneMoney * rate) / 100);
		}
		return allMoney;
	}

	/**
	 * 计算返利比 按照分去计算
	 * 
	 * @param money
	 *            金额业绩 元
	 * @return
	 */
	private long getRate(long money) {
		long fanliRate = 0;// 返利比 单位分
		if (money >= 250000) {
			fanliRate = 25;// 实际是0.25
		} else if (money >= 100000) {
			fanliRate = 20;// 实际是0.2
		} else if (money >= 30000) {
			fanliRate = 15;// 实际是0.15
		} else if (money >= 10000) {
			fanliRate = 10;// 实际是0.1
		} else if (money >= 3000) {
			fanliRate = 5;// 实际是0.05
		}
		return fanliRate;
	}

	/**
	 * 旗下代理业绩 页面显示数据 查询：本日本月业绩，本月返利，返利阶梯，旗下代理人数（昵称，id号码）
	 * 
	 * @param uid
	 *            代理id
	 */
	public ArrayList<ChildYejiDTO> getDirectChilds(String uid) {
		List<Proxy> list = proxyRepository.getDirectChild(uid);// 获取所有直属下级
		// 直属下级的--旗下代理总数
		// 本日本月业绩，本月返利，返利阶梯，旗下代理人数（昵称，id号码）
		// 今日业绩，本月业绩，旗下代理数，返利明细 进行排序， desc高到低，
		ArrayList<ChildYejiDTO> newList = new ArrayList<>();
		for (Proxy p : list) {
			logger.info("开始计算旗下代理{}的业绩", p.getPid());
			ChildYejiDTO dto = new ChildYejiDTO();
			long sum = proxyRepository.getAllSum(p.getPid());// 查询直属下级的 下级总数
			logger.info("{}旗下代理总数{}", p.getPid(), sum);
			// 本日业绩
			long dayMoney = rechargeRepository.getBuyByMonthOrDay(p.getPid(), dateUtil.getFormatDate("yyyy-MM-dd"));// 查询某日，个人购卡的总金额
			logger.info("代理{}在{}时间(日 )的业绩", p.getPid(), dayMoney);
			long monthMoney = rechargeRepository.getBuyByMonthOrDay(p.getPid(), dateUtil.getFormatDate("yyyy-MM"));// 查询某月，个人购卡的总金额
			long monthFanlibi = getRate(monthMoney);// 个人购卡总金额对应的返利比分
			logger.info("开始计算旗下代理{}的业绩", p.getPid());
			long benYueFanli = (monthMoney * monthFanlibi) / 100; // 本月返利
																	// 您的总返利：个人购卡总金额*返利比例

			dto.setSum(sum);// 下级的 下级总数
			dto.setDayMoney(dayMoney);// 本日业绩
			dto.setMonthMoney(monthMoney);// 本月业绩
			dto.setBenYueFanli(benYueFanli);// 本月返利
			dto.setMonthFanlibi(monthFanlibi);// 返利阶梯
			dto.setNickName(p.getNikeName());// 昵称
			dto.setId(p.getNumId());// 代理编号

			newList.add(dto);
		}
		logger.info("计算旗下代理业绩数据计算完成");
		return newList;
	}

	/**
	 * 旗下代理业绩 页面显示数据 进行分页和排序（对上面的方法（getDirectChilds）返回的数据进行分页和排序）
	 * 
	 * @param uid
	 * @return
	 */
	public Map<String, Object> paixuList(String uid, int pageSize, int pageNo, String flag) {
		Map<String, Object> map = new HashMap<String, Object>();
		ArrayList<ChildYejiDTO> newList = getDirectChilds(uid);
		logger.info("旗下代理业绩页面数据获取排序之前的", newList);
		// 按照flag进行排序
		// flag * 1 今日业绩， * 2 本月业绩，* 3 旗下代理数， * 4 返利明细
		Collections.sort(newList, (o1, o2) -> {
			if ("1".equals(flag)) {
				return (int) (o2.getDayMoney() - o1.getDayMoney());
			} else if ("2".equals(flag)) {
				return (int) (o2.getMonthMoney() - o1.getMonthMoney());
			} else if ("3".equals(flag)) {
				return (int) (o2.getSum() - o1.getSum());
			} else if ("4".equals(flag)) {
				return (int) (o2.getBenYueFanli() - o1.getBenYueFanli());
			} else {
				return (int) (o2.getDayMoney() - o1.getDayMoney());
			}
		});

		// Collections.sort(newList, new Comparator<ChildYejiDTO>() {
		// public int compare(ChildYejiDTO o1, ChildYejiDTO o2) {
		// if("1".equals(flag)){
		// return (int) (o2.getDayMoney() - o1.getDayMoney());
		// }else if("2".equals(flag)){
		// return (int) (o2.getMonthMoney() - o1.getMonthMoney());
		// }else if("3".equals(flag)){
		// return (int) (o2.getSum() - o1.getSum());
		// }else if("4".equals(flag)){
		// return (int) (o2.getBenYueFanli() - o1.getBenYueFanli());
		// }else{
		// return (int) (o2.getDayMoney() - o1.getDayMoney());
		// }
		// }
		// });

		// 对list进行分页操作就可以了；
		ArrayList<ChildYejiDTO> result = new ArrayList<>();// 新的list
		if (newList != null && newList.size() > 0) {
			int allCount = newList.size();
			// 计算总页数的方法
			int pageCount = (allCount + pageSize - 1) / pageSize;
			// if(pageNo >= pageCount){
			// pageNo = pageCount;
			// }
			int start = (pageNo - 1) * pageSize;
			int end = pageNo * pageSize;
			if (end >= allCount) {
				end = allCount;
			}
			for (int i = start; i < end; i++) {
				ChildYejiDTO dto = newList.get(i);
				// dto.setPageCount(pageCount);//设置总页数
				result.add(dto);
			}
			map.put("result", (result != null && result.size() > 0) ? result : null);
			logger.info("旗下代理业绩页面数据获取排序之后的", result);
			map.put("pageCount", pageCount);
		}

		return map;
		// return (result != null && result.size() > 0) ? result : null;
	}

	/**
	 * 获取代理某月的实际返利金额 获取代理总业绩额 - 直属代理旗下所有人包括该直属代理的业绩总额
	 * 
	 * @param monthDate
	 *            某月格式：yyyy-MM
	 * @param uid
	 *            代理id
	 * @return
	 * @throws ParseException
	 */
	public MonthDTO getMonthShares(String uid, String monthDate) throws ParseException {
		// MonthDTO month = new MonthDTO();
		// long shijifanli = getPlanfanli(uid,monthDate);
		MonthDTO month = getMonthShare(monthDate, uid); // month.getOneshijiFanliPre()代理个人总业绩*对应的返利比
		long qixiaAndDirect = getDirectChilsAllChils(monthDate, uid);// 代理旗下所有直属下级总业绩*对应的返利比，相加之和
		logger.info("代理{}个人总业绩(元)*对应的返利比是{}", uid, month.getOneshijiFanliPre());
		logger.info("代理{}旗下所有直属代理总业绩(元)*对应的返利比之和是{}", uid, qixiaAndDirect);
		long shijiFanli = month.getOneshijiFanliPre() - qixiaAndDirect;
		month.setShijiFanli(shijiFanli);
		logger.info("代理{}获取的实际返利是{}(元)", uid, shijiFanli);
		return month;
	}

	/**
	 * 按照月份进行统计返利详情 获取代理自己和旗下所有代理在某月的总金额 * 对应返利比
	 * 
	 * @param monthDate
	 *            格式：yyyy-MM
	 * @param uid
	 *            代理id
	 * @throws ParseException
	 *             此方法中的金额的单位为元
	 */
	public MonthDTO getMonthShare(String monthDate, String uid) throws ParseException {
		MonthDTO data = new MonthDTO();
		try {
			// long allMoney = (getBuyByMonth(uid, monthDate)) * 100;//
			// 某月自己购买的房卡总金额
			// long qixiaAllMoney = (getChildBuyByMonth(uid, monthDate)) *
			// 100;// 某月旗下代理购卡：旗下代理购卡总金额
			// long allYeji = allMoney + qixiaAllMoney;// 总业绩
			// long fanliBi = getRate(allMoney);// 您的返利比
			// long allFanli = allMoney * fanliBi;// 您的总返利
			// long qixiaFanliBi = getRate(allMoney);// 旗下代理的返利比
			// long qixiaAllFanli = qixiaAllMoney * qixiaFanliBi;// 旗下代理返利
			// // 您的实际返利计算
			// long allYejiFanliBi = getRate(allYeji);// 总业绩对应的提成比例
			// long directChildyejiAdd = getDirectChildYeji(uid, monthDate);//
			// 获取所有直属下级的业绩*对应提成比例之后
			// long shijiFanli = allYeji * allYejiFanliBi -
			// directChildyejiAdd;// 您的实际返利

			long allMoney = (getBuyByMonth(uid, monthDate));// 某月自己购买的房卡总金额
			long qixiaAllMoney = (getChildBuyByMonth(uid, monthDate));// 某月旗下代理购卡：旗下代理购卡总金额
			long allYeji = allMoney + qixiaAllMoney;// 自己总业绩
			long fanliBi = getRate(allMoney);// 您的返利比分
			long allFanli = (allMoney * fanliBi) / 100;// 您的总返利
			long qixiaFanliBi = getRate(qixiaAllMoney);// 旗下代理的返利比分
			long qixiaAllFanli = (qixiaAllMoney * qixiaFanliBi) / 100;// 旗下代理返利
			// 您的实际返利计算
			long allYejiFanliBi = getRate(allYeji);// 总业绩对应的提成比例
			// long directChildyejiAdd = getDirectChildAllYeji(uid,
			// monthDate);// 获取所有直属下级的总业绩*对应提成比例之后
			long oneshijiFanliPre = (allYeji * allYejiFanliBi) / 100;/* - directChildyejiAdd */// 您的实际返利未减去下级分红之前的

			data.setAllMoney(allMoney);
			data.setQixiaAllMoney(qixiaAllMoney);
			data.setAllYeji(allYeji);
			data.setQixiaFanliBi(qixiaFanliBi);
			data.setAllFanli(allFanli);
			data.setQixiaAllFanli(qixiaAllFanli);
			data.setOneshijiFanliPre(oneshijiFanliPre);
			data.setDriAllmoneyRate(oneshijiFanliPre);

			return data;

		} catch (Exception e) {
			logger.error("每月返利计算异常", e);
		}
		return data;
	}

	/**
	 * 获取uid 所有直属下级总业绩金额 的总和 获取代理(uid)旗下的直属代理，计算每个直属代理包括该直属代理旗下的所有人的某月的业绩总额 *
	 * 对应的返利比 之间的总和
	 * 
	 * @param uid
	 *            上级代理id
	 * @param monthDate
	 *            某月
	 * @return
	 * @throws ParseException
	 */

	public long getDirectChilsAllChils(String monthDate, String uid) throws ParseException {
		// 获取所有的直属下级
		List<Proxy> list = proxyRepository.getDirectChild(uid);// 获取所有直属下级
		// long oneAllMoney = 0;//一个直属下级+ 该直属下面所有下级，某月的总金额
		long allchildMoneys = 0;// 所有直属下级各自总业绩金额*对应的比例 相加的结果
		for (Proxy p : list) {
			MonthDTO monthDto = getMonthShare(monthDate, p.getPid());
			long oneAllMoney = monthDto.getOneshijiFanliPre();
			logger.info("代理商{},的直属下级{}，在{}月份，的个人总业绩总金额*对应的返利比为{}(元)，对应的返利比是{}", uid, p.getPid(), monthDate, list,
					oneAllMoney, monthDto.getDriAllmoneyRate());
			// oneAllMoney = rechargeRepository.getBuyByMonthOrDay(p.getPid(),
			// monthDate);// 查询某月，个人购卡的总金额
			// long rate = getRate(oneAllMoney);//返利比
			// logger.info("代理商{},的直属下级{}，在{}月份，的总业绩总金额为{}，对应的返利比是{}",uid,p.getPid(),monthDate,list,oneAllMoney,rate);
			// allchildMoneys = oneAllMoney*rate + allchildMoneys;
			allchildMoneys += oneAllMoney;
		}
		logger.info("代理商{},的所有直属下级，在{}月份，的总业绩总金额*对应返利比之后的钱为{}(元)", uid, monthDate, list, allchildMoneys);
		return allchildMoneys;
	}

	/**
	 * 设置用户的openid
	 * 
	 * @param uid
	 *            代理 用户id
	 * @param openid
	 *            代理用户的openid
	 */
	public void uptOpenid(String uid, String openid) {
		Proxy proxy = proxyRepository.fingByPid(uid);
		proxy.setOpenId(openid);
		logger.info("设置用户{}的openid{}成功", uid, openid);
	}

	/**
	 * 查询历来代理个人购卡的总金额
	 * 
	 * @param uid
	 *            代理id
	 * @param monthDate
	 *            具体月份 格式：yyyy-MM
	 * @return
	 */
	public long getBuy(String uid) {
		long allMoney = rechargeRepository.getBuy(uid);
		logger.info("代理商{}自己购买的房卡的总金额为{}", uid, allMoney);
		return allMoney;
	}

	/**
	 * 查询历来代理的，旗下代理购卡总金额
	 * 
	 * @param uid
	 *            代理id
	 * @param monthDate
	 *            具体月份 格式：yyyy-MM
	 * @return
	 */
	public long getChildBuy(String uid) throws ParseException {
		long allMoney = 0;
		List<Proxy> list = proxyRepository.getAll(uid);// 获取所有代理
		for (Proxy p : list) {
			long oneMoney = rechargeRepository.getBuy(p.getPid());// 查询代理个人购卡的总金额
			allMoney += oneMoney;// 累计金额
		}
		logger.info("代理商{},旗下的代理{}购买的房卡的总金额为{}", uid, list, allMoney);
		return allMoney;
	}

	/**
	 * 获取历来代理自己购买的房卡数量
	 * 
	 * @param uid
	 *            代理id
	 * @return
	 */
	public long getBuySum(String uid) {
		long sum = rechargeRepository.getBuySum(uid);
		logger.info("代理商{}在{}和{}这段时间自己购买的房卡数量为{}", uid, sum);
		return sum;
	}

	/**
	 * 获取历来代理下级购买的房卡数量
	 * 
	 * @param uid
	 *            代理id
	 * @return
	 */
	public long getChildBuySum(String uid) {
		long allSum = 0;
		List<Proxy> list = proxyRepository.getAll(uid);// 获取所有代理
		for (Proxy p : list) {
			long oneSum = rechargeRepository.getBuySum(p.getPid());
			allSum += oneSum;// 累计总数
		}
		logger.info("代理商{}在{}和{}这段时间自己购买的房卡数量为{}", uid, allSum);
		return allSum;
	}

	/**
	 * 查询当前登录代理直属下级的以下信息
	 * 
	 * @param uid
	 *            当前登录代理的id
	 * @return 代理账号，俱乐部账号，昵称，旗下代理数
	 *         ，代理购卡总数，代理购卡总金额，个人购卡总数，总金额，上级代理手机号，代理等级，代理级别，管理（开启、停止）
	 * @throws ParseException
	 */
	public List<AgentParam> agentPolicy(String uid, PageParam pageParam) throws ParseException {
		List<Proxy> list = proxyRepository.getDirectChild(uid);// 获取所有直属下级
		List<AgentParam> list1 = new ArrayList<AgentParam>();
		for (Proxy proxy : list) {
			AgentParam agentParam = new AgentParam();
			// 查询所有下级的账户的可用性
			Account account = accountRepository.getAccount(proxy.getPid());
			if (account != null && account.getCanUse() != null) {
				agentParam.setCanuse(account.getCanUse());
			}
			agentParam.setAnumId(proxy.getNumId());// 代理编号
			agentParam.setPid(proxy.getPid());// 旗下代理的id
			agentParam.setCid(proxy.getCid());// 旗下代理的俱乐部id
			agentParam.setNikeName(proxy.getNikeName());// 旗下代理的昵称信息
			long sums = getTeamSum(proxy.getPid());// 获取其直属下级的，旗下代理数
			agentParam.setSums(sums);// 旗下代理的 旗下代理数
			long ChildAllmoney = getChildBuy(proxy.getPid());
			long selfAllMoney = getBuy(proxy.getPid());// 代理个人购卡总金额
			agentParam.setChildAllmoney(ChildAllmoney);// 代理下级购卡总金额
			agentParam.setSelfAllMoney(selfAllMoney);
			long selfAllsum = getBuySum(uid);// 个人购卡总数量
			long ChildAllsum = getChildBuySum(uid);// 代理购卡总数
			agentParam.setChildAllsum(ChildAllsum);
			agentParam.setSelfAllsum(selfAllsum);
			agentParam.setAgentLevel(proxy.getAgentLevel());// 代理等级
			list1.add(agentParam);
		}
		logger.info("查询当前登录代理直属下级的以下信息{}", list1);
		return list1;
	}

	/**
	 * 代理政策管理（开启，禁止） 设置账号的登录限制
	 * 
	 * @param agentId
	 * @param canuse
	 *            :no,yes
	 * @return
	 */
	public void setCanuse(String agentId, String canuse) {
		Account account = accountRepository.getAccount(agentId);
		logger.info("设置账号的登录限制，用户{}的登录账号为{}", agentId, account);
		account.setCanUse(canuse);
		logger.info("成功设置账号的登录限制为{}", canuse);
	}

	/**
	 * 查询订单状态
	 * 
	 * @param out_trade_no
	 *            订单id
	 * @return
	 */
	public OrderStatus getOrderstatus(String out_trade_no) {
		Recharge recharge = rechargeRepository.find(out_trade_no);
		logger.info("待查询的支付订单{}的状态{}", out_trade_no, recharge);
		if (recharge != null && recharge.getOrderStatus() != null) {
			return recharge.getOrderStatus();
		}
		return OrderStatus.CREATE;
	}

	/**
	 * 根据用户的订单号，查询用户手机号 主要用于支付宝支付成功 回跳页面使用
	 * 
	 * @param orderId
	 *            订单号
	 * @return
	 */
	public String getPhoneByOrderId(String out_trade_no) {
		Recharge recharge = rechargeRepository.find(out_trade_no);
		if (recharge != null && recharge.getAgentUid() != null) {
			Proxy p = proxyRepository.find(recharge.getAgentUid());// 获取用户id,查询用户手机号
			if (p != null && p.getPhone() != null) {
				return p.getPhone();
			}
		}
		return null;
	}

	/**
	 * 从这开始以下三个方法，计算某代理某月的实际返利，（备用）
	 * 
	 * 某月的实际返利
	 * 
	 * @param uid
	 *            代理id
	 * @param monthTime
	 * @return
	 * @throws ParseException
	 */
	public long getPlanfanli(String uid, String monthTime) throws ParseException {
		long shangjiShiji = getFanli(uid, monthTime);
		long allChildsShiji = getDirectChildsYeji(uid, monthTime);
		return shangjiShiji - allChildsShiji;
	}

	/**
	 * 
	 * 某月的实际返利(未减去下级代理业绩之前的)
	 * 
	 * @param uid
	 *            代理id
	 * @param monthTime
	 *            yyyy-MM
	 * @return
	 * @throws ParseException
	 */
	public long getFanli(String uid, String monthTime) throws ParseException {
		// 总业绩 = 计算某月个人购卡总金额+旗下代理购卡总金额
		// 您的实际返利：您总业绩 * 对应提成比例 - 直属下级A的业绩 * 对应提成比例- 直属下级B的业绩 * 对应提成比例
		long allMoney = getBuyByMonth(uid, monthTime);// 某月代理个人购卡总金额
		long childAllMoney = getChildBuyByMonth(uid, monthTime);// 某月旗下代理购卡总金额
		long allYeji = allMoney + childAllMoney;// 总业绩
		long allYejiFanliBi = getRate(allYeji);// 总业绩对应的提成比例fen
		// long directChildyejiAdd = getDirectChildYeji(uid,
		// beginTime,endTime);//获取所有直属下级的业绩*对应提成比例之后 的总和（总金额）
		long shijiFanlipre = (allYeji * allYejiFanliBi) / 100 /*- directChildyejiAdd*/;// 您的实际返利
		return shijiFanlipre;
	}

	/**
	 * 获取所有 直属下级（包括该直属下级以及改直属下级的所有下级） 某月业绩总额 * 对应的返利比 总和
	 * 
	 * @param uid
	 *            代理id
	 * @param monthDate
	 *            某月
	 * @return
	 * @throws ParseException
	 */
	public long getDirectChildsYeji(String uid, String monthDate) throws ParseException {
		// List<Proxy> list= proxyRepository.getDirectChildBymonth(uid,
		// monthDate);//某月新增的直属下级
		// long oneMoney = 0;//一个直属下级在某月的业绩总额
		long allMoney = 0;// 所有直属下级 某月业绩总额 * 对应的返利比 总和
		List<Proxy> list = proxyRepository.getDirectChild(uid);// 获取所有直属下级
		for (Proxy p : list) {
			long oneMoney = getFanli(p.getPid(), monthDate);// 查询某月，个人购卡的总金额
			long rate = getRate(oneMoney);// 返利比
			logger.info("代理商{},的直属下级{}，在{}月份，的业绩总金额为{}，对应的返利比是{}", uid, p.getPid(), monthDate, list, oneMoney, rate);
			allMoney += ((oneMoney * rate) / 100);
		}
		return allMoney;
	}

	/**
	 * 查询系统订单，判断是否有该订单
	 * 
	 * @param order
	 *            订单信息
	 * @return
	 */
	public Recharge judgehasOrder(BuyParam order) {
		Recharge recharge = rechargeRepository.judgehasOrder(order.getOrderNum());
		return recharge;
	}

	/**
	 * 根据订单id查询订单信息
	 * 
	 * @param order
	 * @return
	 */
	public Recharge judgeOrder(BuyParam order) {
		Recharge recharge = rechargeRepository.find(order.getOrderId());
		return recharge;
	}

	/**
	 * 修改提现状态 从未处理-到已处理
	 * 
	 * @param cashOutId
	 *            提现id
	 * @param adminId
	 *            操作人id
	 * @return 0为操作成功，1为失败
	 */
	public int uptCashOutStatus(String cashOutId, String adminId) {
		CashOut cashOut = cashOutRepository.find(cashOutId, LockModeType.PESSIMISTIC_WRITE);
		if (cashOut != null) {
			cashOut.setDealWith("1");// 状态为已处理
			cashOut.setDealId(adminId);// 处理人
			cashOut.setDealTime(dateUtil.getFormatDate("yyyy-MM-dd HH:mm:ss"));// 处理时间
			return 0;
		}
		return 1;
	}

	/**
	 * 查询所有俱乐部总数
	 * 
	 * @return
	 */
	public long getAllClubSum() {
		long sum = clubRepository.countAll();
		return sum;
	}

	/**
	 * 查询某俱乐部成员数量
	 * 
	 * @param cid
	 * @return
	 */
	public long getOneClubSum(String cid) {
		long sum = clubRepository.getOneClubSum(cid);
		return sum;
	}

	/**
	 * 代理商等级升级、降级操作
	 * 
	 * @param agentId
	 *            要修改的代理32位id
	 * @param level
	 *            要修改的等级
	 * @param uidReq
	 *            修改人admin
	 * @return 0为成功
	 */
	public int uptLevel(String agentId, int level, String uidReq) {
		Proxy proxy = proxyRepository.find(agentId, LockModeType.PESSIMISTIC_WRITE);
		if (proxy != null) {
			proxy.setLevel(level);
			return 0;
		}
		return 1;
	}

	/**
	 * 转移下级操作
	 * 
	 * @param formAgentId
	 *            来源代理32位id
	 * @param toAgentId
	 *            转至代理32的id
	 * @return 0为成功
	 */
	public int moveChild(String formAgentId, String toAgentId) {
		Proxy fromProxy = proxyRepository.find(formAgentId, LockModeType.PESSIMISTIC_WRITE);
		Proxy toProxy = proxyRepository.find(toAgentId, LockModeType.PESSIMISTIC_WRITE);
		if (fromProxy != null && toProxy != null) {
			Set<Proxy> proxylist = fromProxy.getChilds();
			toProxy.getChilds().addAll(proxylist);// 将下级添加到该代理名下
			fromProxy.setChilds(null);// 将该代理下级清空
			return 0;
		}
		return 1;
	}

	/**
	 * 修改上级操作
	 * 
	 * @param formAgentNum
	 *            A 要修改的代理的6位编号
	 * @param toAgentNum
	 *            C 将要换成的上级的6位编号，
	 * @return
	 */
	public int uptFather(String fromAgentNum, String toAgentNum) {
		// 将A的father，parent，ucid，都设置成C的
		Proxy child = proxyRepository.findByNumId(fromAgentNum, LockModeType.PESSIMISTIC_WRITE);
		Proxy parent = proxyRepository.findByNumId(toAgentNum, LockModeType.PESSIMISTIC_WRITE);
		if (child == null || parent == null) {
			return 1;
		}
		child.setFather(parent.getPid());
		child.setParent(parent);
		parent.getChilds().add(child);// 设置下级
		return 0;
	}

}
