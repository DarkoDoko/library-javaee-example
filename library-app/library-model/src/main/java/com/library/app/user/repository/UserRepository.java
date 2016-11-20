package com.library.app.user.repository;

import com.library.app.GenericRepository;
import com.library.app.pagination.PaginatedData;
import com.library.app.user.model.User;
import com.library.app.user.model.filter.UserFilter;
import java.util.HashMap;
import java.util.Map;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Stateless
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

    public User findByEmail(String email) {
        try {
            return (User) em.createQuery("SELECT e FROM User e WHERE e.email = :email")
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public PaginatedData<User> findByFilter(UserFilter userFilter) {
        final StringBuilder clause = new StringBuilder("WHERE e.id IS NOT NULL");
        final Map<String, Object> queryParameters = new HashMap<>();
        if (userFilter.getName() != null) {
            clause.append(" AND UPPER(e.name) LIKE UPPER(:name)");
            queryParameters.put("name", "%" + userFilter.getName() + "%");
        }
        if (userFilter.getUserType() != null) {
            clause.append(" AND e.userType = :userType");
            queryParameters.put("userType", userFilter.getUserType());
        }
        return findByParameters(clause.toString(), userFilter.getPaginationData(), queryParameters, "name ASC");
    }

}
