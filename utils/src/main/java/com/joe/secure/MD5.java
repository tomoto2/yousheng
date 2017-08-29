package com.joe.secure;

import org.apache.commons.codec.digest.DigestUtils;

public class MD5 implements Encipher {
	public String encrypt(String content) {
		return DigestUtils.md5Hex(content);
	}

	public byte[] encrypt(byte[] content) {
		return DigestUtils.md5Hex(content).getBytes();
	}

	public String decrypt(String content) {
		return null;
	}

	public byte[] decrypt(byte[] byteContent) {
		return null;
	}
}
