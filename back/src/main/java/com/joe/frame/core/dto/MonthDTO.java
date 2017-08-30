package com.joe.frame.core.dto;

import lombok.Data;

/**
 * //	个人购卡：个人购卡量总金额
	//	旗下代理购卡：旗下代理购卡总金额
	//	您的总业绩：个人购卡总金额+旗下代理购卡总金额
	//	您的返利比：个人购卡的总金额所对应的返利比
	//	您的总返利：个人购卡总金额*返利比例
	//	旗下代理返利：旗下代理总金额*旗下代理总金额对应的返利比
	//	您的实际返利：您总业绩 * 对应提成比例 - 直属下级A的业绩 * 对应提成比例- 直属下级B的业绩 * 对应提成比例
 * @author lpx
 *
 * 2017年7月25日
 */
@Data
public class MonthDTO {

	/**
	 * 个人购卡量总金额
	 */
	private long allMoney;
	/**
	 * 旗下代理购卡总金额
	 */
	private long qixiaAllMoney;
	/**
	 * 总业绩：个人购卡总金额+旗下代理购卡总金额
	 */
	private long allYeji;
	/**
	 * 返利比：个人购卡的总金额所对应的返利比  分
	 */
	private long fanliBi;
	/**
	 * 总返利：个人购卡总金额*返利比例
	 */
	private long allFanli;
	/**
	 * 代理返利：旗下代理总金额*旗下代理总金额对应的返利比  分
	 */
	private long qixiaAllFanli;
	/**
	 * 实际返利
	 */
	private long shijiFanli;


	/**
	 * 代理个人总业绩的实际返利金额（未减去旗下代理的收益的）
	 */
	private long oneshijiFanliPre;


	/**
	 * 旗下代理总金额对应的返利比
	 */
	
	private long qixiaFanliBi;
	
	
	/**
	 * 旗下直属代理总金额对应的返利比
	 */
	private long driAllmoneyRate;
	

}
