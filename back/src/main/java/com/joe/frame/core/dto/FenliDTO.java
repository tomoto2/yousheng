package com.joe.frame.core.dto;

import lombok.Data;

/**
 *  我的分利页面
 *  
 *  
 * 账户余额
 * 本月实际返利
 * 上月实际返利
 * 本月的
 * 个人总业绩。 返利比例。 个人总返利
 * 旗下代理购卡，旗下代理返利，您实际返利
 * @author lpx
 *
 * 2017年7月25日
 */

@Data
public class FenliDTO {
	private long balance;//账户余额
	private long benShijiFanli;//本月实际返利
	private long benAllyeji;//总业绩
	private long benfanlibi;//本月返利比
	private long benAllfanli;//本月总返利
	private long benqixiaAllMoney;//旗下代理购卡总金额
	private long benqixiaAllFanli;//旗下代理返利
	private long preShijiFanli;//上月实际返利
	
	
}
