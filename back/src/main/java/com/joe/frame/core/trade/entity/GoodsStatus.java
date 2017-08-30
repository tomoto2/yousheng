package com.joe.frame.core.trade.entity;

public enum GoodsStatus {
	CREATE("商品创建"), LOST_EFFICACY("商品失效");
	private String status;

	private GoodsStatus(String status) {
		this.status = status;
	}

	public String toString() {
		return this.status;
	}
}
