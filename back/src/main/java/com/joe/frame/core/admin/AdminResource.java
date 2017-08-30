package com.joe.frame.core.admin;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.joe.frame.common.util.DateUtil;
import com.joe.frame.common.util.PojoUtils;
import com.joe.frame.core.api.BaseResource;
import com.joe.frame.core.dto.ClubDTO;
import com.joe.frame.core.dto.FanliAdminDTO;
import com.joe.frame.core.dto.PageDTO;
import com.joe.frame.core.dto.ProxySetDTO;
import com.joe.frame.core.entity.Account;
import com.joe.frame.core.entity.Proxy;
import com.joe.frame.core.entity.User;
import com.joe.frame.core.param.AccountUseParam;
import com.joe.frame.core.param.ChongZhiParam;
import com.joe.frame.core.param.OneClubParam;
import com.joe.frame.core.param.PageParam;
import com.joe.frame.core.param.UserInfoParam;
import com.joe.frame.core.service.AccountService;
import com.joe.frame.core.service.AdminService;
import com.joe.frame.core.service.AgentService;
import com.joe.frame.core.service.UserInfoService;
import com.joe.frame.web.cache.EhcacheService;
import com.joe.frame.web.cache.RedisCacheService;
import com.joe.frame.web.dto.BaseDTO;
import com.joe.frame.web.dto.NormalDTO;

/**
 * 总后台
 * @author lpx
 * 代理信息、代理架构、代理架构搜索、报表、区间报表、返利区间、返利
 * 数据每五分钟更新一次
 * 
 *
 * 2017年7月21日
 */
@Path("admin")
public class AdminResource extends BaseResource{
	private static final Logger logger = LoggerFactory.getLogger(AdminResource.class);
	@Autowired
	private UserInfoService userInfoService;
	@Context
	HttpServletRequest request;
	@Autowired
	private AdminService adminService;
	@Autowired
	private AgentService agentService;
	@Autowired
	private DateUtil dateUtil;
	@Autowired
	private AccountService accountService;
	@Autowired
	private EhcacheService ehcacheService;
	@Autowired
	private RedisCacheService redisCacheService;





	@GET
	@Path("putehcache")
	@Produces(MediaType.APPLICATION_JSON)
	public BaseDTO<Object> putehcacheService(){
		BaseDTO<Object> dto = new  BaseDTO<Object>();
		PageDTO u = new PageDTO();
		u.setAllPages("100");
		u.setCurrentPage("1");
		ehcacheService.put("u", u, 60*10);
		return dto;
	}


	@GET
	@Path("getehcache")
	@Produces(MediaType.APPLICATION_JSON)
	public BaseDTO<Object> getehcacheService(){
		BaseDTO<Object> dto = new  BaseDTO<Object>();
		PageDTO u = ehcacheService.get("u", PageDTO.class);
		System.out.println(u.getAllPages() +"\n"+ u.getCurrentPage());
		return dto;
	}


	/**
	 * 登录
	 * admin 中的canuser字段为admin，表示是admin用户
	 * @param username 登录账号 id
	 * @param password 登录密码
	 * @return
	 */
	@POST
	@Path("adminLogin")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public NormalDTO<Object> login(@FormParam("usename") String username,@FormParam("password") String password) {
		NormalDTO<Object> dto = new NormalDTO<Object>();
		try {
			Account account = accountService.login(username, password);
			if (account == null) {
				dto.setStatus("401");
				dto.setErrorMessage("账号或密码错误~");
				logger.info("登录，账号或者密码错误");
				return dto;
			}else{
				if(!account.getCanUse().equals("admin") ){
					dto.setStatus("401");
					dto.setErrorMessage("账号或密码错误~");
					logger.info("登录，账号或者密码错误");
					return dto;
				}
				HttpSession session = request.getSession();
				session.setAttribute("info", account);
			}
		}catch (Exception e) {
			dto.setStatus("401");
			dto.setErrorMessage("异常，稍后再试");
			logger.error("admin登录后台系统异常 ", e);
		}
		return dto;
	}


	/**
	 * admin使用代理表存储admin用户信息
	 * 获取个人信息
	 * 
	 * @return
	 */
	@GET
	@Path("getAdminInfo")
	@Produces(MediaType.APPLICATION_JSON)
	public NormalDTO<Object> getAdminInfo(){
		NormalDTO<Object> dto=new NormalDTO<Object>();
		try {
			UserInfoParam pm = userInfoService.getUserInfo(getUidReq(request));
			dto.setData(pm);
		} catch (Exception e) {
			dto.setStatus("401");
			dto.setErrorMessage("admin个人信息查询异常，稍后再试");
			logger.error("getAdminInfo(admin获取个人信息异常)",e);
		}
		return dto;
	}


	/**
	 * 查询注册玩家总数
	 * @return
	 */
	@GET
	@Path("getUserSum")
	@Produces(MediaType.APPLICATION_JSON)
	public NormalDTO<Object> getUserSum(){
		NormalDTO<Object> dto = new NormalDTO<Object>();
		Map<String,Object> map = new HashMap<>();
		try {
			//总注册人数
			Long sum = userInfoService.getAllPlayerSum();
			sum = sum == null ? 0 : sum;
			//玩家当天注册人数
			Long daySum = userInfoService.getPlayerSumDay();
			daySum = daySum == null ? 0 :daySum;
			//在游戏房间的人数--一分钟更新一次,
			Integer sumOnRoom = redisCacheService.get("/game/info/onroom", Integer.class);
			sumOnRoom = sumOnRoom == null ? 0 : sumOnRoom;
			//当天登陆过游戏的人数
			Long sumCurrentDay = userInfoService.getSumCurrentDay();
			sumCurrentDay = sumCurrentDay == null ? 0 :sumCurrentDay;
			//游戏时长超过5分钟的人数
			Long sumbyfivemin = userInfoService.getSumbyfivemin();
			sumbyfivemin = sumbyfivemin == null ? 0 :sumbyfivemin;
			map.put("sum", sum);
			map.put("daySum", daySum);
			map.put("sumOnRoom", sumOnRoom);
			map.put("sumCurrentDay", sumCurrentDay);
			map.put("sumbyfivemin", sumbyfivemin);
			dto.setData(map);
		} catch (Exception e) {
			dto.setStatus("401");
			dto.setErrorMessage("异常，稍后再试");
			logger.error("查询玩家概况异常,异常信息{}", e);
		}
		return dto;
	}
	

	/**
	 * 玩家账号的开启 禁止，修改remove的值  true为禁止。false为开启
	 * @return
	 */
	@POST
	@Path("setCanuse")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public BaseDTO<Object> setuserCanuse(AccountUseParam accountUseParam){
		BaseDTO<Object> dto = new  BaseDTO<Object>();
		try {
			userInfoService.setCanuse(accountUseParam.getUid(),accountUseParam.getCanuse());
		} catch (Exception e) {
			dto.setStatus("401");
			dto.setErrorMessage("异常，稍后再试");
			logger.error("设置账号的登录限制异常,异常信息{}", e);
		}
		return dto;
	}



	/**
	 * 分页查询所有玩家列表 page从1开始
	 * @param pageParam 分页数据
	 * @return 总页数，玩家id，玩家微信昵称，当前房卡数量，注册日期 ，谁的俱乐部（所在俱乐部名称）clubName，充值信息
	 */
	@POST
	@Path("getAllPlayer")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public NormalDTO<Object> getAllPlayer(PageParam pageParam){
		NormalDTO<Object> dto = new  NormalDTO<Object>();
		try {
			List<UserInfoParam> list = userInfoService.getAllPlayer(pageParam);
			long sum = userInfoService.getAllPlayerSum();
			long pageCount = (sum + pageParam.getSize()-1) / pageParam.getSize();
			dto.setPageCount(pageCount);
			dto.setData(list);
		} catch (Exception e) {
			dto.setStatus("401");
			dto.setErrorMessage("异常，稍后再试");
			logger.error("分页查询所有玩家列表异常 ", e);
		}
		return dto;
	}



	/**
	 * 
	 * 根据玩家编号查询玩家 
	 * @return 玩家id
	 */
	@POST
	@Path("getPlayer")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public NormalDTO<Object> getPlayer(long uid){
		NormalDTO<Object> dto = new  NormalDTO<Object>();
		try {
			User user= userInfoService.getPlayer(uid);
			if(user == null){
				dto.setStatus("201");
				dto.setErrorMessage("用户不存在");
				return dto;
			}
			dto.setData(user);
		} catch (Exception e) {
			dto.setStatus("401");
			dto.setErrorMessage("异常，稍后再试");
			logger.error("根据玩家编号查询玩家  ", e);
		}

		return dto;
	}


	/**
	 * 分页查询所有代理列表 page从1开始
	 * @param pageParam 分页数据
	 * @return  代理id，所在的俱乐部id，代理昵称，代理微信号 ，(好像米有写)代理介绍人账号（手机号159***），当前房卡数，当前余额,当前月总充值金额，当前月总充值房卡数量
	 * 
	 */
	@POST
	@Path("getAllProxy")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public NormalDTO<Object> getAllProxy(PageParam pageParam){
		NormalDTO<Object> dto = new  NormalDTO<Object>();
		try {
			ArrayList<UserInfoParam> list = userInfoService.getAllProxy(pageParam);
			Collections.sort(list, new Comparator<UserInfoParam>() {
				public int compare(UserInfoParam o1, UserInfoParam o2) {
					return (int)(o2.getAllchongzhi()- o1.getAllchongzhi());
				}
			});
			long sum = userInfoService.getAllProxySum();
			long pageCount = (sum + pageParam.getSize()-1) / pageParam.getSize();
			dto.setPageCount(pageCount);
			dto.setData(list);
		} catch (Exception e) {
			dto.setStatus("401");
			dto.setErrorMessage("异常，稍后再试");
			logger.error("查询所有代理列表 ", e);
		}
		return dto;
	}


	/**
	 * 
	 * 根据代理编号（邀请码=俱乐部编号=代理编号）查询代理 信息
	 * @return 代理编号 6位数
	 */
	@POST
	@Path("getAgent")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public NormalDTO<Object> getAgent(String anumId){
		NormalDTO<Object> dto = new  NormalDTO<Object>();
		try {
			Proxy proxy= userInfoService.getAgent(anumId);
			if(proxy == null){
				dto.setStatus("201");
				dto.setErrorMessage("用户不存在");
				return dto;
			}
			dto.setData(proxy);
		} catch (Exception e) {
			dto.setStatus("401");
			dto.setErrorMessage("异常，稍后再试");
			logger.error("查询代理 信息异常", e);
		}
		return dto;
	}



	/**
	 * 代理架构
	 * 
	 * 上级代理：展示上一级代理手机号——158****3333
	 * 关系等级：一级代理为1；二级代理为2；三级代理为3
	 * 操作：升级或者降级
	 *  显示所有代理的以下信息： 代理id，所在的俱乐部id，代理昵称，代理微信号 ，代理介绍人账号（手机号159***），当前房卡数，当前余额,当前月总充值金额，当前月总充值房卡数量
	 * 
	 * @return 俱乐部id，代理的昵称，代理的id，上级手机号（159***），个人购卡金额，个人购卡数量，账号可用性
	 * 旗下代理总数，本月旗下代理购卡总数，本月旗下代理购卡总金额，关系等级：一级代理为1；二级代理为2；三级代理为3
	 * 
	 * 
	 * 查询总页数
	 */
	@POST
	@Path("ProxySet")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public NormalDTO<List<ProxySetDTO>> ProxySet(PageParam pageParam){
		NormalDTO<List<ProxySetDTO>> dto =new  NormalDTO<List<ProxySetDTO>>();
		try {
			//			@SuppressWarnings("unchecked")
			//			NormalDTO<List<ProxySetDTO>> u = ehcacheService.get("ProxySet", NormalDTO.class);
			//			if(u != null){
			//				return u;
			//			}
			ArrayList<UserInfoParam> agentlist = userInfoService.getAllProxy(pageParam);
			long sum = userInfoService.getAllProxySum();
			long pageCount = (sum + pageParam.getSize()-1) / pageParam.getSize();
			dto.setPageCount(pageCount);
			List<ProxySetDTO> lists = new ArrayList<ProxySetDTO>();
			for(UserInfoParam param:agentlist){
				ProxySetDTO  datas = new ProxySetDTO();
				long childSum = agentService.getTeamSum(param.getPid());//旗下代理数量
				String nowMonth = dateUtil.getFormatDate("yyyy-MM");//当前月
				long childMoneySum = agentService.getChildBuyByMonth(param.getPid(), nowMonth);// 查询某月，代理的，旗下代理某月购卡总金额
				long childBuySum = agentService.getSumByMonthOrDay(param.getPid(), nowMonth);//本月旗下代理购卡总数
				datas = PojoUtils.copy(param, ProxySetDTO.class);
				Account account = accountService.getAccount(param.getPid());
				if(account != null){
					datas.setCanuse(account.getCanUse());
				}
				datas.setChildSum(childSum);
				datas.setChildMoneySum(childMoneySum);
				datas.setChildBuySum(childBuySum);
				datas.setLevel(param.getLevel());//关系等级：1及代理，2级代理
				if(param.getFather() != null ){
					Proxy proxy = agentService.getProxy(param.getFather());
					if(proxy != null){
						datas.setParentNumId(proxy.getNumId());//上级代理编号
						datas.setParentPhone(proxy.getPhone());//上级代理手机号
					}
				}
				lists.add(datas);
			}
			dto.setData(lists);
			//			ehcacheService.put("ProxySet", dto, 60*5);
		} catch (Exception e){
			dto.setStatus("401");
			dto.setErrorMessage("异常，稍后再试");
			logger.error("代理架构页面数据查询异常", e);

		}
		return dto;
	}



	/**
	 * 代理架构搜索，根据代理编号查询个人代理架构信息
	 * 
	 */
	@GET
	@Path("ProxySetByOne")
	@Produces(MediaType.APPLICATION_JSON)
	public NormalDTO<ProxySetDTO> ProxySetByOne(@QueryParam("agentNum")  String agentNum){
		NormalDTO<ProxySetDTO> dto =new  NormalDTO<ProxySetDTO>();
		try {
			Proxy param = userInfoService.getAgent(agentNum);
			if(param == null){
				dto.setStatus("201");
				dto.setErrorMessage("无此用户");
				return dto;
			}

			/*	@SuppressWarnings("unchecked")
			NormalDTO<ProxySetDTO> u = ehcacheService.get("ProxySetByOne", NormalDTO.class);
			if(u != null){
				return u;
			}*/
			ProxySetDTO  datas = new ProxySetDTO();
			long childSum = agentService.getTeamSum(param.getPid());//旗下代理数量
			String nowMonth = dateUtil.getFormatDate("yyyy-MM");//当前月
			long childMoneySum = agentService.getChildBuyByMonth(param.getPid(), nowMonth);// 查询某月，代理的，旗下代理某月购卡总金额
			long childBuySum = agentService.getSumByMonthOrDay(param.getPid(), nowMonth);//本月旗下代理购卡总数
			datas = PojoUtils.copy(param, ProxySetDTO.class);
			datas.setChildSum(childSum);
			datas.setChildMoneySum(childMoneySum);
			datas.setChildBuySum(childBuySum);
			datas.setLevel(param.getLevel());//关系等级：1及代理，2级代理
			Proxy proxy = agentService.getProxy(param.getPid());
			if(proxy != null){
				datas.setParentPhone(proxy.getPhone());
			}
			dto.setData(datas);
			ehcacheService.put("ProxySetByOne", dto, 60*5);
		} catch (ParseException e){
			dto.setStatus("401");
			dto.setErrorMessage("异常，稍后再试");
			logger.error("代理架构页面数据查询异常", e);

		}
		return dto;
	}

	/**
	 * 
	 * 查询俱乐部信息  分页查询page从1开始
	 * 
	 * 俱乐部账号：代理俱乐部id号
		代理账号：与代理俱乐部ID相同
		代理昵称：代理人昵称
		俱乐部人数：俱乐部人数
		俱乐部管理：解散/移除俱乐部成员
		俱乐部成员列表：
	 * @param pageParam
	 * 查询总页数
	 * @return 
	 */
	@POST
	@Path("getAllClub")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public NormalDTO<Object> getAllClub(PageParam pageParam){
		NormalDTO<Object> dto = new  NormalDTO<Object>();
		try {
			List<ClubDTO> datas  = adminService.getAllClub(pageParam);
			long sum = agentService.getAllClubSum();
			long pageCount = (sum + pageParam.getSize()-1) / pageParam.getSize();
			dto.setPageCount(pageCount);
			dto.setData(datas);
		} catch (Exception e) {
			dto.setStatus("401");
			dto.setErrorMessage("异常，稍后再试");
			logger.error("分页查询俱乐部异常{}",e);
		}
		return dto;
	}




	/**
	 * 
	 * 根据代理编号查询俱乐部信息  
	 * 邀请码=俱乐部编号=代理编号
	 * @param pnumId 代编号
	 * @return 
	 */
	@POST
	@Path("getClubByPnum")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public NormalDTO<Object> getClubByPnum(String pnumId){
		NormalDTO<Object> dto = new  NormalDTO<Object>();
		try {
			ClubDTO datas  = adminService.getClubByPnum(pnumId);
			dto.setData(datas);
		} catch (Exception e) {
			dto.setStatus("401");
			dto.setErrorMessage("异常，稍后再试");
			logger.error("根据代理编号{}查询俱乐部信息  异常{}",pnumId,e);
		}
		return dto;
	}



	/**
	 * 解散俱乐部--清空所有玩家
	 * @param cid 俱乐部id
	 */
	@POST
	@Path("removeClub")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public BaseDTO<Object> removeClub(String cid){
		BaseDTO<Object> dto = new BaseDTO<Object>();
		try {
			long flag = userInfoService.unOpenClub(cid);
			if(flag<0){
				dto.setStatus("201");
				dto.setErrorMessage("俱乐部不存在，俱乐部解散失败");
				logger.error("俱乐部{}解散失败", cid);
				return dto;
			}
		} catch (Exception e) {
			dto.setStatus("401");
			dto.setErrorMessage("异常，稍后再试");
			logger.error("{}解散俱乐部异常{}", cid,e);
		}
		return dto;
	}


	//	* 点击移除后该玩家信息从本俱乐部清除）--在UserResource中最后一个方法实现


	/**
	 * 不对 要修改！！！！！！！！！！！！！
	 * 启用禁用玩家账号
	 * @param uid 玩家id - 6位
	 * @return
	 */
	public BaseDTO<Object> setUse(AccountUseParam accountUseParam){
		BaseDTO<Object> dto=new BaseDTO<Object>();
		//		try {
		//			agentService.setCanuse(accountUseParam.getAgentId(),accountUseParam.getCanuse());
		//		} catch (Exception e) {
		//			dto.setStatus("401");
		//			dto.setErrorMessage("异常，稍后再试");
		//			logger.error("设置账号的登录限制异常,异常信息{}", e);
		//		}
		return dto;
	}





	/**
	 * 查看某俱乐部里面的所有成员:成员ID号，昵称，剩余房卡数量，
	 * @param pageParam
	 * 查询总页数
	 * @return
	 */
	@POST
	@Path("getClubUserBycid")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public NormalDTO<Object> getClubUserBycid(OneClubParam param){
		NormalDTO<Object> dto = new  NormalDTO<Object>();
		try {
			List<User> list  = adminService.getClubUserBycid(param);
			long sum = agentService.getOneClubSum(param.getCid());
			long pageCount = (sum + param.getSize()-1) / param.getSize();
			dto.setPageCount(pageCount);
			dto.setData(list);
		} catch (Exception e) {
			dto.setStatus("401");
			dto.setErrorMessage("异常，稍后再试");
			logger.error("查看某俱乐部里面的所有成员异常",e);
		}
		return dto;
	}



	/**
	 * 
	 * 给玩家用户充值房卡
	 * 
	 * @param 充值数量，玩家id
	 * @return
	 */
	@POST
	@Path("chongzhi")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public BaseDTO<Object> chongzhi(ChongZhiParam param){
		BaseDTO<Object> dto=new BaseDTO<Object>();
		try {
			int flag = adminService.chongzhi(param);
			if(flag == 1){
				dto.setStatus("201");
				dto.setErrorMessage("充值失败，无此用户！");
				return dto;
			}
		} catch (Exception e) {
			dto.setStatus("401");
			dto.setErrorMessage("异常，稍后再试");
			logger.error("admin给玩家用户充值房卡异常",e);
		}
		return dto;
	}


	//根据 条件搜索 	
	//	 充值信息框：昵称，id，剩余房卡，输入框，提示语（充值后将无法撤回），充值按钮

	//	五、报表
	//	时间输入搜索：时间手动选定，分时间 yue段，分月查询
	//	新增代理数：一个月内新增代理数
	//	新注册的玩家：一个月内新注册的玩家
	//	售卡金额：平台在这一个月内卖了的总金额（也就是所有代理在这个平台上买了多少钱的房卡）
	//	售卡数量：平台的一个月内卖了多少张卡（也就是所有代理在这个平台上买了多少房卡）
	//	当月售卡金额：平台当月售卡总金额（此数值先空着）
	//	当月售卡数量：平台当月售卡总数量（此数值先空着）
	@GET
	@Path("getBaobiao")
	@Produces(MediaType.APPLICATION_JSON)
	public NormalDTO<Object> getBaobiao(@QueryParam("monthDay") String monthDay){
		NormalDTO<Object> dto = new  NormalDTO<Object>();
		if(monthDay == null){
			dto.setStatus("401");
			dto.setErrorMessage("参数异常");
			return dto;
		}
		try {
			//	dateUtil.getFormatDate("yyyy-MM")
			Map<String,Object> map =  adminService.getBaobiao(monthDay);
			dto.setData(map);
		} catch (Exception e) {
			dto.setStatus("401");
			dto.setErrorMessage("异常，稍后再试");
			logger.error("计算报表异常",e);
		}
		return  dto;
	}


	/**
	 * 区间查询报表--某月-某月  yyyy-MM ~ yyyy-MM
	 * @param beginTime   yyyy-MM
	 * @param endTime   yyyy-MM
	 * @return
	 */
	@GET
	@Path("getBaobiaoTwoTime")
	@Produces(MediaType.APPLICATION_JSON)
	public NormalDTO<Object> getBaobiaoTwoTime(@QueryParam("beginTime") String beginTime, @QueryParam("endTime") String endTime){
		NormalDTO<Object> dto = new  NormalDTO<Object>();
		try {
			Map<String,Object> map =  adminService.getBaobiaoTwoTime(beginTime,endTime);
			dto.setData(map);
		} catch (Exception e) {
			dto.setStatus("401");
			dto.setErrorMessage("异常，稍后再试");
			logger.error("区间计算报表异常",e);
		}
		return  dto;
	}






	//	六、返利---也许可以优化，查询share

	//	时间输入搜索：时间手动选定，分时间yue 段，分月查询
	//	代理账号：
	//	代理昵称：
	//	售卡金额：代理一个月内的售卡总金额
	//	售卡数量：代理一个月内的售卡总数量
	//	返利比例：根据返利比例表，计算（10%或者15%按照那个返利标准计算，在微信公众号里有这个图片参考）
	//	总返利金额：代理总返利明细（比如说他这个月买了五万块钱，然后返利比是10%，那么这里显示的是五万×返利比的值，还有旗下代理的返利金额，过一段时间更新一次；再比如说一号买了五万的，到二号他们指的是所有人，卖到十万，那么他的返利比是根据返利标准来计算的，显示最新的返利金额；单月的信息不清空，但是到一号的时候数值从0开始计算，通过上面的时间搜索可以查询到上个月的信息，）
	//	平台售卡金额：代理一个月内的售卡总金额（此数值不显示）
	//	旗下代理返利：旗下代理实际返利（也就是说，这个代理旗下的代理的实际返利总金额，比如说这个代理这个月一个都没有卖出，那么他就只能拿他旗下代理的分成差 ，按照那个算分标准来计算）
	//	代理总返利：（此数值先不展示）
	//	个人实际返利：算到最后这个代理该拿多少钱
	//	提现申请：两种状态——未申请，已申请（鼠标移动上去显示代理人提交的微信账号或者支付宝账号和支付宝姓名）
	//	状态：两种状态——已处理，“处理”做成按钮（点击后转换为已处理），

	@GET
	@Path("fanLi")
	@Produces(MediaType.APPLICATION_JSON)
	public NormalDTO<Object> fanLi(@QueryParam("searchTime") String searchTime){
		NormalDTO<Object> dto = new NormalDTO<Object>();
		//查询本月返利
		try {
			List<FanliAdminDTO>  list = adminService.fanLi(searchTime);
			//查询平台所有返利，各直属代理的所有实际返利之和
			long allAllFanli = 0;
			long allOneMoney = 0;
			for(FanliAdminDTO dtos:list){
				allAllFanli += dtos.getAllFanli();
				allOneMoney += dtos.getOneMoney();
			}
			dto.setPageCount(allAllFanli);//平台总支出
			dto.setCurrentPage((int)allOneMoney);//所有代理的购卡总额之和
			dto.setData(list);
		} catch (Exception e) {
			dto.setStatus("401");
			dto.setErrorMessage("异常，稍后再试");
			logger.error("返利计算异常",e);
		}
		return dto;
	}


	//	六、返利 区间查询---也许可以优化，查询share
	@GET
	@Path("fanLiTwoTime")
	@Produces(MediaType.APPLICATION_JSON)
	public NormalDTO<Object> fanLiTwoTime(@QueryParam("beginTime") String beginTime, @QueryParam("endTime") String endTime){
		NormalDTO<Object> dto = new NormalDTO<Object>();
		//查询本月返利
		try {
			//			@SuppressWarnings("unchecked")
			//			NormalDTO<Object> u = ehcacheService.get("fanLiTwoTime", NormalDTO.class);
			//			if(u != null){
			//				return u;
			//			}
			List<FanliAdminDTO>  list =  adminService.fanLiTwoTime(beginTime,endTime);
			dto.setData(list);
			//			ehcacheService.put("fanLiTwoTime", dto, 60*5);
		} catch (Exception e) {
			dto.setStatus("401");
			dto.setErrorMessage("异常，稍后再试");
			logger.error("返利区间计算异常",e);
		}
		return dto;
	}



	/**
	 * 修改提现状态  从未处理-到已处理
	 * 
	 * @param cashOutId 提现账单的ID
	 * @return
	 */
	@POST
	@Path("uptCashOutStatus")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public BaseDTO<Object> uptCashOutStatus(String cashOutId){
		BaseDTO<Object> dto=new BaseDTO<Object>();
		try {
			int outStatus = agentService.uptCashOutStatus(cashOutId,getUidReq(request));
			if(outStatus==0){
				dto.setErrorMessage("修改成功");
			}else if(outStatus==1){
				dto.setStatus("201");
				dto.setErrorMessage("修改失败");
			}
		} catch (Exception e) {
			dto.setStatus("401");
			dto.setErrorMessage("异常，稍后再试");
			logger.error("修改提现状态从未处理到已处理,异常",e);
		}
		return dto;
	}




	/**
	 * 代理的升级降级
	 * 
	 * @param agentId 代理商id 32位的
	 * @param level 要修改的等级
	 * @return
	 */
	@GET
	@Path("uptLevel")
	@Produces(MediaType.APPLICATION_JSON)
	public BaseDTO<Object> uptLevel(@QueryParam("agentId") String agentId,@QueryParam("level") int level){
		BaseDTO<Object> dto=new BaseDTO<Object>();
		try {
			int flag = agentService.uptLevel(agentId,level,getUidReq(request));
			if(flag==0){
				dto.setErrorMessage("修改成功");
			}else if(flag==1){
				dto.setStatus("201");
				dto.setErrorMessage("修改失败");
			}
		} catch (Exception e) {
			dto.setStatus("401");
			dto.setErrorMessage("异常，稍后再试");
			logger.error("修改代理商等级,异常",e);
		}
		return dto;
	}


	/**
	 *  转移下级操作
	 * @param formAgentId 来源代理32位id
	 * @param toAgentId  转至代理32的id
	 * @return
	 */
	@GET
	@Path("moveChild")
	@Produces(MediaType.APPLICATION_JSON)
	public BaseDTO<Object> moveChild(@QueryParam("formAgentId") String formAgentId,@QueryParam("toAgentId") String toAgentId){
		BaseDTO<Object> dto=new BaseDTO<Object>();
		try {
			int flag = agentService.moveChild(formAgentId,toAgentId);
			if(flag==0){
				dto.setErrorMessage("修改成功");
			}else if(flag==1){
				dto.setStatus("201");
				dto.setErrorMessage("修改失败");
			}
		} catch (Exception e) {
			dto.setStatus("401");
			dto.setErrorMessage("异常，稍后再试");
			logger.error("转移下级操作,异常",e);
		}
		return dto;
	}

	//查询总支出操作




	/**
	 * A的上级是B，现在将A的上级改为C
	 * 更改上级 
	 * @param formAgentNum   A 要修改的代理的6位编号
	 * @param toAgentNum   C 将要换成的上级的6位编号，
	 * @return
	 */
	@GET
	@Path("uptFather")
	@Produces(MediaType.APPLICATION_JSON)
	public BaseDTO<Object> uptFather(@QueryParam("formAgentNum") String formAgentNum,@QueryParam("toAgentNum") String toAgentNum){
		BaseDTO<Object> dto=new BaseDTO<Object>();
		try {
			int flag = agentService.uptFather(formAgentNum,toAgentNum);
			if(flag==0){
				dto.setErrorMessage("修改成功");
			}else if(flag==1){
				dto.setStatus("201");
				dto.setErrorMessage("修改失败");
			}
		} catch (Exception e) {
			dto.setStatus("401");
			dto.setErrorMessage("异常，稍后再试");
			logger.error("转移下级操作,异常",e);
		}
		return dto;
	}


}
