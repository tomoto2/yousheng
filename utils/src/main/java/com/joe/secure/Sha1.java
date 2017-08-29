package com.joe.secure;

import org.apache.commons.codec.digest.DigestUtils;

public class Sha1 implements Encipher{

	@Override
	public String encrypt(String content) {
		return DigestUtils.sha1Hex(content);
	}

	@Override
	public byte[] encrypt(byte[] content) {
		return DigestUtils.sha1Hex(content).getBytes();
	}

	@Override
	public String decrypt(String content) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] decrypt(byte[] byteContent) {
		// TODO Auto-generated method stub
		return null;
	}

}
