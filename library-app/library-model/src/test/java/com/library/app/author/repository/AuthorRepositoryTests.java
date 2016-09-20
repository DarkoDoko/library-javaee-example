package com.library.app.author.repository;

import com.library.app.author.AuthorForTestsRepository;
import static com.library.app.author.AuthorForTestsRepository.robertMartin;
import com.library.app.author.model.Author;
import com.library.app.commontests.utils.DBCommandTransactionalExecutor;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import org.junit.After;
import org.junit.Assert;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;

public class AuthorRepositoryTests {
    
    private EntityManagerFactory emf;
    private EntityManager em;
    private DBCommandTransactionalExecutor dbExecutor;
    private AuthorRepository repositoryUnderTest;
    
    @Before
    public void initTest(){
        emf = Persistence.createEntityManagerFactory("libraryPU");
        em = emf.createEntityManager();
        
        repositoryUnderTest = new AuthorRepository();
        repositoryUnderTest.em = em;
        
        dbExecutor = new DBCommandTransactionalExecutor(em);
    }
    
    @After
    public void closeEntityManager(){
        em.close();
        emf.close();
    }
    
    @Test
    public void addAuthorAndFindIt(){
        Long authorAddedId = dbExecutor.executeCommand(() -> {
            return repositoryUnderTest.add(robertMartin()).getId();
        });
        
        assertThat(authorAddedId, is(notNullValue()));
        
        Author author = repositoryUnderTest.findById(authorAddedId);
        
        assertThat(author, is(notNullValue()));
        assertThat(author.getName(), is(equalTo(robertMartin().getName())));
    }
    
}
