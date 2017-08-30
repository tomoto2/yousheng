package com.joe.frame.core.api;

import java.text.ParseException;
import java.util.Map;

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
import com.joe.frame.core.dto.BaobiaoDTO;
import com.joe.frame.core.dto.FenliDTO;
import com.joe.frame.core.dto.ShareDTO;
import com.joe.frame.core.entity.Share;
import com.joe.frame.core.param.BaobiaoParam;
import com.joe.frame.core.param.ChildYejiParam;
import com.joe.frame.core.service.AgentService;
import com.joe.frame.core.service.ShareService;
import com.joe.frame.core.service.UserInfoService;
import com.joe.frame.web.dto.NormalDTO;

@Path("profit")
public class ProfitResource extends BaseResource {
	private static final Logger logger = LoggerFactory.getLogger(ProfitResource.class);
	@Autowired
	private AgentService agentService;
	@Autowired
	private UserInfoService userInfoService;
	@Autowired
	private ShareService shareService;
	@Context
	HttpServletRequest request;
	@Autowired
	private DateUtil dateUtil;

	// 获取旗下代理总数
	public NormalDTO<Object> getTeamSum() {
		NormalDTO<Object> dto = new NormalDTO<Object>();
		long sum = agentService.getTeamSum(getUid(request));
		return dto;
	}

	// 获取某个日期之前的旗下代理总数
	public long getTeamSumByTime(String searchTime) throws ParseException {
		long sum = agentService.getTeamSumByTime(getUid(request), searchTime);
		return sum;
	}

	// 获取某个日期段内 新增的旗下代理数
	public NormalDTO<Object> getTeamNewSumByTime(String beginTime, String endTime) throws ParseException {
		NormalDTO<Object> dto = new NormalDTO<Object>();
		long sum = agentService.getTeamSumByRegion(getUid(request), beginTime, endTime);
		return dto;
	}

	// 每日报表
	// 1.开始时间，截止时间
	// 2.旗下代理数：旗下有多少代理，截止时间之前的所有代理数
	// 3.当日业绩：今天卖掉多少张房卡
	// 4.当日购卡：今天购买多少张房卡
	// 5.实际返利：实际返利多少
	/**
	 * 每日报表 查询某段时间内的，旗下代理总数，期间的业绩，期间的购卡数量，实际返利
	 * 
	 * @param beginTime
	 *            开始时间 yyyy-MM-dd HH:mm:ss
	 * @param endTime
	 *            截止时间yyyy-MM-dd HH:mm:ss
	 * @return
	 * @throws ParseException
	 */
	@POST
	@Path("getTeamByTwoTime")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public NormalDTO<BaobiaoDTO> getTeamByTwoTime(BaobiaoParam baobiaoParam) throws ParseException {
		NormalDTO<BaobiaoDTO> dto = new NormalDTO<BaobiaoDTO>();
		BaobiaoDTO datas = new BaobiaoDTO();
		String now = dateUtil.getFormatDate("yyyy-MM-dd HH:mm:ss");
		//		long int1 = Long.parseLong(now);
		//		long int2 = Long.parseLong(baobiaoParam.getEndTime());
		//		long result = int1-int2;
		//		if(result < 0 ){
		//			dto.error("203", "搜索日期不合规范");
		//			return dto;
		//		}
		boolean flag = dateUtil.compareTime(baobiaoParam.getEndTime(), now);
		if (flag) {// 检索的截止时间大于当前时间
			dto.error("203", "搜索日期不合规范");
			logger.info("搜索日期不合规范");
			return dto;
		}
		try {
			logger.info("查看每日报表");
			long sum = agentService.getTeamSumByTime(getUid(request), baobiaoParam.getEndTime());// 截止日期之前的所有代理总数
			long yeji = agentService.getYeji(getUid(request), baobiaoParam.getBeginTime(), baobiaoParam.getEndTime());// （业绩）这段时间卖掉的房卡数量(算开头和结尾)
			long goumai = agentService.getBuy(getUid(request), baobiaoParam.getBeginTime(), baobiaoParam.getEndTime());// 某段时间自己购买的房卡数量
			long fanli = agentService.getfanli1(getUid(request), baobiaoParam.getBeginTime(),
					baobiaoParam.getEndTime());// 实际返利：实际返利多少
			datas.setAgentSum(sum);
			datas.setYeji(yeji);
			datas.setGouka(goumai);
			datas.setFanli(fanli);
			dto.setData(datas);
		} catch (Exception e) {
			dto.setStatus("401");
			dto.setErrorMessage("异常，稍后再试");
			logger.error("报表计算异常", e);
		}
		return dto;
	}

	/**
	 * 计算某段时间内某代理的实际返利
	 * 
	 * @param uid
	 *            代理id
	 * @param beginTime
	 *            开始时间 yyyy-MM-dd HH:mm:ss
	 * @param endTime
	 *            截止时间yyyy-MM-dd HH:mm:ss
	 * @throws ParseException
	 */
	//	public long getShijiFanli(String uid, String beginTime, String endTime) throws ParseException {
	//		// 总业绩 = 计算某段时间内个人购卡总金额+旗下代理购卡总金额
	//		// 您的实际返利：您总业绩 * 对应提成比例 - 直属下级A的业绩 * 对应提成比例- 直属下级B的业绩 * 对应提成比例
	//		long allMoney = agentService.getBuyByBegin(uid, beginTime, endTime);// 某段时间内代理个人购卡总金额
	//		long childAllMoney = agentService.getChildBegin(uid, beginTime, endTime);// 某段时间内旗下代理购卡总金额
	//		long allYeji = allMoney + childAllMoney;// 总业绩
	//		long allYejiFanliBi = getRate(allYeji);// 总业绩对应的提成比例
	//	//这个方法是错误的需求	long directChildyejiAdd = agentService.getDirectChildYeji(getUid(request), beginTime, endTime);// 获取所有直属下级的业绩*对应提成比例之后
	//		// 的总和（总金额）
	//		long shijiFanli = allYeji * allYejiFanliBi - directChildyejiAdd;// 您的实际返利
	//		return shijiFanli;
	//	}

	/**
	 * 这一块可以简化，从share中查询出来
	 * 按照月份进行统计返利详情
	 * 
	 * @param monthDate
	 *            格式：yyyy-MM
	 * @throws ParseException
	 */
	// 个人购卡：个人购卡量总金额
	// 旗下代理购卡：旗下代理购卡总金额
	// 您的总业绩：个人购卡总金额+旗下代理购卡总金额
	// 您的返利比：个人购卡的总金额所对应的返利比
	// 您的总返利：个人购卡总金额*返利比例
	// 旗下代理返利：旗下代理总金额*旗下代理总金额对应的返利比
	// 您的实际返利：您总业绩 * 对应提成比例 - 直属下级A的业绩 * 对应提成比例- 直属下级B的业绩 * 对应提成比例
	@POST
	@Path("getMonthBenifit")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public NormalDTO<ShareDTO> getMonthBenifit(String monthDate) throws ParseException {
		NormalDTO<ShareDTO> dto = new NormalDTO<ShareDTO>();
		try {
			//			String nowMonth = dateUtil.getFormatDate("yyyy-MM");// 当月
			//			Share share = shareService.getMonthShare(getUid(request),nowMonth);//本月
			Share share = shareService.getMonthShare(getUid(request),monthDate);//某月
			//			long allMoney = (agentService.getBuyByMonth(getUid(request), monthDate)) * 100;// 某月自己购买的房卡总金额
			//			long qixiaAllMoney = (agentService.getChildBuyByMonth(getUid(request), monthDate)) * 100;// 某月旗下代理购卡：旗下代理购卡总金额
			//			long allYeji = allMoney + qixiaAllMoney;// 总业绩
			//			long fanliBi = getRate(allMoney);// 您的返利比
			//			long allFanli = allMoney * fanliBi;// 您的总返利
			//			long qixiaFanliBi = getRate(qixiaAllMoney);// 旗下代理的返利比
			//			long qixiaAllFanli = qixiaAllMoney * qixiaFanliBi;// 旗下代理返利
			//			// 您的实际返利计算
			//			long allYejiFanliBi = getRate(allYeji);// 总业绩对应的提成比例
			//			//long directChildyejiAdd = agentService.getDirectChildYeji(getUid(request), monthDate);// 获取所有直属下级的业绩*对应提成比例之后
			//			//long shijiFanli = allYeji * allYejiFanliBi - directChildyejiAdd;// 您的实际返利
			//
			//			data.setAllMoney(allMoney);
			//			data.setQixiaAllMoney(qixiaAllMoney);
			//			data.setAllYeji(allYeji);
			//			data.setFanliBi(fanliBi);
			//			data.setAllFanli(allFanli);
			//			data.setQixiaAllFanli(qixiaAllFanli);
			//			data.setShijiFanli(shijiFanli);
			ShareDTO datas = new ShareDTO();//单位改为元
			if(share == null){
				dto.setStatus("202");
				dto.setErrorMessage("本月无返利信息");
				logger.error("本月无返利信息");
				return dto;
			}
				datas.setUid(share.getUid());
				datas.setAllCount(share.getAllCount()/100);
				datas.setAllFanli(share.getAllFanli()/100);
				datas.setAllMoney(share.getAllMoney()/100);
				datas.setAllYeji(share.getAllYeji()/100);
				datas.setCount(share.getCount()/100);
				datas.setDate(share.getDate());
				datas.setFanliBi(share.getFanliBi());
				datas.setQixiaAllFanli(share.getQixiaAllFanli()/100);
				datas.setQixiaAllMoney(share.getQixiaAllMoney()/100);
				datas.setShijiFanli(share.getShijiFanli()/100);
				datas.setFanliBi(share.getFanliBi());
				dto.setData(datas);
		} catch (Exception e) {
			dto.setStatus("401");
			dto.setErrorMessage("异常，稍后再试");
			logger.error("显示每月返利计算异常", e);
		}
		return dto;
	}

	/**
	 * 我的返利页面的所有计算 账户余额 本月实际返利 上月实际返利 本月的 个人总业绩。 返利比例。 个人总返利 旗下代理购卡，旗下代理返利，您实际返利
	 * 
	 * @throws ParseException
	 */
	@GET
	@Path("myFenli")
	@Produces(MediaType.APPLICATION_JSON)
	public NormalDTO<FenliDTO> myFenli() throws ParseException {
		NormalDTO<FenliDTO> dtos = new NormalDTO<FenliDTO>();
		try {
			FenliDTO dto = new FenliDTO();
			long balance = userInfoService.getUserInfo(getUid(request)).getBalance();// 账户余额
			logger.info("我的账户余额{}",balance);
			String nowMonth = dateUtil.getFormatDate("yyyy-MM");// 当月
			logger.info("当前月{}",nowMonth);
			Share share = shareService.getMonthShare(getUid(request),nowMonth);//本月
			Share share1 = shareService.getMonthShare(getUid(request),dateUtil.getPreMonth(nowMonth, "yyyy-MM"));//上月
			if(share!=null && share1!=null){
				dto.setBenShijiFanli(share.getShijiFanli()/100);
				dto.setBenAllfanli(share.getShijiFanli()/100);
				dto.setPreShijiFanli(share1.getShijiFanli()/100);
				dto.setBenAllyeji(share.getAllYeji()/100);
				dto.setBenfanlibi(share.getFanliBi());
				dto.setBenqixiaAllMoney(share.getQixiaAllMoney()/100);
				dto.setBenqixiaAllFanli(share.getQixiaAllFanli()/100);
			}
			dto.setBalance(balance/100);
			logger.info("我的分利页面{}",dto);
			dtos.setData(dto);
		} catch (Exception e) {
			dtos.setStatus("401");
			dtos.setErrorMessage("异常，稍后再试");
			logger.error("我的分利计算异常", e);
		}
		return dtos;
	}

	//	@GET
	//	@Path("myFenli")
	//	@Produces(MediaType.APPLICATION_JSON)
	//	public NormalDTO<FenliDTO> myFenli() throws ParseException {
	//		NormalDTO<FenliDTO> dtos = new NormalDTO<FenliDTO>();
	//		try {
	//			FenliDTO fenli = new FenliDTO();
	//			long balance = userInfoService.getUserInfo(getUid(request)).getBalance();// 账户余额
	//			String nowMonth = dateUtil.getFormatDate("yyyy-MM");// 当月
	//			// 本月实际返利
	//			NormalDTO<MonthDTO> dto = getMonthBenifit(nowMonth);
	//			long benShijiFanli = dto.getData().getShijiFanli();// 本月实际返利
	//			long benAllyeji = dto.getData().getAllYeji();// 本月个人总业绩
	//			long benfanlibi = dto.getData().getFanliBi();// 本月个人返利比
	//			long benAllfanli = dto.getData().getAllFanli();// 本月个人总返利
	//			long benqixiaAllMoney = dto.getData().getQixiaAllMoney();// 旗下代理购卡总金额
	//			long benqixiaAllFanli = dto.getData().getQixiaAllFanli();// 旗下代理返利
	//			// 上月实际返利
	//			NormalDTO<MonthDTO> dto1 = getMonthBenifit(dateUtil.getPreMonth(nowMonth, "yyyy-MM"));// 获取上月
	//			long preShijiFanli = dto1.getData().getShijiFanli();
	//
	//			fenli.setBalance(balance);
	//			fenli.setBenAllfanli(benAllfanli);
	//			fenli.setBenAllyeji(benAllyeji);
	//			fenli.setBenfanlibi(benfanlibi);
	//			fenli.setBenqixiaAllFanli(benqixiaAllFanli);
	//			fenli.setBenqixiaAllMoney(benqixiaAllMoney);
	//			fenli.setBenShijiFanli(benShijiFanli);
	//			fenli.setPreShijiFanli(preShijiFanli);
	//
	//			dtos.setData(fenli);
	//		} catch (Exception e) {
	//			dtos.setStatus("401");
	//			dtos.setErrorMessage("异常，稍后再试");
	//			logger.error("我的分利计算异常", e);
	//		}
	//		return dtos;
	//	}

	// 每次发展下线，计算返利，添加到账户余额中啊！
	// 旗下代理业绩
	// 注册代理时候，生成俱乐部

	// 本日本月业绩，本月返利，返利阶梯，旗下代理人数（昵称，id号码）
	// 今日业绩，本月业绩，旗下代理数，返利明细 进行排序， desc高到低，

	/**
	 * 旗下代理业绩 页面显示数据 查询：本日本月业绩，本月返利，返利阶梯，旗下代理人数（昵称，id号码）
	 * 
	 */
	@POST
	@Path("getDirectChildFanliList")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public NormalDTO<Map<String, Object>> getDirectChildFanli(ChildYejiParam childYejiParam) {
		NormalDTO<Map<String, Object>> dto = new NormalDTO<Map<String, Object>>();
		try {
			Map<String, Object> map = agentService.paixuList(getUid(request), childYejiParam.getSize(),childYejiParam.getPageNo(), childYejiParam.getFlag());
			dto.setCurrentPage(childYejiParam.getPageNo());
			dto.setData(map);
		} catch (Exception e) {
			dto.setStatus("401");
			dto.setErrorMessage("异常，稍后再试");
			logger.error("旗下代理业绩 页面显示数据异常", e);
		}
		return dto;
	}

}
