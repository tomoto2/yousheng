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
 * 公告
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
public class ClubNotice extends BaseEntity<String>{
	//ID
	@Id
	@Column(length = 50)
	private String cnid;

	// 所属俱乐部ID
	@Column(length = 50)
	private String cid;
	// 公告内容
	@Column(length = 500)
	private String text;

	/**
	 * 发布时间
	 * 格式:yyyy-MM-dd HH：mm：ss
	 */
	@Column(length = 30)
	private String dateTime;
}
