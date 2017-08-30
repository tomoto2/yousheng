package com.joe.frame.core.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;


/**
 * 代理用户信息
 * @author lpx
 *
 * 2017年7月20日
 */
@Setter
@Getter
//@ToString
@Table
@Entity
public class Proxy extends BaseUser {

	

	/**
	 * 代理
	 * 唯一编号，用于展示前台（6位数，以三开头）
	 */
	@Column(length = 6)
	private String numId;
	
	/**
	 * ID
	 */
	@Id
//	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(length = 32)
	private String pid;
	/**
	 * 支付宝 账号
	 */
	@Column(length = 30)
	private String alipay;
	

	/**
	 * 账户余额
	 * 单位 : 分
	 */
	private long balance = 0;

	/**
	 * 是否是特殊账号 true是，false不是
	 */
	private String isSpecial;

	/**
	 * 代理等级
	 * 普通代理 1
	 * 市级代理 2
	 * 省级代理 3
	 */
	@Column(length = 10)
	private String agentLevel;
	
	/**
	 * 关系等级
	 * 关系等级：一级代理为1；二级代理为2；三级代理为3
	 * 一级代理统统为123456的下级；
	 * 123456的关系等级设置为0
	 * 
	 */
	private int level;

	// 上级ID
	@Column(length = 32)
	private String father;
	
	// 房卡添加号(qgh)
//	private int cardId;

	/**
	 * 该用户的推荐人
	 */
	@JsonIgnore
	@ManyToOne(cascade = CascadeType.ALL)
	private Proxy parent;

	/**
	 * 用户下级
	 */
	
//	@ManyToMany(fetch = FetchType.LAZY)改为@ManyToMany(fetch = FetchType.EAGER)
	
	@OneToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
	@JoinColumn(name = "ucid")
	private Set<Proxy> childs = new HashSet<Proxy>();
}
