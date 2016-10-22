package com.library.app;

import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import com.library.app.pagination.PaginatedData;
import com.library.app.pagination.filter.PaginationData;

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
                "SELECT e FROM " + getPersistentClass().getSimpleName() + " e ORDER BY e." + orderField)
                .getResultList();
    }

    public boolean alreadyExists(String propertyName, String propertyValue, Long id) {
        StringBuilder jpql = new StringBuilder();
        jpql.append("SELECT 1 FROM ")
                .append(getPersistentClass().getSimpleName())
                .append(" e WHERE e.")
                .append(propertyName)
                .append(" = :propertyValue");

        if (id != null) {
            jpql.append(" AND e.id != :id");
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
                .createQuery("SELECT 1 FROM " + getPersistentClass().getSimpleName() + " e WHERE e.id = :id")
                .setParameter("id", id)
                .setMaxResults(1)
                .getResultList().size() > 0;
    }

    protected PaginatedData<T> findByParameters(String clause, PaginationData paginationData,
            Map<String, Object> queryParameters, String defaultSortFieldWithDirection) {
        String clauseSort = "ORDER BY e." + getSortField(paginationData, defaultSortFieldWithDirection);
        Query queryEntities = getEntityManager().createQuery(
                "SELECT e FROM " + getPersistentClass().getSimpleName()
                + " e " + clause + " " + clauseSort);
        applyQueryParametersOnQuery(queryParameters, queryEntities);
        applyPaginationOnQuery(paginationData, queryEntities);

        List<T> entities = queryEntities.getResultList();

        return new PaginatedData<>(countWithFilter(clause, queryParameters), entities);
    }

    private int countWithFilter(String clause, Map<String, Object> queryParameters) {
        Query queryCount = getEntityManager().createQuery(
                "SELECT COUNT(e) FROM " + getPersistentClass().getSimpleName() + " e " + clause);
        applyQueryParametersOnQuery(queryParameters, queryCount);
        return ((Long) queryCount.getSingleResult()).intValue();
    }

    private void applyPaginationOnQuery(PaginationData paginationData, Query query) {
        if (paginationData != null) {
            query.setFirstResult(paginationData.getFirstResult());
            query.setMaxResults(paginationData.getMaxResults());
        }
    }

    private String getSortField(PaginationData paginationData, String defaultSortField) {
        if (paginationData == null || paginationData.getOrderField() == null) {
            return defaultSortField;
        }
        return paginationData.getOrderField() + " " + getSortDirection(paginationData);
    }

    private String getSortDirection(PaginationData paginationData) {
        return paginationData.isAscending() ? "ASC" : "DESC";
    }

    private void applyQueryParametersOnQuery(Map<String, Object> queryParameters, Query query) {
        queryParameters.entrySet().forEach((entryMap) -> {
            query.setParameter(entryMap.getKey(), entryMap.getValue());
        });
    }

}
