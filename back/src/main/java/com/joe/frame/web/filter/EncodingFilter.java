package com.joe.frame.web.filter;

import java.io.IOException;
import java.nio.charset.UnsupportedCharsetException;

import javax.annotation.Priority;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.ReaderInterceptorContext;

import org.glassfish.jersey.server.ContainerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.joe.frame.web.prop.SystemProp;

/**
 * 编码filter，只对POST请求生效，GET请求不会进入
 * 此处有错，需修改
 * 
 * @author qiao9
 *
 */
@PreMatching
@Priority(Integer.MIN_VALUE)
public class EncodingFilter implements ReaderInterceptor ,ContainerResponseFilter{
	private static Logger logger = LoggerFactory.getLogger(EncodingFilter.class);
	@Autowired
	private SystemProp prop;
	/**
	 * 对响应编码，全部采用配置文件配置编码格式
	 */
	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
			throws IOException {
		try {
			ContainerResponse response = (ContainerResponse)responseContext;
			response.setMediaType(response.getMediaType().withCharset("UTF8"));
			logger.debug("对响应信息（增加请求头）采用{}编码" + prop.getResponseCharset());
		} catch (Exception e) {
			logger.warn("系统预设响应字符集为：{}，应用该编码时出错，请检查" , prop.getResponseCharset());
		}
	}

	/**
	 * 对请求信息编码，全部采用配置文件配置编码格式
	 */
	public Object aroundReadFrom(ReaderInterceptorContext context) throws IOException, WebApplicationException {
		try {
			context.setMediaType(context.getMediaType().withCharset(prop.getRequestCharset()));
			logger.debug("对请求信息（POST信息）采用{}编码" + prop.getRequestCharset());
		} catch (UnsupportedCharsetException e) {
			logger.warn("系统预设请求字符集为：{}，应用该编码时出错，请检查" , prop.getRequestCharset());
		}
		return context.proceed();
	}
}
