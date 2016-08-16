package com.library.app.category.repository;

import com.library.app.category.model.Category;
import static com.library.app.commontests.category.CategoryForTestsRepository.java;
import com.library.app.commontests.utils.DBCommandTransactionalExecutor;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import static org.hamcrest.CoreMatchers.equalTo;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class CategoryRepositoryUTest {
    
    private EntityManagerFactory emf;
    private EntityManager em;
    private CategoryRepository cr;
    private DBCommandTransactionalExecutor executor; 
    
    @Before
    public void initTestCase() {
        emf = Persistence.createEntityManagerFactory("libraryPU");
        em = emf.createEntityManager();
        
        cr = new CategoryRepository();
        cr.em = em;
        
        executor = new DBCommandTransactionalExecutor(em);
    }
    
    @After
    public void closeEM(){
        em.close();
        emf.close();
    }
    
    @Test
    public void addCategoryAndFindIt(){
        
        Long categoryAddedId = executor.executeCommand(() -> {
            return cr.add(java()).getId();
        });
        
        assertThat(categoryAddedId, is(notNullValue()));
        
        Category category = cr.findById(categoryAddedId);
        assertThat(category, is(notNullValue()));
        assertThat(category.getName(), is(equalTo(java().getName())));
    }
}
