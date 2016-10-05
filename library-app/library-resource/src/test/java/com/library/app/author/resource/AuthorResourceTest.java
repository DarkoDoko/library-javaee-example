package com.library.app.author.resource;

import com.library.app.FieldNotValidException;
import static com.library.app.author.AuthorForTestsRepository.authorWithId;
import static com.library.app.author.AuthorForTestsRepository.robertMartin;
import com.library.app.author.AuthorNotFoundException;
import com.library.app.author.model.Author;
import com.library.app.author.services.AuthorServices;
import com.library.app.common.model.HttpCode;
import javax.ws.rs.core.Response;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import static com.library.app.commontests.utils.FileTestNameUtils.*;
import static com.library.app.commontests.utils.JsonTestUtils.*;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

public class AuthorResourceTest {

	private AuthorResource resourceUnderTest;

	private static final String PATH_RESOURCE = "authors";

	@Mock
	private AuthorServices services;

	@Before
	public void initTestCase() {
		MockitoAnnotations.initMocks(this);
		resourceUnderTest = new AuthorResource();

		resourceUnderTest.services = services;
		resourceUnderTest.jsonConverter = new AuthorJsonConverter();
	}

	@Test
	public void addValidAuthor() {
		when(services.add(robertMartin())).thenReturn(authorWithId(robertMartin(), 1L));

		final Response response = resourceUnderTest
				.add(readJsonFile(getPathFileRequest(PATH_RESOURCE, "robertMartin.json")));
		assertThat(response.getStatus(), is(equalTo(HttpCode.CREATED.getCode())));
		assertJsonMatchesExpectedJson(response.getEntity().toString(), "{\"id\": 1}");
	}

	@Test
	public void addAuthorWithNullName() throws Exception {
		when(services.add((Author) anyObject())).thenThrow(new FieldNotValidException("name", "may not be null"));

		final Response response = resourceUnderTest
				.add(readJsonFile(getPathFileRequest(PATH_RESOURCE, "authorWithNullName.json")));
		assertThat(response.getStatus(), is(equalTo(HttpCode.VALIDATION_ERROR.getCode())));
		assertJsonResponseWithFile(response, "authorErrorNullName.json");
	}

	@Test
	public void updateValidAuthor() throws Exception {
		Response response = resourceUnderTest.update(1L,
			readJsonFile(getPathFileRequest(PATH_RESOURCE, "robertMartin.json")));

		assertThat(response.getStatus(), is(equalTo(HttpCode.OK.getCode())));
		assertThat(response.getEntity().toString(), is(equalTo("")));

		verify(services).update(authorWithId(robertMartin(), 1L));
	}

	@Test
	public void updateAuthorWithNullName() throws Exception {
		doThrow(new FieldNotValidException("name", "may not be null")).when(services).update(
				(Author) anyObject());

		final Response response = resourceUnderTest.update(1L,
				readJsonFile(getPathFileRequest(PATH_RESOURCE, "authorWithNullName.json")));
		assertThat(response.getStatus(), is(equalTo(HttpCode.VALIDATION_ERROR.getCode())));
		assertJsonResponseWithFile(response, "authorErrorNullName.json");
	}

	@Test
	public void updateAuthorNotFound() throws Exception {
		doThrow(new AuthorNotFoundException()).when(services).update(authorWithId(robertMartin(), 2L));

		final Response response = resourceUnderTest.update(2L,
				readJsonFile(getPathFileRequest(PATH_RESOURCE, "robertMartin.json")));
		assertThat(response.getStatus(), is(equalTo(HttpCode.NOT_FOUND.getCode())));
	}

	@Test
	public void findAuthor() throws AuthorNotFoundException {
		when(services.findById(1L)).thenReturn(authorWithId(robertMartin(), 1L));

		final Response response = resourceUnderTest.findById(1L);
		assertThat(response.getStatus(), is(equalTo(HttpCode.OK.getCode())));
		assertJsonResponseWithFile(response, "robertMartinFound.json");
	}

	@Test
	public void findAuthorNotFound() throws AuthorNotFoundException {
		when(services.findById(1L)).thenThrow(new AuthorNotFoundException());

		final Response response = resourceUnderTest.findById(1L);
		assertThat(response.getStatus(), is(equalTo(HttpCode.NOT_FOUND.getCode())));
	}

	private void assertJsonResponseWithFile(final Response response, final String fileName) {
		assertJsonMatchesFileContent(response.getEntity().toString(), getPathFileResponse(PATH_RESOURCE, fileName));
	}

}
