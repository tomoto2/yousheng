package com.joe.frame.pay.prop;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * 系统信息
 * @author joe
 *
 */
@Data
@ConfigurationProperties(prefix = "com.joe.system")
public class SystemProp {
	//服务器根目录，例如http://llapp.com.cn
	private String serverUrl;
	//服务器服务根目录，例如http://llapp.com.cn/ws
	private String serverServiceUrl;
	//环境，env说明是生产环境，box说明是测试环境
	private String env = "env";
}
