package com.library.app.user.repository;

import com.library.app.GenericRepository;
import com.library.app.user.model.User;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class UserRepository extends GenericRepository<User>{

    @PersistenceContext
    EntityManager em;
    
    @Override
    protected Class<User> getPersistentClass() {
        return User.class;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
}
