package com.joe.frame.web.prop;

import java.io.InputStream;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StatusCode {
	private static final Map<String , String> msg = new TreeMap<String , String>();
	private static final Logger logger = LoggerFactory.getLogger(StatusCode.class);
	/**
	 * 加载配置
	 */
	static{
		logger.info("开始加载配置......................");
		InputStream in = null;
		try {
			in = StatusCode.class.getClassLoader().getResourceAsStream("messages.properties");
			int len = 0;
			byte[] buffer = new byte[1024];
			StringBuilder sb = new StringBuilder();
			while((len = in.read(buffer, 0, buffer.length)) != -1){
				sb.append(new String(buffer , 0 , len , "UTF8"));
			}
			String[] param = sb.toString().split("\n");
			for(int i = 0 ; i < param.length ; i++){
				String[] entity = param[i].replaceAll("\\s", "").split("=");
				msg.put(entity[0], entity[1]);
				logger.debug("配置信息：" + entity[0] + ":" + entity[1]);
			}
			logger.info("配置加载结束.......................");
		} catch (Exception e) {
			msg.put("0", "success");
			msg.put("999", "error,未知原因");
			logger.warn("配置加载异常，采用默认配置" , e);
		}finally{
			if(in != null){
				try {
					in.close();
				} catch (Exception e2) {
					logger.error("读取配置输入流关闭异常" , e2);
				}
			}
		}
	}
	public static String getMessage(String code){
		String message = msg.get(code);
		if(message == null){
			message = error();
		}
		return message;
	}
	
	public static String error(){
		return msg.get("999");
	}
	
	public static String success(){
		return msg.get("0");
	}
}
