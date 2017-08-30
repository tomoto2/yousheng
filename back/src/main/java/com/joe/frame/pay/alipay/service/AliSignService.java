package com.joe.frame.pay.alipay.service;

import java.nio.charset.Charset;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joe.frame.common.secure.IBase64;
import com.joe.frame.pay.common.util.Base64;
import com.joe.frame.pay.prop.AliProp;
import com.joe.frame.web.prop.SecureProp;

/**
 * 阿里签名服务
 * 
 * @author joe
 *
 */
@Service
public class AliSignService {
	private static final Logger logger = LoggerFactory.getLogger(AliSignService.class);
	@Autowired
	private static IBase64 ibase64 = new IBase64();
	@Autowired
	private AliProp aliProp;
	@Autowired
	private SecureProp secureProp;

	/**
	 * sha256WithRsa 加签
	 * 
	 * @param content
	 *            要加密的文本
	 * @return
	 * @throws AlipayApiException
	 */
	public String rsa256Sign(String content, String charset) {
		return rsaSign(content, charset, "SHA256WithRSA");
	}

	public static final String SIGN_ALGORITHMS = "SHA1WithRSA";

	/**
	 * RSA签名
	 * 
	 * @param content
	 *            待签名数据
	 * @param privateKey
	 *            商户私钥
	 * @param input_charset
	 *            编码格式
	 * @return 签名值
	 */

	public String sign(String content) {
		try {
			String privateKey = secureProp.getRsaPrivateKey();
			String input_charset = "utf-8";

			PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(Base64.decode(privateKey));
			KeyFactory keyf = KeyFactory.getInstance("RSA");
			PrivateKey priKey = keyf.generatePrivate(priPKCS8);

			java.security.Signature signature = java.security.Signature.getInstance(SIGN_ALGORITHMS);

			signature.initSign(priKey);
			signature.update(content.getBytes(input_charset));

			byte[] signed = signature.sign();

			return Base64.encode(signed);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 网页付款加密 SHA1WithRSA
	 * 
	 * @param content
	 *            要加密的文本
	 * @param charset
	 * @return
	 */
	public String rsa1Sign(String content, String charset) {
		return rsaSign(content, charset, "SHA1WithRSA");
	}

	private static PrivateKey getPrivateKeyFromPKCS8(String algorithm, String privateKey) throws Exception {
		if (privateKey == null || privateKey.isEmpty() || algorithm == null || algorithm.isEmpty()) {
			return null;
		}
		KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
		byte[] encodedKey = privateKey.getBytes();
		// encodedKey = Base64.decodeBase64(encodedKey);
		encodedKey = ibase64.decrypt(encodedKey);
		return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(encodedKey));
	}

	
	public static void main(String[] args) {
		System.out.println(rsaSign("jlksdfljsdlfjl5s4df654" , "utf8" , "SHA256WithRSA"));
	}
	
	private static String rsaSign(String content, String charset, String way) {
		logger.debug("要进行阿里签名的数据为：{}，charset为：{}", content, charset);
		try {
			PrivateKey priKey = getPrivateKeyFromPKCS8("RSA", "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAMWj+kRLMdzNhKaEHQ5ybHFgVGSWn6V/itL+v25nV3qgY0TGRXh528JaYw/e+9N0sXNCy8OMCHT9Gl3q4n0nTtkF7TQWH31hMwJLEx2uwV2+AGhh98sdLPdzpw3yOCFQm4SpUMqTi8RljyyKkpv5lWZBnoriJ87t5kmFuRZ6wwb7AgMBAAECgYEAxFZ223tbyH7+JTGfb+ep5SOOEvMzG4TNk3fxEitlUvOcSgkxoFJmRvfl6zFYOCN32QnlRJiLGjUKCXy6jRwMjXqcLzy0AbYcXGZ9yn1XoIxGChN+9WOlZyqhAG2CY2NLgCvhJd+NPbBhqWkrA6JZ6pTqkTmrYZKDJ2pv9VYnHwECQQDrhUrj2s9vIAa/uSqEorvYg0mO4KSXVQR9mWu6o2NJ6UF8tQg0/nDbTCFyWrfRmFWpZbuViygXkXXEFXQ7Tw2BAkEA1tN4b4mWi49RXPuxfmxzBKoFETCqmJi6tlIqipWrsSVXdF9CwwNY/9ShaXY/ViI0MFC5a4odYwb5wgbpnoCKewJAO7AXL7HHelEyhKpHtT+Mva1gGf6il9uq/K3CGJXJ1vRvbUxv3QZS0bD0lQeaqFqj8v9eT+LpcTejEeOiBlndgQJAL48/sYnHX3xKKgi64d9Gk3jEiGq6ye++HlEQg//gs+Ytd2EDmcLq+DBtz7hTC1GlNjEY67r3CI5pys/W9rVEcwJAMDfFRpIN2qFcaqQMH/ITP5DHTKMOd3y+HhxG3OoKb9MbiP6af8DN7AaNp02i+rnUiLEcjh3GQo+AX0IgvHg+5A==");
			

			java.security.Signature signature = java.security.Signature.getInstance(way);

			signature.initSign(priKey);
			charset = charset == null ? Charset.defaultCharset().name() : charset;
			signature.update(content.getBytes(charset));

			byte[] signed = signature.sign();
			String result = new String(ibase64.encrypt(signed));
			logger.debug("签名结果为：{}", result);
			return result;
		} catch (Exception e) {
			logger.error("阿里签名错误", e);
			return "";
		}
	}

}
