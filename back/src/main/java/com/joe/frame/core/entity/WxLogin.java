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
 * 微信登录表（其中每用微信登录一次用户的账号和密码都会随机更换一次）
 * 
 * @author joe
 *
 */
@Setter
@Getter
@ToString
@Table
@Entity
public class WxLogin extends BaseEntity<String> {
	// ID
	@Id
	@Column(length = 50)
	private String wlid;
	// 微信oppenid
	@Column(length = 50)
	private String oppenid;
	// 随机生成的账号
	@Column(length = 50)
	private String account;
	// 随机生成的密码
	@Column(length = 50)
	private String password;
	// 对应的用户ID
	@Column(length = 50)
	private String uid;
	// 用户统一标识。针对一个微信开放平台帐号下的应用，同一用户的unionid是唯一的
	@Column(length = 50)
	private String unionid;
}
