package com.joe.frame.core.dto;

import lombok.Data;

@Data
public class UserDTO {

	/**
	 * 玩家id
	 */
	private long uid;
	/**
	 * 玩家编号
	 */
	private String number;
	/**
	 * 玩家昵称
	 */
	private String nikeName;
	
	/**
	 * 剩余房卡数量
	 */
	private long card;
	
	/**
	 * 是否是俱乐部成员
	 * 1 是
	 * 2否
	 */
	private String sign;
}
