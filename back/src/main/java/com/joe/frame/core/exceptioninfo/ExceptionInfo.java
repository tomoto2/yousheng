package com.joe.frame.core.exceptioninfo;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 异常信息收集
 * @author root
 */
public class ExceptionInfo {

	private static final Logger logger = LoggerFactory.getLogger(ExceptionInfo.class);

	/**
	 * 收集信息 
	 *    什么操作
	 *    什么异常
	 *    什么人操作
	 */
	public void collectException(HttpServletRequest request, Exception e){
		String pathInfo = request.getPathInfo();
		System.out.println(pathInfo);

		StackTraceElement[] st = e.getStackTrace();
		for (StackTraceElement stackTraceElement : st) {
			String exclass = stackTraceElement.getClassName();
			String method = stackTraceElement.getMethodName();
			System.out.println(new Date() + ":" + "[类:" + exclass + "]调用"
					+ method + "时在第" + stackTraceElement.getLineNumber()
					+ "行代码处发生异常!异常类型:" + e.getClass().getName());
		}
	}

}
