package com.joe.frame.web.dto;

import java.util.Collections;
import java.util.List;

public class PageDTO<T> extends BaseDTO<T> {
	private static final long serialVersionUID = -3449465221825668356L;
	/**
	 * 是否还有下一页
	 */
	private boolean hasNext;
	/**
	 * 当前页码，第一页为1
	 */
	private Integer currentPageNo;
	/**
	 * 页面总数
	 */
	private Integer pageCount;
	/**
	 * 结果集，当没有更多结果时结果集的size为0（不为null）
	 */
	private List<T> datas;

	public PageDTO() {
		this.currentPageNo = 1;
		this.hasNext = false;
		this.pageCount = 0;
	}

	public boolean isHasNext() {
		return hasNext;
	}

	public void setHasNext(boolean hasNext) {
		this.hasNext = hasNext;
	}

	public Integer getCurrentPageNo() {
		return currentPageNo;
	}

	public void setCurrentPageNo(Integer currentPageNo) {
		this.currentPageNo = currentPageNo;
	}

	public List<T> getDatas() {
		return this.datas;
	}

	public void setDatas(List<T> datas) {
		this.datas = datas;
	}

	public void notResult() {
		super.error("101");
		this.datas = Collections.emptyList();
	}

	public int getPageCount() {
		return this.pageCount;
	}

	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}
}
