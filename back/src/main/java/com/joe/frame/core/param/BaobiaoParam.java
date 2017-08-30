package com.joe.frame.core.param;

import lombok.Data;

@Data
public class BaobiaoParam {
	/**
	 * 报表 开始时间
	 * 格式：yyyy-MM-dd HH:mm:ss
	 */
	private String beginTime;
	/**
	 * 搜索结束时间
	 * yyyy-MM-dd HH:mm:ss
	 */
	private String endTime;
}
