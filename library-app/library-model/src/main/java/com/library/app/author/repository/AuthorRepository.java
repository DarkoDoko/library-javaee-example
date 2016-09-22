package com.library.app.author.repository;

import com.library.app.author.model.Author;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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
    
}
