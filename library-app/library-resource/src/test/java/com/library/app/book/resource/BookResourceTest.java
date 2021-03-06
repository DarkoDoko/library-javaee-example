package com.library.app.book.resource;

import com.library.app.FieldNotValidException;
import com.library.app.author.AuthorNotFoundException;
import com.library.app.author.model.Author;
import com.library.app.author.resource.AuthorJsonConverter;
import static com.library.app.book.BookArgumentMatcher.bookEq;
import static com.library.app.book.BookForTestsRepository.bookWithId;
import static com.library.app.book.BookForTestsRepository.cleanCode;
import static com.library.app.book.BookForTestsRepository.designPatterns;
import com.library.app.book.BookNotFoundException;
import com.library.app.book.model.Book;
import com.library.app.book.model.BookFilter;
import com.library.app.book.services.BookServices;
import com.library.app.category.CategoryNotFoundException;
import com.library.app.category.model.Category;
import com.library.app.category.resource.CategoryJsonConverter;
import com.library.app.common.model.HttpCode;
import static com.library.app.commontests.utils.FileTestNameUtils.getPathFileRequest;
import static com.library.app.commontests.utils.FileTestNameUtils.getPathFileResponse;
import static com.library.app.commontests.utils.JsonTestUtils.assertJsonMatchesExpectedJson;
import static com.library.app.commontests.utils.JsonTestUtils.assertJsonMatchesFileContent;
import static com.library.app.commontests.utils.JsonTestUtils.readJsonFile;
import com.library.app.pagination.PaginatedData;
import java.util.Arrays;
import java.util.List;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Matchers.anyObject;
import org.mockito.Mock;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

public class BookResourceTest {
    
    private BookResource bookResourceUnderTest;
    
    @Mock
    private BookServices bookServices;
    
    @Mock
    private UriInfo uriInfo;
    
    private static final String PATH_RESOURCE = "books";
    
    @Before
    public void initTestCase(){
        MockitoAnnotations.initMocks(this);

		bookResourceUnderTest = new BookResource();

		BookJsonConverter bookJsonConverter = new BookJsonConverter();
		bookJsonConverter.categoryJsonConverter = new CategoryJsonConverter();
		bookJsonConverter.authorJsonConverter = new AuthorJsonConverter();

		bookResourceUnderTest.bookServices = bookServices;
		bookResourceUnderTest.uriInfo = uriInfo;
		bookResourceUnderTest.bookJsonConverter = bookJsonConverter;
        
    }
    
    @Test
	public void addValidBook() throws Exception {
		Book expectedBook = cleanCode();
		expectedBook.setCategory(new Category(1L));
		expectedBook.setAuthors(Arrays.asList(new Author(2L)));
		when(bookServices.add(bookEq(expectedBook))).thenReturn(bookWithId(cleanCode(), 1L));

		Response response = bookResourceUnderTest.add(readJsonFile(getPathFileRequest(PATH_RESOURCE, "cleanCode.json")));
		assertThat(response.getStatus(), is(equalTo(HttpCode.CREATED.getCode())));
		assertJsonMatchesExpectedJson(response.getEntity().toString(), "{\"id\": 1}");
	}

	@Test
	public void addBookWithNullTitle() throws Exception {
		addBookWithValidationError(new FieldNotValidException("title", "may not be null"), "cleanCode.json",
				"bookErrorNullTitle.json");
	}

	@Test
	public void addBookWithInexistentCategory() throws Exception {
		addBookWithValidationError(new CategoryNotFoundException(), "cleanCode.json",
				"bookErrorInexistentCategory.json");
	}

	@Test
	public void addBookWithInexistentAuthor() throws Exception {
		addBookWithValidationError(new AuthorNotFoundException(), "cleanCode.json", "bookErrorInexistentAuthor.json");
	}
    
    @Test
	public void updateBookWithNullTitle() throws Exception {
		updateBookWithError(new FieldNotValidException("title", "may not be null"), HttpCode.VALIDATION_ERROR,
				"cleanCode.json", "bookErrorNullTitle.json");
	}

	@Test
	public void updateBookWithInexistentCategory() throws Exception {
		updateBookWithError(new CategoryNotFoundException(), HttpCode.VALIDATION_ERROR, "cleanCode.json",
				"bookErrorInexistentCategory.json");
	}

	@Test
	public void updateBookWithInexistentAuthor() throws Exception {
		updateBookWithError(new AuthorNotFoundException(), HttpCode.VALIDATION_ERROR, "cleanCode.json",
				"bookErrorInexistentAuthor.json");
	}

	@Test
	public void updateBookNotFound() throws Exception {
		updateBookWithError(new BookNotFoundException(), HttpCode.NOT_FOUND, "cleanCode.json",
				"bookErrorInexistentAuthor.json");
	}
    
    @Test
	public void findBook() throws BookNotFoundException {
		Book book = bookWithId(designPatterns(), 1L);
		book.getCategory().setId(1L);
		for (int i = 1; i <= book.getAuthors().size(); i++) {
			book.getAuthors().get(i - 1).setId(new Long(i));
		}

		when(bookServices.findById(1L)).thenReturn(book);

		Response response = bookResourceUnderTest.findById(1L);
		assertThat(response.getStatus(), is(equalTo(HttpCode.OK.getCode())));
		assertJsonResponseWithFile(response, "designPatternsFound.json");
	}
    
    @Test
	public void findBookNotFound() throws BookNotFoundException {
		when(bookServices.findById(1L)).thenThrow(new BookNotFoundException());

		Response response = bookResourceUnderTest.findById(1L);
		assertThat(response.getStatus(), is(equalTo(HttpCode.NOT_FOUND.getCode())));
	}
    
    @Test
	public void findByBookNoFilter() {
		List<Book> books = Arrays.asList(bookWithId(cleanCode(), 1L), bookWithId(designPatterns(), 2L));
		Long currentCategoryId = 1L;
		Long currentAuthorId = 1L;
		for (Book book : books) {
			book.getCategory().setId(currentCategoryId++);
			for (int i = 0; i < book.getAuthors().size(); i++) {
				book.getAuthors().get(i).setId(currentAuthorId++);
			}
		}

		MultivaluedMap<String, String> multiMap = mock(MultivaluedMap.class);
		when(uriInfo.getQueryParameters()).thenReturn(multiMap);

		when(bookServices.findByFilter((BookFilter) anyObject())).thenReturn(
				new PaginatedData<>(books.size(), books));

		Response response = bookResourceUnderTest.findByFilter();
		assertThat(response.getStatus(), is(equalTo(HttpCode.OK.getCode())));
		assertJsonResponseWithFile(response, "booksAllInOnePage.json");
	}
    
    private void addBookWithValidationError(final Exception exceptionToBeThrown, final String requestFileName,
			final String responseFileName) throws Exception {
		when(bookServices.add((Book) anyObject())).thenThrow(exceptionToBeThrown);

		Response response = bookResourceUnderTest.add(readJsonFile(getPathFileRequest(PATH_RESOURCE, requestFileName)));
		assertThat(response.getStatus(), is(equalTo(HttpCode.VALIDATION_ERROR.getCode())));
		assertJsonResponseWithFile(response, responseFileName);
	}
    
    private void updateBookWithError(Exception exceptionToBeThrown, HttpCode expectedHttpCode,String requestFileName,
			String responseFileName) throws Exception {
		doThrow(exceptionToBeThrown).when(bookServices).update(bookWithId(cleanCode(), 1L));

		Response response = bookResourceUnderTest.update(1L, 
            readJsonFile(getPathFileRequest(PATH_RESOURCE, requestFileName)));
		assertThat(response.getStatus(), is(equalTo(expectedHttpCode.getCode())));
		if (expectedHttpCode != HttpCode.NOT_FOUND) {
			assertJsonResponseWithFile(response, responseFileName);
		}
	}
    
    private void assertJsonResponseWithFile(final Response response, final String fileName) {
		assertJsonMatchesFileContent(response.getEntity().toString(), getPathFileResponse(PATH_RESOURCE, fileName));
	}
    
}
