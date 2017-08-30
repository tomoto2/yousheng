package com.joe.frame.web.boot;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.joe.frame.web.dto.BaseDTO;
import com.joe.frame.web.exception.CodeException;

/**
 * 统一异常处理
 * 
 * @author dengjianjun
 *
 */
@Provider
public class ExceptionMapperSupport implements ExceptionMapper<Throwable> {

	static final Logger logger = LoggerFactory.getLogger(ExceptionMapperSupport.class);

	@Override
	public Response toResponse(Throwable e) {
		BaseDTO<Object> dto = new BaseDTO<Object>();
		if (e instanceof CodeException) {
			dto.setStatus(((CodeException) e).getCode());
			dto.setErrorMessage(((CodeException) e).getErr_message());
		} else if (e instanceof NotFoundException) {
			// 404错误，找不到资源
			return Response.status(Status.NOT_FOUND).build();
		} else {
			dto.error();
		}

		logger.error("{}:{}->{}", dto.getStatus(), dto.getErrorMessage(), e);
		return Response.ok(dto, MediaType.APPLICATION_JSON).build();
	}
}
