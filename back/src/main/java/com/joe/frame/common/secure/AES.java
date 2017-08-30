package com.joe.frame.common.secure;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.joe.frame.web.prop.SecureProp;

@Component
public class AES implements Encipher {
	private static final Logger logger = LoggerFactory.getLogger(AES.class);
	/**
	 * 加密密码
	 */
	private String password;
	/**
	 * 加密器
	 */
	private Cipher encrypt;
	/**
	 * 解密器
	 */
	private Cipher decrypt;
	private IBase64 iBase64;

	public AES(@Autowired SecureProp secureProp) {
		logger.debug("初始化AES");
		try {
			// 初始化密码
			this.password = secureProp.getAes();
			this.iBase64 = new IBase64();

			KeyGenerator kgen = KeyGenerator.getInstance("AES");
			kgen.init(128, new SecureRandom(this.password.getBytes()));
			SecretKey secretKey = kgen.generateKey();
			byte[] enCodeFormat = secretKey.getEncoded();
			SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");

			// 创建加密器和解密器
			this.encrypt = Cipher.getInstance("AES");
			this.decrypt = Cipher.getInstance("AES");
			// 初始化加密器和解密器
			this.encrypt.init(Cipher.ENCRYPT_MODE, key);
			this.decrypt.init(Cipher.DECRYPT_MODE, key);
		} catch (Exception e) {
			logger.error("AES初始化失败", e);
		}
	}

	/**
	 * 加密
	 * 
	 * @param content
	 *            要加密的数据
	 * @return 加密后的数据
	 */
	public String encrypt(String content) {
		byte[] byteContent = content.getBytes();
		byte[] result = encrypt(byteContent);
		if (result == null) {
			return null;
		} else {
			return new String(this.iBase64.encrypt(result));
		}
	}

	/**
	 * 加密
	 * 
	 * @param byteContent
	 *            要加密的数据
	 * @return 加密后的数据
	 */
	public byte[] encrypt(byte[] byteContent) {
		try {
			// 加密
			byte[] ciphertext = encrypt.doFinal(byteContent);
			return ciphertext;
		} catch (Exception e) {
			logger.error("AES加密出错", e);
			return null;
		}
	}

	/**
	 * 解密
	 * 
	 * @param content
	 *            加密数据
	 * @return 解密后的数据
	 */
	public String decrypt(String content) {
		// 获取加密的byte数组
		byte[] ciphertext = this.iBase64.decrypt(content.getBytes());
		byte[] result = decrypt(ciphertext);
		if (result == null) {
			return null;
		} else {
			return new String(result);
		}
	}

	/**
	 * 解密
	 * 
	 * @param byteContent
	 *            加密数据
	 * @return 解密后的数据
	 */
	public byte[] decrypt(byte[] byteContent) {
		try {
			// 解密
			byte[] result = decrypt.doFinal(byteContent);
			return result;
		} catch (Exception e) {
			logger.error("AES解密出错", e);
			return null;
		}
	}
}
