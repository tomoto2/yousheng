package com.joe.frame.web.exception;

public class BeanException extends RuntimeException {
	private static final long serialVersionUID = -7474640657217966244L;

	public BeanException(String message) {
		super(message);
	}

	public BeanException(String message, Throwable cause) {
		super(message, cause);
	}
}
