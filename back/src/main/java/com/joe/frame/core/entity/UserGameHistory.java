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
 * 用户对局历史ID，冗余表，用于快速查找用户的对局历史
 * @author joe
 *
 */
@Setter
@Getter
@ToString
@Table
@Entity
public class UserGameHistory extends BaseEntity<String>{
	//用户对局历史ID
	@Id
	@Column(length = 50)
	private String ughid;
	//对局时间，格式YYYY-MM-dd HH:mm:ss
	@Column(length = 20)
	private String time;
	//用户UID
	@Column(length = 50)
	private String uid;
	//对局历史ID
	@Column(length = 50)
	private String ghid;
}
