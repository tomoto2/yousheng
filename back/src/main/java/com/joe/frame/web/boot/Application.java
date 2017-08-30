package com.joe.frame.web.boot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.cache.ehcache.EhCacheFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.alibaba.druid.pool.DruidDataSource;
import com.joe.frame.pay.prop.AliProp;
import com.joe.frame.pay.prop.WechatApiProp;
import com.joe.frame.pay.prop.WechatProp;
import com.joe.frame.web.prop.DruidProp;
import com.joe.frame.web.prop.SecureProp;
import com.joe.frame.web.prop.SystemProp;

import net.sf.ehcache.CacheManager;

@SpringBootApplication(scanBasePackages = "com")
@EntityScan("com") 
@EnableScheduling
@EnableTransactionManagement
@EnableConfigurationProperties({ SystemProp.class, SecureProp.class, DruidProp.class,AliProp.class, WechatProp.class ,WechatApiProp.class })
public class Application extends SpringBootServletInitializer 
implements EmbeddedServletContainerCustomizer{
	private static final Logger logger = LoggerFactory.getLogger(Application.class);
	@Autowired
	private DruidProp druidProp;

	public static void main(String[] ar) {
		SpringApplication.run(Application.class);
	}

	/*@Bean
	public ServletRegistrationBean indexServletRegistration() {
		ServletRegistrationBean registration = new ServletRegistrationBean(new PayServlet());
		registration.addUrlMappings("/pays");
		return registration;
	}*/

	@Bean
	public EhCacheFactoryBean ehCacheFactoryBean() {
		EhCacheFactoryBean cacheFactoryBean = new EhCacheFactoryBean();
		cacheFactoryBean.setCacheManager(CacheManager.create());
		return cacheFactoryBean;
	}

	@Bean(destroyMethod = "close", initMethod = "init")
	public DruidDataSource dataSource() {
		logger.info("初始化数据源..................................");
		DruidDataSource dataSource = new DruidDataSource();

		logger.debug("数据源URL：{}", druidProp.getUrl());
		dataSource.setUrl(druidProp.getUrl());
		logger.debug("数据源用户名：{}", druidProp.getUsername());
		dataSource.setUsername(druidProp.getUsername());
		dataSource.setPassword(druidProp.getPassword());
		logger.debug("数据源最大活动连接数：{}", druidProp.getMaxActive());
		dataSource.setMaxActive(druidProp.getMaxActive());
		logger.debug("数据源最小空闲连接数：{}", druidProp.getMinIdle());
		dataSource.setMinIdle(druidProp.getMinIdle());
		logger.debug("数据源初始大小：{}", druidProp.getInitialSize());
		dataSource.setInitialSize(druidProp.getInitialSize());
		logger.debug("数据源验证SQL语句：{}", druidProp.getValidationQuery());
		dataSource.setValidationQuery(druidProp.getValidationQuery());
		logger.debug("数据源是否在获取连接时测试：{}", druidProp.isTestOnBorrow());
		dataSource.setTestOnBorrow(druidProp.isTestOnBorrow());
		logger.debug("数据源是否在返回连接时测试：{}", druidProp.isTestOnReturn());
		dataSource.setTestOnReturn(druidProp.isTestOnReturn());
		logger.debug("数据源连接是否在空闲时测试：{}", druidProp.isTestWhileIdle());
		dataSource.setTestWhileIdle(druidProp.isTestWhileIdle());
		logger.debug("数据源最大等待时间：{}", druidProp.getMaxWait());
		dataSource.setMaxWait(druidProp.getMaxWait());
		logger.debug("监控记录每{}毫秒打印到日志一次", druidProp.getTimeBetweenLogStatsMillis());
		dataSource.setTimeBetweenLogStatsMillis(druidProp.getTimeBetweenLogStatsMillis());
		dataSource.setPoolPreparedStatements(druidProp.isPoolPreparedStatements());
		try {
			dataSource.addFilters(druidProp.getFilters());
			logger.info("数据源filter：{}" , druidProp.getFilters());
		} catch (Exception e) {
			logger.error("数据源filter异常啦.........................." + e);
		}
		logger.info("数据源初始化完成..................................");
		return dataSource;
	}

	// 1 . 重新设置端口    实现接口   EmbeddedServletContainerCustomizer 
	@Override
	public void customize(ConfigurableEmbeddedServletContainer container) {
		//container.setPort(8088);//设置tomcat端口
	}

	//2 . 重新设置端口   	
	//	@Bean
	//	public EmbeddedServletContainer servletContainer(){
	//		TomcatEmbeddedServletContainerFactory factory = new TomcatEmbeddedServletContainerFactory();
	//		factory.setPort(8088);
	//		factory.setSessionTimeout(10,TimeUnit.MINUTES);
	//		factory.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND,"no_fount.html"));
	//		return (EmbeddedServletContainer) factory;
	////		AbstractConfigurableEmbeddedServletContainer
	//	}
}
