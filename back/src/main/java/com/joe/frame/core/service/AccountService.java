package com.joe.frame.core.service;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joe.frame.common.secure.Encipher;
import com.joe.frame.common.secure.MD5;
import com.joe.frame.common.util.DateUtil;
import com.joe.frame.core.entity.Account;
import com.joe.frame.core.entity.AppVersion;
import com.joe.frame.core.param.PasswordParam;
import com.joe.frame.core.repository.AccountRepository;
import com.joe.frame.core.repository.ProxyRepository;
import com.joe.frame.web.cache.EhcacheService;

@Service
@Transactional
public class AccountService {
	private static final Logger logger = LoggerFactory.getLogger(AccountService.class);
	@Autowired
	private ProxyRepository userInfoRepository;
	@Autowired
	private AccountRepository accountRepository;
	@Autowired
	private DateUtil dateUtil;
	@Autowired
	private EhcacheService ehcacheService;

	private Encipher encipher = new MD5();


	/**
	 * 通过用户id查询账户信息
	 * @param uid 用户id
	 * @return
	 */
	public Account getAccount(String uid){
		return accountRepository.getAccount(uid);

	}

	/**
	 * 登录
	 * @param phone 手机号（账号）
	 * @param password 密码
	 */
	public Account login(String phone, String password){
		Account account = accountRepository.login(phone, encipher.encrypt(password));
		if(account == null){
			logger.info("账号或密码错误");
			return null;
		}
		return account;
	}

	/**
	 * 手机号唯一性验证
	 * @param phone 手机号
	 */
	public int OnlyForPhone(String phone){
		Account account = accountRepository.getAccountForPhone(phone);
		if(account != null){
			System.out.println("手机号已存在");
			return 0;
		}

		return 1;
	}

	/**
	 * 忘记密码
	 * @param phone 手机号（账号）
	 * @param password 密码
	 */
	public void forgetPassord(String phone, String password){
		Account account = accountRepository.getAccountForPhone(phone);
		if(account == null){
			logger.info("手机号不存在");
			return ;
		}
		account.setPassword(encipher.encrypt(password));
		logger.info("密码修改成功",account);
		//保存修改实体
		//		accountRepository.merge(account);
	}


	/**
	 * 修改密码
	 * @param passwordParam
	 */
	public void uptPassword(PasswordParam passwordParam,Account account) {
		account.setPassword(encipher.encrypt(passwordParam.getNewPass()));//设置新密码
	}


	/**
	 * app 查询版本号
	 * @return 版本号
	 */
	public String getVersion() {
		return accountRepository.getVersion();
	}





}
