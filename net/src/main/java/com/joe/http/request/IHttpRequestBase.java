package com.joe.http.request;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import com.joe.http.config.IHttpConfig;

import lombok.Data;

@Data
public abstract class IHttpRequestBase {
	public static final String CONTENT_TYPE = "application/json";
	// Http配置
	private IHttpConfig httpConfig;
	// contentType，默认json
	private String contentType;
	// 请求URL
	private String url;
	// 请求头
	private Map<String, String> headers;
	// URL参数
	private Map<String, String> queryParams;
	// 请求
	private String charset;
	// 请求body，如果请求方法是get的话自动忽略该字段
	private String entity;

	public IHttpRequestBase(String url) {
		this.url = url;
		this.headers = new HashMap<String, String>();
		this.queryParams = new HashMap<String, String>();
		this.contentType = CONTENT_TYPE;
		this.httpConfig = new IHttpConfig();
		this.charset = Charset.defaultCharset().name();
	}

	/**
	 * 添加请求头
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 */
	public void addHeader(String key, String value) {
		headers.put(key, value);
	}

	/**
	 * 添加URL参数
	 * 
	 * @param key
	 *            参数键
	 * @param value
	 *            参数值
	 */
	public void addQueryParam(String key, String value) {
		if (key == null) {
			throw new NullPointerException("key 不能为null");
		}
		queryParams.put(key, value == null ? "" : value);
	}
}
