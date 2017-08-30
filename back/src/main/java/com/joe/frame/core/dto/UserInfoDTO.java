package com.joe.frame.core.dto;

import java.util.HashSet;
import java.util.Set;

import com.joe.frame.core.entity.Proxy;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class UserInfoDTO extends PageDTO{
	/**
	 * ID
	 */
	private String id;
	/**
	 * 用户编号
	 * 随机不重复。
	 */
	private String num;
	/**
	 * 用户身份
	 * 体验员
	 * 推广员
	 * 宣传员
	 * 渠道商
	 * 渠道主任
	 * 渠道经理
	 * 总监
	 * 董事
	 * 总头
	 */
	private String identity;
	
	/**
	 * 用户头像
	 */
	private String picture;
	
	/**
	 * 用户角色ID
	 */
	private String ueId;
	
	/**
	 * 名称
	 */
	private String name;
	
	/**
	 * 手机号
	 */
	private String phone;
	
	/**
	 * 年龄
	 */
	private String age;
	
	/**
	 * 性别
	 */
	private String sex;
	
	/**
	 * 身份证号
	 */
	private String idCard;
	
	/**
	 * 银行卡
	 */
	private String bankCard;
	
	/**
	 * 余额
	 *     单位：元
	 */
	private String balance;
	
	/**
	 * 积分
	 */
	private String integral;
	
	/**
	 * 地区
	 */
	private String region;
	
	/**
	 * 报单ID
	 *     为null时 非服务中心
	 */
	private String cid;
	
	/**
	 * 上级ID
	 */
	private String superiorId;
	
	/**
	 * 最顶级ID
	 */
	private String topLevelId;
	
	/**
	 * 添加时间(注册日期)
	 * 格式：yyyyMMdd
	 */
	private String datetime;
	
	/**
	 * 升级时间
	 * 格式：yyyyMMdd
	 */
	private String upgradeTime;
	
	/**
	 * 该用户的推荐人
	 */
	private Proxy parent;

	/**
	 * 用户下级
	 */
	private Set<Proxy> childs = new HashSet<Proxy>();

	
	/**
	 * 升级之前的等级
	 */
	private String preLevel;
}
