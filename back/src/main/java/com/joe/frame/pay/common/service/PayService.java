package com.joe.frame.pay.common.service;

import com.joe.frame.core.entity.Recharge;

public abstract class PayService {
	/**
	 * 将系统订单退款
	 * 
	 * @param order
	 *            要退款的系统订单
	 * @return
	 *         <li>true：退款成功</li>
	 *         <li>false：退款失败</li>
	 */
	public abstract boolean gateway(Recharge order);
}
