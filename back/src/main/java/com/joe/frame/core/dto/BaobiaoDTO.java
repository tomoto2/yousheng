package com.joe.frame.core.dto;

import lombok.Data;
//每日报表
//	1.开始时间，截止时间
//	2.旗下代理数：旗下有多少代理，截止时间之前的所有代理数
//	3.当日业绩：今天卖掉多少张房卡
//	4.当日购卡：今天购买多少张房卡
//	5.实际返利：实际返利多少

/**
 * 
 * @author lpx
 *
 * 2017年7月24日
 */
@Data
public class BaobiaoDTO {
	/**
	 * 旗下的代理总数
	 */
	private long agentSum;
	/**
	 * 业绩
	 */
	private long yeji;
	/**
	 * 购卡总数
	 */
	private long gouka;

	/**
	 * 实际返利
	 */
	private long fanli;

}
