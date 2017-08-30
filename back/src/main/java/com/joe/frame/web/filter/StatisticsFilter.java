package com.joe.frame.web.filter;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.Priority;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

import org.glassfish.jersey.message.MessageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.joe.frame.web.bean.InterfaceInfo;
import com.joe.parse.json.JsonParser;

/**
 * 统计filter，统计请求次数与请求时间
 * 
 * @author joe
 *
 */
@Provider
@Priority(0)
@PreMatching
public class StatisticsFilter implements ContainerRequestFilter, ContainerResponseFilter {
	private static Logger logger = LoggerFactory.getLogger("Statistics");
	public static volatile Map<String, AtomicLong> interfaceInvokeInfo = new TreeMap<String, AtomicLong>();
	private static final int MAX_READ_SIZE = 512;
	private JsonParser jsonParser = JsonParser.getInstance();
	@Context
	private HttpServletResponse response;
	@Context
	private HttpServletRequest request;

	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
			throws IOException {
		Object obj = responseContext.getEntity();
		logger.debug("响应结果是：" + jsonParser.toJson(obj));
		InterfaceInfo info = getInterfaceInfo(requestContext);
		logger.info("此次请求共耗时" + info.getConsumeTime() + "ms");
		// 设置允许跨域
		//
		//		response.setHeader("Access-Control-Allow-Origin", "*");
		//		response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
		//		response.setHeader("Access-Control-Allow-Headers", "x-requested-with,content-type");


//		response.setHeader("Access-Control-Allow-Origin", "*");
//		response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
//		response.setHeader("Access-Control-Max-Age", "3600");
//		response.setHeader("Access-Control-Allow-Headers", "Content-Type, Access-Control-Allow-Headers, Authorization, Access-Token");

		//		response.setContentType("textml;charset=UTF-8");
		response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
		response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
		response.setHeader("Access-Control-Max-Age", "0");
		response.setHeader("Access-Control-Allow-Headers", "Origin, No-Cache, X-Requested-With, If-Modified-Since, Pragma, Last-Modified, Cache-Control, Expires, Content-Type, X-E4M-With,userId,token,Access-Control-Allow-Headers");
		response.setHeader("Access-Control-Allow-Credentials", "true");
		response.setHeader("XDomainRequestAllowed","1");

	}
	//        res.setContentType("textml;charset=UTF-8");
	//        res.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
	//        res.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
	//        res.setHeader("Access-Control-Max-Age", "0");
	//        res.setHeader("Access-Control-Allow-Headers", "Origin, No-Cache, X-Requested-With, If-Modified-Since, Pragma, Last-Modified, Cache-Control, Expires, Content-Type, X-E4M-With,userId,token");
	//        res.setHeader("Access-Control-Allow-Credentials", "true");
	//        res.setHeader("XDomainRequestAllowed","1");
	//        filterChain.doFilter(servletRequest,servletResponse);


	public void filter(ContainerRequestContext requestContext) throws IOException {
		String method = requestContext.getMethod();
		UriInfo uriInfo = requestContext.getUriInfo();
		String path = uriInfo.getRequestUri().toString();

		InterfaceInfo info = getInterfaceInfo(requestContext);
		logger.info("接收到请求，请求方法为：" + method + "，请求地址为：" + path + "，请求接口为：" + info.getRealRequestAddr() + "，开始处理");
		logRequestInfo(requestContext);
	}

	private InterfaceInfo getInterfaceInfo(ContainerRequestContext requestContext) {
		return (InterfaceInfo) requestContext.getProperty("InterfaceInfo");
	}

	/**
	 * 读取请求中的信息（最大读取MAX_READ_SIZE字节）
	 * 
	 * @param requestContext
	 * @throws IOException
	 */
	private void logRequestInfo(ContainerRequestContext requestContext) throws IOException {
		if (requestContext.getMethod().equals("GET")) {
			return;
		}
		InputStream input = requestContext.getEntityStream();
		if (!input.markSupported()) {
			input = new BufferedInputStream(input);
		}

		input.mark(MAX_READ_SIZE + 1);
		byte[] data = new byte[MAX_READ_SIZE + 1];
		int readSize = input.read(data);
		StringBuilder sb = new StringBuilder();
		if (readSize < 1) {
			sb.append("请求post信息为空");
			logger.info(sb.toString());
			return;
		}
		sb.append("请求post信息为：");
		sb.append(new String(data, 0, Math.min(readSize, MAX_READ_SIZE),
				MessageUtils.getCharset(requestContext.getMediaType())));
		if (readSize > MAX_READ_SIZE) {
			sb.append("...more....");
		}
		logger.info(sb.toString());
		input.reset();
		requestContext.setEntityStream(input);
		return;
	}
}