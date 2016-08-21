package com.library.app.category.repository;

import com.library.app.category.model.Category;
import java.util.List;
import javax.persistence.EntityManager;

/**
 *
 * @author ddoko
 */
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
}
