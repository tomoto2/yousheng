package com.joe.frame.web.filter;

import java.io.IOException;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.joe.parse.json.JsonParser;

/**
 * 表单验证失败处理
 *
 */
@Provider
public class ValidationErrorFilter implements WriterInterceptor {
	private static final MediaType type = MediaType.TEXT_HTML_TYPE;
	private static final Logger logger = LoggerFactory.getLogger(ValidationErrorFilter.class);
	private JsonParser jsonParser = JsonParser.getInstance();

	public void aroundWriteTo(WriterInterceptorContext context) throws IOException, WebApplicationException {
//		if(context.getMediaType() != null){
//			if (type.getType().equals(context.getMediaType().getType())
//					&& type.getSubtype().equals(context.getMediaType().getSubtype())) {
//				logger.error("验证失败，失败详情：{}", jsonParser.toJson(context.getEntity()));
//				context.setEntity(null);
//				context.setMediaType(MediaType.APPLICATION_JSON_TYPE);
//			}
//		}
		context.proceed();
	}

}
