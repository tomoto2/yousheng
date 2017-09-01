package com.joe.frame.core.repository;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.LockModeType;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.joe.frame.common.util.DateUtil;
import com.joe.frame.core.database.AbstractRepository;
import com.joe.frame.core.entity.Proxy;
import com.joe.frame.core.entity.QProxy;

/**
 * 代理商
 * 
 * @author lpx
 *
 *         2017年7月21日
 */

@Repository
@Transactional(TxType.SUPPORTS)
public class ProxyRepository extends AbstractRepository<Proxy, String> {

	QProxy e = QProxy.proxy;

	@Autowired
	private DateUtil dateUtil;



	/**
	 * 根据手机号查询代理信息
	 * @param phone
	 * @return
	 */
	public Proxy getUserByPhone(String phone){
		return selectFrom().where(e.phone.eq(phone)).fetchFirst();
	}


	/**
	 * 获取uid下的直属下级
	 * 
	 * @param uid
	 *            代理商id
	 * @return
	 */
	public List<Proxy> getDirectChild(String uid) {
		Proxy info = find(uid);
		if (info == null) {
			return Collections.emptyList();
		}
		logger.info("{}", info);
		Set<Proxy> set = info.getChilds();
		//				Set<Proxy> set = null;
		List<Proxy> list = new ArrayList<Proxy>(set.size());
		list.addAll(set);
		logger.info("用户{}的直属下级为：{}", uid, list);
		return list;
	}

	/**
	 * 获取直属下级数量
	 * 
	 * @param uid
	 *            用户UID
	 * @return
	 */
	public long getDirectChildCount(String uid) {
		Proxy info = find(uid);
		if (info == null) {
			return 0;
		}
		logger.info("{}", info);
		Set<Proxy> set = info.getChilds();
		//		Set<Proxy> set = null;
		return set.size();
	}

	/**
	 * 获取所有下级
	 * 
	 * @param uid
	 * @return
	 */
	public List<Proxy> getAll(String uid) {
		List<Proxy> list = getDirectChild(uid);
		if (list.isEmpty()) {
			return list;
		}
		for (int i = 0; i < list.size(); i++) {
			list.addAll(getDirectChild(list.get(i).getPid()));
		}
		logger.info("用户{}的所有下级为：{}", uid, list);
		return list;
	}


	/**
	 * 获取所有下级的数量
	 * 
	 * @param uid
	 * @return
	 */
	public long getAllSum(String uid) {
		List<Proxy> list = getDirectChild(uid);
		if (list.isEmpty()) {
			return 0;
		}
		for (int i = 0; i < list.size(); i++) {
			list.addAll(getDirectChild(list.get(i).getPid()));
		}
		logger.info("用户{}的所有下级为：{}", uid, list);
		return list.size();
	}


	/**
	 *  获取某个日期段区域内新增的旗下代理数
	 * @param uid 父级id
	 * @param beginTime 日期开始
	 * @param endTime 日期结束
	 * @return
	 * @throws ParseException
	 */
	public long getTeamSumByRegion(String uid,String beginTime,String endTime) throws ParseException{
		List<Proxy> listSum = getAll(uid);//获取所有下级
		List<Proxy> newListSum = new ArrayList<Proxy>();
		for(Proxy p:listSum){
			boolean flag = dateUtil.compareInTime(beginTime,endTime,p.getInviteTime());
			if(flag){
				newListSum.add(p);
			}
		}
		return newListSum.size();
	}


	/**
	 * 获取某日期之前的所有下级
	 * 
	 * @param uid  用户id 该用户下级
	 * @param mou 格式：yyyy-MM-dd HH:mm:ss
	 * @return
	 * @throws ParseException 
	 */
	public List<Proxy> getAllNew(String uid,String mou) throws ParseException {
		List<Proxy> list = getDirectChildNew(uid,mou);
		if (list.isEmpty()) {
			return list;
		}
		for (int i = 0; i < list.size(); i++) {
			list.addAll(getDirectChildNew(list.get(i).getPid(),mou));
		}
		logger.info("用户{},{}时间新增的所有下级为：{}", uid,mou,list);
		return list;
	}





	/**
	 * 获取某日期之前的所有下级数量
	 * 
	 * @param uid  用户id 该用户下级
	 * @param mou 格式：yyyy-MM-dd HH:mm:ss
	 * @return
	 * @throws ParseException 
	 */
	public long getAllNewSum(String uid,String mou) throws ParseException {
		List<Proxy> list = getDirectChildNew(uid,mou);
		if (list.isEmpty()) {
			return 0;
		}
		for (int i = 0; i < list.size(); i++) {
			list.addAll(getDirectChildNew(list.get(i).getPid(),mou));
		}
		logger.info("用户{},{}时间新增的所有下级为：{}", uid,mou,list);
		return list.size();
	}




	/**
	 * 获取某个日期之前的旗下直推代理
	 * 
	 * @param uid  该用户
	 * @param mou 格式：yyyy-MM-dd HH:mm:ss
	 * @return
	 * @throws ParseException 
	 */
	public List<Proxy> getDirectChildNew(String uid,String mou) throws ParseException {
		Proxy info = find(uid);
		if (info == null) {
			return Collections.emptyList();
		}
		logger.info("{}", info);
		Set<Proxy> set = info.getChilds();
		//		Set<Proxy> set = null;
		Set<Proxy> setNew = new HashSet<Proxy>();
		for (Proxy user : set) {
			String d1 = mou;//某个时间
			String d2 = user.getInviteTime();//注册时间
			boolean flag = dateUtil.compareTime(d1,d2);
			if(flag){//筛选出来本日(包括本日)之前注册的用户
				setNew.add(user);
			}
		}
		List<Proxy> list = new ArrayList<Proxy>(setNew.size());
		list.addAll(setNew);
		logger.info("用户{}的新增下级为：{}", uid, list);
		return list;
	}


	/**
	 * 获取某个日期之前的旗下直推代理数量
	 * 
	 * @param uid  该用户
	 * @param mou 格式：yyyy-MM-dd HH:mm:ss
	 * @return
	 * @throws ParseException 
	 */
	public long getDirectChildNewSum(String uid,String mou) throws ParseException {
		Proxy info = find(uid);
		if (info == null) {
			return 0;
		}
		logger.info("{}", info);
		Set<Proxy> set = info.getChilds();
		//		Set<Proxy> set = null;
		Set<Proxy> setNew = new HashSet<Proxy>();
		for (Proxy user : set) {
			String d1 = mou;//某个时间
			String d2 = user.getInviteTime();//注册时间
			boolean flag = dateUtil.compareTime(d1,d2);
			if(flag){//筛选出来本日(包括本日)之前注册的用户
				setNew.add(user);
			}
		}
		return setNew.size();
	}



	/**
	 *  获取某月内新增的旗下代理
	 * @param uid 父级id
	 * @param monthTime 某月
	 * @return
	 * @throws ParseException
	 */
	public List<Proxy> getChildBuyByMonth(String uid,String monthTime) throws ParseException{
		List<Proxy> list = getAll(uid);//获取所有下级
		List<Proxy> newList = new ArrayList<Proxy>();
		for(Proxy p:list){
			boolean flag = dateUtil.compareTime1(monthTime,p.getInviteTime());
			if(flag){
				newList.add(p);
			}
		}
		return newList;
	}





	/**
	 * 获取uid下的某月新增的直属下级
	 * 
	 * @param uid
	 *            代理商id
	 *            @param monthTime 某月
	 * @return
	 * @throws ParseException 
	 */
	public List<Proxy> getDirectChildBymonth(String uid,String monthTime) throws ParseException {
		List<Proxy> list= 	getDirectChild(uid);//获取所有直属下级
		List<Proxy> newList = new ArrayList<Proxy>();
		for(Proxy p:list){
			boolean flag = dateUtil.compareTime1(monthTime,p.getInviteTime());
			if(flag){
				newList.add(p);
			}
		}
		return newList;
	}


	/**
	 * 通过邀请码/代理6位编号 查询代理信息
	 * @param parentId 邀请码
	 * @return
	 */
	public Proxy fingByCode(String parentId) {
		logger.info("通过邀请码{}查询代理信息",parentId);
		Proxy proxy = selectFrom().where(e.inviteCode.eq(parentId)).fetchFirst();
		if(proxy != null){
			logger.info("通过邀请码{}查询代理信息为",parentId,proxy);
			return proxy;
		}
		return null;
	}

	/**
	 * 通过邀请码/代理6位编号查询代理信息 
	 * @param fromAgentNum  6位编号
	 * @param lock 
	 * @return
	 */
	public Proxy findByNumId(String fromAgentNum, LockModeType lock) {
		Proxy proxy = selectFrom().where(e.numId.eq(fromAgentNum)).setLockMode(lock).fetchFirst();
		return proxy;
	}



	/**
	 * 通过id查询代理信息
	 * @param uid 代理id（pid）
	 * @return
	 */
	public Proxy fingByPid(String uid) {
		return selectFrom().where(e.pid.eq(uid)).fetchFirst();
	}



	/**
	 * 分页查询所有代理信息 page从1开始
	 * @param size
	 * @param pageNo
	 * @return
	 */
	public List<Proxy> getAllProxy(int size,int pageNo) {
		return selectFrom().orderBy(e.inviteTime.desc()).offset((pageNo - 1) * size).limit(size).fetch();
	}


	/**
	 * 通过俱乐部id查询代理信息
	 * @param cid 俱乐部id
	 * 
	 * @return
	 */
	public Proxy getProxyByCid(String cid) {
		Proxy proxy = selectFrom().where(e.cid.eq(cid)).fetchFirst();
		return proxy;
	}


	/**
	 * 查询某月新注册的代理总数
	 * @param thisMonth yyyy-MM
	 * @return
	 */
	public long findRegister(String thisMonth) {
		//		e.inviteTime 格式：2017-08-07 19:47:55
		long sum = selectFrom().where(e.inviteTime.substring(0,7).eq(thisMonth)).fetchCount();
		logger.info("某月{}新注册的代理总数为{}",thisMonth,sum);
		return sum;
	}


	/**
	 * 查询某月区间新注册的代理总数
	 * @param beginTime ~~ endTime yyyy-MM
	 * @return
	 */
	//	e.inviteTime 格式：2017-08-07 19:47:55
	//		be = e.createTime.between(beginTime, endTime);
	public long findRegisterByTwoTime(String beginTime, String endTime) {
		long sum = selectFrom().where(e.inviteTime.substring(0,7).between(beginTime, endTime)).fetchCount();
		logger.info("某月{}到{}月新注册的代理总数为{}",beginTime, endTime,sum);
		return sum;
	}


	/**
	 * 获取某日期之前的所有代理
	 * @param thisMonth 某月 yyyy-MM
	 * @return
	 */
	public List<Proxy> findAllByTime(String thisMonth) {
		List<Proxy> list = selectFrom().where(e.inviteTime.substring(0,7).loe(thisMonth)).fetch();
		return list;
	}



	/**
	 * 获取某日期之间的所有代理
	 * @param beginTime 某月 yyyy-MM
	 * @return
	 */
//	public List<Proxy> findAllByTwoTime(String beginTime,String endTime ) {
//		List<Proxy> list = selectFrom().where(e.inviteTime.goe(beginTime).and(e.inviteTime.loe(endTime))).fetch();
//		return list;
//	}


	//查询指定用户的等级




}
