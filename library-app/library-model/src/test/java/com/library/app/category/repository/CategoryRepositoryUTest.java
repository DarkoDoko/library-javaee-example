package com.library.app.category.repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CategoryRepositoryUTest {
    
    private EntityManagerFactory emf;
    private EntityManager em;
    
    @Before
    public void initTestCase() {
        emf = Persistence.createEntityManagerFactory("libraryPU");
        em = emf.createEntityManager();
    }
    
    @After
    public void closeEM(){
        em.close();
        emf.close();
    }
    
    @Test
    public void addCategoryAndFindIt(){
        
        Long categoryAddedId = null;
        
        try{
            
            em.getTransaction().begin();


            em.getTransaction().commit();
            em.clear();
        } catch(Exception e){
            em.getTransaction().rollback();
        }        
    }
}
