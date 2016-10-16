package com.library.app.category.repository;

import com.library.app.GenericRepository;
import com.library.app.category.model.Category;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class CategoryRepository extends GenericRepository<Category>{
    
    @PersistenceContext
    EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    protected Class<Category> getPersistentClass(){
        return Category.class;
    }
        
    public boolean alreadyExists(Category category){
        return alreadyExists("name", category.getName(), category.getId());
    }

}
