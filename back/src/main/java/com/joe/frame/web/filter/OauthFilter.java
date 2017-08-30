package com.joe.frame.web.filter;

import java.io.IOException;

import javax.annotation.Priority;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.joe.frame.web.cache.CacheService;
import com.joe.frame.web.cache.CacheServiceProxy;
import com.joe.frame.web.cache.RedisCacheService;

/**
 * 权限认证filter
 * 有问题，暂时不使用
 * 
 * @author Administrator
 *
 */
@PreMatching
@Priority(Priorities.AUTHENTICATION)
public class OauthFilter implements ContainerRequestFilter {
	private static Logger logger = LoggerFactory.getLogger("Authorization");
	@Context
	private HttpServletRequest request;
	/**
	 * 使用@Resource注入必须在类上使用@Component注解，否则注入失败
	 */
	@Autowired
	private RedisCacheService cache;

	public void filter(ContainerRequestContext requestContext) throws IOException {
		// 获取用户token和IP用以验证权限
//		String token = request.getHeader("token");
//		String ip = request.getRemoteAddr();
//		logger.debug("user token is {} ,user ip is {}", token, ip);
//		// 获取缓存中该用户的信息，该信息在登陆时存进来，其中数组的第一个是IP信息，第二个是权限信息
//		Object[] info = cache.get(token, Object[].class);
//		AppSecurityContext securityContext = new AppSecurityContext(null);
//
//		if (info != null) {
//			logger.debug("该token存在缓存，缓存中ip为：{}", info[0]);
//			if ((ip.equals((String) info[0]))) {
//				logger.debug("ip一致，缓存中的权限信息为：{}", info[1]);
//				securityContext.setUser(((User) info[1]));
//			}
//		}
//		requestContext.setSecurityContext(securityContext);
	}
}
