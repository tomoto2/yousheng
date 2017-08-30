package com.joe.frame.web.filter;

import java.io.IOException;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.joe.frame.web.dto.BaseDTO;

/**
 * 控制服务器打开关闭
 * 
 * @author joe
 *
 */
@PreMatching
@Priority(1)
@Provider
public class ControlFilter implements ContainerRequestFilter {
	private static final Logger logger = LoggerFactory.getLogger(ControlFilter.class);
	/*
	 * 标记服务器是否打开，true为打开
	 */
	private static boolean open = true;

	/**
	 * 打开服务器
	 */
	public static void open() {
		logger.warn("打开服务器");
		open = true;
	}

	/**
	 * 关闭服务器
	 */
	public static void shutdown() {
		logger.warn("关闭服务器");
		open = false;
	}

	/**
	 * 判断服务器是否打开
	 * 
	 * @return
	 *         <li>true：服务器未关闭</li>
	 *         <li>false：服务器已关闭</li>
	 */
	public static boolean isOpen() {
		return open;
	}

	public void filter(ContainerRequestContext requestContext) throws IOException {
		if (!isOpen()) {
			logger.warn("服务器已关闭");
			BaseDTO<Object> dto = new BaseDTO<Object>();
			dto.setStatus("900");
			// 直接返回
			requestContext.abortWith(Response.ok().entity(dto).type(MediaType.APPLICATION_JSON_TYPE).build());
		}
	}
}
