package com.joe.frame.core.param;

import lombok.Data;

/**
 * 公告
 * 
 * @author lpx
 *
 * 2017年7月20日
 */

@Data
public class ClubNoticeParam{

	/**
	 * 公告id
	 */
	private String cnid;
	
	/**
	 * 所属俱乐部ID
	 */
	private String cid;

	/**
	 * 公告内容
	 */
	private String text;

	/**
	 * 发布时间
	 * 格式:yyyyMMddHHmmss
	 */
	private String dateTime;

}
