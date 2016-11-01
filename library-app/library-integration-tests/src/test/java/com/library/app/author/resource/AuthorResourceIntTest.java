package com.library.app.author.resource;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import static com.library.app.author.AuthorForTestsRepository.donRoberts;
import static com.library.app.author.AuthorForTestsRepository.erichGamma;
import static com.library.app.author.AuthorForTestsRepository.jamesGosling;
import static com.library.app.author.AuthorForTestsRepository.johnBrant;
import static com.library.app.author.AuthorForTestsRepository.johnVlissides;
import static com.library.app.author.AuthorForTestsRepository.joshuaBloch;
import static com.library.app.author.AuthorForTestsRepository.kentBeck;
import static com.library.app.author.AuthorForTestsRepository.martinFowler;
import static com.library.app.author.AuthorForTestsRepository.ralphJohnson;
import static com.library.app.author.AuthorForTestsRepository.richardHelm;
import static com.library.app.author.AuthorForTestsRepository.robertMartin;
import static com.library.app.author.AuthorForTestsRepository.williamOpdyke;
import com.library.app.author.model.Author;
import com.library.app.common.model.HttpCode;
import com.library.app.commontests.utils.ArquillianTestUtils;
import static com.library.app.commontests.utils.FileTestNameUtils.getPathFileRequest;
import static com.library.app.commontests.utils.FileTestNameUtils.getPathFileResponse;
import com.library.app.commontests.utils.IntTestUtils;
import static com.library.app.commontests.utils.JsonTestUtils.assertJsonMatchesFileContent;
import com.library.app.commontests.utils.ResourceClient;
import com.library.app.json.JsonReader;
import static com.library.app.user.UserForTestsRepository.admin;
import java.net.URL;
import javax.ws.rs.core.Response;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class AuthorResourceIntTest {
    
    @ArquillianResource
    private URL url;
    
    private ResourceClient resourceClient;
    
    private static final String PATH_RESOURCE = "authors";
    
    @Deployment
    public static WebArchive createDeployment() {
        return ArquillianTestUtils.createDeploymentArchive();
    }
    
    @Before
    public void initTestCase() {
        resourceClient = new ResourceClient(url);
        resourceClient.resourcePath("/DB").delete();
        resourceClient.resourcePath("DB/users").postWithContent("");
        resourceClient.user(admin());
    }
    
    @Test
    @RunAsClient
    public void addValidAuthorAndFindIt() {
        final Long authorId = addAuthorAndGetId("robertMartin.json");
        findAuthorAndAssertResponseWithAuthor(authorId, robertMartin());
    }

    @Test
    @RunAsClient
    public void addAuthorWithNullName() {
        final Response response = resourceClient.resourcePath(PATH_RESOURCE).postWithFile(
                        getPathFileRequest(PATH_RESOURCE, "authorWithNullName.json"));

        assertThat(response.getStatus(), is(equalTo(HttpCode.VALIDATION_ERROR.getCode())));
        assertJsonResponseWithFile(response, "authorErrorNullName.json");
    }

    @Test
    @RunAsClient
    public void updateValidAuthor() {
        final Long authorId = addAuthorAndGetId("robertMartin.json");
        findAuthorAndAssertResponseWithAuthor(authorId, robertMartin());

        final Response responseUpdate = resourceClient.resourcePath(PATH_RESOURCE + "/" + authorId).putWithFile(
                        getPathFileRequest(PATH_RESOURCE, "uncleBob.json"));
        assertThat(responseUpdate.getStatus(), is(equalTo(HttpCode.OK.getCode())));

        final Author uncleBob = new Author();
        uncleBob.setName("Uncle Bob");
        findAuthorAndAssertResponseWithAuthor(authorId, uncleBob);
    }

    @Test
    @RunAsClient
    public void updateAuthorNotFound() {
        final Response response = resourceClient.resourcePath(PATH_RESOURCE + "/" + 999).putWithFile(
                        getPathFileRequest(PATH_RESOURCE, "robertMartin.json"));
        assertThat(response.getStatus(), is(equalTo(HttpCode.NOT_FOUND.getCode())));
    }

    @Test
    @RunAsClient
    public void findAuthorNotFound() {
        final Response response = resourceClient.resourcePath(PATH_RESOURCE + "/" + 999).get();
        assertThat(response.getStatus(), is(equalTo(HttpCode.NOT_FOUND.getCode())));
    }

    @Test
    @RunAsClient
    public void findByFilterPaginatingAndOrderingDescendingByName() {
        resourceClient.resourcePath("DB/" + PATH_RESOURCE).postWithContent("");

        // first page
        Response response = resourceClient.resourcePath(PATH_RESOURCE + "?page=0&per_page=10&sort=-name").get();
        assertThat(response.getStatus(), is(equalTo(HttpCode.OK.getCode())));
        assertResponseContainsTheAuthors(response, 12, williamOpdyke(), robertMartin(), richardHelm(), ralphJohnson(),
                        martinFowler(), kentBeck(), joshuaBloch(), johnVlissides(), johnBrant(), jamesGosling());

        // second page
        response = resourceClient.resourcePath(PATH_RESOURCE + "?page=1&per_page=10&sort=-name").get();
        assertThat(response.getStatus(), is(equalTo(HttpCode.OK.getCode())));
        assertResponseContainsTheAuthors(response, 12, erichGamma(), donRoberts());
    }
    
    private Long addAuthorAndGetId(final String fileName) {
        return IntTestUtils.addElementWithFileAndGetId(resourceClient, PATH_RESOURCE, PATH_RESOURCE, fileName);
    }

    private void findAuthorAndAssertResponseWithAuthor(final Long authorIdToBeFound, final Author expectedAuthor) {
        final String json = IntTestUtils.findById(resourceClient, PATH_RESOURCE, authorIdToBeFound);

        final JsonObject categoryAsJson = JsonReader.readAsJsonObject(json);
        assertThat(JsonReader.getStringOrNull(categoryAsJson, "name"), is(equalTo(expectedAuthor.getName())));
    }

    private void assertJsonResponseWithFile(final Response response, final String fileName) {
        assertJsonMatchesFileContent(response.readEntity(String.class), getPathFileResponse(PATH_RESOURCE, fileName));
    }

    private void assertResponseContainsTheAuthors(final Response response, final int expectedTotalRecords,
                final Author... expectedAuthors) {
        final JsonArray authorsList = IntTestUtils.assertJsonHasTheNumberOfElementsAndReturnTheEntries(response,
                        expectedTotalRecords, expectedAuthors.length);

        for (int i = 0; i < expectedAuthors.length; i++) {
            final Author expectedAuthor = expectedAuthors[i];
            assertThat(authorsList.get(i).getAsJsonObject().get("name").getAsString(),
                        is(equalTo(expectedAuthor.getName())));
        }
    }    
    
}
