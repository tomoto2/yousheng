package com.joe.frame.core.dto;

import lombok.Data;

@Data
public class ClubAdminDTO {
	//ID
	private String cid;
	// 创建者名称
	private String owner;
	//创建人ID
	private String ownerId;
	//俱乐部名称
	private String name;
	// 俱乐部最大成员数
	private int max = 500;
	// 俱乐部当前成员数
	private long now = 0;
	// 俱乐部当前公告ID
	private String cnid;
	
	/**
	 * 俱乐部唯一编号，用于展示前台（6位数，以三开头）
	 */
	private String numId;
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
