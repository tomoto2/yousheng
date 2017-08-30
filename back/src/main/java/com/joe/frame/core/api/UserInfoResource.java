package com.joe.frame.core.api;

import javax.annotation.security.PermitAll;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;

import com.joe.frame.core.param.UserInfoParam;
import com.joe.frame.core.service.UserInfoService;
import com.joe.frame.web.boot.Application;
import com.joe.frame.web.dto.BaseDTO;
import com.joe.frame.web.dto.NormalDTO;

@Path("userInfo")
@PermitAll
public class UserInfoResource extends BaseResource {
	private static final Logger logger = LoggerFactory.getLogger(UserInfoResource.class);
	@Autowired
	private UserInfoService userInfoService;
	@Context
	HttpServletRequest request;
	
	public static void main(String[] args) {
		SpringApplication.run(Application.class);
	}
	

	/**
	 * 代理
	 * 获取个人信息
	 * 
	 * @return
	 */
	@GET
	@Path("getUserInfo")
	@Produces(MediaType.APPLICATION_JSON)
	public NormalDTO<Object> getUserInfo(){
		NormalDTO<Object> dto=new NormalDTO<Object>();
		try {
			UserInfoParam pm = userInfoService.getUserInfo(getUid(request));
			dto.setData(pm);
		} catch (Exception e) {
			dto.setStatus("401");
			dto.setErrorMessage("个人信息查询异常，稍后再试");
			logger.error("getagentInfo(获取个人信息异常)",e);
		}
		return dto;
	}

	
	/**
	 * 代理
	 * 修改个人信息
	 */
	@POST
	@Path("uptUserInfo")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public BaseDTO<Object> uptUserInfo(UserInfoParam param){
		BaseDTO<Object> dto = new BaseDTO<Object>();
		try {
			userInfoService.uptUserInfo(param,getUid(request));
		} catch (Exception e) {
			dto.setStatus("401");
			dto.setErrorMessage("修改信息的异常");
			logger.error("uptUserInfo 修改信息的异常",e);
		}
		return dto;
	}


//	
//	
//	/**
//	 * 获取所有下级（包括下级的下级)
//	 * 
//	 * @param request
//	 * @param now
//	 *            现在时间 格式：yyyyMM
//	 * @return
//	 */
//	@GET
//	@Path("getAll")
//	@Produces(MediaType.APPLICATION_JSON)
//	public NormalDTO<List<UserInfo>> getAll(@Context HttpServletRequest request) {
//		List<UserInfo> list = sysService.getAllChilds(getUid(request));
//		NormalDTO<List<UserInfo>> dto = new NormalDTO<List<UserInfo>>();
//		dto.setData(list);
//		return dto;
//	}

}
