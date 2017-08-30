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
 * 账号
 * @author root
 *
 */
@Setter
@Getter
@ToString
@Table
@Entity
public class Account extends BaseEntity<String> {
	
	/**
	 * 登录账号（用户手机号）
	 */
	@Id
	@Column(length = 32, unique = true)
	private String id;
	
	/**
	 * 用户ID
	 */
	@Column(length = 32)
	private String uid;

	/**
	 * 密码 
	 */
	@Column(length = 32)
	private String password;
	
	/**
	 * 添加时间
	 */
	@Column(length = 32)
	private String datetime;
	
	
	/**
	 * 账号可用性
	 * false 不可用
	 * ture 可用
	 */
	private String canUse;
	
}
