package com.joe.frame.common.secure;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;

@Component
public class MD5 implements Encipher {
	public String encrypt(String content) {
		return DigestUtils.md5Hex(content);
	}

	public byte[] encrypt(byte[] content) {
		return null;
	}

	public String decrypt(String content) {
		return null;
	}

	public byte[] decrypt(byte[] byteContent) {
		return null;
	}
	
	/**
	 * MD5加密
	 * 
	 * @param data
	 *            要加密的数据
	 * @return 加密后的MD5数据，32位
	 */
	public String md5Hex(String data) {
		return DigestUtils.md5Hex(data);
	}
}
