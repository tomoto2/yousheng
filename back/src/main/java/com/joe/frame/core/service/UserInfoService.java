package com.joe.frame.core.service;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.LockModeType;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joe.frame.common.util.DateUtil;
import com.joe.frame.common.util.OrderNum;
import com.joe.frame.common.util.PojoUtils;
import com.joe.frame.common.util.Tools;
import com.joe.frame.core.dto.UserDTO;
import com.joe.frame.core.dto.UserPayDTO;
import com.joe.frame.core.entity.Club;
import com.joe.frame.core.entity.Proxy;
import com.joe.frame.core.entity.Recharge;
import com.joe.frame.core.entity.User;
import com.joe.frame.core.param.PageParam;
import com.joe.frame.core.param.UserInfoParam;
import com.joe.frame.core.repository.AccountRepository;
import com.joe.frame.core.repository.ClubRepository;
import com.joe.frame.core.repository.LoginHistoryRepository;
import com.joe.frame.core.repository.ProxyRepository;
import com.joe.frame.core.repository.RechargeRepository;
import com.joe.frame.core.repository.UserGameHistoryRepository;
import com.joe.frame.core.repository.UsersRepository;
import com.joe.frame.core.trade.entity.OrderStatus;

@Service
@Transactional
public class UserInfoService {

	private static final Logger logger = LoggerFactory.getLogger(UserInfoService.class);
	@Autowired
	private Tools tools;
	@Autowired
	private DateUtil dateUtil;
	@Autowired
	private AccountRepository accountRepository;
	@Autowired
	private ProxyRepository proxyRepository;
	@Autowired
	private UsersRepository usersRepository;
	@Autowired
	private ClubRepository clubRepository;
	@Autowired
	private RechargeRepository rechargeRepository;
	@Autowired
	private LoginHistoryRepository loginHistoryRepository;
	@Autowired
	private UserGameHistoryRepository userGameHistoryRepository;

	/**
	 * 游戏时长超过5分钟的人数
	 * @return
	 */
	public Long getSumbyfivemin(){
		long sum = usersRepository.getSumbyfivemin();
		//		Long sum = userGameHistoryRepository.getSumbyfivemin();
		return sum;
	}



	/**
	 * 查询玩家信息
	 * @param uid 玩家id
	 * @return
	 */
	public User getPlayer(long uid){
		User u = usersRepository.getUserByMunber(uid,LockModeType.PESSIMISTIC_WRITE);//玩家信息
		logger.info("玩家id{},玩家信息为{}",uid,u);
		return u;
	}

	/**
	 * 判断是否是某俱乐部成员
	 * 
	 * @param uid
	 *            代理商id
	 * @param palyerUid
	 *            玩家id/玩家编号
	 *            
	 * @return true 是， false 不是
	 */
	public boolean isClubMember(long palyerUid, String uid) {
		//		String cid = proxyRepository.fingByPid(uid).getCid();// 代理所在的俱乐部id
		String cid = proxyRepository.fingByPid(uid).getNumId();// 代理所在的俱乐部6位数id
		//		User u = usersRepository.find(palyerUid);
		//		User u = usersRepository.getUserMsgByMunber(palyerUid);//玩家信息
		User u = usersRepository.getUserByMunber(palyerUid,LockModeType.PESSIMISTIC_WRITE);//玩家信息
		if("".equals(cid)|| cid == null){//无此俱乐部
			return false;
		}
		if (cid.equals(u.getCid())) {//&& u.getStatus().equals("0")
			logger.info("俱乐部{}存在，{}是此俱乐部成员",cid,u.getNikeName());
			return true;//俱乐部存在，且是此部的成员
		} else{
			//不是本俱乐部成员
			return false;
		}
	}

	/**
	 * 根据编号查询玩家信息
	 * @param number 玩家编号
	 */
	//	public User getUserMsgByMunber(String number) {
	//		return usersRepository.getUserMsgByMunber(number);
	//	}

	/**
	 * 根据编号查询玩家信息
	 * @param number 玩家编号
	 */
	public UserDTO getUserMsgByNum(long number) {
		//		User user=  usersRepository.getUserMsgByMunber(number);
		UserDTO userDto =new UserDTO();
		userDto.setSign("0");
		User user= usersRepository.getUserByMunber(number,LockModeType.PESSIMISTIC_WRITE);
		if(user == null){
			userDto.setSign("3");//无此用户！
			return userDto; 
		}
		if(user !=null){
			if(user.getCid() == null){
				//该用户没有加入任何俱乐部
				userDto.setSign("4");//可以进行充值
			}
			if(user.getName()!= null){
				userDto.setNikeName(user.getName());//昵称
			}
			//	userDto.setUid(user.getUid());
			userDto.setCard(user.getCard());//剩余房卡数量
			userDto.setUid(user.getUid());
			logger.info("根据编号查询玩家信息",userDto);
		}
		return userDto;
	}

	/**
	 * 分页查询查询所有俱乐部成员
	 * 
	 * page从1开始
	 * @param cid
	 *            俱乐部id
	 * @return
	 */
	public List<User> getClumMember(String uid,int pageNo, int size) {
		//		String cid = proxyRepository.find(uid).getCid();//俱乐部id
		String cid = proxyRepository.find(uid).getNumId();//俱乐部6位数id
		List<User> userList = usersRepository.getClumMember(cid, pageNo, size);
		logger.info("俱乐部所有成员",userList);
		return userList;
	}



	/**
	 * 根据编号修改玩家在俱乐部的备注
	 * @param number 玩家编号（用户的id）
	 * @return
	 */
	public void uptRemarks(long number,String remarks) {
		//		User user= usersRepository.getUserMsgByMunber(number);
		//		User user= usersRepository.find(number);
		User user= usersRepository.getUserByMunber(number,LockModeType.PESSIMISTIC_WRITE);
		if(user!= null){
			user.setRemarks(remarks);
		}
		logger.info("{}在俱乐部中的备注修改成功{}",user.getNikeName(),remarks);
	}


	/**
	 * 将玩家移出俱乐部
	 * @param number 玩家编号==uid(玩家id)
	 */
	public void removeClub(long number) {
		User user= usersRepository.getUserByMunber(number,LockModeType.PESSIMISTIC_WRITE);
		logger.info("将玩家所在俱乐部成员人数减去1");
		//根据玩家所在的俱乐部6位编号 查询俱乐部信息
		Club club = clubRepository.getClubByPnum(user.getCid(),LockModeType.PESSIMISTIC_WRITE);
		if(club != null){
			club.setNow((club.getNow()) - 1);//俱乐部人数-1
		}
		logger.info("准备将{}移出俱乐部",number);
		//		User user= usersRepository.getUserMsgByMunber(number);
		if(user!= null ){
			//	user.setStatus("2");//移出俱乐部状态
			user.setCid(null);
		}
		logger.info("将{}移出俱乐部{}成功",user.getName(),club.getName());
	}



	/**
	 * 查询个人信息
	 * @param uid 用户ID
	 * @return
	 */
	public Proxy find(String uid){
		Proxy userInfo = proxyRepository.find(uid);
		return userInfo;
	}

	/**
	 * 查询个人信息
	 * @param uid 用户ID
	 * @return
	 */
	public Proxy findByid(String uid){
		Proxy userInfo = proxyRepository.fingByPid(uid);
		return userInfo;
	}


	/**
	 * 查询代理用户个人信息
	 * @param uid
	 * @return
	 */
	public UserInfoParam getUserInfo(String uid){
		Proxy userInfo = proxyRepository.fingByPid(uid);
		logger.info("代理信息",userInfo);
		UserInfoParam param = PojoUtils.copy(userInfo, UserInfoParam.class);
		param.setPnumId(userInfo.getNumId());//代理编号
		return param;
	}

	/**
	 * 修改用户信息
	 * 昵称（只能修改一次），微信，城市地区，身份证，
	 * @param uid 代理id
	 */
	public void uptUserInfo(UserInfoParam  param,String uid){
		Proxy userInfo = proxyRepository.find(uid);
		if(userInfo != null){
			//设置昵称
			if(param.getNikeName()!= null ){
				userInfo.setNikeName(param.getNikeName());
			}
			//真实姓名，
			if(param.getName()!= null ){
				userInfo.setName(param.getName());
			}
			//设置微信号
			if(param.getWeChat()!= null ){
				userInfo.setWx(param.getWeChat());
			}
			//设置支付宝账号
			if(param.getAlipay()!= null ){
				userInfo.setAlipay(param.getAlipay());
			}
			//设置地区
			if(param.getLocation()!= null ){
				userInfo.setLocation(param.getLocation());
			}
			//设置身份证号
			if(param.getIdCard()!= null ){
				userInfo.setIdCard(param.getIdCard());
			}
			proxyRepository.merge(userInfo);
			logger.info("用户信息修改成功");
		}
	}




	//----------------------------管理员操作0------------------------------


	/**
	 * 分页查询所有玩家列表 page从1开始
	 * @param pageParam 分页数据
	 * @return
	 */
	public List<UserInfoParam> getAllPlayer(PageParam pageParam) {
		List<User> list = usersRepository.getAllPlayer(pageParam.getSize(),pageParam.getPageNo());
		List<UserInfoParam> paramList = PojoUtils.copy(list, UserInfoParam.class);
		for(UserInfoParam user :paramList){
			if(user.getCid() != null){
				Club club = clubRepository.find(user.getCid());
				if(club != null){
					String clubname = club.getName();
					user.setClubName(clubname);
				}
			}
		}
		logger.info("分页查询所有玩家列表{}",list);
		return paramList;
	}

	/**
	 * 查询所有玩家总数
	 * @return
	 */
	public Long getAllPlayerSum() {
		Long sum = usersRepository.countAll();
		return sum;
	}



	/**
	 * 分页查询所有代理列表 page从1开始
	 * @param pageParam 分页数据
	 * @return
	 */
	public ArrayList<UserInfoParam> getAllProxy(PageParam pageParam) {
		List<Proxy> list =	proxyRepository.getAllProxy(pageParam.getSize(),pageParam.getPageNo());
		List<UserInfoParam> paramList = PojoUtils.copy(list, UserInfoParam.class);
		ArrayList<UserInfoParam> newList = new ArrayList<UserInfoParam>();
		//		总充值金额，总充值房卡数量
		for(UserInfoParam param:paramList){
			param.setBalance(param.getBalance()/100);//单位转为元
			
			if(param.getPid() != null){
				String newMonth = dateUtil.getFormatDate("yyyyMM");
				long money = rechargeRepository.getBuyByMonthOrDay(param.getPid(),newMonth);//购卡金额
				long sum = rechargeRepository.getSumByMonthOrDay(param.getPid(),newMonth);//购卡数量
				param.setAllchongzhi(money);
				param.setAllgoumai(sum);
			}
		}
		newList.addAll(paramList);
		return newList;
	}


	/**
	 * 查询所有代理总数
	 * @return
	 */
	public long getAllProxySum() {
		long sum = proxyRepository.countAll();
		return sum;
	}


	/**
	 * 通过邀请码查询代理信息
	 * @param anumId 邀请码 = 代理编号
	 * @return
	 */
	public Proxy getAgent(String anumId) {
		Proxy proxy = proxyRepository.fingByCode(anumId);
		return proxy;
	}


	/**
	 * 解散俱乐部--清空所有玩家
	 * @param cid 俱乐部id
	 * @return >0成功
	 */
	public long unOpenClub(String cid) {
		Club club = clubRepository.find(cid);
		if(club == null){
			return -1; 
		}
		//		清空--user --cid
		List<User> user = usersRepository.getUserByCid(cid);
		for(User u:user){
			u.setCid(null);
			logger.info("清空俱乐部{}所有玩家{}成功",cid,user);
		}
		Proxy proxy = proxyRepository.getProxyByCid(cid);
		if(proxy!= null ){
			proxy.setCid(null);
			logger.info("清空俱乐部{}的代理{}成功",cid,proxy);
		}

		//		清空--proxy --cid
		//删除俱乐部
		long flag  = clubRepository.removeClub(cid);
		return flag;
	}




	/**
	 * 设置玩家账号的开启禁止
	 * 
	 * @param uid 玩家id
	 * @param canuse true为禁止，false为开启
	 */
	public void setCanuse(long uid, String canuse) {
		boolean flag = false;
		if(canuse.equals("true")){
			flag = true;
		}else if(canuse.equals("false")){
			flag = false;
		}
		//		User user  = usersRepository.find(uid,LockModeType.PESSIMISTIC_WRITE);
		User user  = usersRepository.getUserByMunber(uid,LockModeType.PESSIMISTIC_WRITE);
		logger.info("设置账号的登录限制，玩家用户{}的登录账号为{}",uid,user);
		user.setRemove(flag);
		logger.info("成功设置账号的登录限制为{}",canuse);
	}



	/**
	 *  当天登录过游戏的人数
	 * @return
	 */
	public Long getSumCurrentDay() {
		String curDay = dateUtil.getFormatDate("yyyy-MM-dd");//今天
		return usersRepository.getSumCurrentDay(curDay);
		//		return loginHistoryRepository.getSumCurrentDay();
	}


	/**
	 * 玩家自己充值房卡
	 * 若此玩家是自由玩家则收益归平台
	 * 若此玩家不是自由玩家，将受益给自己所在的俱乐部
	 * @param uid 玩家id
	 * @param num 充值数量
	 * @return 订单详情
	 */
	public UserPayDTO addUserOrder(long uid, int num) {
		User user = usersRepository.getUserByMunber(uid, LockModeType.PESSIMISTIC_WRITE);
		//判断玩家是否存在
		if(user != null){
			Recharge recharge = new Recharge();
			logger.info("开始生成订单信息");
			String id = tools.createUUID();
			recharge.setId(id);//实际使用
			String orderNum = OrderNum.getOrderNo();// 订单编号
			recharge.setOrderNum(orderNum);// 订单编号
			recharge.setOrderStatus(OrderStatus.CREATE);// 未支付状态(订单创建)
			recharge.setStatus("1");//订单创建  未支付
			recharge.setPname("玩家自定义");
			recharge.setNumber(num);
			recharge.setMoney(num * 400);//数据库单位为分 4元/张
			recharge.setCreateTime(dateUtil.getFormatDate("yyyyMMddHHmmss"));
			recharge.setPayWay("wechatpay");//支付方式
			recharge.setUid(String.valueOf(uid));//玩家id
			recharge.setExpire("");
			recharge.setPayTime("");
			rechargeRepository.persist(recharge);
			logger.info("生成订单{}",recharge);
			if(user.getCid()!= null){
				//该玩家已经加入了俱乐部
				logger.info("该玩家{}已经加入了俱乐部{}",user,user.getCid());
				//	user.getCid()  6位编号，邀请码=俱乐部编号=代理编号
				String cid = proxyRepository.fingByCode(user.getCid()).getPid();// 代理32位id
				recharge.setAgentUid(cid);//设置代理id
			}else{
				//自由玩家
				logger.info("该玩家{}是自由玩家",user);
				recharge.setAgentUid(null);//
			}
			UserPayDTO payDto = PojoUtils.copy(recharge, UserPayDTO.class);
			payDto.setUid(user.getUid());
			payDto.setName(user.getName());
			payDto.setSmoney(String.valueOf((double) payDto.getMoney() / 100));//返回前端单位为元
			payDto.setNumbers(payDto.getNumber());
			String openId = usersRepository.getOpenid(String.valueOf(uid));
			payDto.setOpenId(openId);//此openid不能用于支付，需要重新获取
			return payDto;
		}
		return null;//玩家不存在，返回null
	}



	/**
	 * 保存用户的openid
	 * @param uid 玩家id
	 * @param openid 玩家的openid
	 */
	public void uptUserOpenid(long uid, String openid) {
		User user = usersRepository.getUserByMunber(uid, LockModeType.PESSIMISTIC_WRITE);
		user.setOpenId(openid);
		logger.info("设置玩家用户{}的openid{}成功",uid,openid);
	}



	/**
	 * 查询玩家当天注册人数
	 * @return
	 */
	public long getPlayerSumDay() {
		String curDay = dateUtil.getFormatDate("yyyy-MM-dd");//今天
		long sum = usersRepository.getPlayerSumDay(curDay);
		return sum;
	}



}










