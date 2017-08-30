package com.joe.frame.core.repository;

import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.springframework.stereotype.Repository;

import com.joe.frame.core.database.AbstractRepository;
import com.joe.frame.core.entity.QUserGameHistory;
import com.joe.frame.core.entity.UserGameHistory;

@Repository
@Transactional(TxType.SUPPORTS)
public class UserGameHistoryRepository extends AbstractRepository<UserGameHistory,String>{
	QUserGameHistory e = QUserGameHistory.userGameHistory;

	/**
	 * 统计游戏时间长度超过5分钟的人数
	 * @return
	 */
	public long getSumbyfivemin() {
		return selectFrom().groupBy(e.uid).fetchCount();
	}

}
