package com.joe.frame.core.dto;

import lombok.Data;

/**
 * 分红
 * 
 * @author lpx
 *
 */
@Data
public class ShareDTO{
	/**
	 * 分红ID
	 */
	private String id;

	/**
	 * 分红日期，格式：yyyy-MM-dd HH:mm:ss
	 */
	private String date;

	/**
	 * 每月分红金额/推荐收益，单位：分
	 * 
	 */
	private long count = 0;

	/**
	 * 已分红的所有金额
	 */
	private long allCount = 0;

	/**
	 * 获得分红的用户ID
	 */
	private String uid;



	/**
	 * 个人购卡量总金额
	 */
	private long allMoney = 0;
	/**
	 * 旗下代理购卡总金额
	 */
	private long qixiaAllMoney = 0;
	/**
	 * 总业绩：个人购卡总金额+旗下代理购卡总金额
	 */
	private long allYeji =0;
	/**
	 * 返利比：个人购卡的总金额所对应的返利比
	 */
	private long fanliBi = 0;
	/**
	 * 总返利：个人购卡总金额*返利比例
	 */
	private long allFanli =0;
	/**
	 * 代理返利：旗下代理总金额*旗下代理总金额对应的返利比
	 */
	private long qixiaAllFanli = 0;
	/**
	 * 实际返利
	 */
	private long shijiFanli = 0;

}
