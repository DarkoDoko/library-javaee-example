package com.library.app.book.services.impl;

import com.library.app.FieldNotValidException;
import static com.library.app.author.AuthorForTestsRepository.erichGamma;
import static com.library.app.author.AuthorForTestsRepository.robertMartin;
import com.library.app.author.AuthorNotFoundException;
import com.library.app.author.services.AuthorServices;
import static com.library.app.book.BookArgumentMatcher.bookEq;
import static com.library.app.book.BookForTestsRepository.bookWithId;
import static com.library.app.book.BookForTestsRepository.cleanCode;
import static com.library.app.book.BookForTestsRepository.designPatterns;
import com.library.app.book.BookNotFoundException;
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
import static org.mockito.Matchers.anyLong;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

public class BookServicesTest {
    
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
    
    @Test(expected = CategoryNotFoundException.class)
	public void addBookWithInexistentCategory() throws Exception{
		when(categoryServices.findById(1L)).thenThrow(new CategoryNotFoundException());

		final Book book = cleanCode();
		book.getCategory().setId(1L);

        bookServicesUnderTest.add(book);
	}

	@Test(expected = AuthorNotFoundException.class)
	public void addBookWithInexistentAuthor() throws Exception {
		when(categoryServices.findById(anyLong())).thenReturn(designPatterns().getCategory());
		when(authorServices.findById(1L)).thenReturn(erichGamma());
		when(authorServices.findById(2L)).thenThrow(new AuthorNotFoundException());

		final Book book = designPatterns();
		book.getAuthors().get(0).setId(1L);
		book.getAuthors().get(1).setId(2L);

		bookServicesUnderTest.add(book);
	}

	@Test
	public void addValidBook() throws Exception {
		when(categoryServices.findById(anyLong())).thenReturn(cleanCode().getCategory());
		when(authorServices.findById(anyLong())).thenReturn(robertMartin());
		when(bookRepository.add(bookEq(cleanCode()))).thenReturn(bookWithId(cleanCode(), 1L));

		final Book bookAdded = bookServicesUnderTest.add(cleanCode());
		assertThat(bookAdded.getId(), equalTo(1L));
	}
    
    @Test
	public void updateAuthorWithShortTitle() {
		final Book book = cleanCode();
		book.setTitle("short");
		try {
			bookServicesUnderTest.update(book);
			fail("An error should have been thrown");
		} catch (final FieldNotValidException e) {
			assertThat(e.getFieldName(), is(equalTo("title")));
		} catch (final Exception e) {
			fail("An Exception should not have been thrown");
		}
	}

	@Test(expected = BookNotFoundException.class)
	public void updateBookNotFound() throws Exception {
		when(bookRepository.existsById(1L)).thenReturn(false);

		bookServicesUnderTest.update(bookWithId(cleanCode(), 1L));
	 }

	@Test(expected = CategoryNotFoundException.class)
	public void updateBookWithInexistentCategory() throws Exception {
		when(bookRepository.existsById(1L)).thenReturn(true);
		when(categoryServices.findById(1L)).thenThrow(new CategoryNotFoundException());

		final Book book = bookWithId(cleanCode(), 1L);
		book.getCategory().setId(1L);

		bookServicesUnderTest.update(book);
	}

	@Test(expected = AuthorNotFoundException.class)
	public void updateBookWithInexistentAuthor() throws Exception {
		when(bookRepository.existsById(1L)).thenReturn(true);
		when(categoryServices.findById(anyLong())).thenReturn(cleanCode().getCategory());
		when(authorServices.findById(1L)).thenReturn(erichGamma());
		when(authorServices.findById(2L)).thenThrow(new AuthorNotFoundException());

		final Book book = bookWithId(designPatterns(), 1L);
		book.getAuthors().get(0).setId(1L);
		book.getAuthors().get(1).setId(2L);

		bookServicesUnderTest.update(book);
	}

	@Test
	public void updateValidBook() throws Exception {
		final Book bookToUpdate = bookWithId(cleanCode(), 1L);
		when(categoryServices.findById(anyLong())).thenReturn(cleanCode().getCategory());
		when(authorServices.findById(anyLong())).thenReturn(robertMartin());
		when(bookRepository.existsById(1L)).thenReturn(true);

		bookServicesUnderTest.update(bookToUpdate);
		verify(bookRepository).update(bookEq(bookToUpdate));
	}
    
    private void addBookWithInvalidField(final Book book, final String expectedInvalidFieldName) {
		try {
			bookServicesUnderTest.add(book);
			fail("An error should have been thrown");
		} catch (final FieldNotValidException e) {
			assertThat(e.getFieldName(), is(equalTo(expectedInvalidFieldName)));
		} catch (CategoryNotFoundException ex) {
            Logger.getLogger(BookServicesTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (AuthorNotFoundException ex) {
            Logger.getLogger(BookServicesTest.class.getName()).log(Level.SEVERE, null, ex);
        }
	}
    
}
