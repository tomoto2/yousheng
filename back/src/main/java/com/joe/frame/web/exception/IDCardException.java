package com.joe.frame.web.exception;

import com.joe.frame.web.prop.StatusCode;

/**
 * 身份证异常
 * @author Administrator
 *
 */
public class IDCardException extends CodeException {
	private static final long serialVersionUID = -6981965114522219723L;
	
	public IDCardException(String code){
		this(code , StatusCode.getMessage(code));
	}
	
	public IDCardException(String code, String message) {
		super(code , message);
	}

	public IDCardException(String code, String message, Throwable cause) {
		super(code , message , cause);
	}
	
	public IDCardException(String code, Throwable cause) {
		this(code , StatusCode.getMessage(code) , cause);
	}
}
