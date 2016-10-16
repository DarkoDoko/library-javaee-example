package com.library.app.author.services.impl;

import com.library.app.FieldNotValidException;
import static com.library.app.author.AuthorForTestsRepository.authorWithId;
import static com.library.app.author.AuthorForTestsRepository.robertMartin;
import com.library.app.author.AuthorNotFoundException;
import com.library.app.author.model.Author;
import com.library.app.author.model.AuthorFilter;
import com.library.app.author.repository.AuthorRepository;
import com.library.app.author.services.AuthorServices;
import com.library.app.pagination.PaginatedData;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.validation.Validation;
import javax.validation.Validator;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import static org.mockito.Matchers.anyObject;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

public class AuthorServicesTest {

    private static Validator validatorCollaborator;
    private AuthorServices servicesUnderTest;

    @Mock
    private AuthorRepository repositoryCollaborator;

    @BeforeClass
    public static void initializeTestClass() {
        validatorCollaborator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Before
    public void initializeTestCase() {
        MockitoAnnotations.initMocks(this);

        servicesUnderTest = new AuthorServicesImpl();

        ((AuthorServicesImpl) servicesUnderTest).repository = repositoryCollaborator;
        ((AuthorServicesImpl) servicesUnderTest).validator = validatorCollaborator;
    }

    @Test
    public void addAuthorWithNullName() {
        addAuthorWithInvalidName(null);
    }

    @Test
    public void addAuthorWithShortName() {
        addAuthorWithInvalidName("A");
    }

    @Test
    public void addAuthorWithLongName() {
        addAuthorWithInvalidName("This is a very long name that will cause an exception to be thrown");
    }

    @Test
    public void addValidAuthor() {
        when(repositoryCollaborator.add(robertMartin())).thenReturn(authorWithId(robertMartin(), 1L));

        try {
            Author authorAdded = servicesUnderTest.add(robertMartin());
            assertThat(authorAdded.getId(), is(equalTo(1L)));
        } catch (FieldNotValidException e) {
            fail("No error should have been thrown");
        }
    }

    @Test
    public void updateAuthorWithNullName() {
        updateAuthorWithInvalidName(null);
    }

    @Test
    public void updateAuthorWithShortName() {
        updateAuthorWithInvalidName("A");
    }

    @Test
    public void updateAuthorWithLongName() {
        updateAuthorWithInvalidName("This is a very long name that will cause an exception to be thrown");
    }
    
    @Test(expected = AuthorNotFoundException.class)
    public void updateAuthorNotFound() throws Exception {
        when(repositoryCollaborator.existsById(1L)).thenReturn(false);

        servicesUnderTest.update(authorWithId(robertMartin(), 1L));
    }

    @Test
    public void updateValidAuthor() throws Exception {
        final Author authorToUpdate = authorWithId(robertMartin(), 1L);
        when(repositoryCollaborator.existsById(1L)).thenReturn(true);

        servicesUnderTest.update(authorToUpdate);
        verify(repositoryCollaborator).update(authorToUpdate);
    }

    @Test(expected = AuthorNotFoundException.class)
    public void findAuthorByIdNotFound() throws AuthorNotFoundException {
        when(repositoryCollaborator.findById(1L)).thenReturn(null);

        servicesUnderTest.findById(1L);
    }

    @Test
    public void findAuthorById() throws AuthorNotFoundException {
        when(repositoryCollaborator.findById(1L)).thenReturn(authorWithId(robertMartin(), 1L));

        final Author author = servicesUnderTest.findById(1L);
        assertThat(author, is(notNullValue()));
        assertThat(author.getName(), is(equalTo(robertMartin().getName())));
    }

    @Test
    public void findAuthorByFilter() {
        final PaginatedData<Author> authors = new PaginatedData<>(1, Arrays.asList(authorWithId(robertMartin(),
                        1L)));
        when(repositoryCollaborator.findByFilter((AuthorFilter) anyObject())).thenReturn(authors);

        final PaginatedData<Author> authorsReturned = servicesUnderTest.findByFilter(new AuthorFilter());
        assertThat(authorsReturned.getNumberOfRows(), is(equalTo(1)));
        assertThat(authorsReturned.getRow(0).getName(), is(equalTo(robertMartin().getName())));
}

    private void updateAuthorWithInvalidName(final String name) {
        try {
            servicesUnderTest.update(new Author(name));
            fail("An error should have been thrown");
        } catch (final FieldNotValidException e) {
            assertThat(e.getFieldName(), is(equalTo("name")));
        } catch (AuthorNotFoundException ex) {
            Logger.getLogger(AuthorServicesTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void addAuthorWithInvalidName(final String name) {
        try {
            servicesUnderTest.add(new Author(name));
            fail("An error should have been thrown");
        } catch (final FieldNotValidException e) {
            assertThat(e.getFieldName(), is(equalTo("name")));
        }
    }
}
