package com.joe.frame.core.repository;

import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.springframework.stereotype.Repository;

import com.joe.frame.core.database.AbstractRepository;
import com.joe.frame.core.entity.Account;
import com.joe.frame.core.entity.AppVersion;
import com.joe.frame.core.entity.QAccount;
import com.joe.frame.core.entity.QAppVersion;
import com.querydsl.core.Tuple;


@Repository
@Transactional(TxType.SUPPORTS)
public class AccountRepository extends AbstractRepository<Account, String>{
	QAccount account = QAccount.account;
	QAppVersion appversion = QAppVersion.appVersion;

	/**
	 * 登录查询
	 * @param phone  手机号（账号）
	 * @param password 密码
	 * @return
	 */
	public Account login(String phone, String password){
		Account first_account = selectFrom().where(account.id.eq(phone).and(account.password.eq(password))).fetchFirst();
		return first_account;
	}

	/**
	 * 手机号 查询账号
	 * @param phone 手机号
	 * @return
	 */
	public Account getAccountForPhone(String phone){
		Account first_account = selectFrom().where(account.id.eq(phone)).fetchFirst();
		return first_account;
	}



	/**
	 * 根据用户id查询账号信息
	 * @param uid 用户id
	 * @return
	 */
	public Account getAccount(String uid){
		Account first_account = selectFrom().where(account.uid.eq(uid)).fetchFirst();
		return first_account;
	}

	/**
	 * app获取版本号
	 * @return 版本号
	 */
	public String getVersion() {
		Tuple t = select(appversion.version).from(appversion).fetchFirst();
		String version  = t.get(appversion.version);
		return version;
	}


}
