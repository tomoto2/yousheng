package com.joe.frame.core.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 玩家信息
 * @author lpx
 *
 * 2017年7月20日
 */
@Getter
@Setter
@ToString
@Table
@Entity
public class User extends BaseUser{

	/**
	 * 玩家编号
	 * 注册玩家ID为6位数，首位数字从3开始31***
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(length = 6)
	private long uid;
	
	
	/**
	 *  头像链接
	 */
	@Column(length = 500)
	private String head;
	
	/**
	 * 在俱乐部中的备注
	 * 
	 */
	@Column(length = 50)
	private String remarks;
	/**
	 * 在俱乐部中的状态（0激活状态，2被移出状态）
	 */
//	@Column(length = 2)
//	private String status = "0";
	

	/**
	 * 性别
	 * 男1 
	 * 女2
	 * 保密3
	 */
	private int sex;

}
