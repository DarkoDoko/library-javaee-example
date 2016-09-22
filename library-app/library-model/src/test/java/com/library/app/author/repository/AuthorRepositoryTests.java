package com.library.app.author.repository;

import com.library.app.author.AuthorForTestsRepository;
import static com.library.app.author.AuthorForTestsRepository.robertMartin;
import com.library.app.author.model.Author;
import com.library.app.commontests.utils.DBCommandTransactionalExecutor;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.hamcrest.CoreMatchers;
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
    
    @Test
    public void findAuthorByIdNotFound(){
        Author author = repositoryUnderTest.findById(999L);
        
        assertThat(author, is(CoreMatchers.nullValue()));
        
    }
    
    @Test
    public void updateAuthor(){
        Long authorAddedId = dbExecutor.executeCommand(() -> {
            return repositoryUnderTest.add(robertMartin()).getId();
        });
        assertThat(authorAddedId, is(notNullValue()));
        
        Author author = repositoryUnderTest.findById(authorAddedId);
        assertThat(author, is(notNullValue()));
        assertThat(author.getName(), is(equalTo(robertMartin().getName())));
        
        author.setName("Uncle Bob");
        dbExecutor.executeCommand(() -> {
            repositoryUnderTest.update(author);
            return null;
        });
        
        Author authorAfterUpdate = repositoryUnderTest.findById(authorAddedId);
        assertThat(authorAfterUpdate, is(notNullValue()));
        assertThat(authorAfterUpdate.getName(), is(equalTo("Uncle Bob")));
    }
    
    @Test
    public void existsById(){
        Long authorAddedId = dbExecutor.executeCommand(() -> {
            return repositoryUnderTest.add(robertMartin()).getId();
        });
        
        assertThat(repositoryUnderTest.existsById(authorAddedId), is(equalTo(true)));
        assertThat(repositoryUnderTest.existsById(999L), is(equalTo(false)));
    }
}
