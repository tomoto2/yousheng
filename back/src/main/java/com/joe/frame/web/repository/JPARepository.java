package com.joe.frame.web.repository;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.slf4j.Logger;

/**
 * JPA的方式操作实体
 * 
 * @author qiao9
 *
 * @param <T>
 * @param <ID>
 */

@Transactional(TxType.SUPPORTS)
public abstract class JPARepository<T extends BaseEntity<ID>, ID extends Serializable> {
	protected Logger logger;

	@PersistenceContext
	protected EntityManager entityManager;

	// 对应的entity的class对象
	protected Class<T> clazz;

	/**
	 * 保存实体
	 * 
	 * @param listEntity
	 * @return
	 */
	public void persist(List<T> listEntity) {
		if (listEntity != null && !listEntity.isEmpty()) {
			for (int i = 1; i <= listEntity.size(); i++) {
				persist(listEntity.get(i - 1));
				if (i % 20 == 0) {
					flush();
				}
			}
		}
	}

	public void persist(T entity) {
		logger.info("保存实体：{}", entity.toString());
		entityManager.persist(entity);
	}

	public void detach(T entity) {
		entityManager.detach(entity);
	}

	public T find(ID id) {
		return entityManager.find(clazz, id);
	}

	public T find(ID id, LockModeType lockModeType) {
		return entityManager.find(clazz, id, lockModeType);
	}

	public T merge(T entity) {
		logger.info("更新实体：{}", entity);
		return entityManager.merge(entity);
	}

	/**
	 * 根据分页查询（默认返回为空，请重写该方法）
	 * 
	 * @param pageNo
	 *            分页页码
	 * @param limit
	 *            分页大小
	 * @return Collections.emptyList()
	 */
	public List<T> findByPage(long pageNo, long limit) {
		return Collections.emptyList();
	}
	
	public List<T> merge(List<T> list) {
		for (int i = 1; i <= list.size(); i++) {
			merge(list.get(i - 1));
			if (i % 20 == 0) {
				flush();
			}
		}
		return list;
	}

	public void refresh(T entity) {
		entityManager.refresh(entity);
	}

	public void flush() {
		entityManager.flush();
	}

	public void clear() {
		entityManager.clear();
	}

	/**
	 * 删除实体，该实体必须处于托管状态
	 * 
	 * @param entity
	 */
	public void remove(T t) {
		if (t != null) {
			entityManager.remove(t);
			logger.info("删除实体：{}", t);
		}
	}

	/**
	 * 根据ID删除实体
	 * 
	 * @param id
	 *            实体ID
	 */
	public void remove(ID id) {
		remove(find(id));
	}

	/**
	 * 删除实体
	 * 
	 * @param entitys
	 */
	public void remove(Collection<T> entitys) {
		if (entitys != null && !entitys.isEmpty()) {
			for (T t : entitys) {
				remove(t);
			}
		}
	}
}
