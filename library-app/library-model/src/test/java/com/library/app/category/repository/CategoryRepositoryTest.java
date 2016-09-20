package com.library.app.category.repository;

import com.library.app.category.model.Category;
import static com.library.app.category.CategoryForTestsRepository.allCategories;
import static com.library.app.category.CategoryForTestsRepository.architecture;
import static com.library.app.category.CategoryForTestsRepository.cleanCode;
import static com.library.app.category.CategoryForTestsRepository.java;
import static com.library.app.category.CategoryForTestsRepository.networks;
import com.library.app.commontests.utils.DBCommandTransactionalExecutor;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import static org.hamcrest.CoreMatchers.equalTo;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class CategoryRepositoryTest {
    
    private EntityManagerFactory emf;
    private EntityManager em;
    private DBCommandTransactionalExecutor executor; 
    
    private CategoryRepository repositoryUnderTest;
    
    @Before
    public void initTestCase() {
        emf = Persistence.createEntityManagerFactory("libraryPU");
        em = emf.createEntityManager();
        
        repositoryUnderTest = new CategoryRepository();
        repositoryUnderTest.em = em;
        
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
            return repositoryUnderTest.add(java()).getId();
        });
        
        assertThat(categoryAddedId, is(notNullValue()));
        
        Category category = repositoryUnderTest.findById(categoryAddedId);
        assertThat(category, is(notNullValue()));
        assertThat(category.getName(), is(equalTo(java().getName())));
    }
    
    @Test
    public void findCategoryByIdNotFound(){
        Category cat = repositoryUnderTest.findById(999L);
        assertThat(cat, is(nullValue()));
    }
    
    @Test
    public void findCategoryByIdWithNullId() {
        Category category = repositoryUnderTest.findById(null);
        assertThat(category, is(nullValue()));
    }
    
    @Test
    public void updateCategory(){
        Long categoryAddedId = executor.executeCommand(() -> {
            return repositoryUnderTest.add(java()).getId();
        });
        
        Category categoryAfterAdd = repositoryUnderTest.findById(categoryAddedId);
        assertThat(categoryAfterAdd.getName(), is(equalTo(java().getName())));
        
        categoryAfterAdd.setName(cleanCode().getName());
        executor.executeCommand(() -> {
            repositoryUnderTest.update(categoryAfterAdd);
            return null;
        });
        
        Category categoryAfterUpdate = repositoryUnderTest.findById(categoryAddedId);
        assertThat(categoryAfterUpdate.getName(), is(equalTo(cleanCode().getName())));
    }
    
    @Test
    public void findAllCategories(){
        executor.executeCommand(() -> {
            allCategories().forEach(repositoryUnderTest::add);
            return null;
        });
        
        List<Category> categories = repositoryUnderTest.findAll("name");
        
        assertThat(categories.size(), is(equalTo(4)));
        assertThat(categories.get(0).getName(), is(equalTo(architecture().getName())));
        assertThat(categories.get(1).getName(), is(equalTo(cleanCode().getName())));
        assertThat(categories.get(2).getName(), is(equalTo(java().getName())));
        assertThat(categories.get(3).getName(), is(equalTo(networks().getName())));
    }
    
    @Test
    public void alreadyExistsForAdd(){
        executor.executeCommand(() -> {
            repositoryUnderTest.add(java());
            return null;
        });
        assertThat("Existing element reported as non-existing.", 
                repositoryUnderTest.alreadyExists(java()), is(equalTo(true)));
        assertThat("Non-existing element reported as existing.", 
                repositoryUnderTest.alreadyExists(cleanCode()), is(equalTo(false)));
    }
    
    @Test
    public void alreadyExistsCategoryWithId(){
        Category java = executor.executeCommand(() -> {
            repositoryUnderTest.add(cleanCode());
            return repositoryUnderTest.add(java());
        });
        
        assertThat(repositoryUnderTest.alreadyExists(java), is(equalTo(false)));
        
        java.setName(cleanCode().getName());
        assertThat(repositoryUnderTest.alreadyExists(java), is(equalTo(true)));
        
        java.setName(networks().getName());
        assertThat(repositoryUnderTest.alreadyExists(java), is(equalTo(false)));
    }
    
    @Test
    public void existsById(){
        Long categoryAddedId = executor.executeCommand(() -> {
            return repositoryUnderTest.add(java()).getId();
        });

        assertThat(repositoryUnderTest.existsById(categoryAddedId), is(equalTo(true)));
        assertThat(repositoryUnderTest.existsById(999L), is(equalTo(false)));
        
    }
}
