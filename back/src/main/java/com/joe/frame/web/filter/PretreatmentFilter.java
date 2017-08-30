package com.joe.frame.web.filter;

import java.io.IOException;

import javax.annotation.Priority;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.server.ExtendedUriInfo;
import org.springframework.beans.factory.annotation.Autowired;

import com.joe.frame.common.util.DateUtil;
import com.joe.frame.web.bean.InterfaceInfo;
import com.joe.frame.web.cache.CacheService;
import com.joe.frame.web.cache.CacheServiceProxy;
import com.joe.frame.web.service.InterfaceService;

/**
 * 请求预处理，优先级应为最高
 * 
 * @author Administrator
 *
 */
@Provider
@PreMatching
@Priority(Integer.MIN_VALUE)
public class PretreatmentFilter implements ContainerRequestFilter, ContainerResponseFilter {
	@Resource(type = CacheServiceProxy.class)
	private CacheService cache;
	@Autowired
	private InterfaceService service;
	@Autowired
	private DateUtil dateUtil;
	@Context
	private HttpServletRequest httpServletRequest;
	private String format = "yyyy-MM-dd HH:mm:ss SSS";

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		ContainerRequest request = (ContainerRequest) requestContext;
		ExtendedUriInfo uriInfo = request.getUriInfo();
		// Class<?> resourceClass = uriInfo.getClass();
		// Method resourceMethod =
		// uriInfo.getMatchedResourceMethod().getInvocable().getDefinitionMethod();
		// 判断当前接口是否允许访问
		// if(!service.isAllow(resourceMethod, resourceClass)){
		// //当前接口不允许访问
		// BaseDTO dto = new BaseDTO();
		// Response response = Response.ok(dto,
		// MediaType.APPLICATION_JSON_TYPE).build();
		// request.abortWith(response);
		// }
		InterfaceInfo info = new InterfaceInfo();
		info.setBeginTime(dateUtil.getFormatDate(format));
		info.setRealRequestAddr(uriInfo.getPath());
		// info.setInterfaceName(service.getResourcePath(resourceMethod,
		// resourceClass));
		info.setIp(httpServletRequest.getRemoteAddr());
		service.process(info);
		requestContext.setProperty("InterfaceInfo", info);
	}

	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
			throws IOException {
		InterfaceInfo info = (InterfaceInfo) requestContext.getProperty("InterfaceInfo");
		info.setFinish(true);
		info.setEndTime(dateUtil.getFormatDate(format));
		try {
			int consume = (int) (dateUtil.parse(info.getEndTime(), format).getTime()
					- dateUtil.parse(info.getBeginTime(), format).getTime());
			info.setConsumeTime(consume);
		} catch (Exception e) {
		}
		service.process(info);
	}
}
