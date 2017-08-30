package com.joe.frame.core.utils;

import org.springframework.stereotype.Component;

/**
 * 
 * @author root
 *
 */
@Component
public class MoneyUtils {

	/**
	 * 报单中心 的 3%（激活 得 奖励）
	 * @param money 钱数
	 * @return 奖励的钱数（3%）
	 */
	public double currencyCenter(int money){
		double db = money * 0.03;
		return db;
	}
	
	
	/**
	 * 判断角色的等级 的价钱
	 * @param role
	 * @return
	 */
	public int role(int role){
		if(role == 1){
			return 298;
		}else if(role == 2){
			return 1980;
		}else if(role == 3){
			return 9980;
		}
		return 0;
	}
	
}
