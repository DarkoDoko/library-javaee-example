package com.library.app.user.repository;

import com.library.app.GenericRepository;
import com.library.app.user.model.User;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

public class UserRepository extends GenericRepository<User> {

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

    public boolean alreadyExists(User user) {
        return alreadyExists("email", user.getEmail(), user.getId());
    }

    public User findByEmail(final String email) {
        try {
            return (User) em.createQuery("SELECT e FROM User e WHERE e.email = :email")
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (final NoResultException e) {
            return null;
        }
    }

}
