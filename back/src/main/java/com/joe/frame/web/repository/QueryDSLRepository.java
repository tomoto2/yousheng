package com.joe.frame.web.repository;

import java.io.Serializable;
import java.util.List;

import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.jpa.impl.JPADeleteClause;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAUpdateClause;

/**
 * queryDSL查询框架
 * 
 * @author qiao9
 *
 * @param <T>
 * @param <ID>
 */
@Transactional(TxType.SUPPORTS)
public abstract class QueryDSLRepository<T extends BaseEntity<ID>, ID extends Serializable>
		extends JPARepository<T, ID> {
	// 对应的查询实体
	protected EntityPathBase<T> e;

	protected JPAQuery<T> selectFrom() {
		return select(this.e).from(this.e);
	}

	protected JPAQuery<Tuple> select(Expression<?>... exprs) {
		return new JPAQuery<T>(entityManager).select(exprs);
	}

	protected JPAQuery<T> select(Expression<T> expr) {
		return new JPAQuery<T>(entityManager).select(expr);
	}

	protected JPADeleteClause delete() {
		return new JPADeleteClause(entityManager, this.e);
	}

	protected JPAUpdateClause update() {
		return new JPAUpdateClause(entityManager, this.e);
	}

	protected BooleanExpression and(BooleanExpression left, BooleanExpression right) {
		if (right == null) {
			return left;
		}
		if (left != null) {
			left = left.and(right);
		} else {
			left = right;
		}

		return left;
	}

	protected BooleanExpression or(BooleanExpression left, BooleanExpression right) {
		if (right == null) {
			return left;
		}
		if (left != null) {
			left = left.or(right);
		} else {
			left = right;
		}

		return left;
	}

	/**
	 * 查找所有数据
	 * 
	 * @param e
	 * @return
	 */
	public List<T> findAll() {
		return selectFrom().fetch();
	}

	/**
	 * 查找所有数据的总数
	 * 
	 * @return
	 */
	public Long countAll() {
		return selectFrom().fetchCount();
	}

	/**
	 * 返回分页总页数
	 * 
	 * @param size
	 *            分页大小
	 * @return 分页总页数
	 */
	public long countPage(long size) {
		return (long) Math.ceil(countAll() / size);
	}
}
