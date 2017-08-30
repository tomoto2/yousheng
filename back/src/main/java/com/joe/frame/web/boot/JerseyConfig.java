package com.joe.frame.web.boot;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.joe.frame.web.filter.OauthFilter;
import com.joe.frame.web.prop.SystemProp;

@Component
@ApplicationPath("ws")
public class JerseyConfig extends ResourceConfig {
	public JerseyConfig(@Autowired SystemProp prop) {
		// API扫描
		packages("com.joe.frame");
		// 注册文件上传
		register(MultiPartFeature.class);
		// 是否显示WADL文件
		property(ServerProperties.WADL_FEATURE_DISABLE, !prop.isAllowApplicationWadl());
		if (prop.isRolesAllowedDynamicFeature()) {
			// 允许权限控制
			register(RolesAllowedDynamicFeature.class);
			// 注册权限控制filter
			register(OauthFilter.class);
		}

		// 在响应信息中发送验证失败原因
		property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true);
	}
}