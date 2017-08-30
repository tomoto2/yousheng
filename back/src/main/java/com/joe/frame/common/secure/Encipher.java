package com.joe.frame.common.secure;

public interface Encipher {
	/**
	 * 加密
	 * @param content
	 * 要加密的内容
	 * @return
	 * 失败时返回null
	 */
	public String encrypt(String content);

	/**
	 * 加密
	 * @param byteContent
	 * 要加密的内容
	 * @return
	 * 失败时返回null
	 */
	public  byte[] encrypt(byte[] byteContent);

	/**
	 * 解密
	 * @param content
	 * 要解密的密文
	 * @return
	 * 失败时返回null
	 */
	public String decrypt(String content);

	/**
	 * 解密
	 * @param byteContent
	 * 要解密的内容
	 * @return
	 * 失败时返回null
	 */
	public byte[] decrypt(byte[] byteContent);
}
