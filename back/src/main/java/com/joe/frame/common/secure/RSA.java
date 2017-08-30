package com.joe.frame.common.secure;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;

import javax.crypto.Cipher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.joe.collection.ByteArray;
import com.joe.frame.web.prop.SecureProp;


/**
 * RSA实现
 * 
 * @author joe
 *
 */
@Component
public class RSA implements Encipher {
	private static final Logger logger = LoggerFactory.getLogger(RSA.class);
	private static final IBase64 iBase64 = new IBase64();
	/*
	 * 私钥
	 */
	private String privateKey;
	/*
	 * 公钥
	 */
	private String publicKey;

	public RSA(@Autowired SecureProp secureProp) {
		this.privateKey = secureProp.getRsaPrivateKey();
		this.publicKey = secureProp.getRsaPublicKey();
	}

	@Override
	public String encrypt(String content) {
		try {
			return this.encryptByPublicKey(content, publicKey);
		} catch (Exception e) {
			logger.error("RSA加密失败", e);
			return null;
		}
	}

	@Override
	public byte[] encrypt(byte[] byteContent) {
		try {
			return this.encryptByPublicKey(byteContent, publicKey);
		} catch (Exception e) {
			logger.error("RSA加密失败", e);
			return null;
		}
	}

	@Override
	public String decrypt(String content) {
		try {
			return this.decryptByPrivateKey(content, privateKey);
		} catch (Exception e) {
			logger.error("RSA加密失败", e);
			return null;
		}
	}

	@Override
	public byte[] decrypt(byte[] byteContent) {
		try {
			return this.decryptByPrivateKey(byteContent, privateKey);
		} catch (Exception e) {
			logger.error("RSA加密失败", e);
			return null;
		}
	}

	/**
	 * 生成公钥和私钥
	 * 
	 * @return 返回公钥和私钥的base64加密后的字符串
	 * @throws NoSuchAlgorithmException
	 * 
	 */
	public HashMap<String, String> getKeys() throws NoSuchAlgorithmException {
		HashMap<String, String> map = new HashMap<String, String>();
		KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
		keyPairGen.initialize(1024);
		KeyPair keyPair = keyPairGen.generateKeyPair();
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
		map.put("public", new String(iBase64.encrypt(publicKey.getEncoded())));
		map.put("private", new String(iBase64.encrypt(privateKey.getEncoded())));
		return map;
	}

	/**
	 * 公钥加密
	 * 
	 * @param data
	 *            要加密的数据
	 * @param key
	 *            公钥字符串（BASE64加密过的）
	 * @return 加密后的数据
	 * @throws Exception
	 */
	public String encryptByPublicKey(String data, String key) throws Exception {
		return new String(this.encryptByPublicKey(data.getBytes(), key));
	}

	/**
	 * 公钥加密
	 * 
	 * @param data
	 *            要加密的数据
	 * @param key
	 *            公钥字符串（BASE64加密过的）
	 * @return 加密后的数据
	 * @throws Exception
	 */
	public byte[] encryptByPublicKey(byte[] data, String key) throws Exception {
		RSAPublicKey publicKey = (RSAPublicKey) getPublicKey(key);
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		// 模长
		int key_len = publicKey.getModulus().bitLength() / 8;
		byte[] dataByte = data;
		// 加密数据长度必须 <= 模长-11
		byte[][] datas = splitArray(dataByte, key_len - 11);
		ByteArray byteArray = new ByteArray();
		// 如果明文长度大于模长-11则要分组加密
		for (byte[] array : datas) {
			byteArray.append(iBase64.encrypt(cipher.doFinal(array)));
		}
		return byteArray.getData();
	}

	/**
	 * 私钥解密
	 * 
	 * @param data
	 *            加密后的数据
	 * @param key
	 *            私钥字符串（BASE64加密过的）
	 * @return 解密后的数据
	 * @throws Exception
	 */
	public String decryptByPrivateKey(String data, String key) throws Exception {
		return new String(this.decryptByPrivateKey(data.getBytes(), key));
	}

	/**
	 * 私钥解密
	 * 
	 * @param data
	 *            加密后的数据
	 * @param key
	 *            私钥字符串（BASE64加密过的）
	 * @return 解密后的数据
	 * @throws Exception
	 */
	public byte[] decryptByPrivateKey(byte[] data, String key) throws Exception {
		RSAPrivateKey privateKey = (RSAPrivateKey) getPrivateKey(key);
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		// 模长
		int key_len = privateKey.getModulus().bitLength() / 8;
		byte[] bytes = iBase64.decrypt(data);
		// 如果密文长度大于模长则要分组解密
		String result = "";
		byte[][] arrays = splitArray(bytes, key_len);
		for (byte[] arr : arrays) {
			result += new String(cipher.doFinal(arr));
		}
		return result.getBytes();
	}

	/**
	 * 从密钥文件中读取公钥
	 * 
	 * @param kstorefile
	 *            密钥文件
	 * @param kstoretype
	 *            密钥文件类型，例如：JKS
	 * @param kstorepwd
	 *            密钥文件访问密码
	 * @param alias
	 *            别名
	 * @return 公钥
	 */
	public PublicKey getPublicKey(String kstorefile, String kstoretype, String kstorepwd, String alias) {
		try {
			KeyStore ks;
			try (FileInputStream in = new FileInputStream(kstorefile)) {
				ks = KeyStore.getInstance(kstoretype);
				ks.load(in, kstorepwd.toCharArray());
			}
			if (!ks.containsAlias(alias)) {
				return null;
			}
			Certificate cert = ks.getCertificate(alias);
			return cert.getPublicKey();
		} catch (KeyStoreException | NoSuchAlgorithmException | CertificateException ex) {
			return null;
		} catch (FileNotFoundException ex) {
			return null;
		} catch (IOException ex) {
			return null;
		}
	}

	/**
	 * 从密钥文件中读取私钥
	 * 
	 * @param kstorefile
	 *            密钥文件
	 * @param kstoretype
	 *            密钥文件类型，例如：JKS
	 * @param kstorepwd
	 *            密钥文件访问密码
	 * @param alias
	 *            别名
	 * @return 私钥
	 */
	public PrivateKey getPrivateKey(String kstorefile, String kstoretype, String kstorepwd, String alias,
			String keypwd) {
		try {
			KeyStore ks;
			try (FileInputStream in = new FileInputStream(kstorefile)) {
				ks = KeyStore.getInstance(kstoretype);
				ks.load(in, kstorepwd.toCharArray());
			}
			if (!ks.containsAlias(alias)) {
				return null;
			}
			return (PrivateKey) ks.getKey(alias, keypwd.toCharArray());
		} catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | UnrecoverableKeyException ex) {
			return null;
		} catch (FileNotFoundException ex) {
			return null;
		} catch (IOException ex) {
			return null;
		}
	}

	/**
	 * 根据字符串得到公钥对象
	 * 
	 * @param key
	 *            密钥字符串（经过base64编码）
	 * @throws Exception
	 */
	public PublicKey getPublicKey(String key) throws Exception {
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(iBase64.decrypt(key.getBytes()));
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PublicKey publicKey = keyFactory.generatePublic(keySpec);
		return publicKey;
	}

	/**
	 * 根据字符串得到私钥对象
	 * 
	 * @param key
	 *            密钥字符串（经过base64编码）
	 * @throws Exception
	 */
	public PrivateKey getPrivateKey(String key) throws Exception {
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(iBase64.decrypt(key.getBytes()));
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
		return privateKey;
	}

	/**
	 * 使用模和指数生成RSA公钥
	 * 注意：【此代码用了默认补位方式，为RSA/None/PKCS1Padding，不同JDK默认的补位方式可能不同，如Android默认是RSA
	 * /None/NoPadding】
	 * 
	 * @param modulus
	 *            模
	 * @param exponent
	 *            指数
	 * @return
	 */
	public RSAPublicKey getPublicKey(String modulus, String exponent) {
		try {
			BigInteger b1 = new BigInteger(modulus);
			BigInteger b2 = new BigInteger(exponent);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			RSAPublicKeySpec keySpec = new RSAPublicKeySpec(b1, b2);
			return (RSAPublicKey) keyFactory.generatePublic(keySpec);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 使用模和指数生成RSA私钥
	 * 注意：【此代码用了默认补位方式，为RSA/None/PKCS1Padding，不同JDK默认的补位方式可能不同，如Android默认是RSA
	 * /None/NoPadding】
	 * 
	 * @param modulus
	 *            模
	 * @param exponent
	 *            指数
	 * @return
	 */
	public RSAPrivateKey getPrivateKey(String modulus, String exponent) {
		try {
			BigInteger b1 = new BigInteger(modulus);
			BigInteger b2 = new BigInteger(exponent);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(b1, b2);
			return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 拆分数组
	 * 
	 * @param data
	 *            要拆分的数组
	 * @param len
	 *            拆分后每个数组的最大长度
	 * @return 拆分后的数组的数组
	 */
	private byte[][] splitArray(byte[] data, int len) {
		int x = data.length / len;
		int y = data.length % len;
		int z = 0;
		if (y != 0) {
			z = 1;
		}
		byte[][] arrays = new byte[x + z][];
		byte[] arr;
		for (int i = 0; i < x; i++) {
			arr = new byte[len];
			System.arraycopy(data, i * len, arr, 0, len);
			arrays[i] = arr;
		}
		if (y != 0) {
			arr = new byte[y];
			System.arraycopy(data, x * len, arr, 0, y);
			arrays[x] = arr;
		}
		return arrays;
	}
}
