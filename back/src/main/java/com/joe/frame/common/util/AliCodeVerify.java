package com.joe.frame.common.util;



import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;

/**
 * 阿里短信验证码
 * @author lpx
 *
 * 2017年7月26日
 */
@Component
public class AliCodeVerify {
	private final static Logger logger = LoggerFactory.getLogger(AliCodeVerify.class);
	/**
	 * 发送短信
	 * @param phone 要发送的手机
	 * @param code 接收的验证码
	 * @throws ServerException
	 * @throws ClientException
	 * @return 验证码
	 */
	public String sendMsg(String phone) throws ServerException, ClientException{
		String code = generateVerifyCode();//生成验证码
		System.out.println("验证码"+code);
		logger.info("短信随机验证码是{},接收手机号是{}",code,phone);
		//设置超时时间-可自行调整
		System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
		System.setProperty("sun.net.client.defaultReadTimeout", "10000");
		//初始化ascClient需要的几个参数
		final String product = "Dysmsapi";//短信API产品名称（短信产品名固定，无需修改）
		final String domain = "dysmsapi.aliyuncs.com";//短信API产品域名（接口地址固定，无需修改）
		//替换成你的AK
		final String accessKeyId = "LTAIbV9ak0Lh7oKQ";//你的accessKeyId,参考本文档步骤2
		final String accessKeySecret = "i9LkE4D5W8Z6GQJXREBsW52kBzMnzv";//你的accessKeySecret，参考本文档步骤2
		//初始化ascClient,暂时不支持多region
		IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId,
				accessKeySecret);
		DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
		IAcsClient acsClient = new DefaultAcsClient(profile);
		//组装请求对象
		SendSmsRequest request = new SendSmsRequest();
		//必填:待发送手机号。支持以逗号分隔的形式进行批量调用，批量上限为1000个手机号码,批量调用相对于单条调用及时性稍有延迟,验证码类型的短信推荐使用单条调用的方式
		request.setPhoneNumbers(phone);
		//必填:短信签名-可在短信控制台中找到
		request.setSignName("名门互娱");
		//必填:短信模板-可在短信控制台中找到
		request.setTemplateCode("SMS_79020006");
		//可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
		request.setTemplateParam("{\"code\":"+code+"}");
//		request.setTemplateParam("{\"name\":\"Tom\", \"code\":"+code+"}");
		//可选-上行短信扩展码(无特殊需求用户请忽略此字段)
		//request.setSmsUpExtendCode("90997");
		//可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
		//		 request.setOutId("yourOutId");
		//请求失败这里会抛ClientException异常
		logger.info("开始发送短信");
		SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
		if(sendSmsResponse.getCode() != null && sendSmsResponse.getCode().equals("OK")) {
			logger.info("短信验证码发送成功");
			return code;//请求成功
		}else{
			logger.info("短信验证码发送失败,返回的错误码是{}",sendSmsResponse.getCode());
			return "";
		}
	}

	/**
	 * 	生成验证码
	 * @return 验证码
	 */
	public String generateVerifyCode(){
		Random rad=new Random();  
		int nextInt = rad.nextInt(1000000);
		String verificationCode = String.valueOf(nextInt);
		if(verificationCode.length()!=6){  
			return generateVerifyCode();  
		}  
		return verificationCode;
	}

}
