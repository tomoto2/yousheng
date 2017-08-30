package com.joe.frame.core.repository;

import java.util.List;

import javax.persistence.LockModeType;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.springframework.stereotype.Repository;

import com.joe.frame.core.database.AbstractRepository;
import com.joe.frame.core.entity.QLoginHistory;
import com.joe.frame.core.entity.QUser;
import com.joe.frame.core.entity.QUserGameHistory;
import com.joe.frame.core.entity.QWxLogin;
import com.joe.frame.core.entity.User;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;

/**
 * 玩家用户
 * 
 * @author lpx
 *
 *         2017年7月21日
 */
@Repository
@Transactional(TxType.SUPPORTS)
public class UsersRepository extends AbstractRepository<User, String> {
	QUser e = QUser.user;
	QUserGameHistory userGameHistory = QUserGameHistory.userGameHistory;
	QLoginHistory loginHistory = QLoginHistory.loginHistory;
	QWxLogin wxLogin = QWxLogin.wxLogin;

	/**
	 * 根据玩家的编号查询玩家信息
	 * 
	 * @param number
	 *            玩家编号（6位数，以3开头）
	 * @return
	 */
	//	public User getUserMsgByMunber(String number) {
	//		return selectFrom().where(e.number.eq(number)).fetchFirst();
	//	}

	public String getOpenid(String uid){
		Tuple t = select(wxLogin.oppenid).from(wxLogin).where(wxLogin.uid.eq(uid)).fetchFirst();
		if(t == null){
			return null;
		}
		return t.get(wxLogin.oppenid);
	}


	/**
	 * 根据玩家的编号查询玩家信息
	 * 
	 * @param number
	 *            玩家编号（6位数，以3开头）
	 * @return
	 */
	public User getUserByMunber(long number , LockModeType lock) {
		return selectFrom().where(e.uid.eq(number)).setLockMode(lock).fetchFirst();
	}


	/**
	 * 根据玩家的所在的俱乐部id查询玩家信息
	 * 
	 * @param cid
	 *           俱乐部id
	 * @return
	 */
	public List<User> getUserByCid(String cid) {
		return selectFrom().where(e.cid.eq(cid)).fetch();
	}


	/**
	 * 分页查询查询所有俱乐部成员
	 * 
	 * page从1开始
	 * @param cid  6位数cid
	 *            俱乐部id
	 *            e.status.eq("0") == 是在本俱乐部
	 * @return
	 */
	public List<User> getClumMember(String cid, int pageNo, int size) {
		return selectFrom().where(e.cid.eq(cid)).orderBy(e.inviteTime.desc()).offset((pageNo - 1) * size).limit(size).fetch();
	}

	/**
	 * 分页查询所有玩家信息列表 page从1开始
	 * @param size 每页大小
	 * @param pageNo 当前页
	 * @return
	 */
	public List<User> getAllPlayer(int size, int pageNo) {
		//		return selectFrom().orderBy(e.inviteTime.desc()).offset((pageNo - 1) * size).limit(size).fetch();
		return selectFrom().orderBy(e.createTime.desc()).offset((pageNo - 1) * size).limit(size).fetch();
	}


	/**
	 * 根据俱乐部id，查询该俱乐部人数
	 * @param cid 俱乐部id
	 * @return
	 */
	public long findClubSum(String cid) {
		long sum  = selectFrom().where(e.cid.eq(cid)).fetchCount();
		return sum ;
	}


	/**
	 * 
	 * 查询某月新注册的玩家总数
	 * @param thisMonth 某月：yyyy-MM
	 * @return
	 */
	public long findRegister(String thisMonth) {
		//		e.inviteTime 格式：2017-08-07 21:31:43
		//		long sum  = selectFrom().where(e.inviteTime.substring(0,7).eq(thisMonth)).fetchCount();
		long sum  = selectFrom().where(e.createTime.substring(0,7).eq(thisMonth)).fetchCount();
		logger.info("某月{}新注册的玩家总数为{}",thisMonth,sum);
		return sum;
	}


	/**
	 * 
	 * 查询某月区间新注册的玩家总数
	 * @param beginTime ~~ endTime 某月：yyyy-MM
	 * @return
	 */
	public long findRegisterByTwoTime(String beginTime, String endTime) {
		long sum = selectFrom().where(e.createTime.substring(0,7).between(beginTime, endTime)).fetchCount();
		logger.info("某月{}到{}月新注册的玩家总数为{}",beginTime, endTime,sum);
		return sum;
	}


	/**
	 * 统计游戏时间长度超过5分钟的人数
	 * @return
	 */
	public long getSumbyfivemin() {
		return select().from(userGameHistory).groupBy(userGameHistory.uid).fetchCount();
	}


	/**
	 * 当天登陆过游戏的人数
	 * @param curDay 当前日 yyyy-MM-dd
	 * 2017-08-25 01:42:29 312 -- createTime
	 * @return
	 */
	public long getSumCurrentDay(String curDay) {
		return select().from(loginHistory).where(loginHistory.createTime.substring(0,10).eq(curDay)).groupBy(loginHistory.uid).fetchCount();
	}


	/**
	 * 当天注册玩家人数
	 * @param curDay 当前日 yyyy-MM-dd
	 * @return
	 */
	public long getPlayerSumDay(String curDay) {
		return 	selectFrom().where(e.createTime.substring(0,10).eq(curDay)).fetchCount();
	}
	
	
	
	
}