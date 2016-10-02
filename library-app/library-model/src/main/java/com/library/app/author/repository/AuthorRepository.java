package com.library.app.author.repository;

import com.library.app.GenericRepository;
import com.library.app.author.model.Author;
import com.library.app.author.model.AuthorFilter;
import com.library.app.pagination.PaginatedData;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public class AuthorRepository extends GenericRepository<Author>{

    @PersistenceContext
    EntityManager em;

    @Override
    protected Class<Author> getPersistentClass() {
        return Author.class;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
    public PaginatedData<Author> findByFilter(AuthorFilter filter){
        
        StringBuilder clause = new StringBuilder("WHERE e.id IS NOT NULL");
        Map<String, Object> queryParameters = new HashMap<>();
        
        if(filter.getName() != null){
            clause.append(" AND UPPER(e.name) LIKE UPPER(:name)");
            queryParameters.put("name", "%" + filter.getName() + "%");
        }
        
        StringBuilder clauseSort = new StringBuilder();
        if(filter.hasOrderField()){
            clauseSort.append("ORDER BY e." + filter.getPaginationData().getOrderField());
            clauseSort.append(filter.getPaginationData().isAscending() ? " ASC" : " DESC");
        } else {
            clauseSort.append("ORDER BY e.name ASC");
        }
        
        Query queryAuthors = em.createQuery("SELECT e FROM Author e " + clause.toString() + " " + clauseSort.toString());
        applyParametersOnQuery(queryParameters, queryAuthors);
        
        if(filter.hasPaginationData()){
            queryAuthors.setFirstResult(filter.getPaginationData().getFirstResult());
            queryAuthors.setMaxResults(filter.getPaginationData().getMaxResults());
        }
        
        List<Author> authors = queryAuthors.getResultList();
        
        Query queryCount = em.createQuery("SELECT Count(e) FROM Author e " + clause.toString());
        applyParametersOnQuery(queryParameters, queryCount);
        
        Integer count = ((Long) queryCount.getSingleResult()).intValue();
        
        return new PaginatedData<>(count, authors);
    }

    private void applyParametersOnQuery(Map<String, Object> queryParameters, Query query) {
        for(Entry<String, Object> entryMap : queryParameters.entrySet()){
            query.setParameter(entryMap.getKey(), entryMap.getValue());
        }
    }
    
}
