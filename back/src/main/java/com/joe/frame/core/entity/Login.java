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
 * 登陆详情表
 * 
 * @author Administrator
 *
 */
@Setter
@Getter
@ToString
@Table
@Entity
public class Login extends BaseEntity<String>{
	/**
	 * 登陆记录表ID
	 */
	@Id
	@Column(length=32)
	private String id;

	/**
	 * 登陆ID（账号）
	 */
	@Column(length=16)
	private String aid;
	
	/**
	 * 用户ID
	 */
	@Column(length=32)
	private String uid;
	
	/**
	 * 登录日期
	 *          格式：yyyyMMddHHmmss
	 */
	@Column(length=14)
	private String logintime;
	
	/**
	 * 登录ip
	 */
	@Column(length=20)
	private String loginip;
	public int hashCode(){
		return id.hashCode();
	}
}
