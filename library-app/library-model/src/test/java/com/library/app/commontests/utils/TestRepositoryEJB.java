package com.library.app.commontests.utils;

import com.library.app.category.model.Category;
import java.util.Arrays;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.Ignore;

@Ignore
@Stateless
public class TestRepositoryEJB {
    
    @PersistenceContext
    private EntityManager em;
    
    private static final List<Class<?>> ENTITIES_TO_REMOVE = Arrays.asList(Category.class);
    
    public void deleteAll(){
        for(Class<?> entityClass : ENTITIES_TO_REMOVE){
            deleteAllForEntity(entityClass);
        }
    }
    
    private void deleteAllForEntity(Class<?> entityClass){
        List<Object> rows = em.createQuery("SELECT e FROM " + entityClass.getSimpleName() + " e").getResultList();
        for(Object row : rows){
            em.remove(row);
        }
    }
}
