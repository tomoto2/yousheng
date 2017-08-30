package com.joe.frame.pay.wechatpay.resource;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.joe.frame.core.api.BaseResource;
import com.joe.frame.core.entity.Proxy;
import com.joe.frame.core.entity.User;
import com.joe.frame.core.service.AgentService;
import com.joe.frame.core.service.UserInfoService;
import com.joe.frame.pay.prop.WechatProp;
import com.joe.frame.web.dto.NormalDTO;

import net.sf.json.JSONObject;
@Path("wechat")
public class WechatgetUser extends BaseResource{
	private static final Logger logger = LoggerFactory.getLogger(WechatgetUser.class);
	@Context
	HttpServletRequest request;
	@Context
	HttpServletResponse httpResponse;
	@Autowired
	private AgentService proxyService;
	@Autowired
	private WechatProp wechatPayProp;
	
	@Autowired
	private UserInfoService userInfoService;

	
	//获取 用户的Openid
	@POST
	@Path("getOpeniddb")
	@Produces(MediaType.APPLICATION_JSON)
	public NormalDTO<Object> getOpenidbyDB(){
		NormalDTO<Object> dto = new NormalDTO<Object>();
		Proxy user= proxyService.getProxy(getUid(request));
		//先判断  在数据库中有没有openid
		if(!("".equals(user.getOpenId())) || !(user.getOpenId() == null)){
			String openId = user.getOpenId();
			dto.setData(openId);
		}
		return dto;
	}


	//获取 用户的Openid
	@POST
	@Path("getOpenid")
	@Produces(MediaType.APPLICATION_JSON)
	public NormalDTO<Object> getOpenid() throws UnsupportedEncodingException{
		NormalDTO<Object> dto = new NormalDTO<Object>();
		//			Account a = getAccount(request);
		Proxy user= proxyService.getProxy(getUid(request));
		//先判断  在数据库中有没有openid
		if("".equals(user.getOpenId()) || user.getOpenId() == null){
			//没有 就获取 唯一标示--openid
			//String APPID = WXConfig.APP_ID;
			//授权后重定向的回调链接地址，请使用urlencode对链接进行处理
			//http://mingmen.youshengtec.net/ws/wechat/sendGetOpenid
			//String url = "http%3A%2F%2Fwww.sanhui.shop%2Fmarketplace%2Fws%2FWXPay%2FsendGetOpenid";
			String url = java.net.URLEncoder.encode("http://mingmen.mingmenhuyu.com/back/ws/wechat/sendGetOpenid/", "utf8");
			String SNSAPI_BASE = "snsapi_base";  //只可以获取到 opneid
			//重定向后会带上state参数
			String STATE = user.getPid();
			String URL = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="+wechatPayProp.getAppid()+"&redirect_uri="+url+"&response_type=code&scope="+SNSAPI_BASE+"&state="+STATE+"#wechat_redirect";
			dto.setStatus("0");
			dto.setData(URL);
		}else{
			String openId = user.getOpenId();
			System.out.println("openId=="+openId);
			dto.setStatus("1");
			dto.setData(openId);
		}
		return dto;
	}

	
	//获取玩家 用户的Openid
	@GET
	@Path("getOpenidUser")
	@Produces(MediaType.APPLICATION_JSON)
	public NormalDTO<Object> getOpeniduser(@QueryParam("uid") long uid) throws UnsupportedEncodingException{
		NormalDTO<Object> dto = new NormalDTO<Object>();
		User user = userInfoService.getPlayer(uid);
		//先判断  在数据库中有没有openid
		if("".equals(user.getOpenId()) || user.getOpenId() == null){
			//没有 就获取 唯一标示--openid
			//String APPID = WXConfig.APP_ID;
			//授权后重定向的回调链接地址，请使用urlencode对链接进行处理
			String url = java.net.URLEncoder.encode("http://mingmen.mingmenhuyu.com/back/ws/wechat/sendGetOpenidUser/", "utf8");
			String SNSAPI_BASE = "snsapi_base";  //只可以获取到 opneid
			//重定向后会带上state参数
			String STATE = String.valueOf(user.getUid());
			String URL = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="+wechatPayProp.getAppid()+"&redirect_uri="+url+"&response_type=code&scope="+SNSAPI_BASE+"&state="+STATE+"#wechat_redirect";
			dto.setStatus("0");
			dto.setData(URL);
		}else{
			String openId = user.getOpenId();
			System.out.println("openId=="+openId);
			dto.setStatus("1");
			dto.setData(openId);
		}
		return dto;
	}
	
	@GET
	@Path("sendGetOpenidUser")
	public void sendGetOpenidUser(String params){
		String CODE = request.getParameter("code");
		String state = request.getParameter("state");//pid
		logger.info("STATE{}",state);
		System.out.println("params == "+params);
		System.out.println("CODE:"+CODE+"   |  STATE:"+state);
		logger.info("CODE:"+CODE+"   |  STATE:"+state);
		String URL ="https://api.weixin.qq.com/sns/oauth2/access_token?appid="+wechatPayProp.getAppid()+"&secret="+wechatPayProp.getSecret()+"&code="+CODE+"&grant_type=authorization_code";
		JSONObject jsonObject =sendGet(URL);
		if(jsonObject != null){
			//将获得的 openid 存到数据库
			String openid = (String)jsonObject.get("openid");
			logger.info("openid  == "+openid);
			System.out.println("openid  == "+openid);
			if(openid != null )
				userInfoService.uptUserOpenid(Long.parseLong(state), openid);
		}
	}
	

//	public static void main(String[] args) throws UnsupportedEncodingException {
//		String urlStr = java.net.URLEncoder.encode("http://mingmen.mingmenhuyu.com/back/ws/wechat/sendGetOpenid/", "utf8");
//		System.out.println(urlStr);
//	}


	@GET
	@Path("sendGetOpenid")
	public void sendGetOpenid(String params){
		String CODE = request.getParameter("code");
		String STATE = request.getParameter("state");//pid
		logger.info("STATE{}",STATE);
		System.out.println("params == "+params);
		System.out.println("CODE:"+CODE+"   |  STATE:"+STATE);
		logger.info("CODE:"+CODE+"   |  STATE:"+STATE);
		String URL ="https://api.weixin.qq.com/sns/oauth2/access_token?appid="+wechatPayProp.getAppid()+"&secret="+wechatPayProp.getSecret()+"&code="+CODE+"&grant_type=authorization_code";
		JSONObject jsonObject =sendGet(URL);
		if(jsonObject != null){
			//将获得的 openid 存到数据库
			//				proxyService.find(STATE);
//			Account a = getAccount(request);
			String openid = (String)jsonObject.get("openid");
			logger.info("openid  == "+openid);
			System.out.println("openid  == "+openid);
			if(openid != null )
				//					proxyService.uptOpenid(a.getUid(), openid);
				proxyService.uptOpenid(STATE, openid);
		}
	}


	//向微信端 发送get请求
	public static JSONObject sendGet(String requestUrl){
		CloseableHttpClient httpclient = HttpClientBuilder.create().build();
		HttpGet httpGet = new HttpGet(requestUrl);
		CloseableHttpResponse response=null;
		try {
			response = httpclient.execute(httpGet);
		} catch (UnsupportedEncodingException e1) {
			logger.error("微信 get 请求报错"+e1);
			logger.info("微信 get 请求报错"+e1.getMessage());
			e1.printStackTrace();
		} catch (ClientProtocolException e) {
			logger.error("微信 get 请求报错"+e);
			logger.info("微信 get 请求报错"+e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			logger.error("微信 get 请求报错"+e);
			logger.info("微信 get 请求报错"+e.getMessage());
			e.printStackTrace();
		}
		/**请求发送成功，并得到响应**/  
		JSONObject jsonObject=null;
		if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
			HttpEntity httpEntity = response.getEntity();
			String result=null;
			try {
				result = EntityUtils.toString(httpEntity,"UTF-8");
			} catch (ParseException e) {
				logger.error("微信 get 解析 返回结果报错"+e);
				logger.info("微信 get 解析 返回结果报错"+e.getMessage());
			} catch (IOException e) {
				logger.error("微信 get 解析 返回结果报错"+HttpStatus.SC_OK);
				logger.info("微信 get 解析 返回结果报错"+HttpStatus.SC_OK);
			}// 返回json格式：
			jsonObject = JSONObject.fromObject(result);
		}
		/*String access_token = (String)jsonObject.get("access_token");*/
		return jsonObject;
	}

}
