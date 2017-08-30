package com.joe.frame.web.dto;

import java.io.Serializable;

import com.joe.frame.web.prop.StatusCode;

/**
 * 基本DTO，所有DTO父类
 * @author Administrator
 *
 */
public class BaseDTO<T> implements Serializable {
	private static final long serialVersionUID = 5075924626508128661L;
	/**
	 * 请求状态
	 */
	private String status;
	/**
	 * 错误消息
	 */
	private String errorMessage;

	public BaseDTO() {
		this.success();
	}

	/**
	 * 错误，未知原因
	 * 
	 * @param status
	 */
	public void error() {
		this.error("999");
	}

	/**
	 * 错误
	 * 
	 * @param status
	 *            状态码
	 */
	public void error(String status) {
		this.setStatus(status);
	}

	/**
	 * 错误
	 * 
	 * @param status
	 *            状态码
	 * @param message
	 *            错误消息
	 */
	public void error(String status, String message) {
		this.status = status;
		this.errorMessage = message;
	}

	/**
	 * 请求成功
	 */
	private void success() {
		setStatus("0");
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.errorMessage = StatusCode.getMessage(status);
		this.status = status;
	}

	public String getErrorMessage() {
		return errorMessage;
	}
	
	public void setErrorMessage(String errorMessage){
		this.errorMessage = errorMessage;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String toString() {
		return this.status + ":" + this.errorMessage;
	}
}
