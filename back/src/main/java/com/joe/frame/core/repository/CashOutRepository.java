package com.joe.frame.core.repository;

import java.util.List;

import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.springframework.stereotype.Repository;

import com.joe.frame.core.database.AbstractRepository;
import com.joe.frame.core.entity.CashOut;
import com.joe.frame.core.entity.QCashOut;

/**
 * 代理商钱包记录
 * @author lpx
 *
 * 2017年7月21日
 */

@Repository
@Transactional(TxType.SUPPORTS)
public class CashOutRepository extends AbstractRepository<CashOut, String>{

	QCashOut e = QCashOut.cashOut;


	/**
	 * 查询代理商的提现操作记录（提现）
	 * page从1开始
	 * 
	 * @param uid 代理商id
	 * @param pageNo 当前页 
	 * @param size 每页大小
	 * @return 所有记录
	 */
	public List<CashOut> getAgentCashOut(String uid, int pageNo, int size) {
		List<CashOut> list = selectFrom().where(e.uid.eq(uid)).offset((pageNo-1)*size).limit(size).fetch();
		return list;
	}

	/**
	 * 查询代理商的提现操作记录总数
	 * @param uid 代理商id
	 */
	public long getAgentCashOutSum(String uid) {
		long sum  = selectFrom().where(e.uid.eq(uid)).fetchCount();
		return sum;
	}



	/**
	 * 查询代理本月的提现记录
	 * @param uid 代理id
	 * @param formatDate 本月
	 * @return
	 */
	public CashOut isCashOutThisMonth(String uid, String formatDate) {
		CashOut cashOut = selectFrom().where(e.uid.eq(uid).and(e.dateTime.substring(0,7).eq(formatDate))).fetchFirst();
		return cashOut;

	}
	

	/**
	 *根据代理商id查询他的提现记录 
	 * @param pid 代理商id
	 * @return
	 */
	public CashOut findByPid(String pid) {
		CashOut cashOut = selectFrom().where(e.uid.eq(pid)).fetchFirst();
		return cashOut;
	}




}
