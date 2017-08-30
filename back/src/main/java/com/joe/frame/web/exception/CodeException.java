package com.joe.frame.web.exception;

public class CodeException extends RuntimeException {
	private static final long serialVersionUID = 2916814266308480941L;
	/**
	 * 异常代码
	 */
	protected String code;
	/**
	 * 异常信息
	 */
	protected String err_message;
	/**
	 * 异常原因
	 */
	protected Throwable cause;
	
	public CodeException(String code, String message) {
		super(code + " -> " + message);
		this.code = code;
		this.err_message = message;
	}

	public CodeException(String code, String message, Throwable cause) {
		super(code + ":" + message, cause);
		this.code = code;
		this.err_message = message;
		this.cause = cause;
	}

	public String getCode() {
		return code;
	}

	protected void setCode(String code) {
		this.code = code;
	}

	public String getErr_message() {
		return err_message;
	}

	protected void setErr_message(String err_message) {
		this.err_message = err_message;
	}

	protected static long getSerialversionuid() {
		return serialVersionUID;
	}
}
