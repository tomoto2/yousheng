package com.joe.frame.core.param;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SearchParam extends PageParam{

	/**
	 * 搜索日期
	 */
	private String searchTime;
	
	//直属下级手机号
	private String phone;
	
}
