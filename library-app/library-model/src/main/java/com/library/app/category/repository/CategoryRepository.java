package com.library.app.category.repository;

import com.library.app.category.model.Category;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.Query;

@Stateless
public class CategoryRepository {
    
    EntityManager em;
    
    public Category add(Category category){
        em.persist(category);
        return category;
    }
    
    public Category findById(Long id){
        if(id == null){
            return null;
        }
        return em.find(Category.class, id);
    }

    public void update(Category category) {
        em.merge(category);
    }

    public List<Category> findAll(String orderField) {
        return em.createQuery("SELECT e FROM Category e ORDER BY e." + orderField).getResultList();
    }
    
    public boolean alreadyExists(Category category){
        StringBuilder jpql = new StringBuilder();
        jpql.append("SELECT 1 FROM Category e WHERE e.name = :name");
        
        if(category.getId() != null){
            jpql.append(" AND e.id != :id");
        }
        
        Query query = em.createQuery(jpql.toString());
        query.setParameter("name", category.getName());
        if(category.getId() != null){
            query.setParameter("id", category.getId());
        }
        
        return query.setMaxResults(1).getResultList().size() > 0;
    }

    public boolean existsById(Long id) {
        return em.createQuery("SELECT 1 FROM Category c WHERE c.id = :id")
                        .setParameter("id", id)
                        .setMaxResults(1)
                        .getResultList().size() > 0;
    }

}
