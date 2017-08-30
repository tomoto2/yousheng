package com.joe.frame.core.repository;

import java.util.List;

import javax.persistence.LockModeType;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.springframework.stereotype.Repository;

import com.joe.frame.core.database.AbstractRepository;
import com.joe.frame.core.entity.Club;
import com.joe.frame.core.entity.QClub;

@Repository
@Transactional(TxType.SUPPORTS)
public class ClubRepository extends AbstractRepository<Club, String>{
	QClub e = QClub.club;


	/**
	 * 根据创建人查询俱乐部信息（创建人=代理）
	 * @param uid 代理id
	 * @return
	 */
	public Club findByAgentUid(String uid) {
		return selectFrom().where(e.ownerId.eq(uid)).fetchFirst();

	}


	/**
	 * 查询俱乐部列表  page从1开始
	 * @param size 每页大小
	 * @param pageNo 每页显示数量
	 * @return
	 */
	public List<Club> getAllClub(int pageNo,int size) {
		return selectFrom().offset((pageNo - 1) * size).limit(size).fetch();
	}


	/**
	 * 删除俱乐部
	 * @param cid
	 * @return
	 */
	public long removeClub(String cid) {
		long execute = delete().where(e.cid.eq(cid)).execute();
		logger.info("删除俱乐部{}成功",cid);
		return execute;
	}


	/**
	 * 根据代理编号、俱乐部编号 查询俱乐部信息
	 * @param pnumId 代理编号/俱乐部编号
	 * @return
	 */
	public Club getClubByPnum(String pnumId, LockModeType lock) {
		Club club = selectFrom().where(e.numId.eq(pnumId)).setLockMode(lock).fetchFirst();
		return club;
	}
	/**
	 * 查询某俱乐部成员数量
	 * @param cid
	 * @return
	 */
	public long getOneClubSum(String cid) {
		long sum = selectFrom().where(e.cid.eq(cid)).fetchCount();
		return sum;
	}



}
