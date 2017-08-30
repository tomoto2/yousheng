package com.joe.frame.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VerificationCode {
//		String verifyCode = VerificationCode.getVerificationCode(phone);
	
	private static final Logger logger = LoggerFactory.getLogger(VerificationCode.class);
	/**
	 * 	生成验证码
	 * @return 验证码
	 */
	public static String generateVerificationCode(){
		Random rad=new Random();  

		int nextInt = rad.nextInt(1000000);
		String verificationCode = String.valueOf(nextInt);

		if(verificationCode.length()!=6){  
			return generateVerificationCode();  
		}  
		return verificationCode;
	}

	/**
	 * 生成验证码，并发送到手机
	 * @param phone 手机号
	 * @return 验证码
	 */
	public static String getVerificationCode(String phone){
		//验证码
		String verificationCode=generateVerificationCode();
		try {
			// 用户名
			String name="13837698143"; 
			// 密码
			String pwd="9FA6EB4D075A98F84F896CF0D451"; 
			// 电话号码字符串，中间用英文逗号间隔
			StringBuffer mobileString=new StringBuffer(phone);
			// 内容字符串(验证码)
			StringBuffer contextString=new StringBuffer("你正在操作的验证码："+verificationCode+"，验证码15分钟内有效，请勿泄露");
			// 签名
			String sign="闪汇商城";
			// 追加发送时间，可为空，为空为及时发送
			String stime="";
			// 扩展码，必须为数字 可为空
			StringBuffer extno=new StringBuffer();
			//doPost(name, pwd, mobileString, contextString, sign, stime, extno);
			//logger.info(name+"--"+pwd+"--"+mobileString+"--"+contextString+"--"+sign+"--"+stime+"--"+extno);
			System.out.println(name+"--"+pwd+"--"+mobileString+"--"+contextString+"--"+sign+"--"+stime+"--"+extno);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return verificationCode;
	}


	/**
	 * 发送短信
	 * 
	 * @param name			用户名
	 * @param pwd			密码
	 * @param mobileString	电话号码字符串，中间用英文逗号间隔
	 * @param contextString	内容字符串
	 * @param sign			签名
	 * @param stime			追加发送时间，可为空，为空为及时发送
	 * @param extno			扩展码，必须为数字 可为空
	 * @return				
	 * @throws Exception
	 */
	public static String doPost(String name, String pwd, 
			StringBuffer mobileString, StringBuffer contextString,
			String sign, String stime, StringBuffer extno) throws Exception {
		StringBuffer param = new StringBuffer();
		param.append("name="+name);
		param.append("&pwd="+pwd);
		param.append("&mobile=").append(mobileString);
		param.append("&sign=").append(URLEncoder.encode(sign,"UTF-8"));
		param.append("&content=").append(URLEncoder.encode(contextString.toString(),"UTF-8"));
		param.append("&stime="+stime);
		param.append("&type=pt");
		param.append("&extno=").append(extno);

		URL localURL = new URL("http://web.cr6868.com/asmx/smsservice.aspx?");
		URLConnection connection = localURL.openConnection();
		HttpURLConnection httpURLConnection = (HttpURLConnection)connection;

		httpURLConnection.setDoOutput(true);
		httpURLConnection.setRequestMethod("POST");
		httpURLConnection.setRequestProperty("Accept-Charset", "utf-8");
		httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		httpURLConnection.setRequestProperty("Content-Length", String.valueOf(param.length()));

		OutputStream outputStream = null;
		OutputStreamWriter outputStreamWriter = null;
		InputStream inputStream = null;
		InputStreamReader inputStreamReader = null;
		BufferedReader reader = null;
		String resultBuffer = "";

		try {
			outputStream = httpURLConnection.getOutputStream();
			outputStreamWriter = new OutputStreamWriter(outputStream);

			outputStreamWriter.write(param.toString());
			outputStreamWriter.flush();

			if (httpURLConnection.getResponseCode() >= 300) {
				throw new Exception("HTTP Request is not success, Response code is " + httpURLConnection.getResponseCode());
			}

			inputStream = httpURLConnection.getInputStream();
			resultBuffer = convertStreamToString(inputStream);

		} finally {
			if (outputStreamWriter != null) outputStreamWriter.close();

			if (outputStream != null) outputStream.close();

			if (reader != null) reader.close();

			if (inputStreamReader != null) inputStreamReader.close();

			if (inputStream != null) inputStream.close();
		}
		return resultBuffer;
	}


	/**
	 * 转换返回值类型为UTF-8格式.
	 * @param is
	 * @return
	 */
	public static String convertStreamToString(InputStream is) {    
		StringBuilder sb1 = new StringBuilder();    
		byte[] bytes = new byte[4096];  
		int size = 0;  

		try {    
			while ((size = is.read(bytes)) > 0) {  
				String str = new String(bytes, 0, size, "UTF-8");  
				sb1.append(str);  
			}  
		} catch (IOException e) {    
			e.printStackTrace();    
		} finally {    
			try {    
				is.close();    
			} catch (IOException e) {    
				e.printStackTrace();    
			}    
		}    
		return sb1.toString();    
	}

	
	public static void main(String ags[]){
		String ss = getVerificationCode("15993382551");
		System.out.println(ss);
	}
}
