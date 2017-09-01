package com.joe.frame.core.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.LockModeType;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.ws.rs.core.Context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joe.frame.common.secure.Encipher;
import com.joe.frame.common.secure.MD5;
import com.joe.frame.common.util.DateUtil;
import com.joe.frame.common.util.PojoUtils;
import com.joe.frame.common.util.Tools;
import com.joe.frame.core.dto.ClubDTO;
import com.joe.frame.core.dto.FanliAdminDTO;
import com.joe.frame.core.entity.CashOut;
import com.joe.frame.core.entity.Club;
import com.joe.frame.core.entity.Proxy;
import com.joe.frame.core.entity.User;
import com.joe.frame.core.param.ChongZhiParam;
import com.joe.frame.core.param.OneClubParam;
import com.joe.frame.core.param.PageParam;
import com.joe.frame.core.repository.CashOutRepository;
import com.joe.frame.core.repository.ClubRepository;
import com.joe.frame.core.repository.ProxyRepository;
import com.joe.frame.core.repository.RechargeRepository;
import com.joe.frame.core.repository.UsersRepository;
import com.joe.frame.web.cache.EhcacheService;

@Service
@Transactional
public class AdminService {
	private static final Logger logger = LoggerFactory.getLogger(AgentService.class);
	@Autowired
	private Tools tools;
	@Autowired
	private DateUtil dateUtil;
	private Encipher encipher = new MD5();
	@Autowired
	private  RechargeRepository rechargeRepository;
	@Autowired
	private UsersRepository usersRepository;// 玩家
	@Autowired
	private ProxyRepository proxyRepository;// 代理商
	@Autowired
	private CashOutRepository cashOutRepository;// 代理商提现记录
	@Autowired
	private ClubRepository clubRepository;
	@Context
	private HttpServletRequest request;
	@Autowired
	private AgentService agentService;
	@Autowired
	private EhcacheService ehcacheService;


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
	 * @return 
	 */
	public List<ClubDTO> getAllClub(PageParam pageParam) {
		List<Club> list = clubRepository.getAllClub(pageParam.getPageNo(), pageParam.getSize());
		List<ClubDTO> dto = PojoUtils.copy(list, ClubDTO.class);
	/*	for(ClubDTO clubdto :dto){
			clubdto.setMemberSum(usersRepository.findClubSum(clubdto.getCid()));
		}*/
		return dto;
	}



	/**
	 * 
	 * 给玩家用户充值房卡
	 * 
	 * 
	 * @param request
	 * @param cashOutParameter
	 * @return
	 */
	public int chongzhi(ChongZhiParam param) {
		User user = usersRepository.getUserByMunber(param.getUid(),LockModeType.PESSIMISTIC_WRITE);
		if(user == null){
			return 1;
		}
		user.setCard(user.getCard() + param.getCartSum());
		return 0;
	}




	/**
	 * 根据俱乐部id查询俱乐部里所有成员
	 * @param param 俱乐部id、分页信息
	 * @return
	 */
	public List<User> getClubUserBycid(OneClubParam  param) {
		List<User> userList = usersRepository.getClumMember(param.getCid(),param.getPageNo(),param.getSize());
		return userList;
	}



	/**
	 * 
	 * 根据代理编号查询俱乐部信息  
	 * 邀请码=俱乐部编号=代理编号
	 * 
	 * @return 
	 */
	public ClubDTO getClubByPnum(String pnumId) {
		Club club  = clubRepository.getClubByPnum(pnumId,LockModeType.PESSIMISTIC_WRITE);
		logger.info("根据代理编号{}查询的俱乐部信息为{}",pnumId,club);
		ClubDTO dto = PojoUtils.copy(club, ClubDTO.class);
		return dto;
	}





	//	五、报表
	//	时间输入搜索：时间手动选定，分时间 yue段，分月查询
	//	新增代理数：一个月内新增代理数
	//	新注册的玩家：一个月内新注册的玩家
	//	售卡金额：平台在这一个月内卖了的总金额（也就是所有代理在这个平台上买了多少钱的房卡）
	//	售卡数量：平台的一个月内卖了多少张卡（也就是所有代理在这个平台上买了多少房卡）
	//	当月售卡金额：平台当月售卡总金额（此数值先空着）
	//	当月售卡数量：平台当月售卡总数量（此数值先空着）
	/**
	 * 
	 * @param thisMonth 格式：yyyy-MM
	 */
	public Map<String,Object> getBaobiao(String thisMonth) {
		Map<String,Object> map = new  HashMap<String,Object>();
		List<Proxy> list = proxyRepository.findAllByTime(thisMonth);
		logger.info("查询某{}日期之前的所有代理{}",thisMonth,list);
		long allMoney = 0;//也就是所有代理在这个平台上买了多少钱的房卡
		long allSum = 0;//也就是所有代理在这个平台上买了多少房卡
		//		if(list == null){
		//			return Collections.emptyMap();
		//		}
		if(list != null){
			for(Proxy p:list){
				long oneMoney = rechargeRepository.getBuyByMonthOrDay(p.getPid(),thisMonth);//查询某代理在某月的实际花费金额（元）
				logger.info("查询某代理{}在某月{}的实际花费金额{}元",p.getPid(),thisMonth,oneMoney);
				allMoney += oneMoney;
				long sum = rechargeRepository.getSumByMonthOrDay(p.getPid(), thisMonth);//查询某代理在某月的实际购卡总数
				logger.info("查询某代理{}在某月{}的实际购卡总数",p.getPid(),thisMonth,sum);
				allSum +=sum;
			}
			long proxySum = proxyRepository.findRegister(thisMonth);//查询某月新注册的代理总数
			long userSum = usersRepository.findRegister(thisMonth);//查询某月新注册的玩家总数
			map.put("proxySum", proxySum);
			map.put("userSum", userSum);
		}
		map.put("allMoney", allMoney);//平台售卡总额
		map.put("allSum", allSum);//平台售卡总数
		return map;
	}


	/**
	 * 区间查询报表--某月-某月  yyyy-MM ~ yyyy-MM
	 * @param beginTime   yyyy-MM
	 * @param endTime   yyyy-MM
	 * @return
	 */
	public Map<String, Object> getBaobiaoTwoTime(String beginTime, String endTime) {
		Map<String,Object> map = new  HashMap<String,Object>();
		List<Proxy> list = proxyRepository.findAllByTime(endTime);
		logger.info("查询所有代理{}",list);
		long allMoney = 0;//也就是所有代理在这个平台上买了多少钱的房卡
		long allSum = 0;//也就是所有代理在这个平台上买了多少房卡
		if(list != null){
			for(Proxy p:list){
				long oneMoney = rechargeRepository.getBuyByBegin(p.getPid(),beginTime,endTime);//查询某代理在某月区间的实际花费金额（元）
				logger.info("查询某代理{}在某月{}到某月{}的实际花费金额{}元",p.getPid(),beginTime,endTime,oneMoney);
				allMoney += oneMoney;
				long sum = rechargeRepository.getBuy(p.getPid(), beginTime,endTime);//查询某代理在某月区间的实际购卡总数
				logger.info("查询某代理{}在某月{}的实际购卡总数",p.getPid(),beginTime,endTime,sum);
				allSum +=sum;
			}
			long proxySum = proxyRepository.findRegisterByTwoTime(beginTime,endTime);//查询某月区间新注册的代理总数
			long userSum = usersRepository.findRegisterByTwoTime(beginTime,endTime);//查询某月区间新注册的玩家总数
			map.put("proxySum", proxySum);
			map.put("userSum", userSum);
		}
		map.put("allMoney", allMoney);
		map.put("allSum", allSum);
		return map;
	}


	//	六、返利
	//	时间输入搜索：时间手动选定，分时间yue 段，分月查询
	//	代理账号：
	//	代理昵称：
	//	售卡金额：代理一个月内的售卡总金额
	//	售卡数量：代理一个月内的售卡总数量
	//	返利比例：根据返利比例表，计算（10%或者15%按照那个返利标准计算，在微信公众号里有这个图片参考）
	//	总返利金额：代理总返利明细（比如说他这个月买了五万块钱，然后返利比是10%，那么这里显示的是五万×返利比的值，还有旗下代理的返利金额，过一段时间更新一次；再比如说一号买了五万的，到二号他们指的是所有人，卖到十万，那么他的返利比是根据返利标准来计算的，显示最新的返利金额；单月的信息不清空，但是到一号的时候数值从0开始计算，通过上面的时间搜索可以查询到上个月的信息，）
	//（个人购卡总金额+旗下代理购卡总金额）   *   对应的返利比
	//	旗下代理返利：旗下代理实际返利（也就是说，这个代理旗下的代理的实际返利总金额，比如说这个代理这个月一个都没有卖出，那么他就只能拿他旗下代理的分成差 ，按照那个算分标准来计算）
	//	直属A*对应的返利比  + 直属B * 对应的返利比
	//	代理总返利：（此数值先不展示）
	//	个人实际返利：算到最后这个代理该拿多少钱
	//	提现申请：两种状态——未申请，已申请（鼠标移动上去显示代理人提交的微信账号或者支付宝账号和支付宝姓名）
	//	状态：两种状态——已处理，“处理”做成按钮（点击后转换为已处理），
	/**
	 * @param searchTime
	 * @return
	 * @throws ParseException
	 */
	public List<FanliAdminDTO> fanLi(String searchTime) throws ParseException {
		List<FanliAdminDTO> listDTO = new  ArrayList<FanliAdminDTO>();
		List<Proxy> list = proxyRepository.findAllByTime(searchTime);
		logger.info("查询所有代理{}",list);
		if(list != null){
			for(Proxy p:list){
				FanliAdminDTO dto = new FanliAdminDTO();
				String pid = p.getPid();//代理id
				String alicode = p.getAlipay();//支付宝 账号
				String wxCode = p.getWx();//微信账号
				String realname = p.getName();//真实姓名
				String nikeName = p.getNikeName();//代理昵称
				String pnumId = p.getInviteCode();//代理ID
				//				金额：代理一个月内的购卡总金额              //				数量：代理一个月内的购卡总数量
				long oneMoney = rechargeRepository.getBuyByMonthOrDay(p.getPid(),searchTime);//查询某代理在某月的实际花费金额（元）
				logger.info("查询某代理{}在某月{}的实际花费金额{}元",p.getPid(),searchTime,oneMoney);
				long sum = rechargeRepository.getSumByMonthOrDay(p.getPid(), searchTime);//查询某代理在某月的实际购卡总数
				logger.info("查询某代理{}在某月{}的实际购卡总数",p.getPid(),searchTime,sum);
				long qixiaAllMoney = (agentService.getChildBuyByMonth(p.getPid(), searchTime));// 某月旗下代理购卡：旗下代理购卡总金额
				long onerate = getRate(oneMoney);//个人购卡金额对应的  个人返利比分
				long allYeji = oneMoney+qixiaAllMoney;
				long allrate = getRate(allYeji);//总业绩对应的个人返利比分
				long AllFanli = (allYeji * allrate)/100; //总返利金额 = (个人购卡总金额+旗下代理购卡总金额）   *   对应的返利比
				long qixiaAndDirect = agentService.getDirectChilsAllChils(searchTime,p.getPid());//代理旗下所有直属下级总业绩*对应的返利比，相加之和     //旗下代理返利：直属A*对应的返利比  + 直属B * 对应的返利比
				long shijiFanli = AllFanli - qixiaAndDirect;//代理实际返利金额
				CashOut cashOut = cashOutRepository.findByPid(p.getPid());
				logger.info("代理{}的提现记录{}",p.getPid(),cashOut);
				//	提现申请：两种状态——未申请，已申请（鼠标移动上去显示代理人提交的微信账号或者支付宝账号和支付宝姓名）
				//	状态：两种状态——已处理，“处理”做成按钮（点击后转换为已处理），
				if(cashOut!= null && cashOut.getStatus()!= null){
					dto.setStatus(cashOut.getStatus());
					dto.setCashOutId(cashOut.getId());
					if(cashOut.getDealWith()!= null){
						dto.setDealWith(cashOut.getDealWith());
						logger.info("设置处理状态{}为",cashOut.getDealWith(),dto.getDealWith());
					}
				}
				dto.setName(realname);
				dto.setAlipay(alicode);
				dto.setWx(wxCode);
				dto.setPid(pid);
				dto.setAllFanli(AllFanli);
				dto.setNikeName(nikeName);
				dto.setOneMoney(oneMoney);
				dto.setSum(sum);
				dto.setOnerate(onerate);
				dto.setPnumId(pnumId);
				dto.setQixiaAllMoney(qixiaAllMoney);
				dto.setShijiFanli(shijiFanli);
				dto.setQixiaAndDirect(qixiaAndDirect);
				listDTO.add(dto);
			}
		}
		return listDTO;
	}

	
	
	/**
	 * 查询所有代理
	 * 将代理放进缓存5分钟
	 * @return
	 */
//	private List<Proxy> getAllEhcache(){
//		@SuppressWarnings("unchecked")
//		List<Proxy> u = (List<Proxy>) ehcacheService.get("proxylist",Proxy.class);
//		if(u != null){
//			return u;
//		}
//		List<Proxy> list = proxyRepository.findAll();
//		ehcacheService.put("proxylist", list, 60*5);
//		return list;
//	}
	
	

	/**
	 * 返利区间查询
	 * @param beginTime
	 * @param endTime
	 * @return
	 * @throws ParseException 
	 */
	public List<FanliAdminDTO> fanLiTwoTime(String beginTime, String endTime) throws ParseException {
		List<FanliAdminDTO> listDTO = new  ArrayList<FanliAdminDTO>();
		List<Proxy> list = proxyRepository.findAllByTime(endTime);
		logger.info("查询所有代理{}",list);
		if(list != null){
			for(Proxy p:list){
				FanliAdminDTO dto = new FanliAdminDTO();
				String pid = p.getPid();//代理id
				String alicode = p.getAlipay();//支付宝 账号
				String wxCode = p.getWx();//微信账号
				String realname = p.getName();//真实姓名
				String nikeName = p.getNikeName();//代理昵称
				String pnumId =p.getInviteCode();//代理ID
				//				金额：代理一个月内的购卡总金额              //				数量：代理一个月内的购卡总数量
				long oneMoney = rechargeRepository.getBuyByBegin(p.getPid(),beginTime,endTime);//查询某代理在月区间的实际花费金额（元）
				logger.info("查询某代理{}在某月{}到某{}月之间的的实际花费金额{}元",p.getPid(),beginTime,endTime,oneMoney);
				long sum = rechargeRepository.getBuy(p.getPid(), beginTime,endTime);//查询某代理在月区间的实际购卡总数
				logger.info("查询某代理{}在某月{}到某月{}之间的实际购卡总数",p.getPid(),beginTime,endTime,sum);
				long qixiaAllMoney = (agentService.getChildBegin(p.getPid(), beginTime,endTime));// 某月区间旗下代理购卡：旗下代理购卡总金额
				long onerate = getRate(oneMoney);//个人购卡金额对应的  个人返利比
				long allYeji = oneMoney+qixiaAllMoney;
				long allrate = getRate(allYeji);//总业绩对应的个人返利比分
				long AllFanli = (allYeji * allrate)/100; //总返利金额 = (个人购卡总金额+旗下代理购卡总金额）   *   对应的返利比
				long qixiaAndDirect = agentService.getDirectChildYejis(p.getPid(),beginTime,endTime);//代理旗下所有直属下级总业绩*对应的返利比，相加之和     //旗下代理返利：直属A*对应的返利比  + 直属B * 对应的返利比
				long shijiFanli = AllFanli - qixiaAndDirect;//代理实际返利金额
				CashOut cashOut = cashOutRepository.findByPid(p.getPid());

				//	提现申请：两种状态——未申请，已申请（鼠标移动上去显示代理人提交的微信账号或者支付宝账号和支付宝姓名）
				//	状态：两种状态——已处理，“处理”做成按钮（点击后转换为已处理），
				if(cashOut!= null && cashOut.getStatus()!= null){
					dto.setStatus(cashOut.getStatus());
					dto.setCashOutId(cashOut.getId());
					if(cashOut.getDealWith()!= null){
						dto.setDealWith(cashOut.getDealWith());
						logger.info("设置处理状态{}为",cashOut.getDealWith(),dto.getDealWith());
					}
				}
				dto.setName(realname);
				dto.setAlipay(alicode);
				dto.setWx(wxCode);
				dto.setPid(pid);
				dto.setAllFanli(AllFanli);
				dto.setNikeName(nikeName);
				dto.setOneMoney(oneMoney);
				dto.setSum(sum);
				dto.setOnerate(onerate);
				dto.setPnumId(pnumId);
				dto.setQixiaAllMoney(qixiaAllMoney);
				dto.setShijiFanli(shijiFanli);
				dto.setQixiaAndDirect(qixiaAndDirect);
				listDTO.add(dto);
			}
		}
		return listDTO;
	}
	
	
	
	/**
	 * 计算返利比 单位为分
	 * @param money 金额业绩 
	 * @return
	 */
	private long getRate(long money){
		long fanliRate = 0;//返利比  单位为分
		if(money >= 250000){
			fanliRate = 25;//分，实际是25/100
		}else if(money >= 100000){
			fanliRate = 20;
		}else if(money >= 30000){
			fanliRate = 15;
		}else if(money >= 10000){
			fanliRate = 10;
		}else if(money >= 3000){
			fanliRate = 5;
		}
		return fanliRate;
	}



}
