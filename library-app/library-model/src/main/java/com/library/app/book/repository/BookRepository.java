package com.library.app.book.repository;

import com.library.app.GenericRepository;
import com.library.app.book.model.Book;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class BookRepository extends GenericRepository<Book>{
    
    @PersistenceContext
    EntityManager em;

    @Override
    protected Class<Book> getPersistentClass() {
        return Book.class;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
}
