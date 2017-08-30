package com.joe.frame.core.param;


import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
/**
 * 分页查询用户列表信息参数
 * @author lpx
 */
@Data
public class PageParam {

	/**
	 * 页面显示数量
	 */
	@JsonProperty(value = "size")
	private int size;
	
	/**
	 * 显示第几页
	 */
	@JsonProperty(value = "pageNo")
	private int pageNo;

}
