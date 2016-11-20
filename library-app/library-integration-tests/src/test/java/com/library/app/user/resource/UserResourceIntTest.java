package com.library.app.user.resource;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.library.app.common.model.HttpCode;
import com.library.app.commontests.utils.ArquillianTestUtils;
import static com.library.app.commontests.utils.FileTestNameUtils.getPathFileRequest;
import static com.library.app.commontests.utils.FileTestNameUtils.getPathFileResponse;
import com.library.app.commontests.utils.IntTestUtils;
import static com.library.app.commontests.utils.JsonTestUtils.assertJsonMatchesFileContent;
import com.library.app.commontests.utils.ResourceClient;
import com.library.app.json.JsonReader;
import static com.library.app.user.UserForTestsRepository.admin;
import static com.library.app.user.UserForTestsRepository.johnDoe;
import static com.library.app.user.UserForTestsRepository.mary;
import com.library.app.user.model.User;
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
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class UserResourceIntTest {

    @ArquillianResource
    private URL deploymentUrl;

    private ResourceClient client;

    private static final String PATH_RESOURCE = "users";

    @Deployment
    public static WebArchive createDeployment() {
        return ArquillianTestUtils.createDeploymentArchive();
    }

    @Before
    public void initTestCase() {
        client = new ResourceClient(deploymentUrl);

        client.resourcePath("/DB").delete();
        client.resourcePath("DB/" + PATH_RESOURCE + "/admin").postWithContent("");
    }

    @Test
    @RunAsClient
    public void addValidCustomerAndFindIt() {
        final Long userId = addUserAndGetId("customerJohnDoe.json");

        findUserAndAssertResponseWithUser(userId, johnDoe());
    }

    @Test
    @RunAsClient
    public void addUserWithNullName() {
        addUserWithValidationError("customerWithNullName.json", "userErrorNullName.json");
    }

    @Test
    @RunAsClient
    public void addExistentUser() {
        addUserAndGetId("customerJohnDoe.json");
        addUserWithValidationError("customerJohnDoe.json", "userAlreadyExists.json");
    }

    @Test
    @RunAsClient
    public void updateValidCustomerAsAdmin() {
        final Long userId = addUserAndGetId("customerJohnDoe.json");
        findUserAndAssertResponseWithUser(userId, johnDoe());

        final Response response = client.resourcePath(PATH_RESOURCE + "/" + userId).putWithFile(
            getPathFileRequest(PATH_RESOURCE, "updateCustomerJohnDoeWithNewName.json"));
        assertThat(response.getStatus(), is(equalTo(HttpCode.OK.getCode())));

        final User expectedUser = johnDoe();
        expectedUser.setName("New name");
        findUserAndAssertResponseWithUser(userId, expectedUser);
    }

    @Test
    @RunAsClient
    public void updateValidLoggedCustomerAsCustomer() {
        final Long userId = addUserAndGetId("customerJohnDoe.json");
        findUserAndAssertResponseWithUser(userId, johnDoe());

        final Response response = client.user(johnDoe()).resourcePath(PATH_RESOURCE + "/" + userId)
            .putWithFile(
                getPathFileRequest(PATH_RESOURCE, "updateCustomerJohnDoeWithNewName.json"));
        assertThat(response.getStatus(), is(equalTo(HttpCode.OK.getCode())));

        client.user(admin());
        final User expectedUser = johnDoe();
        expectedUser.setName("New name");
        findUserAndAssertResponseWithUser(userId, expectedUser);
    }

    @Test
    @RunAsClient
    public void updateCustomerButNotTheLoggedCustomer() {
        final Long userId = addUserAndGetId("customerJohnDoe.json");
        findUserAndAssertResponseWithUser(userId, johnDoe());
        addUserAndGetId("customerMary.json");

        final Response response = client.user(mary()).resourcePath(PATH_RESOURCE + "/" + userId).putWithFile(
            getPathFileRequest(PATH_RESOURCE, "updateCustomerJohnDoeWithNewName.json"));
        assertThat(response.getStatus(), is(equalTo(HttpCode.FORBIDDEN.getCode())));
    }

    private void addUserWithValidationError(String requestFileName, String responseFileName) {
        final Response response = client.user(null).resourcePath(PATH_RESOURCE)
            .postWithFile(getPathFileRequest(PATH_RESOURCE, requestFileName));

        assertThat(response.getStatus(), is(equalTo(HttpCode.VALIDATION_ERROR.getCode())));
        assertJsonResponseWithFile(response, responseFileName);
    }

    private void assertResponseContainsTheUsers(Response response, int expectedTotalRecords,
        User... expectedUsers) {

        JsonArray usersList = IntTestUtils.assertJsonHasTheNumberOfElementsAndReturnTheEntries(response,
            expectedTotalRecords, expectedUsers.length);

        for (int i = 0; i < expectedUsers.length; i++) {
            User expectedUser = expectedUsers[i];
            assertThat(usersList.get(i).getAsJsonObject().get("name").getAsString(),
                is(equalTo(expectedUser.getName())));
        }
    }

    private boolean authenticate(String email, String password) {
        Response response = client.user(null).resourcePath(PATH_RESOURCE + "/authenticate")
            .postWithContent(getJsonWithEmailAndPassword(email, password));
        return response.getStatus() == HttpCode.OK.getCode();
    }

    private Long addUserAndGetId(final String fileName) {
        client.user(null);
        return IntTestUtils.addElementWithFileAndGetId(client, PATH_RESOURCE, PATH_RESOURCE, fileName);
    }

    private void findUserAndAssertResponseWithUser(Long userIdToBeFound, User expectedUser) {
        client.user(admin());
        String bodyResponse = IntTestUtils.findById(client, PATH_RESOURCE, userIdToBeFound);
        assertResponseWithUser(bodyResponse, expectedUser);
    }

    private void assertResponseWithUser(String bodyResponse, User expectedUser) {
        JsonObject userJson = JsonReader.readAsJsonObject(bodyResponse);
        assertThat(userJson.get("id").getAsLong(), is(notNullValue()));
        assertThat(userJson.get("name").getAsString(), is(equalTo(expectedUser.getName())));
        assertThat(userJson.get("email").getAsString(), is(equalTo(expectedUser.getEmail())));
        assertThat(userJson.get("type").getAsString(), is(equalTo(expectedUser.getUserType().toString())));
        assertThat(userJson.get("createdAt").getAsString(), is(notNullValue()));

        JsonArray roles = userJson.getAsJsonArray("roles");
        assertThat(roles.size(), is(equalTo(expectedUser.getRoles().size())));
        for (int i = 0; i < roles.size(); i++) {
            String actualRole = roles.get(i).getAsJsonPrimitive().getAsString();
            String expectedRole = expectedUser.getRoles().get(i).toString();
            assertThat(actualRole, is(equalTo(expectedRole)));
        }
    }

    private void assertJsonResponseWithFile(Response response, String fileName) {
        assertJsonMatchesFileContent(response.readEntity(String.class), getPathFileResponse(PATH_RESOURCE, fileName));
    }

    private static String getJsonWithEmailAndPassword(String email, String password) {
        return String.format("{\"email\":\"%s\",\"password\":\"%s\"}", email, password);
    }

}
