package com.joe.frame.core.repository;

import java.text.ParseException;
import java.util.List;

import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.springframework.stereotype.Repository;

import com.joe.frame.core.database.AbstractRepository;
import com.joe.frame.core.entity.PlayerRecharge;
import com.joe.frame.core.entity.QPlayerRecharge;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;

@Repository
@Transactional(TxType.SUPPORTS)
public class PlayerRechargeRepository extends AbstractRepository<PlayerRecharge, String>{

	QPlayerRecharge e = QPlayerRecharge.playerRecharge;

	/**
	 * 获取代理商给用户的充值记录 分页查询 page从1开始
	 * page 从1开始
	 * 
	 * @param uid 代理商id
	 * @param pageNo 当前页
	 * @param size 每页大小
	 */
	public List<PlayerRecharge> findList(String uid,int pageNo,int size) {
		List<PlayerRecharge> list= selectFrom().where(e.agentUid.eq(uid)).orderBy(e.datetime.desc()).offset((pageNo-1)*size).limit(size).fetch();
		return list;
	}

	/**
	 * 获取代理商给用户的充值记录总数
	 * 
	 * @param uid 代理商id
	 */
	public long findListSize(String uid) {
		long size= selectFrom().where(e.agentUid.eq(uid)).fetchCount();
		return size;
	}


	/**
	 * 获取某代理某段时间内，卖掉的房卡的数量
	 * @param uid 代理id
	 * @param beginTime 开始日期 2017-01-02 12:00:00  或者  yyyy-MM
	 * @param endTime 截止日期2017-01-02 12:00:00 或者  yyyy-MM
	 * @return
	 * .and(e.datetime.lt(endTime).and(e.datetime.gt(beginTime)))
	 * @throws ParseException 
	 */
	public long getYeji(String uid, String beginTime, String endTime){
		beginTime = formatDate(beginTime);
		endTime = formatDate(endTime);
		BooleanExpression be = null;
		logger.info("开始查询{}在{}和{}之间的购卡数量",uid,beginTime,endTime);
		if(beginTime.length() == 6){
			be = e.datetime.substring(0,6).between(beginTime, endTime); 
		}
		else{
			be = e.datetime.between(beginTime, endTime);
		}
		long sum = 0;
		Tuple tuple =  select(e.cartSum.sum()).from(e).where(e.agentUid.eq(uid).and(be)).fetchFirst();
		if(tuple!= null&& tuple.get(e.cartSum.sum())!= null && tuple.get(e.cartSum.sum())!= 0){
			sum = tuple.get(e.cartSum.sum());
		}
		return sum;
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
