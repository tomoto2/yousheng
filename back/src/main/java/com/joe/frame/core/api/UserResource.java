package com.joe.frame.core.api;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.joe.frame.core.dto.UserDTO;
import com.joe.frame.core.dto.UserPayDTO;
import com.joe.frame.core.entity.User;
import com.joe.frame.core.param.PageParam;
import com.joe.frame.core.param.RemarkesParam;
import com.joe.frame.core.param.UserPayParam;
import com.joe.frame.core.service.UserInfoService;
import com.joe.frame.web.dto.BaseDTO;
import com.joe.frame.web.dto.NormalDTO;

@Path("user")
public class UserResource extends BaseResource{
	private static final Logger logger = LoggerFactory.getLogger(UserResource.class);
	//玩家操作

	@Autowired
	private UserInfoService userInfoService;
	@Context
	HttpServletRequest request;
	
	//玩家自己购买方法
	@POST
	@Path("userPay")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public NormalDTO<Object> userPay(UserPayParam userPayParam){
		NormalDTO<Object> dto = new NormalDTO<Object>();
		try {
			UserPayDTO  buyParam  = userInfoService.addUserOrder(userPayParam.getUid(),userPayParam.getNum());
			if(buyParam == null ){
				dto.setStatus("203");
				dto.setErrorMessage("该玩家不存在！");
				return dto;
			}
			dto.setData(buyParam);
		} catch (Exception e) {
			dto.setStatus("401");
			dto.setErrorMessage("异常，稍后再试");
			logger.error("玩家自己充值异常", e);
		}
		return dto;
	}
	

	/**
	 * 分页查询查询所有俱乐部成员
	 * 
	 * page从1开始
	 * @param cid
	 *            俱乐部id
	 * @return
	 */
	@POST
	@Path("getClumMember")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public NormalDTO<Object> getClumMember(PageParam pageParam){
		NormalDTO<Object> dto  = new NormalDTO<Object>();
		try {
			List<User> list = userInfoService.getClumMember(getUid(request),pageParam.getPageNo(),pageParam.getSize());
			dto.setData(list);
		} catch (Exception e) {
			dto.setStatus("401");
			dto.setErrorMessage("异常，稍后再试");
			logger.error("俱乐成员查询异常", e);
		}
		return dto;
	}

	/**
	 * 根据玩家的编号查询玩家信息
	 * 若此玩家是本俱乐部成员，则直接显示玩家信息，若不是，则直接显示充卡小框，并提示不是本俱乐部成员，
	 * 给玩家充值，若此玩家是本俱乐部成员可以充值，若未加入过任何俱乐部可以充值；若加入别的俱乐部了不可以充值；
	 * @param number 玩家编号（6位数，以3开头）
	 * @return
	 */
	@POST
	@Path("getUserMsgByMunber")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public NormalDTO<UserDTO> getUserMsgByMunber(long number){
		NormalDTO<UserDTO> dto = new NormalDTO<UserDTO>();
		UserDTO user = userInfoService.getUserMsgByNum(number);
		if(user.getSign()!= null && user.getSign().equals("3")){
			dto.setErrorMessage("无此用户！");
			dto.setStatus("204");
			return dto;
		}
		if(user.getSign()!= null && user.getSign().equals("4")){
			dto.setErrorMessage("该用户没有加入任何俱乐部");//可以充值
			dto.setData(user);
			return dto;
		}
		boolean flag = userInfoService.isClubMember(number,getUid(request));
		if(flag){
			user.setSign("1");//俱乐部存在，且是此部的成员，可以充值
		}else{
			user.setSign("2");//俱乐部不存在或者不是本俱乐部成员，不能充值
		}
		
		dto.setData(user);
		return dto;
	}




	/**
	 * 根据编号修改玩家在俱乐部的备注
	 * @param number 玩家编号
	 * @param remarks 备注
	 * @return
	 */
	@POST
	@Path("uptRemarks")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public BaseDTO<Object> uptRemarks(RemarkesParam remarkesParam){
		BaseDTO<Object> dto = new BaseDTO<Object>();
		try {
			userInfoService.uptRemarks(remarkesParam.getNumber(),remarkesParam.getRemarks());
		} catch (Exception e) {
			dto.setStatus("401");
			dto.setErrorMessage("异常，稍后再试");
			logger.error("修改玩家在俱乐部的备注异常", e);
		}
		return dto;
	}


	/**
	 * 根据编号将某玩家移出俱乐部
	 * @param number 玩家编号
	 */
	@POST
	@Path("removeClub")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public BaseDTO<Object> removeClub(long number){
//		number = number.indexOf("=") > 0 ? number.substring(number.indexOf("=") + 1, number.length()) : number;
		BaseDTO<Object> dto = new BaseDTO<Object>();
		try {
			userInfoService.removeClub(number);
		} catch (Exception e) {
			dto.setStatus("401");
			dto.setErrorMessage("异常，稍后再试");
			logger.error("{}用户移出俱乐部异常{}", number,e);
		}
		return dto;
	}





}
