package com.joe.frame.core.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.joe.frame.core.database.AutoEntity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@Table
@Entity
public class AppVersion extends AutoEntity{

	/**
	 * 版本号
	 */
	@Column(length = 6)
	private String version;

}
