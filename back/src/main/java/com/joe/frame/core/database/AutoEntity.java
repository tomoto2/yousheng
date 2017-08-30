package com.joe.frame.core.database;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import com.joe.utils.StringUtils;
import com.joe.utils.Tools;

import lombok.Getter;
import lombok.Setter;

/**
 * 可以自动生成主键的实体
 * @author joe
 *
 */
@Getter
@Setter
@MappedSuperclass
public abstract class AutoEntity extends BaseEntity<String>{
	@Id
	@Column(length=50)
	private String id;
	
	@Override
	public void persistData() {
		super.persistData();
		if(StringUtils.isEmpty(id)){
			this.id = Tools.createUUID();
		}
	}
}
