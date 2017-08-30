package com.joe.frame.core.trade.entity;

/**
 * 订单状态
 * 
 * @author joe
 *6 2 9 10 11
 */
public enum OrderStatus {
	CREATE("订单创建"), PAID("已支付"), TIMEOUT("订单支付超时"), DELIVERY("已接单，发货中"), CONFIRM("确认收货"), APPLYRETURE(
			"退货申请中"), RETURN("已经退货"), TROUBLE("纠纷订单"), CONFIRMBYTIMEOUT(
					"超时自动确认收货"), COMMENT("评价"), COMMENTTIMEOUT("超时自动评价"), REFUSE("未接单"), RETURNBYTIMEOUT("退货超时，自动退货");
	private String description;

	private OrderStatus(String description) {
		this.description = description;
	}

	public String toString() {
		return this.description;
	}
}