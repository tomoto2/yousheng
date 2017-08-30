package com.joe.frame.web.prop;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * 系统配置
 * 
 * @author dengjianjun
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





	/** 响应字符集 */
	private String responseCharset = "UTF8";
	/** 系统内发起http请求的数据字符编码 */
	private String requestCharset = "UTF8";
	/**
	 * 是否允许显示WADL文件
	 * <li>true：允许显示</li>
	 * <li>false：不允许显示</li>
	 */
	private boolean allowApplicationWadl = false;
	/**
	 * 是否开启权限控制
	 * <li>true：开启</li>
	 * <li>false：关闭</li>
	 */
	private boolean rolesAllowedDynamicFeature = false;
	/**
	 * 缓存实现，默认ehcache，可选redis，不区分大小写
	 */
	private String cacheService = "EHCACHE";
	/**
	 * 系统能够记录的接口请求信息最大历史记录数量
	 */
	private int maxHistory = 50;
	/**
	 * 接口平均时间计算数量，只对最近的count条记录计算平均时间
	 */
	private int count = 100;
	// /**
	// * 是否允许开启根据等级过滤请求
	// * <li>true：允许</li>
	// * <li>false：不允许</li>
	// */
	// private boolean allowFilterRequest = false;
	// /**
	// * 是否允许接口自动限流（当请求过多时自动抛弃低等级的接口请求）
	// * <li>true：允许</li>
	// * <li>false：不允许</li>
	// */
	// private boolean allowAPIAutoLimit = false;
	// /**
	// * 当接口处理平均时间达到该上限时自动关闭当前等级最低的接口的请求
	// */
	// private int upperLimit = 100;
	// /**
	// * 当接口处理平均时间达到该下限时自动打开当前等级最低的接口的更低一级的接口的请求
	// */
	// private int lowerLimit = 70;

}
