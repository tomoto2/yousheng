package com.joe.frame.web.dynamic;

import com.joe.frame.core.admin.AdminResource;
import com.joe.frame.core.param.UserInfoParam;
import com.joe.frame.web.dto.NormalDTO;

public class Test {
	public static void main(String[] args) {
		ResourceFactory factory = ResourceFactory.build("http://127.0.0.1/ws");
		AdminResource resource = factory.build(AdminResource.class);
		UserInfoParam userInfoParam = new UserInfoParam();
		userInfoParam.setPhone("15993382551");
		userInfoParam.setPassword("123");
		NormalDTO<Object> dto = resource.login("15993382551", "123");
		System.out.println(dto);
	}

}
