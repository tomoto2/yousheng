package com.joe.frame.pay.alipay;

import com.joe.frame.pay.alipay.dto.CashOut;

/**
 * 提现函数接口
 * 
 * @author joe
 *
 */
public interface Function {
	/**
	 * 提现函数
	 * @param cashOut
	 * 提现对象
	 * @param e
	 * 异常原因（只有异常时才有该参数）
	 */
	public void function(CashOut cashOut , Throwable e);
}
