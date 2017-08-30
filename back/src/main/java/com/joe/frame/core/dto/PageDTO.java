package com.joe.frame.core.dto;

import lombok.Data;

@Data
public class PageDTO {

	/**
	 * 总页数
	 */
	private String allPages;
	
	/**
	 * 当前页
	 */
	private String currentPage;
	
}
