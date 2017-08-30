package com.joe.frame.core.repository;

import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.springframework.stereotype.Repository;

import com.joe.frame.core.database.AbstractRepository;
import com.joe.frame.core.entity.LoginHistory;
import com.joe.frame.core.entity.QLoginHistory;

@Repository
@Transactional(TxType.SUPPORTS)
public class LoginHistoryRepository extends AbstractRepository<LoginHistory, String>{
	QLoginHistory e = QLoginHistory.loginHistory;

	/**
	 * 当天登陆过游戏的人数
	 * @return
	 */
	public long getSumCurrentDay() {
		long sum =  selectFrom().groupBy(e.uid).fetchCount();
		return sum;
	}

}
