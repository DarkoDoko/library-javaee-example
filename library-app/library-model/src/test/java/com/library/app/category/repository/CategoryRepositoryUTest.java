package com.library.app.category.repository;

import com.library.app.category.model.Category;
import static com.library.app.commontests.category.CategoryForTestsRepository.java;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import org.junit.After;
import org.junit.Assert;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;

public class CategoryRepositoryUTest {
    
    private EntityManagerFactory emf;
    private EntityManager em;
    private CategoryRepository cr;
    
    @Before
    public void initTestCase() {
        emf = Persistence.createEntityManagerFactory("libraryPU");
        em = emf.createEntityManager();
        
        cr = new CategoryRepository();
        cr.em = em;
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
            categoryAddedId = cr.add(java()).getId();

            assertThat(categoryAddedId, is(notNullValue()));

            em.getTransaction().commit();
            em.clear();
        } catch(Exception e){
            fail("Exception should not have been thrown");
            em.getTransaction().rollback();
        }
        
        Category category = cr.findById(categoryAddedId);
        assertThat(category, is(notNullValue()));
        assertThat(category.getName(), is(equalTo(java().getName())));
    }
}
