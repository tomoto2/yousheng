package com.joe.frame.core.repository;

import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.springframework.stereotype.Repository;

import com.joe.frame.core.database.AbstractRepository;
import com.joe.frame.core.entity.ClubNotice;
import com.joe.frame.core.entity.QClubNotice;

/**
 * 公告
 * @author lpx
 *
 * 2017年7月21日
 */

@Repository
@Transactional(TxType.SUPPORTS)
public class ClubNoticeRepository extends AbstractRepository<ClubNotice, String>{

	QClubNotice e = QClubNotice.clubNotice;



	/**
	 * 根据俱乐部id查询俱乐部公告信息
	 * @param cid 俱乐部id
	 * @return
	 */
	public ClubNotice getNotice(String cid){
		return selectFrom().where(e.cid.eq(cid)).fetchFirst();
	}


}
