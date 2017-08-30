package com.joe.frame.core.param;

import lombok.Data;

@Data
public class UserInfoParam {
	/**
	 * 代理ID
	 */
	private String pid;
	
	/**
	 * 玩家ID
	 */
//	private String uid;
	private long uid;
	
	/**
	 * 上级代理的32id
	 */
	private String father;
	
	/**
	 * 代理编号—6位数
	 */
	private String pnumId;
	
	/**
	 * 玩家编号---6位数
	 */
	private long unumId;
	
	
	/**
	 *  昵称
	 */
	private String nikeName;
	/**
	 *  真实姓名
	 */
	private String name;
	
	/**
	 *  头像链接
	 */
	private String head;
	
	/**
	 * 微信号（查询个人信息使用）
	 */
	private String wx;
	
	/**
	 *  房卡数量
	 */
	private long card;
	/**
	 *  房卡ID
	 */
	private String cardId;
	/**
	 *  邀请人，谁邀请的该用户
	 */
	private String inviteUser;
	/**
	 * 加入的俱乐部名称
	 */
	private String clubName;
	/**
	 *  邀请时间，格式YYYY-MM-dd HH:mm:ss
	 *  注册日期：玩家首次登录游戏的时间
	 */
	private String inviteTime;
	private String createTime;
	/**
	 *  所属俱乐部id（所属的哪个代理），没有俱乐部时该值为空
	 */
	private String cid;

	/**
	 * 性别
	 * 男1 
	 * 女2
	 * 保密3
	 */
	private String sex;

	/**
	 * 微信号
	 */
	private String weChat;
	/**
	 * 账户余额
	 */
	private long balance;
	
	/**
	 * 微信昵称
	 */
	private String WeChatName;
	
	/**
	 * 代理等级
	 * 普通代理 1
	 * 市级代理 2
	 * 省级代理 3
	 */
	private String agentLevel;
	
	/**
	 * 玩家编号
	 * 注册玩家ID为6位数，首位数字从3开始31***
	 */
	private String number;
	
	/**
	 * 身份证号
	 */
	private String idCard;
	/**
	 * 手机号码
	 */
	private String phone;

	/**
	 * 城市地区
	 */
	private String location;
	
	/**
	 * 协议信息
	 */
	private String protocol;

	/**
	 * 邀请码(代理编号)
	 */
	private String inviteCode;

	//用户的密码
	private String password;
	
	//支付宝账号
	private String alipay;
	
	//手机验证码
	private String verifyCode;
	
	//注册方式
	private int type;//注册方式，1：自己注册申请（通过邀请码进行注册）。2：代理替代注册（代理内部注册）
	
	private String childPhone;//下级手机号
	
	private long allchongzhi;//总充值金额
	
	private long allgoumai;//总购卡量
	
	private String openId;//代理的openid
	
	private String parentPhone;//代理商上级手机号
	
	private int level;//关系等级
	
	
	/**
	 * 账号可用性
	 * false 不可用
	 * ture 可用
	 */
	private String canUse;
	
	// 如果为true说明该数据已经被删除，默认没有删除
	private boolean remove;

	/**
	 * 是否是特殊的代理账号：默认为false（不是），true是特殊账号
	 */
	private String isSpecial;
	
//	//推荐人
//	@JsonProperty(value = "parent")
//	private String parent;
//
//	//用户的下级
//	@JsonProperty(value = "childs")
//	private String childs;
}
