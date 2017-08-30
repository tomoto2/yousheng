package com.joe.frame.core.param;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ChildYejiParam extends PageParam{

	/**
	 * 排序
	 * 1 今日业绩， 默认
	 * 2 本月业绩，
	 * 3 旗下代理数，
	 * 4 返利明细 
	 * 
	 * 进行排序，  desc高到低，
	 */
	private String flag;
	
	
	
}
