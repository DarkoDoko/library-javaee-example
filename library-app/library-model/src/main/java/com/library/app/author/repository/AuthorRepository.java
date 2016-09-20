package com.library.app.author.repository;

import com.library.app.author.model.Author;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;

@Stateless
public class AuthorRepository {

    EntityManager em;

    Author add(Author author) {
        em.persist(author);
        return author;
    }

    Author findById(Long id) {
        if(id == null){
            return null;
        }
        
        return em.find(Author.class, id);
    }
    
}
