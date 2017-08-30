package com.joe.frame.core.api;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

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

import com.joe.frame.common.util.DateUtil;
import com.joe.frame.common.util.PojoUtils;
import com.joe.frame.core.dto.CashOutDTO;
import com.joe.frame.core.dto.ClubDTO;
import com.joe.frame.core.entity.ClubNotice;
import com.joe.frame.core.entity.Proxy;
import com.joe.frame.core.param.AccountUseParam;
import com.joe.frame.core.param.AgentParam;
import com.joe.frame.core.param.BuyParam;
import com.joe.frame.core.param.CashOutParam;
import com.joe.frame.core.param.ChongZhiParam;
import com.joe.frame.core.param.ClubNoticeParam;
import com.joe.frame.core.param.PageParam;
import com.joe.frame.core.param.RechargeParam;
import com.joe.frame.core.param.SearchParam;
import com.joe.frame.core.param.TaocanParam;
import com.joe.frame.core.param.UserInfoParam;
import com.joe.frame.core.service.AgentService;
import com.joe.frame.core.service.ShareService;
import com.joe.frame.web.dto.BaseDTO;
import com.joe.frame.web.dto.NormalDTO;

@Path("agent")
public class AgentResource extends BaseResource {
	private static final Logger logger = LoggerFactory.getLogger(AgentResource.class);
	@Autowired
	private AgentService agentService;
	@Context
	HttpServletRequest request;
	@Autowired
	private DateUtil dateUtil;
	@Autowired
	private ShareService shareService;


	/**
	 * 个人所有业绩 = （个人+ 旗下所有人业绩总额 ）* 对应的返利比
	 * 旗下直属A　＝　（个人+ 旗下所有人业绩总额 ）* 对应的返利比
	 * 
	 * 旗下直属下级所有业绩 = （旗下直属A * 对应的返利比　　＋　旗下直属Ｂ * 对应的返利比　＋．．．．）
	 * 
	 *返利计算（）个人所有业绩 - 旗下直属下级所有业绩
	 * @return
	 */
	//	@GET
	//	@Path("cacl")
	//	@Produces(MediaType.APPLICATION_JSON)
	//	public BaseDTO<Object> cacl(){
	//		 BaseDTO<Object>  dto = new  BaseDTO<Object> ();
	//		String now = dateUtil.getFormatDate("yyyyMMdd");
	//		try {
	//			shareService.getShare(now);
	//			return dto;
	//		} catch (java.text.ParseException e) {
	//			logger.info("每月分红计算失败",e);
	//			System.out.println(e);
	//			dto.setErrorMessage("分红计算失败{}");
	//		}
	//		return dto;
	//	}



	/**
	 * 代理商给玩家充值房卡
	 * 给玩家添加相应房卡数量，给代理减去相应的房卡数
	 * 
	 * @param param 
	 * @return
	 */
	@POST
	@Path("chongzhi")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public BaseDTO<Object> chongzhi(ChongZhiParam param) {
		BaseDTO<Object> dto = new BaseDTO<Object>();
		try {
			int flag = agentService.chongzhi(param, getUid(request));
			if(flag == 1){
				dto.error("201", "房卡数量不足，充值失败");
				return dto;
			}
		} catch (Exception e) {
			dto.setStatus("401");
			dto.setErrorMessage("异常，稍后再试");
			logger.error("充值异常", e);
		}
		return dto;
	}




	/**
	 * 
	 * 代理商个人购买房卡(无用)
	 * 
	 * @param param
	 *            购买房卡信息
	 * @return
	 */
	//	@POST
	//	@Path("buy")
	//	@Produces(MediaType.APPLICATION_JSON)
	//	@Consumes(MediaType.APPLICATION_JSON)
	//	public NormalDTO<Object> buy(BuyParam param) {
	//		NormalDTO<Object> dto = new NormalDTO<Object>();
	//		try {
	//							String datas = agentService.buy(param, getUid(request));// 代理商
	//			//			dto.setData(datas);
	//		} catch (Exception e) {
	//			dto.setStatus("401");
	//			dto.setErrorMessage("异常，稍后再试");
	//			logger.error("充值异常", e);
	//		}
	//		return dto;
	//	}

	/**
	 * 获取代理商给用户的充值记录 分页查询 page从1开始
	 * 
	 * @param pageparam
	 *            分页数据
	 * @return 玩家id，昵称，剩余房卡，售卡数量，售卡时间（修改 玩家编号）
	 */
	@POST
	@Path("palyerCZList")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public NormalDTO<Object> getPalyerRechargeList(PageParam pageParam) {
		NormalDTO<Object> dto = new NormalDTO<Object>();
		try {
			List<ChongZhiParam> listData = agentService.getPalyerRechargeList(getUid(request), pageParam);
			long sum = agentService.getgetPalyerRechargeListSize(getUid(request));
			long pageCount = (sum + pageParam.getSize()-1) / pageParam.getSize();
			dto.setCurrentPage(pageParam.getPageNo());//当前页
			dto.setPageCount(pageCount);//总页数
			dto.setData(listData);
		} catch (Exception e) {
			dto.setStatus("401");
			dto.setErrorMessage("获取代理商给用户的充值记录异常，稍后再试");
			logger.error("获取代理商给用户的充值记录异常", e);
		}
		return dto;
	}



	//	
	//	setPage(listData,dto,pageParam);
	//	private void setPage(List<T> listData , NormalDTO<Object> dto,PageParam pageParam){
	//		int pageCount = (listData.size() + pageParam.getSize()-1) / pageParam.getSize();
	//		dto.setCurrentPage(pageParam.getPageNo());//当前页
	//		dto.setPageCount(pageCount);//总页数
	//	}


	/**
	 * 获取代理商个人的充值（房卡）记录 分页查询 page从1开始
	 * 
	 * @param pageparam
	 *            分页数据
	 * @return 订单编号，套餐名称，金额/张数，日期，支付状态，支付方式
	 */
	@POST
	@Path("agentCZList")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public NormalDTO<Object> getAgentRechargeList(PageParam pageParam) {
		NormalDTO<Object> dto = new NormalDTO<Object>();
		try {
			List<RechargeParam> listData = agentService.getAgentRechargeList(getUid(request), pageParam);
			long sum = agentService.getAgentRechargeListSum(getUid(request));
			long pageCount = (sum + pageParam.getSize()-1) / pageParam.getSize();
			dto.setCurrentPage(pageParam.getPageNo());//当前页
			dto.setPageCount(pageCount);//总页数
			dto.setData(listData);
		} catch (Exception e) {
			dto.setStatus("401");
			dto.setErrorMessage("获取代理商的充值记录异常，稍后再试");
			logger.error("获取代理商的充值记录异常", e);
		}
		return dto;
	}

	/**
	 * 根据日期获取代理商个人的充值（房卡）记录 分页查询 page从1开始
	 * 
	 * @param pageparam time 格式 2017-01
	 *            分页数据
	 * @return
	 */
	@POST
	@Path("agentDateList")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public NormalDTO<Object> getCZDayList(SearchParam searchParam) {
		NormalDTO<Object> dto = new NormalDTO<Object>();
		try {
			List<RechargeParam> listData = agentService.getCZDayList(getUid(request), searchParam);
			long sum = agentService.getCZDayListSum(getUid(request),searchParam.getSearchTime());
			long pageCount = (sum + searchParam.getSize()-1) / searchParam.getSize();
			dto.setCurrentPage(searchParam.getPageNo());//当前页
			dto.setPageCount(pageCount);//总页数
			dto.setData(listData);
		} catch (Exception e) {
			dto.setStatus("401");
			dto.setErrorMessage(" 根据日期获取代理商个人的充值异常，稍后再试");
			logger.error("获取代理商{}的充值记录异常,异常信息{}",searchParam.getSearchTime(), e);
		}
		return dto;
	}


	/**
	 * 每月的月末提现，每月提现一次，提现金额高于300）//提现操作---重新设置用户资金
	 * 
	 * 代理商提现申请， 添加申请提现记录
	 * 
	 * @param param
	 *            申请提现的基本信息 :money 金额，type：类型
	 * @param uid
	 *            代理id
	 */
	@POST
	@Path("applyCash")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public BaseDTO<Object> applyCash(CashOutParam param) {
		BaseDTO<Object> dto = new BaseDTO<Object>();
		try {
			if (param.getMoney() < 300 && ("".equals(param.getMoney()))) {
				dto.setStatus("202");
				dto.setErrorMessage("提现金额必须高于300元！");
				return dto;
			}
			//			if(!dateUtil.isMonthLastDay(new Date())){
			//				dto.setStatus("202");
			//				dto.setErrorMessage("非提现日期，请在每月月末进行提现！");
			//				return dto;
			//			}
			boolean flag = agentService.isCashOutThisMonth(getUid(request));
			if(flag){
				//已经提现了
				dto.setStatus("202");
				dto.setErrorMessage("每月只能提现一次，您已经申请过提现！");
				return dto;
			}

			byte sign = agentService.applyCash(param, getUid(request));// 代理商
			if (sign == 0) {
				dto.setStatus("0");
				dto.setErrorMessage("提现申请成功");
			} else if (sign == 1) {
				dto.setStatus("203");
				dto.setErrorMessage("提现申请失败，余额不足");
			}
		} catch (Exception e) {
			dto.setStatus("401");
			dto.setErrorMessage("异常，稍后再试");
			logger.error("提现申请异常", e);
		}
		return dto;
	}

	/**
	 * 获取代理商提现记录
	 * 
	 * 分页查询 page从1开始
	 * 
	 * @param pageparam
	 *            分页数据
	 * @return // 微信昵称、账号、日期、状态、金额
	 */
	@POST
	@Path("agentCashOut")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public NormalDTO<Object> getAgentCashOut(PageParam pageParam) {
		NormalDTO<Object> dto = new NormalDTO<Object>();
		try {
			List<CashOutDTO> listData = agentService.getAgentCashOut(getUid(request), pageParam);
			long sum = agentService.getAgentCashOutSum(getUid(request));
			long pageCount = (sum + pageParam.getSize()-1) / pageParam.getSize();
			dto.setCurrentPage(pageParam.getPageNo());//当前页
			dto.setPageCount(pageCount);//总页数
			dto.setData(listData);
		} catch (Exception e) {
			dto.setStatus("401");
			dto.setErrorMessage("获取代理商提现记录，稍后再试");
			logger.error("获取代理商提现记录", e);
		}
		return dto;
	}


	/**
	 * 获取uid下的直属下级列表
	 * 手机号用**标记例如159***2551
	 * 
	 * @param uid
	 *            代理商id
	 * @return 推荐人，推荐人手机号，昵称，注册日期，手机号，地区
	 */
	@POST
	@Path("getDirectChild")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public NormalDTO<Object> getDirectChild(PageParam pageParam) {
		NormalDTO<Object> dto = new NormalDTO<Object>();
		try {
			List<UserInfoParam> list = agentService.getDirectChild(getUid(request));
			for(UserInfoParam pa :list){
				pa.setPhone(getPhone(request));//设置手机号码，在推荐人一栏
			}
			//对list进行分页操作就可以了；
			int pageSize = pageParam.getSize();
			int pageNo = pageParam.getPageNo();
			ArrayList<UserInfoParam> result = new ArrayList<>();//新的list
			if(list != null && list.size() > 0){
				int allCount = list.size();
				//			计算总页数的方法
				//				int pageCount = (allCount + pageSize-1) / pageSize;
				//				if(pageNo >= pageCount){
				//					pageNo = pageCount;
				//				}
				int start = (pageNo-1) * pageSize;
				int end = pageNo * pageSize;
				if(end >= allCount){
					end = allCount;
				}
				for(int i = start; i < end; i ++){
					UserInfoParam dtos = list.get(i);
					//	dto.setPageCount(pageCount);//设置总页数
					result.add(dtos);
				}
			}
			dto.setData(result);
		} catch (Exception e) {
			dto.setStatus("401");
			dto.setErrorMessage("获取直属下级异常，请稍后再试");
			logger.error(e.getMessage());
		}
		return dto;
	}


	/**
	 * 根据手机号码查询某个直属下级信息   手机号用**标记例如159***2551
	 * 分页查询 page从1开始
	 * 
	 * @param phone
	 *            直属下级的手机号码
	 * @return 推荐人，推荐人手机号，昵称，注册日期，手机号，地区
	 */
	@POST
	@Path("oneDirectChild")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public NormalDTO<Object> getoneDirectChild(String phone) {
		NormalDTO<Object> dto = new NormalDTO<Object>();
		try {
			Proxy p = agentService.getoneDirectChild(getUid(request), phone);
			if(p==null){
				dto.setStatus("201");
				dto.setErrorMessage("没有此下级！");
				logger.info("没有此下级");
				return dto;
			}
			UserInfoParam userparam = PojoUtils.copy(p, UserInfoParam.class);
			userparam.setPhone(getPhone(request));//设置手机号码，在推荐人一栏
			userparam.setChildPhone(p.getPhone());//下级的手机号
			dto.setData(userparam);
		} catch (Exception e) {
			dto.setStatus("401");
			dto.setErrorMessage("获取直属下级信息异常，稍后再试");
			logger.error("获取某个{}直属下级信息异常,异常信息{}",phone, e);
		}
		return dto;
	}




	/**
	 * 添加俱乐部公告
	 * 
	 * @param clubNoticeParam
	 *            公告详情
	 * 
	 * @param uid
	 *            代理商id
	 */
	@POST
	@Path("addNotice")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public BaseDTO<Object> addNotice(ClubNoticeParam clubNoticeParam) {
		BaseDTO<Object> dto = new BaseDTO<Object>();
		try {
			agentService.addNotice(clubNoticeParam, getUid(request));
		} catch (Exception e) {
			dto.setStatus("401");
			dto.setErrorMessage("添加公告异常，稍后再试");
			logger.error("添加公告异常,异常信息{}", e);
		}
		return dto;
	}


	/**
	 * 每次俱乐部公告只有一条
	 * 查询俱乐部公告
	 * 
	 * @param clubNoticeParam
	 *            公告详情
	 * 
	 * @param uid
	 *            代理商id
	 */
	@GET
	@Path("getNotice")
	@Produces(MediaType.APPLICATION_JSON)
	public NormalDTO<ClubNotice> getNotice() {
		NormalDTO<ClubNotice> dto = new NormalDTO<ClubNotice>();
		try {
			ClubNotice notice = agentService.getNotice(getUid(request));
			dto.setData(notice);
		} catch (Exception e) {
			dto.setStatus("401");
			dto.setErrorMessage("查询公告异常，稍后再试");
			logger.error("查询公告异常,异常信息{}", e);
		}
		return dto;
	}



	/**
	 * 查询俱乐部信息（创建人=代理）
	 * @return
	 */
	@GET
	@Path("getClubMsg")
	@Produces(MediaType.APPLICATION_JSON)
	public NormalDTO<ClubDTO> getClubMsg(){
		NormalDTO<ClubDTO> dto = new  NormalDTO<ClubDTO>();
		try {
			ClubDTO club = agentService.getClubMsg(getUid(request));
			dto.setData(club);
		} catch (Exception e) {
			dto.setStatus("401");
			dto.setErrorMessage("查询俱乐部信息异常，稍后再试");
			logger.error("查询俱乐部信息异常,异常信息{}", e);
		}
		return dto;
	}


	//计算套餐 //	显示 套餐名称，房卡数量，售价，代理账号（手机号码）
	//	200张 = 300元  1.5元/张
	//	500张 = 750元  1.5元/张	
	//	1000张 = 1475元  1.45元/张
	//	2000张 = 2800元  1.4元/张	
	//	5000张 = 6750元  1.35元/张
	//	10000张 = 13000元  1.3元/张	


	/**
	 * 生成订单，显示订单内容
	 * 
	 * 点击购买房卡中的确定，
	 * （提供具体的套餐的规则）
	 * 选择好套餐之后，回显套餐详情， 套餐名称，房卡数量，售价，代理账号（手机号码）订单编号，同时生成订单
	 * @param selectItem 选择的套餐的编号（名称）
	 * @param num 自定义套餐的数量
	 * @return
	 */
	@POST
	@Path("addMenuOrder")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public NormalDTO<Object> showMenu(TaocanParam taocanParam){
		NormalDTO<Object> dto = new NormalDTO<Object>();
		BuyParam buyParam  = agentService.addOrder(taocanParam.getPayWay(),taocanParam.getNum(),taocanParam.getSelectItem(),getUid(request),getPhone(request));
		dto.setData(buyParam);
		return dto;
	}

	/**
	 * 代理架构页面显示数据
	 * 查询当前登录代理直属下级的以下信息
	 * 
	 * @return 代理账号，俱乐部账号，昵称，旗下代理数 ，代理购卡总数，代理购卡总金额，个人购卡总数，总金额，上级代理手机号，代理等级，代理级别，管理（开启、停止） 
	 * @throws ParseException 
	 */
	@GET
	@Path("agentPolicy")
	@Produces(MediaType.APPLICATION_JSON)
	public NormalDTO<Object> agentPolicy() throws ParseException{
		NormalDTO<Object> dto = new NormalDTO<Object>();
		try {
			PageParam pageParam = null;
			List<AgentParam> list = agentService.agentPolicy(getUid(request),pageParam);
			dto.setData(list);
		} catch (Exception e) {
			dto.setStatus("401");
			dto.setErrorMessage("数据查询异常，稍后再试");
			logger.error("查询代理政策信息异常,异常信息{}", e);
		}
		return dto;
	}


	/**
	 * 代理政策管理（开启，禁止）
	 * 设置账号的登录限制
	 * @param agentId
	 * @param canuse :no,yes
	 * @return
	 */
	@POST
	@Path("setCanuse")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public BaseDTO<Object> setCanuse(AccountUseParam accountUseParam)	{
		BaseDTO<Object> dto = new  BaseDTO<Object>();
		try {
			agentService.setCanuse(accountUseParam.getAgentId(),accountUseParam.getCanuse());
		} catch (Exception e) {
			dto.setStatus("401");
			dto.setErrorMessage("异常，稍后再试");
			logger.error("设置账号的登录限制异常,异常信息{}", e);
		}
		return dto;
	}
}
