package com.library.app.commontests.utils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.junit.Ignore;

@Ignore
public class TestBaseRepository {
    private EntityManagerFactory emf;

    protected EntityManager em;
    protected DBCommandTransactionalExecutor dbExecutor;

    protected void initializeTestDB() {
        emf = Persistence.createEntityManagerFactory("libraryPU");
        em = emf.createEntityManager();

        dbExecutor = new DBCommandTransactionalExecutor(em);
    }

    protected void closeEntityManager() {
        em.close();
        emf.close();
    }
}