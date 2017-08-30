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
 * 
 * 代理商给玩家的充值记录
 * 	
 * @author lpx
 */
@Setter
@Getter
@ToString
@Table
@Entity
public class PlayerRecharge extends BaseEntity<String>{
	
	/**
	 * 记录ID
	 */
	@Id
	@Column(length = 32)
	private String id;
	
	/**
	 * 充值用户编号
	 */
	@Column(length = 6)
	private long uNumId;
	
	/**
	 * 充值的代理商ID
	 */
	@Column(length = 32)
	private String agentUid;
	
	/**
	 * （充卡数量）
	 *  单位 : 张
	 */
	private long cartSum = 0;
	
	/**
	 * 标注
	 * 代理商可以对玩家进行编辑标注
	 */
	private String tag;
	
	/**
	 * 充值日期
	 * 格式：yyyyMMddHHmmss
	 */
	@Column(length = 20)
	private String datetime;

	
}
