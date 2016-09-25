package com.library.app.author.repository;

import com.library.app.author.model.Author;
import com.library.app.author.model.AuthorFilter;
import com.library.app.common.model.PaginatedData;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public class AuthorRepository {

    @PersistenceContext
    EntityManager em;

    public Author add(Author author) {
        em.persist(author);
        return author;
    }

    public Author findById(Long id) {
        if(id == null){
            return null;
        }
        
        return em.find(Author.class, id);
    }

    public void update(Author author) {
        em.merge(author);
    }
    
    public boolean existsById(Long id){
        return em.createQuery("SELECT 1 FROM Author e where e.id = :id")
                .setParameter("id", id)
                .setMaxResults(1)
                .getResultList().size() > 0;
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
            clauseSort.append(filter.getPaginationData().isAscending() ? "ASC" : "DESC");
        } else {
            clauseSort.append("ORDER BY e.name ASC");
        }
        
        Query queryAuthors = em.createQuery("SELECT e FROM Author e " + clause.toString() + " " + clauseSort.toString());
        for(Entry<String, Object> entryMap : queryParameters.entrySet()){
            queryAuthors.setParameter(entryMap.getKey(), entryMap.getValue());
        }
        
        if(filter.hasPaginationData()){
            queryAuthors.setFirstResult(filter.getPaginationData().getFirstResult());
            queryAuthors.setMaxResults(filter.getPaginationData().getMaxResults());
        }
        
        List<Author> authors = queryAuthors.getResultList();
        
        Query queryCount = em.createQuery("SELECT Count(e) FROM Author e " + clause.toString());
        for(Entry<String, Object> entryMap : queryParameters.entrySet()){
            queryCount.setParameter(entryMap.getKey(), entryMap.getValue());
        }
        
        Integer count = ((Long) queryCount.getSingleResult()).intValue();
        
        return new PaginatedData<>(count, authors);
    }
    
}
