package com.library.app.author.repository;

import com.library.app.GenericRepository;
import com.library.app.author.model.Author;
import com.library.app.author.model.AuthorFilter;
import com.library.app.pagination.PaginatedData;
import java.util.HashMap;
import java.util.Map;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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
        
        return findByParameters(clause.toString(), filter.getPaginationData(), queryParameters, "name ASC");
    }
}
