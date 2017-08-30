package com.joe.frame.web.manager;


import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.joe.monitoring.ClassLoadInfo;
import com.joe.monitoring.JVMMemoryInfo;
import com.joe.monitoring.Manager;
import com.joe.monitoring.MemoryInfo;
import com.joe.parse.json.JsonParser;

/**
 * 监控系统
 * 
 * @author qiao9
 *
 */
@Component
public class Task {
	private static final Logger logger = LoggerFactory.getLogger("JVMStatus");
	
	private JsonParser jsonParser = JsonParser.getInstance();

	/**
	 * 监控功能虚拟机状态，每分钟查看一次JVM虚拟机状态
	 */
	@Scheduled(fixedRate = 60 * 1000)
	public void monitor() {
		ClassLoadInfo classLoadInfo = Manager.classLoadManager();
		JVMMemoryInfo jVMMemoryInfo = Manager.getJVMMemoryInfo();
		List<MemoryInfo> memoryInfo = Manager.getMemoryInfo();

		String classLoadInfoStr = jsonParser.toJson(classLoadInfo);
		String jVMMemoryInfoStr = jsonParser.toJson(jVMMemoryInfo);
		String memoryInfoStr = jsonParser.toJson(memoryInfo);

		logger.info(classLoadInfoStr);
		logger.info(jVMMemoryInfoStr);
		logger.info(memoryInfoStr);
		logger.info("--------------------------------------------------------------------------------");
	}
}
