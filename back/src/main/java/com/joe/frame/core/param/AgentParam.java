package com.joe.frame.core.param;

import lombok.Data;

@Data
public class AgentParam {
	
	private long sums;//其直属下级的旗下代理总数
	
	private String cid;//所属的俱乐部id
	
	private String pid;//个人账号id
	
	private String nikeName;//昵称
	
	private long selfAllMoney;//代理个人购卡总金额
	
	private long ChildAllmoney;//代理下级购卡总金额
	
	private long selfAllsum;//代理个人购卡总数
	
	private long ChildAllsum;//代理下级购卡总数
	
	private String agentLevel;//代理等级
	
	private String canuse;//账号的可用性 yes 可用，no 不可用
	
	private String anumId;//代理编号
	
}
