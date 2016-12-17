package com.library.app.book.resource;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.library.app.author.model.Author;
import com.library.app.book.BookForTestsRepository;
import static com.library.app.book.BookForTestsRepository.cleanCode;
import static com.library.app.book.BookForTestsRepository.designPatterns;
import static com.library.app.book.BookForTestsRepository.effectiveJava;
import static com.library.app.book.BookForTestsRepository.peaa;
import static com.library.app.book.BookForTestsRepository.refactoring;
import com.library.app.book.model.Book;
import com.library.app.category.model.Category;
import com.library.app.common.model.HttpCode;
import com.library.app.commontests.utils.ArquillianTestUtils;
import static com.library.app.commontests.utils.FileTestNameUtils.getPathFileResponse;
import com.library.app.commontests.utils.IntTestUtils;
import com.library.app.commontests.utils.JsonTestUtils;
import static com.library.app.commontests.utils.JsonTestUtils.assertJsonMatchesFileContent;
import com.library.app.commontests.utils.ResourceClient;
import com.library.app.json.JsonReader;
import com.library.app.json.JsonWriter;
import static com.library.app.user.UserForTestsRepository.admin;
import static com.library.app.user.UserForTestsRepository.johnDoe;
import java.net.URL;
import javax.ws.rs.core.Response;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.json.JSONObject;
import org.junit.Assert;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class BookResourceIntTest {
    
    @ArquillianResource
    private URL deploymentUrl;
    
    private ResourceClient resourceClient;
    
    private static final String PATH_RESOURCE = "books";
    
    @Deployment
    public static WebArchive createDeployment() {
        return ArquillianTestUtils.createDeploymentArchive();
    }
    
    @Before
    public void initTestCase() {
        resourceClient = new ResourceClient(deploymentUrl);
        
        resourceClient.resourcePath("DB/").delete();
        resourceClient.resourcePath("DB/users").postWithContent("");
        resourceClient.resourcePath("DB/categories").postWithContent("");
        resourceClient.resourcePath("DB/authors").postWithContent("");
        
        resourceClient.user(admin());
    } 
    
    @Test
    @RunAsClient
    public void addValidBookAndFindIt() {
        Long bookId = addBookAndGetId(normalizeDependenciesWithRest(designPatterns()));
        findBookAndAssertResponseWithBook(bookId, designPatterns());
    }
    
    @Test
	@RunAsClient
	public void addBookWithNullTitle() {
		Book book = normalizeDependenciesWithRest(cleanCode());
		book.setTitle(null);
		addBookWithValidationError(book, "bookErrorNullTitle.json");
	}

	@Test
	@RunAsClient
	public void addBookWithInexistentCategory() {
		final Book book = normalizeDependenciesWithRest(cleanCode());
		book.getCategory().setId(999L);
		addBookWithValidationError(book, "bookErrorInexistentCategory.json");
	}

	@Test
	@RunAsClient
	public void addBookWithInexistentAuthor() {
		final Book book = normalizeDependenciesWithRest(cleanCode());
		book.getAuthors().get(0).setId(999L);
		addBookWithValidationError(book, "bookErrorInexistentAuthor.json");
	}
    
    @Test
	@RunAsClient
	public void updateValidBook() {
		Long bookId = addBookAndGetId(normalizeDependenciesWithRest(designPatterns()));
		findBookAndAssertResponseWithBook(bookId, designPatterns());

		Book book = normalizeDependenciesWithRest(designPatterns());
		book.setPrice(10D);
		book.getAuthors().remove(0);

		Response response = resourceClient.
                            resourcePath(PATH_RESOURCE + "/" + bookId).
                            putWithContent(getJsonForBook(book));
        
		assertThat(response.getStatus(), is(equalTo(HttpCode.OK.getCode())));

		findBookAndAssertResponseWithBook(bookId, book);
	}

	@Test
	@RunAsClient
	public void updateBookNotFound() {
		Book book = normalizeDependenciesWithRest(cleanCode());
		Response response = resourceClient.
                            resourcePath(PATH_RESOURCE + "/" + 999).
                            putWithContent(getJsonForBook(book));
        
		assertThat(response.getStatus(), is(equalTo(HttpCode.NOT_FOUND.getCode())));
	}

    @Test
	@RunAsClient
	public void findBookNotFound() {
		Response response = resourceClient.resourcePath(PATH_RESOURCE + "/" + 999).get();
		assertThat(response.getStatus(), is(equalTo(HttpCode.NOT_FOUND.getCode())));
	}

	@Test
	@RunAsClient
	public void findByFilterPaginatingAndOrderingDescendingByTitle() {
		resourceClient.resourcePath("DB/" + PATH_RESOURCE).postWithContent("");

		// first page
		Response response = resourceClient.resourcePath(PATH_RESOURCE + "?page=0&per_page=3&sort=-title").get();
		assertThat(response.getStatus(), is(equalTo(HttpCode.OK.getCode())));
		assertResponseContainsTheBooks(response, 5, refactoring(), peaa(), effectiveJava());

		// second page
		response = resourceClient.resourcePath(PATH_RESOURCE + "?page=1&per_page=3&sort=-title").get();
		assertThat(response.getStatus(), is(equalTo(HttpCode.OK.getCode())));
		assertResponseContainsTheBooks(response, 5, designPatterns(), cleanCode());
	}

	@Test
	@RunAsClient
	public void findByFilterWithNoUser() {
		Response response = resourceClient.user(null).resourcePath(PATH_RESOURCE).get();
		assertThat(response.getStatus(), is(equalTo(HttpCode.UNAUTHORIZED.getCode())));
	}

	@Test
	@RunAsClient
	public void findByFilterWithUserCustomer() {
		Response response = resourceClient.user(johnDoe()).resourcePath(PATH_RESOURCE).get();
		assertThat(response.getStatus(), is(equalTo(HttpCode.OK.getCode())));
	}

	@Test
	@RunAsClient
	public void findByIdIdWithUserCustomer() {
		Response response = resourceClient.user(johnDoe()).resourcePath(PATH_RESOURCE + "/999").get();
		assertThat(response.getStatus(), is(equalTo(HttpCode.FORBIDDEN.getCode())));
	}
    
    private void addBookWithValidationError(Book bookToAdd, String responseFileName) {
		Response response = resourceClient.
                            resourcePath(PATH_RESOURCE).
                            postWithContent(getJsonForBook(bookToAdd));
        
		assertThat(response.getStatus(), is(equalTo(HttpCode.VALIDATION_ERROR.getCode())));
		assertJsonResponseWithFile(response, responseFileName);
	}
    
    private void assertJsonResponseWithFile(Response response, String fileName) {
		assertJsonMatchesFileContent(response.readEntity(String.class), getPathFileResponse(PATH_RESOURCE, fileName));
    }
    
    private void assertResponseContainsTheBooks(Response response, int expectedTotalRecords, Book... expectedBooks) {

		JsonArray booksList = IntTestUtils.assertJsonHasTheNumberOfElementsAndReturnTheEntries(response,
				expectedTotalRecords, expectedBooks.length);

		for (int i = 0; i < expectedBooks.length; i++) {
			Book expectedBook = expectedBooks[i];
			assertThat(booksList.get(i).getAsJsonObject().get("title").getAsString(),
					is(equalTo(expectedBook.getTitle())));
		}
	}

    private Long addBookAndGetId(Book book) {
        return IntTestUtils.addElementWithContentAndGetId(resourceClient, PATH_RESOURCE, getJsonForBook(book));
    }
    
    private String getJsonForBook(Book book){
        JsonObject bookJson = new JsonObject();
        bookJson.addProperty("title", book.getTitle());
        bookJson.addProperty("description", book.getDescription());
        bookJson.addProperty("categoryId", book.getCategory().getId());
        
        JsonArray authorsIds = new JsonArray();
        book.getAuthors().forEach((author) -> authorsIds.add(new JsonPrimitive(author.getId())));
        
        bookJson.add("authorsIds", authorsIds);
        bookJson.addProperty("price", book.getPrice());
        return JsonWriter.writeToString(bookJson);
    }
    
    private Book normalizeDependenciesWithRest(Book book){
        book.getCategory().setId(loadCategoryFromRest(book.getCategory()).getId());
        book.getAuthors().forEach((author) -> author.setId(loadAuthorFromRest(author).getId()));
        
        return book;
    }
    
    private Category loadCategoryFromRest(Category category) {
        Response response = resourceClient.resourcePath("DB/categories/" + category.getName()).get();
        assertThat(response.getStatus(), is(equalTo(HttpCode.OK.getCode())));
        
        String bodyResponse = response.readEntity(String.class);
        return new Category(JsonTestUtils.getIdFromJson(bodyResponse));
    }
    
    private Author loadAuthorFromRest(Author author) {
		Response response = resourceClient.resourcePath("DB/authors/" + author.getName()).get();
		assertThat(response.getStatus(), is(equalTo(HttpCode.OK.getCode())));

		String bodyResponse = response.readEntity(String.class);
		return new Author(JsonTestUtils.getIdFromJson(bodyResponse));
	}
    
    private void findBookAndAssertResponseWithBook(Long bookIdToBeFound, Book expectedBook) {
		String bodyResponse = IntTestUtils.findById(resourceClient, PATH_RESOURCE, bookIdToBeFound);

		JsonObject bookJson = JsonReader.readAsJsonObject(bodyResponse);
		assertThat(bookJson.get("id").getAsLong(), is(notNullValue()));
		assertThat(bookJson.get("title").getAsString(), is(equalTo(expectedBook.getTitle())));
		assertThat(bookJson.get("description").getAsString(), is(equalTo(expectedBook.getDescription())));
		assertThat(bookJson.getAsJsonObject("category").get("name").getAsString(), is(equalTo(expectedBook
				.getCategory().getName())));

		JsonArray authors = bookJson.getAsJsonArray("authors");
		assertThat(authors.size(), is(equalTo(expectedBook.getAuthors().size())));
		for (int i = 0; i < authors.size(); i++) {
			String actualAuthorName = authors.get(i).getAsJsonObject().get("name").getAsString();
			String expectedAuthorName = expectedBook.getAuthors().get(i).getName();
			assertThat(actualAuthorName, is(equalTo(expectedAuthorName)));
		}

		assertThat(bookJson.get("price").getAsDouble(), is(equalTo(expectedBook.getPrice())));
	}
    
}
