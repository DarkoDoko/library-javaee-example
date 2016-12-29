package com.library.app.commontests.utils;

import com.library.app.author.model.Author;
import com.library.app.book.model.Book;
import com.library.app.category.model.Category;
import com.library.app.logaudit.model.LogAudit;
import com.library.app.order.model.Order;
import com.library.app.user.model.User;
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
    
    private static final List<Class<?>> ENTITIES_TO_REMOVE = Arrays.asList(LogAudit.class, Order.class, Book.class, User.class,
        Category.class, Author.class);
    
    public void deleteAll(){
        ENTITIES_TO_REMOVE.forEach((entityClass) -> deleteAllForEntity(entityClass));
    }
    
    public void add(Object entity){
        em.persist(entity);
    }
    
    private void deleteAllForEntity(Class<?> entityClass){
        List<Object> rows = em.createQuery("SELECT e FROM " + entityClass.getSimpleName() + " e").getResultList();
        rows.forEach((row) -> em.remove(row));
    }
}
