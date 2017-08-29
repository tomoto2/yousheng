package com.joe.parse.json;

/**
 * 序列化接口，提供序列化能力
 * 
 * @author joe
 *
 * @param <T>
 */
public interface Serializer<T> {
	/**
	 * 将对象序列化
	 * 
	 * @return 对象序列化后的数据
	 * @throws SerializeException
	 *             序列化异常时应抛出该异常
	 */
	byte[] write(T t) throws SerializeException;

	/**
	 * 将数据序列化为对象
	 * 
	 * @param data
	 *            对象的数据
	 * @return 数据反序列化后的对象
	 * @throws SerializeException
	 *             序列化异常时应抛出该异常
	 */
	T read(byte[] data) throws SerializeException;
}
