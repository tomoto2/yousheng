package com.joe.frame.core.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.joe.frame.core.database.BaseEntity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 分红
 * 
 * @author lpx
 *
 */
@Setter
@Getter
@ToString
@Table
@Entity
public class Share extends BaseEntity<String> {
	/**
	 * 分红ID
	 */
	@Id
	@Column(length = 32)
	private String id;

	/**
	 * 分红日期，格式：yyyy-MM-dd HH:mm:ss
	 */
	@Column(length=30)
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
	@Column(length = 32)
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
	 * 返利比：个人购卡的总金额所对应的返利比 单位:分
	 */
	private long fanliBi = 0;
	/**
	 * 总返利：个人购卡总金额*返利比例
	 */
	private long allFanli =0;
	/**
	 * 代理返利：旗下代理总金额*旗下代理总金额对应的返利比 单位：分
	 */
	private long qixiaAllFanli = 0;
	/**
	 * 实际返利
	 */
	private long shijiFanli = 0;
	
	public int hashCode(){
		return id.hashCode();
	}

}
