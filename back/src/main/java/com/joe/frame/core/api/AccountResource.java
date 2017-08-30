package com.joe.frame.core.api;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.joe.frame.common.secure.Encipher;
import com.joe.frame.common.secure.MD5;
import com.joe.frame.common.util.AliCodeVerify;
import com.joe.frame.core.entity.Account;
import com.joe.frame.core.entity.Proxy;
import com.joe.frame.core.param.PasswordParam;
import com.joe.frame.core.param.UserInfoParam;
import com.joe.frame.core.service.AccountService;
import com.joe.frame.core.service.AgentService;
import com.joe.frame.web.cache.RedisCacheService;
import com.joe.frame.web.dto.BaseDTO;
import com.joe.frame.web.dto.NormalDTO;

import redis.clients.jedis.Jedis;

@Path("account")
public class AccountResource extends BaseResource {
	private static final Logger logger = LoggerFactory.getLogger(AccountResource.class);
	@Autowired
	private AccountService accountService;
	@Autowired
	private AgentService agentService;
	@Context
	HttpServletRequest request;
	@Autowired
	private AliCodeVerify aliCodeVerify;
	//	@Autowired
	//		private EhcacheService ehcacheService;

	private static Encipher encipher = new MD5();

	@Autowired
	private RedisCacheService redisCacheService;


	public static void main(String[] args) {
		//		String token = encipher.encrypt("18437931136");
		//		String token = encipher.encrypt("15993382551");
		//		String token = encipher.encrypt("13633868765");
		//		String token = encipher.encrypt("18511285720");
		//		String token = encipher.encrypt("18437931136");
		//		String token = encipher.encrypt("13241665042");
		//		String token = encipher.encrypt("18339827479");
		//		System.out.println(token);
		Jedis jedis = new Jedis("192.168.0.120");
		//		        jedis.auth("ldd");
		//		        jedis.set("age", "1");
		//		     System.out.println(jedis.get("age"));
		System.out.println(jedis);
	}



	/**
	 * app获取版本信息
	 * @return 版本信息
	 */
	@GET
	@Path("app/version")
	@Produces(MediaType.APPLICATION_JSON)
	public NormalDTO<Object> getVersion(){
		NormalDTO<Object> dto = new NormalDTO<Object>();
		try {
			String v = accountService.getVersion();
			dto.setData(v);
		} catch (Exception e) {
			dto.setStatus("202");
			dto.setErrorMessage("异常");
			logger.error("获取版本信息异常,{}",e);
		}
		return dto;
	}



	//	@GET
	//	@Path("saveUser")
	//	@Produces(MediaType.APPLICATION_JSON)
	//	public BaseDTO<Object> saveUser() {
	//		BaseDTO<Object> dto = new BaseDTO<Object>();
	//		try {
	//			User u = new User();
	//			u.setClubName("假的");
	//			u.setLocation("地址");
	//			u.setNikeName("nike");
	//			u.setName("test id自动增加");
	//			int aha = agentService.saveUser(u);
	//			System.out.println(aha);
	//			return dto;
	//		} catch (Exception e) {
	//			dto.setStatus("400");
	//			dto.setErrorMessage("请求异常~");
	//			logger.error("修改密码失败{}异常信息",e);
	//			return dto;
	//		}
	//	}


	/**
	 * 
	 * 清除redis缓存 
	 * 
	 * @return
	 */
	@GET
	@Path("clearredis")
	@Produces(MediaType.APPLICATION_JSON)
	public BaseDTO<Object> clearredis(){
		BaseDTO<Object> dto = new BaseDTO<Object>();
		List<Proxy> list = agentService.getAllproxy();
		for(Proxy p:list){
			String token = encipher.encrypt(p.getPhone());
			redisCacheService.remove(token);
		}
		return dto;
	}



	/**
	 * 获取token值，用户支付宝支付完成之后的回调
	 * @param orderId 订单id
	 * @return 
	 */
	@POST
	@Path("getuserToken")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public NormalDTO<Object> getuserToken(String orderId){
		NormalDTO<Object> dto = new  NormalDTO<Object>();
		Map<String,String> map = new HashMap<String,String>();
		String phone = agentService.getPhoneByOrderId(orderId);
		if(phone == null){
			dto.setErrorMessage("获取token失败");
			dto.setStatus("202");
		}
		String token = encipher.encrypt(phone);
		//将信息  存入缓存中
		Account e_a = redisCacheService.get(token, Account.class);
		if(e_a == null){
			dto.setErrorMessage("获取用户信息失败");
			dto.setStatus("202");
		}
		map.put("uid", e_a.getUid());
		map.put("token",token);
		dto.setData(map);
		return dto;
	}



	/**
	 * 退出登录
	 */
	@GET
	@Path("goOut")
	@Produces(MediaType.APPLICATION_JSON)
	public NormalDTO<Object> goOut(){
		NormalDTO<Object> dto = new NormalDTO<Object>();
		String token = request.getHeader("Access-Control-Allow-Headers");
		if(token ==null){
			Enumeration<String> headerNames=request.getHeaderNames();
			for(Enumeration<String> e=headerNames;e.hasMoreElements();){
				String thisName=e.nextElement().toString();
				if("Access-Control-Allow-Headers".equals(thisName)){
					token=request.getHeader(thisName);
					break;
				}
			}
		}
		//		ehcacheService.remove(token);
		redisCacheService.remove(token);
		logger.info("退出登录成功");
		return dto;
	}


	/**
	 * 登录
	 * 
	 * @param phone
	 *            手机号（账号）
	 * @param password
	 *            密码
	 * @return
	 */
	@POST
	@Path("login")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public NormalDTO<Object> login(UserInfoParam userInfoParam) {
		NormalDTO<Object> dto = new NormalDTO<Object>();
		try {
			Account account = accountService.login(userInfoParam.getPhone(), userInfoParam.getPassword());
			if (account == null) {
				dto.setStatus("401");
				dto.setErrorMessage("账号或密码错误~");
				logger.info("登录，账号或者密码错误");
				return dto;
			}
			if("false".equals(account.getCanUse())){
				dto.setStatus("401");
				dto.setErrorMessage("账号已经被禁止使用~");
				logger.info("账号{}已经被禁止使用",account);
				return dto;
			}
			//将token缓存起来
			Proxy userInfo = agentService.getProxy(account.getUid());
			String token = encipher.encrypt(userInfo.getPhone());
			//将信息  存入缓存中
			//			Account e_a = ehcacheService.get(token, Account.class);
			//			Account e_a = cacheServiceProxy.get(token, Account.class);
			Account e_a = redisCacheService.get(token, Account.class);
			if(e_a == null){
				//int failureTime = getFailureTime();
				//				cacheServiceProxy.put(token, account);
				//				ehcacheService.put(token, account,600*60);//不过期
				logger.info("登录，将用户信息存到缓存，永不过时，除非退出登录，清空缓存,token={}",token);
				redisCacheService.put(token, account);
			}
			dto.setData(token);
			return dto;
		} catch (Exception e) {
			dto.setStatus("401");
			dto.setErrorMessage("请求异常~");
			logger.error("登录失败{}异常信息",e);
			return dto;
		}
	}

	/**
	 * 修改密码
	 * 
	 * @param oldpass
	 *            新密码
	 * @param newpass
	 *            密码
	 */
	@POST
	@Path("uptPassword")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public BaseDTO<Object> uptPassword(PasswordParam passwordParam) {
		BaseDTO<Object> dto = new BaseDTO<Object>();
		try {
			Account account = accountService.getAccount(getUid(request));
			if (!account.getPassword().equals(passwordParam.getOldPass())) {
				dto.setStatus("203");
				dto.setErrorMessage("原始密码不正确");
				logger.info("原始密码不正确");
				return dto;
			}
			// 修改密码
			accountService.uptPassword(passwordParam, account);
			return dto;
		} catch (Exception e) {
			dto.setStatus("400");
			dto.setErrorMessage("请求异常~");
			logger.error("修改密码失败{}异常信息",e);
			return dto;
		}
	}

	/**
	 * 
	 * 添加代理 待完善--手机号码 验证码验证
	 * 
	 * @param userInfoParam
	 *            手机号，昵称，验证码，密码 代理信息(用户名,密码,手机号)
	 * @return
	 */
	@POST
	@Path("addAgent")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public NormalDTO<Object> addAgent(UserInfoParam userInfoParam) {
		NormalDTO<Object> dto = new NormalDTO<Object>();
		try {
			//			HttpSession session = request.getSession();
			//			String verifyCode = (String) session.getAttribute("verifyCode");
			//			ehcacheService.put("code", verifyCode);
			//			String verifyCode = ehcacheService.get("code", String.class);
			if(userInfoParam.getNikeName().length() > 5){
				dto.setStatus("201");
				dto.setErrorMessage("昵称太长了~");
				logger.info("昵称太长了~");
				return dto; 
			}
			String verifyCode = redisCacheService.get("code", String.class);
			if (verifyCode== null || "".equals(verifyCode) || userInfoParam.getVerifyCode() == null || !(verifyCode.equals(userInfoParam.getVerifyCode()))) {
				// 验证码错误
				dto.setStatus("201");
				dto.setErrorMessage("验证码错误~");
				logger.info("验证码错误~");
				return dto; 
			}
			byte sign = 0;
			if (userInfoParam.getType() == 1) {// 自己申请注册
				logger.info("type=1，自己申请注册开始");
				sign = agentService.register(userInfoParam.getInviteCode(), userInfoParam.getNikeName(),
						userInfoParam.getPassword(), userInfoParam.getPhone(),userInfoParam.getIsSpecial());
			} else if (userInfoParam.getType() == 2) {// 代理替代注册
				logger.info("type=2，代理替代注册开始");
				sign = agentService.register(getUid(request), userInfoParam.getNikeName(), userInfoParam.getPassword(),
						userInfoParam.getPhone(),userInfoParam.getIsSpecial());
			}
			if (sign == 2) {
				dto.setStatus("202");
				dto.setErrorMessage("用户手机号码已经存在");
				logger.info("用户手机号码已经存在");
				return dto;
			}
			if (sign == 3) {
				dto.setStatus("202");
				dto.setErrorMessage("未获取上级信息");
				logger.info("未获取上级信息,请填写邀请码");
				return dto;
			}
			//			String token = new MD5().encrypt(userInfoParam.getPhone());
			//			//将信息  存入缓存中
			//			Account e_a = ehcacheService.get(token, Account.class);
			//			if(e_a == null){
			//				ehcacheService.put(token, account, 1800);//30min
			//			}
			dto.setStatus("0");
			dto.setErrorMessage("注册成功");
			//			dto.setData(token);
			return dto;

		} catch (Exception e) {
			dto.setStatus("203");
			dto.setErrorMessage("系统异常，请稍后再试");
			logger.error("注册失败异常信息{}",e.getMessage());
		}
		return dto;
	}

	/**
	 * 发送手机验证码
	 * 
	 * @param phone
	 *            手机号
	 * @return
	 */
	@POST
	@Path("verifyCode")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public NormalDTO<Object> getVerifyCode(String phone) {
		NormalDTO<Object> nd = new NormalDTO<Object>();
		phone = phone.indexOf("=") > 0 ? phone.substring(phone.indexOf("=") + 1, phone.length()) : phone;
		try {
			// 验证码
			String code = aliCodeVerify.sendMsg(phone);
			if(code == null){
				nd.setData("202");
				nd.setErrorMessage("验证码发送失败，请一分钟后再试");
				return nd;
			}
			logger.info("获取的{} 手机验证码为{}",phone,code);
			System.out.println("verifyCode==" + code);
			//			HttpSession session = request.getSession();
			////			session.setMaxInactiveInterval(10 * 60);// 10分钟内有效
			//			session.setAttribute("verifyCode", code);

			//			ehcacheService.put("code", code);//放到缓存
			redisCacheService.put("code", code);//放到缓存
			nd.setData(code);
		} catch (Exception e) {
			logger.error("验证码发送异常" + e);
			nd.setStatus("400");
			nd.setErrorMessage("请求异常~");
		}
		return nd;
	}

	/**
	 * 忘记密码
	 * 
	 * @param phone
	 *            手机号（账号）
	 * @param password
	 *            密码
	 * @param code
	 *            验证码
	 */
	@POST
	@Path("forgetPassord")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public BaseDTO<Object> forgetPassord(PasswordParam passwordParam) {
		BaseDTO<Object> dto = new BaseDTO<Object>();
		try {
			//	HttpSession session = request.getSession();
			//	String verificationCode = (String) session.getAttribute("verifyCode");
			//			String verifyCode = ehcacheService.get("code", String.class);
			String verifyCode = redisCacheService.get("code", String.class);
			if (passwordParam.getCode() == null || !(verifyCode.equals(passwordParam.getCode()))) {
				// 验证码错误
				dto.setStatus("401");
				dto.setErrorMessage("验证码错误~");
				logger.info("修改密码中验证码{}错误~",verifyCode);
				return dto;
			}
			// 判断验证码是否正确
			accountService.forgetPassord(passwordParam.getPhone(), passwordParam.getNewPass());
			return dto;
		} catch (Exception e) {
			dto.setStatus("400");
			dto.setErrorMessage("请求异常~");
			logger.error("修改密码中异常~",e);
			return dto;
		}
	}
	//
	//	public static void main(String[] args) {
	//		String password = "123456..";
	//		if (!Pattern.matches("^[0-9a-zA-Z]{6,18}", password)) {
	//			System.out.println("success");
	//		}
	//
	//	}


}
