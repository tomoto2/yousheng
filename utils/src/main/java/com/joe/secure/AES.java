package com.joe.secure;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AES加密
 * 
 * @author joe
 *
 */
public class AES implements Encipher {
	private static final Logger logger = LoggerFactory.getLogger(AES.class);
	/*
	 * 加密密码
	 */
	private String password;
	/*
	 * 加密器
	 */
	private Cipher encrypt;
	/*
	 * 解密器
	 */
	private Cipher decrypt;
	/*
	 * BASE64加密器
	 */
	private IBase64 encipher;

	public AES(String password) {
		init(password);
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
		byte[] base64 = encipher.encrypt(encrypt(byteContent));
		return new String(base64);
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
		}
		return "".getBytes();
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
		byte[] ciphertext = encipher.decrypt(content.getBytes());
		return new String(decrypt(ciphertext));
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
		}
		return "".getBytes();
	}

	/**
	 * 获取当前密码
	 * 
	 * @return 当前密码
	 */
	public String getPassword() {
		return this.password;
	}

	/**
	 * 初始化加密器、解密器和密码
	 */
	private void init(String password) {
		logger.debug("初始化AES");
		try {
			// 初始化密码
			this.password = password;
			this.encipher = new IBase64();

			KeyGenerator kgen = KeyGenerator.getInstance("AES");
			kgen.init(128, new SecureRandom(password.getBytes()));
			SecretKey secretKey = kgen.generateKey();
			byte[] enCodeFormat = secretKey.getEncoded();
			SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");

			// 创建加密器和解密器
			encrypt = Cipher.getInstance("AES");
			decrypt = Cipher.getInstance("AES");
			// 初始化加密器和解密器
			encrypt.init(Cipher.ENCRYPT_MODE, key);
			decrypt.init(Cipher.DECRYPT_MODE, key);
		} catch (Exception e) {
			logger.error("AES初始化失败", e);
		}
	}
}
