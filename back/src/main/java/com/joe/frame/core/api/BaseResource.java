package com.joe.frame.core.api;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;

import com.joe.frame.core.entity.Account;
import com.joe.frame.web.cache.RedisCacheService;

public abstract class BaseResource {

	//	@Autowired
//		private EhcacheService ehcacheService;
	//	@Autowired
	//	private CacheServiceProxy cacheServiceProxy;

	//	@Resource(type=RedisCacheService.class)
//		private CacheService redisCacheService;
	//	
	@Autowired
	private RedisCacheService redisCacheService;


	/**
	 * 从请求头中获取 token 
	 * 从ehcache 中用 token 换取 account信息
	 * @param request
	 * @return
	 */
	protected Account getToken(HttpServletRequest request) {
		String token = request.getHeader("Access-Control-Allow-Headers");
		if(token ==null){
			Enumeration<String> headerNames=request.getHeaderNames();
			for(Enumeration<String> e=headerNames;e.hasMoreElements();){
				String thisName=e.nextElement().toString();
				if("token".equals(thisName)){
					token=request.getHeader(thisName);
					break;
				}
			}
		}
		//从缓存中 获取 token 对应的 信息
		Account account = null;
		if(token != null){
			//			account = ehcacheService.get(token, Account.class);
			account =redisCacheService.get(token, Account.class);
			//			account= cacheServiceProxy.get(token, Account.class);
		}
		System.out.println("token == "+token);
		return account;
	}

	/**
	 * 从token中获取用户id
	 * @param request
	 * @return
	 */
	public String getUid(HttpServletRequest request){
		Account account = getToken(request);
		if(account == null){
			return null;
		}
		return account.getUid();
	}


	public Account getAccount(HttpServletRequest request){
		Account account = getToken(request);
		return account;
	}

	/**
	 * 从token中获取用户手机号码
	 * @param request
	 * @return
	 */
	public String getPhone(HttpServletRequest request){
		Account account = getToken(request);
		if(account == null){
			return null;
		}
		return account.getId();
	}

	//
	//	/**
	//	 * 从request获取用户已登录账号
	//	 * @param request
	//	 * @return
	//	 */
	//	protected Account getAccount(HttpServletRequest request) {
	//		HttpSession session = request.getSession();
	//		//此处肯定有值，没有值的情况filter会处理
	//		Account account = (Account) session.getAttribute("info");
	//		return account;
	//	}
	//
	//	/**
	//	 * 获取uid
	//	 * @param request
	//	 * @return
	//	 */
	//	protected String getUid(HttpServletRequest request) {
	//		return getAccount(request).getUid();
	//	}
	//	
	//	
	//	/**
	//	 * 获取手机号（登录账号）
	//	 * @param request
	//	 * @return
	//	 */
	//	protected String getPhone(HttpServletRequest request){
	//		return getAccount(request).getId();
	//	}


	/**
	 * 从request获取用户已登录账号
	 * @param request
	 * @return
	 */
	protected Account getAccountbyReq(HttpServletRequest request) {
		HttpSession session = request.getSession();
		//此处肯定有值，没有值的情况filter会处理
		Account account = (Account) session.getAttribute("info");
		return account;
	}

	/**
	 * 获取uid
	 * @param request
	 * @return
	 */
	protected String getUidReq(HttpServletRequest request) {
		return getAccountbyReq(request).getUid();
	}

}
