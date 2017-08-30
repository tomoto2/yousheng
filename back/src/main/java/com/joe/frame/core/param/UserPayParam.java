package com.joe.frame.core.param;

import lombok.Data;

/**
 * 玩家自己充值房卡
 * @author lpx
 *
 * 2017年8月23日
 */
@Data
public class UserPayParam {

	/**
	 * 玩家6位id
	 */
	private long uid;
	/**
	 * 充值数量
	 */
	private int num;
	
}
