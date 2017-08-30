package com.joe.frame.core.repository;

import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.springframework.stereotype.Repository;

import com.joe.frame.core.database.AbstractRepository;
import com.joe.frame.core.entity.QShare;
import com.joe.frame.core.entity.Share;

@Repository
@Transactional(TxType.SUPPORTS)
public class ShareRepository extends AbstractRepository<Share,String>{

	QShare e = QShare.share;

	
	/**
	 * 获取总历史分红总额（不管有没有提现）
	 * @param uid 获得分红的用户ID
	 * @return
	 */
	/*	public double getAll(String uid) {
		Tuple tuple = select(share.count.sum()).from(share).where(share.uid.eq(uid)).fetchFirst();
		if (tuple != null && tuple.get(share.count.sum()) != null && tuple.get(share.count.sum()) != 0) {
			return tuple.get(share.count.sum()) / 100;
		} else {
			return 0;
		}
	}*/


	/**
	 * 获取某月返利详情
	 * @param uid 代理id
	 * @param month 某月日期 格式：yyyy-MM
	 */
	public Share getMonthShare(String uid,String month){
		Share share = selectFrom().where(e.uid.eq(uid).and(e.date.substring(0,7).eq(month))).fetchFirst();
		return share;
	}

}
