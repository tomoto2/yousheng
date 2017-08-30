package com.joe.frame.web.repository;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.slf4j.LoggerFactory;

import com.querydsl.core.types.PathMetadataFactory;
import com.querydsl.core.types.dsl.EntityPathBase;

@Transactional(TxType.SUPPORTS)
public abstract class AbstractRepository<T extends BaseEntity<ID>, ID extends Serializable>
		extends QueryDSLRepository<T, ID> {
	@SuppressWarnings("unchecked")
	public AbstractRepository() {
		// 获取repository对应的entity的class对象
		try {
			String className = getClass().getName().replace("repository", "entity").replace("Repository", "");
			super.clazz = (Class<T>) Class.forName(className);
			String domainName = clazz.getSimpleName();
			domainName = domainName.substring(0 , 1).toLowerCase() + domainName.substring(1);
			super.e = new EntityPathBase<T>(clazz, PathMetadataFactory.forVariable(domainName));
			super.logger = LoggerFactory.getLogger(clazz);
			return;
		} catch (Exception e) {
			Type genericSuperclass = getClass().getGenericSuperclass();
			// 只检查一层Repository泛型参数，不检查父类
			if (genericSuperclass instanceof ParameterizedType) {
				ParameterizedType parameterizedType = (ParameterizedType) genericSuperclass;
				Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
				super.clazz = (Class<T>) actualTypeArguments[0];
				String domainName = clazz.getSimpleName();
				domainName = domainName.substring(0 , 1).toLowerCase() + domainName.substring(1);
				super.e = new EntityPathBase<T>(clazz, PathMetadataFactory.forVariable(domainName));
				super.logger = LoggerFactory.getLogger(clazz);
				return;
			} else {
				throw new RuntimeException("请检查Repository类泛型或命名");
			}
		}
	}
}
