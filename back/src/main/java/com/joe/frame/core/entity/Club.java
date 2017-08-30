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
 * 
 * 俱乐部
 * 
 * @author lpx
 *
 * 2017年7月20日
 */

@Setter
@Getter
@ToString
@Table
@Entity
public class Club extends BaseEntity<String> {
	//ID
	@Id
	@Column(length = 50)
	private String cid;
	/**
	 * 俱乐部唯一编号，用于展示前台（6位数，以三开头）
	 */
//	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(length = 6,unique = true)
	private String numId;
	// 创建者名称
	@Column(length = 50)
	private String owner;
	//创建人ID
	@Column(length = 50)
	private String ownerId;
	//俱乐部名称
	@Column(length = 50)
	private String name;
	// 俱乐部最大成员数
	private int max = 500;
	// 俱乐部当前成员数
	private int now = 0;
	// 俱乐部当前公告ID
	@Column(length = 50)
	private String cnid;
	
	/**
	 * 剩余房卡剩余数量
	 * 俱乐部房卡剩余量，也就是代理商房卡剩余量
	 */
	private int cRoomSum;
	/**
	 * 成员数量
	 */
	private int MemberSum;
}
