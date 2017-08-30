package com.joe.frame.pay.wechatpay.repository;

import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.springframework.stereotype.Repository;

import com.joe.frame.pay.wechatpay.entity.WechatOrder;
import com.joe.frame.web.repository.AbstractRepository;

@Repository
@Transactional(TxType.SUPPORTS)
public class WechatOrderRepository extends AbstractRepository<WechatOrder, String> {
//	QWechatOrder e = QWechatOrder.wechatOrder;
//
//	/**
//	 * 查询所有未支付订单
//	 * 
//	 * @param limit
//	 *            分页大小
//	 * @param pageNo
//	 *            要获取的分页页码，从1开始
//	 * @return
//	 */
//	public List<WechatOrder> findUnpaid(long limit, int pageNo) {
//		List<WechatOrder> list = select(e).where(e.status.eq(WechatOrderStatus.CREATE).or(e.status.eq(WechatOrderStatus.UNPAID))).limit(limit + 1).offset((pageNo - 1) * limit).fetch();
//		return list;
//	}
}
