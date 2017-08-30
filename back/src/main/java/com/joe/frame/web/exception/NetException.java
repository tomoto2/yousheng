package com.joe.frame.web.exception;

import com.joe.frame.web.prop.StatusCode;

/**
 * 网络异常
 * @author Administrator
 *
 */
public class NetException extends CodeException {
	private static final long serialVersionUID = 6179851531167563791L;

	public NetException() {
		super("500" , StatusCode.getMessage("500"));
	}
	public NetException(Throwable cause) {
		super("500" , StatusCode.getMessage("500") , cause);
	}
}
