package com.joe.frame.core.entity;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import com.joe.frame.core.database.BaseEntity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@MappedSuperclass
public abstract class BaseUser extends BaseEntity<String>{
	
	/**
	 *  昵称
	 */
	@Column(length = 5)
	private String nikeName;
	/**
	 *  真实姓名(qgh--user昵称)
	 */
	@Column(length = 50)
	private String name;

	/**
	 *  当前房卡数量
	 */
	private int card = 0;
	/**
	 *  房卡ID
	 */
	@Column(length = 50)
	private String cardId;

	/**
	 *  所属俱乐部id（所属的哪个代理），没有俱乐部时该值为空
	 */
	@Column(length = 50)
	private String cid;

	/**
	 * 加入的俱乐部名称
	 */
	@Column(length = 12)
	private String clubName;

	/**
	 * 微信号
	 */
	@Column(length = 30)
	private String wx;
	/**
	 * 身份证号
	 */
	@Column(length = 30)
	private String idCard;
	/**
	 * 手机号码
	 */
	@Column(length = 20)
	private String phone;

	/**
	 * 城市地区
	 */
	@Column(length = 50)
	private String location;

	/**
	 * 协议信息
	 */
	private String protocol;

	/**
	 * 邀请码
	 */
	@Column(length = 6)
	private String inviteCode;

	/**
	 *  邀请时间，格式YYYY-MM-dd HH:mm:ss
	 */
	@Column(length = 20)
	private String inviteTime;
	
	// 邀请人，谁邀请的该用户
	@Column(length = 50)
	private String inviteUser;

	private String openId;//微信中的openid
	
	
	
}
