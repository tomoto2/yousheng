package com.joe.frame.core.database;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Transient;

import com.joe.utils.DateUtil;
import com.joe.utils.StringUtils;

import lombok.Data;

/**
 * 所有的entity接口
 * 
 * @author Administrator
 *
 * @param <ID>
 */
@Data
@MappedSuperclass
public abstract class BaseEntity<ID extends Serializable> {
	// 不保存该字段
	@Transient
	private static final String format = "yyyy-MM-dd HH:mm:ss SSS";
	// 修改时间
	@Column(length = 23)
	private String modifyTime;
	// 创建时间
	@Column(length = 23)
	private String createTime;
	@Column(length = 23)
	private String removeTime;
	// 如果为true说明该数据已经被删除，默认没有删除
	private boolean remove = false;

	@PreUpdate
	public void updateData() {
		this.modifyTime = DateUtil.getFormatDate(format);
		if (this.remove && StringUtils.isEmpty(removeTime)) {
			this.removeTime = DateUtil.getFormatDate(format);
		}
	}

	@PrePersist
	public void persistData() {
		this.modifyTime = DateUtil.getFormatDate(format);
		this.createTime = DateUtil.getFormatDate(format);
	}
}
