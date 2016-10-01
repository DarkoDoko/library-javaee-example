package com.library.app;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;

public abstract class GenericRepository<T> {

	protected abstract Class<T> getPersistentClass();

	protected abstract EntityManager getEntityManager();

	public T add(T entity) {
		getEntityManager().persist(entity);
		return entity;
	}

	public T findById(Long id) {
		if (id == null) {
			return null;
		}
		return getEntityManager().find(getPersistentClass(), id);
	}

	public void update(T entity) {
		getEntityManager().merge(entity);
	}

	public List<T> findAll(String orderField) {
		return getEntityManager().createQuery(
				"Select e From " + getPersistentClass().getSimpleName() + " e Order by e." + orderField)
				.getResultList();
	}

	public boolean alreadyExists(String propertyName, String propertyValue, Long id) {
		StringBuilder jpql = new StringBuilder();
		jpql.append("Select 1 From " + getPersistentClass().getSimpleName() + " e where e." + propertyName
				+ " = :propertyValue");
		if (id != null) {
			jpql.append(" and e.id != :id");
		}

		Query query = getEntityManager().createQuery(jpql.toString());
		query.setParameter("propertyValue", propertyValue);
		if (id != null) {
			query.setParameter("id", id);
		}

		return query.setMaxResults(1).getResultList().size() > 0;
	}

	public boolean existsById(Long id) {
		return getEntityManager()
				.createQuery("Select 1 From " + getPersistentClass().getSimpleName() + " e where e.id = :id")
				.setParameter("id", id)
				.setMaxResults(1)
				.getResultList().size() > 0;
	}
    
}
