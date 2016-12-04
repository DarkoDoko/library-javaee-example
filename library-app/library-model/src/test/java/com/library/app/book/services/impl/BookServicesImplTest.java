package com.library.app.book.services.impl;

import com.library.app.FieldNotValidException;
import com.library.app.author.AuthorNotFoundException;
import com.library.app.author.services.AuthorServices;
import static com.library.app.book.BookForTestsRepository.cleanCode;
import com.library.app.book.model.Book;
import com.library.app.book.repository.BookRepository;
import com.library.app.book.services.BookServices;
import com.library.app.category.CategoryNotFoundException;
import com.library.app.category.services.CategoryServices;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.validation.Validation;
import javax.validation.Validator;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class BookServicesImplTest {
    
    private static Validator validator;
    private BookServices bookServicesUnderTest;
    
    @Mock
    private BookRepository bookRepository;
    
    @Mock
    private CategoryServices categoryServices;
    
    @Mock
    private AuthorServices authorServices;
    
    @BeforeClass
    public static void initTestClass() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }
    
    @Before
    public void initTestCase(){
        MockitoAnnotations.initMocks(this);
        
        bookServicesUnderTest = new BookServicesImpl();
        
        ((BookServicesImpl) bookServicesUnderTest).bookRepository = bookRepository;
        ((BookServicesImpl) bookServicesUnderTest).validator = validator;
        ((BookServicesImpl) bookServicesUnderTest).authorServices = authorServices;
        ((BookServicesImpl) bookServicesUnderTest).categoryServices = categoryServices;
    }
    
    @Test
	public void addBookWithNullTitle() {
		Book book = cleanCode();
		book.setTitle(null);
		addBookWithInvalidField(book, "title");
	}

	@Test
	public void addBookWithNullCategory() {
		final Book book = cleanCode();
		book.setCategory(null);
		addBookWithInvalidField(book, "category");
	}

	@Test
	public void addBookWithNoAuthors() {
		final Book book = cleanCode();
		book.setAuthors(new ArrayList<>());
		addBookWithInvalidField(book, "authors");
	}

	@Test
	public void addBookWithShortDescription() {
		final Book book = cleanCode();
		book.setDescription("short");
		addBookWithInvalidField(book, "description");
	}

	@Test
	public void addBookWithNullPrice() {
		final Book book = cleanCode();
		book.setPrice(null);
		addBookWithInvalidField(book, "price");
	}
    
    private void addBookWithInvalidField(final Book book, final String expectedInvalidFieldName) {
		try {
			bookServicesUnderTest.add(book);
			fail("An error should have been thrown");
		} catch (final FieldNotValidException e) {
			assertThat(e.getFieldName(), is(equalTo(expectedInvalidFieldName)));
		} catch (CategoryNotFoundException ex) {
            Logger.getLogger(BookServicesImplTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (AuthorNotFoundException ex) {
            Logger.getLogger(BookServicesImplTest.class.getName()).log(Level.SEVERE, null, ex);
        }
	}
    
}
