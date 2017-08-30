package com.joe.frame.web.filter;

import java.io.IOException;
import java.util.Enumeration;

import javax.annotation.Priority;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.joe.frame.core.entity.Account;
import com.joe.frame.web.cache.EhcacheService;

/**
 * 登录拦截
 * @author joe
 *
 */
@PreMatching
@Priority(1)
@Provider 
public class LoginFilter implements ContainerRequestFilter{
	private static Logger logger = LoggerFactory.getLogger(LoginFilter.class);
	@Autowired
	private EhcacheService ehcacheService;
	@Context
	private HttpServletRequest request;


	public void filter(ContainerRequestContext requestContext) throws IOException {
		String path = requestContext.getUriInfo().getRequestUri().toString();
		if(path.endsWith("/login"))
			return  ;
		String token = request.getHeader("Access-Control-Allow-Headers");
		System.out.println(token);
		
		System.out.println(path);

		/*if(path.endsWith("/sendGetOpenid"))
			return  ;

		if(path.indexOf("ws/pay/get") > 0)
			return ;

		if(path.indexOf("ws/pay/post") > 0)
			return ;

		if(path.endsWith("/inqueryInfoResource"))
			return  ;

		if(path.endsWith("/uptPasswordResource"))
			return  ;

		if(path.endsWith("/question_password"))
			return  ;

		if(path.endsWith("/alipayCallBack"))
			return  ;

		if(path.endsWith("/getNum") || path.endsWith("/getAgentNum"))
			return  ;*/

		/*boolean boo = true;
		if(!path.endsWith("accountRe/login") && !path.endsWith("accountRe/adminLogin")){ 
			logger.debug("判断用户是否登陆");
			HttpSession session = request.getSession();
			if (session == null || session.getAttribute("info") == null) {
				boo = false;
			}
		}

		//从ehcache 中用 token 换取 account信息
		if(!boo){
			String token = request.getHeader("token");
			if(token ==null){
				Enumeration<String> headerNames=request.getHeaderNames();
				for(Enumeration<String> e=headerNames;e.hasMoreElements();){
					String thisName=e.nextElement().toString();
					if("token".equals(thisName)){
						//System.out.println("header的key:"+thisName+"--------------header的value:"+token);
						token=request.getHeader(thisName);
						break;
					}
				}
			}

			//从缓存中 获取 token 对应的 信息
			Account account = null;
			if(token != null){
				account = ehcacheService.get(token, Account.class);
				if(account == null){
					boo = false;
				}else{
					boo = true;
				}
			}

		}

		if(!boo){
			logger.info("用户未登录");
			BaseDTO<Object> dto = new BaseDTO<Object>();
			dto.setStatus("800");
			// 直接返回
			requestContext.abortWith(Response.ok(dto).build());
		}else{
			System.out.println("登录成功");
			logger.info("用户已登录");
		}*/

	}
	
	

	
	
	
}
