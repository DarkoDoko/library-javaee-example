package com.library.app.author.repository;

import static com.library.app.author.AuthorForTestsRepository.erichGamma;
import static com.library.app.author.AuthorForTestsRepository.jamesGosling;
import static com.library.app.author.AuthorForTestsRepository.martinFowler;
import static com.library.app.author.AuthorForTestsRepository.robertMartin;
import com.library.app.author.model.Author;
import com.library.app.author.model.AuthorFilter;
import com.library.app.pagination.PaginatedData;
import com.library.app.pagination.filter.PaginationData;
import com.library.app.pagination.filter.PaginationData.OrderMode;
import com.library.app.commontests.utils.TestBaseRepository;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import org.junit.After;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;

public class AuthorRepositoryTests extends TestBaseRepository {
    
    private AuthorRepository repositoryUnderTest;
    
    @Before
    public void initTest(){

        initializeTestDB();
        
        repositoryUnderTest = new AuthorRepository();
        repositoryUnderTest.em = em;
    }
    
    @After
    public void setDownTestCase(){
        closeEntityManager();
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
        
        assertThat(author, is(nullValue()));
    }
    
    @Test
    public void updateAuthor(){
        Long authorAddedId = dbExecutor.executeCommand(() -> {
            return repositoryUnderTest.add(robertMartin()).getId();
        });
        assertThat(authorAddedId, is(notNullValue()));
        
        Author author = repositoryUnderTest.findById(authorAddedId);
        assertThat(author.getName(), is(equalTo(robertMartin().getName())));
        
        author.setName("Uncle Bob");
        dbExecutor.executeCommand(() -> {
            repositoryUnderTest.update(author);
            return null;
        });
        
        Author authorAfterUpdate = repositoryUnderTest.findById(authorAddedId);
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
    
    @Test
    public void findByFilterNoFilter(){
        loadDataForFindByFilter();
        
        PaginatedData<Author> result = repositoryUnderTest.findByFilter(new AuthorFilter());
        
        assertThat(result.getNumberOfRows(), is(equalTo(4)));
        assertThat(result.getRows().size(), is(equalTo(4)));
        
        assertThat(result.getRow(0).getName(), is(equalTo(erichGamma().getName())));
        assertThat(result.getRow(1).getName(), is(equalTo(jamesGosling().getName())));
        assertThat(result.getRow(2).getName(), is(equalTo(martinFowler().getName())));
        assertThat(result.getRow(3).getName(), is(equalTo(robertMartin().getName())));
    }
    
    @Test
    public void findByFilterFilteringNameAndOrderingDescending() {
        loadDataForFindByFilter();

        final AuthorFilter authorFilter = new AuthorFilter();
        authorFilter.setName("o");
        authorFilter.setPaginationData(new PaginationData(0, 2, "name", OrderMode.DESCENDING));

        PaginatedData<Author> result = repositoryUnderTest.findByFilter(authorFilter);
        
        assertThat(result.getNumberOfRows(), is(equalTo(3)));
        assertThat(result.getRows().size(), is(equalTo(2)));
        
        assertThat(result.getRow(0).getName(), is(equalTo(robertMartin().getName())));
        assertThat(result.getRow(1).getName(), is(equalTo(martinFowler().getName())));

        authorFilter.setPaginationData(new PaginationData(2, 2, "name", OrderMode.DESCENDING));
        result = repositoryUnderTest.findByFilter(authorFilter);

        assertThat(result.getNumberOfRows(), is(equalTo(3)));
        assertThat(result.getRows().size(), is(equalTo(1)));
        assertThat(result.getRow(0).getName(), is(equalTo(jamesGosling().getName())));
    }

    private void loadDataForFindByFilter() {
        dbExecutor.executeCommand(() -> {
            repositoryUnderTest.add(robertMartin());
            repositoryUnderTest.add(jamesGosling());
            repositoryUnderTest.add(martinFowler());
            repositoryUnderTest.add(erichGamma());            
            return null;
        });
    }
    
}
