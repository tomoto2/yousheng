package com.joe.frame.core.repository;

import java.util.List;

import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.springframework.stereotype.Repository;

import com.joe.frame.core.database.AbstractRepository;
import com.joe.frame.core.entity.QRecharge;
import com.joe.frame.core.entity.Recharge;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.StringExpression;

/**
 * 代理商购买房卡
 * @author lpx
 *
 * 2017年7月21日
 */

@Repository
@Transactional(TxType.SUPPORTS)
public class RechargeRepository extends AbstractRepository<Recharge, String>{
	QRecharge e = QRecharge.recharge;



	/**
	 * 根据订单编号查询 订单信息
	 * @param orderNum 编号
	 * @return
	 */
	public Recharge judgehasOrder(String orderNum) {
		Recharge recharge = selectFrom().where(e.orderNum.eq(orderNum)).fetchFirst();
		return recharge;
	}



	/**
	 * 获取代理商个人的充值记录 分页查询 page从1开始
	 * page 从1开始
	 * 
	 * @param uid 代理商id
	 * @param pageNo 当前页
	 * @param size 每页大小
	 */
	public List<Recharge> findList(String uid,int pageNo,int size) {
		//		List<Recharge> list= selectFrom().where(e.agentUid.eq(uid)).orderBy(e.createTime.desc()).offset((pageNo-1)*size).limit(size).fetch();

		//只显示支付成功的
		List<Recharge> list= selectFrom().where(e.agentUid.eq(uid).and(e.status.eq("2"))).orderBy(e.createTime.desc()).offset((pageNo-1)*size).limit(size).fetch();

		//		for(Recharge param :list){
		//			param.setMoney(param.getMoney()/100);//这样做直接就改变数据库了，是错误的
		//		}
		return list;
	}



	/**
	 * 获取代理商个人的充值记录总数
	 * @param uid 代理id
	 * @return
	 */
	public long getAgentRechargeListSum(String uid) {
		//		long sum= selectFrom().where(e.agentUid.eq(uid)).fetchCount();
		//只显示支付成功的
		long sum= selectFrom().where(e.agentUid.eq(uid).and(e.status.eq("2"))).fetchCount();
		return sum;
	}

	/**
	 * 获取代理商个人的充值记录 分页查询 page从1开始
	 * page 从1开始
	 * 
	 * @param uid 代理商id
	 * @param searchTime 检索时间 2017-01
	 * @param pageNo 当前页
	 * @param size 每页大小
	 */
	public List<Recharge> findDayList(String uid,String searchTime,int pageNo,int size) {
		searchTime = formatDate(searchTime);
		List<Recharge> list= selectFrom().where(e.agentUid.eq(uid).and(e.status.eq("2")).and(e.createTime.substring(0,8).eq(searchTime))).orderBy(e.createTime.desc()).offset((pageNo-1)*size).limit(size).fetch();
		//		for(Recharge param :list){
		//			param.setMoney(param.getMoney()/100);
		//		}
		return list;
	}


	/**
	 * 获取代理商个人的充值记录总数
	 */
	public long findDayListSum(String uid,String searchTime) {
		searchTime = formatDate(searchTime);
		long sum = selectFrom().where(e.agentUid.eq(uid).and(e.createTime.substring(0,8).eq(searchTime))).fetchCount();
		return sum;
	}



	/**
	 * 获取某段时间内，代理自己购买的房卡数量
	 * .and(e.orderStatus.eq("2"))
	 * @param uid 代理id
	 * @param beginTime 开始时间  yyyy-MM-dd HH:mm:ss  或者 yyyy-MM
	 * @param endTime 结束时间   yyyy-MM-dd HH:mm:ss  或者 yyyy-MM
	 * status == 2,已支付成功
	 * 需要补充状态
	 *.and(e.createTime.lt(endTime).and(e.createTime.gt(beginTime))
	 * @return
	 */
	public long getBuy(String uid, String beginTime, String endTime) {
		beginTime = formatDate(beginTime);
		endTime = formatDate(endTime);
		BooleanExpression be = null;
		logger.info("开始查询{}在{}和{}之间的购卡数量",uid,beginTime,endTime);
		if(beginTime.length() == 6){
			be = e.createTime.substring(0,6).between(beginTime, endTime); 
		}
		else{
			be = e.createTime.between(beginTime, endTime);
		}
		
		Tuple tuple =  select(e.number.sum()).from(e).where(e.agentUid.eq(uid).and(e.status.eq("2")).and(be)).fetchFirst();
		if(tuple!= null &&tuple.get(e.number.sum())!= null && tuple.get(e.number.sum())!= 0){
			return  tuple.get(e.number.sum());
		}else{
			return 0;
		}
	}

	
	/**
	 * 查询某段时间内，代理个人购卡的总金额 
	 * @param uid 代理id
	 * @param beginTime
	 *            开始时间 yyyy-MM-dd HH:mm:ss 或者 yyyy-MM
	 * @param endTime
	 *            截止时间yyyy-MM-dd HH:mm:ss 或者 yyyy-MM
	 *            .and(e.status.eq("2"))
	 *            需要补充状态
	 * @return
	 */
	public long getBuyByBegin(String uid, String beginTime ,String endTime) {
		beginTime = formatDate(beginTime);
		endTime = formatDate(endTime);
		BooleanExpression be = null;
		logger.info("开始查询{}在{}和{}之间的购卡总金额",uid,beginTime,endTime);
		if(beginTime.length() == 6){
			be = e.createTime.substring(0,6).between(beginTime, endTime); 
		}
		else{
			be = e.createTime.between(beginTime, endTime);
		}
		Tuple tuple =  select(e.money.sum()).from(e).where(e.agentUid.eq(uid).and(e.status.eq("2")).and(be)).groupBy(e.agentUid).fetchFirst();
		if(tuple!= null && tuple.get(e.money.sum())!= null && tuple.get(e.money.sum())!= 0){
			return  (tuple.get(e.money.sum())/100);
		}else{
			return 0;
		}
	}



	/**
	 * 查询某月或者某日，代理个人购卡的总金额 (* 2017-02-01 12:30:11，   日 0--10，月 0--7)
	 * @param uid 代理id
	 * @param monthDate 具体月份或者日  格式：yyyy-MM 或者yyyy-MM-dd（每日报表）
	 * 
	 * 需要补充状态
	 * @return 返回的金额单位是元
	 */
	public long getBuyByMonthOrDay(String uid, String monthDate) {
		BooleanExpression be = null;
		StringExpression be1 = null;
		logger.info("开始查询{}在{}的购卡总金额",uid,monthDate);
		monthDate = formatDate(monthDate);
		if(monthDate.length() == 6){
			be1 = e.createTime.substring(0,6);
			be = e.createTime.substring(0,6).eq(monthDate); 
		}
		else if(monthDate.length() == 8){
			be1 =  e.createTime.substring(0,8);
			be = e.createTime.substring(0,8).eq(monthDate);
		}
		Tuple tuple =  select(e.money.sum()).from(e).where(e.agentUid.eq(uid).and(e.status.eq("2")).and(be)).groupBy(be1).fetchFirst();//.and(e.status.eq("2"))
		if (tuple != null && tuple.get(e.money.sum()) != null && tuple.get(e.money.sum()) != 0) {
			logger.info("查询到{}在{}的购卡总金额{}分",uid,monthDate,tuple.get(e.money.sum()));
			return (tuple.get(e.money.sum())/100);
		} else {
			return 0;
		}
	}

	
	
	
	/**
	 * 查询某月或者某日，代理个人购卡的总数量
	 * @param uid 代理id
	 * @param monthDate 具体月份或者日  格式：yyyy-MM 或者yyyy-MM-dd 或者 yyyyMM 或者yyyyMMdd
	 * 
	 * 需要补充状态
	 * @return 返回的金额单位是元
	 */
	public long getSumByMonthOrDay(String uid, String monthDate) {
		BooleanExpression be = null;
		StringExpression be1 = null;
		logger.info("开始查询{}在{}的购卡总数",uid,monthDate);
		monthDate = formatDate(monthDate);
		if(monthDate.length() == 6){
			be1 = e.createTime.substring(0,6);
			be = e.createTime.substring(0,6).eq(monthDate); 
		}
		else if(monthDate.length() == 8){
			be1 =  e.createTime.substring(0,8);
			be = e.createTime.substring(0,8).eq(monthDate);
		}
		Tuple tuple =  select(e.number.sum()).from(e).where(e.agentUid.eq(uid).and(e.status.eq("2")).and(be)).groupBy(be1).fetchFirst();//.and(e.status.eq("2"))
		if (tuple != null && tuple.get(e.number.sum()) != null && tuple.get(e.number.sum()) != 0) {
			logger.info("查询到{}在{}的购卡总数{}",uid,monthDate,tuple.get(e.number.sum()));
			return (tuple.get(e.number.sum()));
		} else {
			return 0;
		}
	}
	



	/**
	 * 查询历来代理个人购卡的总金额 
	 * @param uid 代理id
	 * 需要补充状态
	 * @return
	 */
	public long getBuy(String uid) {
		Tuple tuple =  select(e.money.sum()).from(e).where(e.agentUid.eq(uid).and(e.status.eq("2"))).groupBy(e.agentUid).fetchFirst();//.and(e.status.eq("2"))
		if (tuple != null && tuple.get(e.money.sum()) != null && tuple.get(e.money.sum()) != 0) {
			return (tuple.get(e.money.sum())/100);
		} else {
			return 0;
		}
	}

	/**
	 * 获取历来代理自己购买的房卡数量
	 * @param uid 代理id
	 * status == 2,已支付成功
	 * .and(e.status.eq("2"))
	 * 需要补充状态
	 * @return
	 */
	public long getBuySum(String uid) {
		Tuple tuple =  select(e.number.sum()).from(e).where(e.agentUid.eq(uid).and(e.status.eq("2"))).groupBy(e.agentUid).fetchFirst();
		if(tuple!= null &&tuple.get(e.number.sum())!= null && tuple.get(e.number.sum())!= 0){
			return  tuple.get(e.number.sum());
		}else{
			return 0;
		}
	}



	/**
	 * 将yyyy-MM-dd HH:mm:ss 改为yyyyMMddHHmmss
	 * @param dates
	 * @return
	 */
	public String formatDate(String dates){
		dates = dates.replace("-", "");
		dates = dates.replace(":", "");
		dates = dates.replace(" ", "");
		return dates;
	}



}
