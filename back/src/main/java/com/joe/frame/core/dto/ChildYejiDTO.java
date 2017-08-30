package com.joe.frame.core.dto;

import lombok.Data;

@Data
public class ChildYejiDTO {
	
	private long sum; //下级的 下级总数
	private long dayMoney;//本日业绩
	private long monthMoney;//本月业绩
	private long benYueFanli;//本月返利
	private long monthFanlibi;//返利阶梯
	private String nickName;//昵称
	private String id;//6位数的代理编号
	private int pageCount;//总页数
}
