package com.joe.frame.core.param;

import lombok.Data;

/**
 * 修改密码
 * @author lpx
 *
 * 2017年7月24日
 */
@Data
public class PasswordParam {

	private String newPass;
	
	private String oldPass;
	
	private String phone;
	//验证码
	private String code;
	
}
