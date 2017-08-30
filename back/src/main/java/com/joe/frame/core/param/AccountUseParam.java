package com.joe.frame.core.param;

import lombok.Data;

@Data
public class AccountUseParam {
	private String agentId; //要修改的代理id
	private String canuse; //要设置的状态
	
	private long uid;//要修改的玩家id
	
	
}
