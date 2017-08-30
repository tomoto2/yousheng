package com.joe.frame.web.dto;

/**
 * 普通DTO
 * @author Administrator
 *
 */
public class NormalDTO<T> extends BaseDTO<T> {
	private static final long serialVersionUID = 8245508625478769472L;
	/**
	 * 响应数据
	 */
	private T data;
	
	public long getPageCount() {
		return pageCount;
	}

	public void setPageCount(long pageCount) {
		this.pageCount = pageCount;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	private long pageCount;

	private int currentPage;

	public void setData(T data) {
		this.data = data;
	}

	public T getData() {
		return this.data;
	}
}
