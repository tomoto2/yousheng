package com.joe.frame.pay.common.dto;

import lombok.Data;

/**
 * 前台订单对象 去掉用户ID，增加订单ID，删除终端IP
 * 
 * @author Administrator
 *
 */
@Data
public class OrderDTO {
	/**
	 * 订单ID
	 */
	private String orderId;
	// 优惠券ID
	private String couponId;

}
