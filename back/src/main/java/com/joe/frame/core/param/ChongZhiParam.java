package com.joe.frame.core.param;

import com.joe.frame.core.dto.PageDTO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
/**
 * 代理商给用户充值
 * @author lpx
 *
 * 2017年7月21日
 */
@Setter
@Getter
@ToString
public class ChongZhiParam extends PageDTO{

	/**
	 * 用户唯一编号，用于展示前台（6位数，以三开头）long
	 */
	private long uNumId;
	
	/**
	 * 订单编号
	 */
	private String orderNum;
	/**
	 * 充卡数量
	 */
	private int cartSum;

	/**
	 *剩余房卡数量 
	 */
	private int card;
	
	/**
	 * 充值用户ID， 玩家编号6位数
	 */
	private long uid;
	
	/**
	 * 昵称，玩家昵称 
	 */
	private String nickName;

	/**
	 * 充值的代理商ID
	 */
	private String agentUid;

	/**
	 * 标注
	 * 代理商可以对玩家进行编辑标注
	 */
	private String tag;
	
	/**
	 * 充值日期
	 * 格式：yyyy-MM-dd HH：mm：ss
	 */
	private String datetime;
}
