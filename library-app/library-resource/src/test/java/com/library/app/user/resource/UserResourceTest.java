package com.library.app.user.resource;

import com.library.app.FieldNotValidException;
import com.library.app.common.model.HttpCode;
import static com.library.app.commontests.utils.FileTestNameUtils.getPathFileRequest;
import static com.library.app.commontests.utils.FileTestNameUtils.getPathFileResponse;
import static com.library.app.commontests.utils.JsonTestUtils.assertJsonMatchesExpectedJson;
import static com.library.app.commontests.utils.JsonTestUtils.assertJsonMatchesFileContent;
import static com.library.app.commontests.utils.JsonTestUtils.readJsonFile;
import com.library.app.pagination.PaginatedData;
import static com.library.app.user.UserArgumentMatcher.userEq;
import com.library.app.user.UserExistentException;
import static com.library.app.user.UserForTestsRepository.admin;
import static com.library.app.user.UserForTestsRepository.allUsers;
import static com.library.app.user.UserForTestsRepository.johnDoe;
import static com.library.app.user.UserForTestsRepository.mary;
import static com.library.app.user.UserForTestsRepository.userWithIdAndCreatedAt;
import com.library.app.user.UserNotFoundException;
import com.library.app.user.model.User;
import com.library.app.user.model.User.Roles;
import com.library.app.user.model.filter.UserFilter;
import com.library.app.user.services.UserServices;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

public class UserResourceTest {

    private static final String PATH_RESOURCE = "users";

    private UserResource resourceUnderTest;

    @Mock
    private UserServices servicesCollaborator;

    @Mock
    private UriInfo info;

    @Mock
    private SecurityContext securityContext;

    @Before
    public void initTestCase() {
        MockitoAnnotations.initMocks(this);

        resourceUnderTest = new UserResource();
        resourceUnderTest.services = servicesCollaborator;
        resourceUnderTest.jsonConverter = new UserJsonConverter();
        resourceUnderTest.uriInfo = info;
        resourceUnderTest.securityContext = securityContext;
    }

    @Test
    public void addValidCustomer() {
        when(servicesCollaborator.add(userEq(johnDoe()))).
            thenReturn(userWithIdAndCreatedAt(johnDoe(), 1L));

        Response response = resourceUnderTest.add(readJsonFile(
            getPathFileRequest(PATH_RESOURCE, "customerJohnDoe.json")));

        assertThat(response.getStatus(), is(equalTo(HttpCode.CREATED.getCode())));
        assertJsonMatchesExpectedJson(response.getEntity().toString(), "{\"id\": 1}");
    }

    @Test
    public void addValidEmployee() {
        Response response = resourceUnderTest.add(readJsonFile(
            getPathFileRequest(PATH_RESOURCE, "employeeAdmin.json")));

        assertThat(response.getStatus(), is(equalTo(HttpCode.FORBIDDEN.getCode())));
    }

    @Test
    public void addExistentUser() {
        when(servicesCollaborator.add(userEq(johnDoe()))).thenThrow(new UserExistentException());

        Response response = resourceUnderTest.add(readJsonFile(getPathFileRequest(PATH_RESOURCE,
            "customerJohnDoe.json")));
        assertThat(response.getStatus(), is(equalTo(HttpCode.VALIDATION_ERROR.getCode())));
        assertJsonResponseWithFile(response, "userAlreadyExists.json");
    }

    @Test
    public void addUserWithNullName() {
        when(servicesCollaborator.add((User) anyObject())).thenThrow(new FieldNotValidException("name", "may not be null"));

        final Response response = resourceUnderTest.add(readJsonFile(
            getPathFileRequest(PATH_RESOURCE, "customerWithNullName.json")));

        assertThat(response.getStatus(), is(equalTo(HttpCode.VALIDATION_ERROR.getCode())));
        assertJsonResponseWithFile(response, "userErrorNullName.json");
    }

    @Test
    public void updateValidCustomer() {
        when(securityContext.isUserInRole(Roles.ADMINISTRATOR.name())).thenReturn(true);

        Response response = resourceUnderTest.update(1L, readJsonFile(
            getPathFileRequest(PATH_RESOURCE, "updateCustomerJohnDoe.json")));

        assertThat(response.getStatus(), is(equalTo(HttpCode.OK.getCode())));
        assertThat(response.getEntity().toString(), is(equalTo("")));

        User expectedUser = userWithIdAndCreatedAt(johnDoe(), 1L);
        expectedUser.setPassword(null);
        verify(servicesCollaborator).update(userEq(expectedUser));
    }

    @Test
    public void updateValidCustomerLoggedAsCustomerToBeUpdated() {
        setUpPrincipalUser(userWithIdAndCreatedAt(johnDoe(), 1L));
        when(securityContext.isUserInRole(Roles.ADMINISTRATOR.name())).thenReturn(false);

        final Response response = resourceUnderTest.update(1L,
            readJsonFile(getPathFileRequest(PATH_RESOURCE, "updateCustomerJohnDoe.json")));

        assertThat(response.getStatus(), is(equalTo(HttpCode.OK.getCode())));
        assertThat(response.getEntity().toString(), is(equalTo("")));

        User expectedUser = userWithIdAndCreatedAt(johnDoe(), 1L);
        expectedUser.setPassword(null);
        verify(servicesCollaborator).update(userEq(expectedUser));
    }

    @Test
    public void updateValidCustomerLoggedAsOtherCustomer() {
        setUpPrincipalUser(userWithIdAndCreatedAt(mary(), 2L));
        when(securityContext.isUserInRole(Roles.ADMINISTRATOR.name())).thenReturn(false);

        final Response response = resourceUnderTest.update(1L,
            readJsonFile(getPathFileRequest(PATH_RESOURCE, "updateCustomerJohnDoe.json")));

        assertThat(response.getStatus(), is(equalTo(HttpCode.FORBIDDEN.getCode())));
    }

    @Test
    public void updateValidEmployee() {
        when(securityContext.isUserInRole(Roles.ADMINISTRATOR.name())).thenReturn(true);

        final Response response = resourceUnderTest.update(1L,
            readJsonFile(getPathFileRequest(PATH_RESOURCE, "updateEmployeeAdmin.json")));

        assertThat(response.getStatus(), is(equalTo(HttpCode.OK.getCode())));
        assertThat(response.getEntity().toString(), is(equalTo("")));

        User expectedUser = userWithIdAndCreatedAt(admin(), 1L);
        expectedUser.setPassword(null);
        verify(servicesCollaborator).update(userEq(expectedUser));
    }

    @Test
    public void updateUserWithEmailBelongingToOtherUser() {
        when(securityContext.isUserInRole(Roles.ADMINISTRATOR.name())).thenReturn(true);
        doThrow(new UserExistentException()).when(servicesCollaborator).update(userWithIdAndCreatedAt(johnDoe(), 1L));

        final Response response = resourceUnderTest.update(1L,
            readJsonFile(getPathFileRequest(PATH_RESOURCE, "updateCustomerJohnDoe.json")));

        assertThat(response.getStatus(), is(equalTo(HttpCode.VALIDATION_ERROR.getCode())));
        assertJsonResponseWithFile(response, "userAlreadyExists.json");
    }

    @Test
    public void updateUserWithNullName() {
        when(securityContext.isUserInRole(Roles.ADMINISTRATOR.name())).thenReturn(true);

        doThrow(new FieldNotValidException("name", "may not be null")).when(servicesCollaborator).update(
            userWithIdAndCreatedAt(johnDoe(), 1L));

        final Response response = resourceUnderTest.update(1L,
            readJsonFile(getPathFileRequest(PATH_RESOURCE, "customerWithNullName.json")));

        assertThat(response.getStatus(), is(equalTo(HttpCode.VALIDATION_ERROR.getCode())));
        assertJsonResponseWithFile(response, "userErrorNullName.json");
    }

    @Test
    public void updateUserNotFound() {
        when(securityContext.isUserInRole(Roles.ADMINISTRATOR.name())).thenReturn(true);
        doThrow(new UserNotFoundException()).when(servicesCollaborator).update(userWithIdAndCreatedAt(johnDoe(), 2L));

        final Response response = resourceUnderTest.update(2L,
            readJsonFile(getPathFileRequest(PATH_RESOURCE, "updateCustomerJohnDoe.json")));

        assertThat(response.getStatus(), is(equalTo(HttpCode.NOT_FOUND.getCode())));
    }

    @Test
    public void updateUserPassword() {
        when(securityContext.isUserInRole(Roles.ADMINISTRATOR.name())).thenReturn(true);

        final Response response = resourceUnderTest.updatePassword(1L, getJsonWithPassword("123456"));

        assertThat(response.getStatus(), is(equalTo(HttpCode.OK.getCode())));
        assertThat(response.getEntity().toString(), is(equalTo("")));
        verify(servicesCollaborator).updatePassword(1L, "123456");
    }

    @Test
    public void updateCustomerPasswordLoggedAsCustomerToBeUpdated() {
        setUpPrincipalUser(userWithIdAndCreatedAt(johnDoe(), 1L));
        when(securityContext.isUserInRole(Roles.ADMINISTRATOR.name())).thenReturn(false);

        final Response response = resourceUnderTest.updatePassword(1L, getJsonWithPassword("123456"));

        assertThat(response.getStatus(), is(equalTo(HttpCode.OK.getCode())));
        assertThat(response.getEntity().toString(), is(equalTo("")));
        verify(servicesCollaborator).updatePassword(1L, "123456");
    }

    @Test
    public void updateCustomerPasswordLoggedAsOtherCustomer() {
        setUpPrincipalUser(userWithIdAndCreatedAt(mary(), 2L));
        when(securityContext.isUserInRole(Roles.ADMINISTRATOR.name())).thenReturn(false);

        Response response = resourceUnderTest.updatePassword(1L, getJsonWithPassword("123456"));
        assertThat(response.getStatus(), is(equalTo(HttpCode.FORBIDDEN.getCode())));
    }

    @Test
    public void findCustomerById() {
        when(servicesCollaborator.findById(1L)).thenReturn(userWithIdAndCreatedAt(johnDoe(), 1L));

        Response response = resourceUnderTest.findById(1L);
        
        assertThat(response.getStatus(), is(equalTo(HttpCode.OK.getCode())));
        assertJsonResponseWithFile(response, "customerJohnDoeFound.json");
    }

    @Test
    public void findUserByIdNotFound() {
        when(servicesCollaborator.findById(1L)).thenThrow(new UserNotFoundException());

        Response response = resourceUnderTest.findById(1L);
        
        assertThat(response.getStatus(), is(equalTo(HttpCode.NOT_FOUND.getCode())));
    }

    @Test
    public void findEmployeeByEmailAndPassword() {
        when(servicesCollaborator.findByEmailAndPassword(admin().getEmail(), admin().getPassword())).
        thenReturn(userWithIdAndCreatedAt(admin(), 1L));

        Response response = resourceUnderTest.findByEmailAndPassword(
            getJsonWithEmailAndPassword(admin().getEmail(), admin().getPassword()));
        
        assertThat(response.getStatus(), is(equalTo(HttpCode.OK.getCode())));
        assertJsonResponseWithFile(response, "employeeAdminFound.json");
    }

    @Test
    public void findUserByEmailAndPasswordNotFound() {
        when(servicesCollaborator.findByEmailAndPassword(admin().getEmail(), admin().getPassword())).
        thenThrow(new UserNotFoundException());

        Response response = resourceUnderTest.findByEmailAndPassword(
            getJsonWithEmailAndPassword(admin().getEmail(), admin().getPassword()));
        
        assertThat(response.getStatus(), is(equalTo(HttpCode.NOT_FOUND.getCode())));
    }

    @Test
    public void findByFilterNoFilter() {
        List<User> users = new ArrayList<>();
        List<User> allUsers = allUsers();
        for (int i = 1; i <= allUsers.size(); i++) {
            users.add(userWithIdAndCreatedAt(allUsers.get(i - 1), new Long(i)));
        }

        MultivaluedMap<String, String> multiMap = mock(MultivaluedMap.class);
        when(info.getQueryParameters()).thenReturn(multiMap);

        when(servicesCollaborator.findByFilter((UserFilter) anyObject())).thenReturn(
            new PaginatedData<>(users.size(), users));

        final Response response = resourceUnderTest.findByFilter();
        assertThat(response.getStatus(), is(equalTo(HttpCode.OK.getCode())));
        assertJsonResponseWithFile(response, "usersAllInOnePage.json");
    }

    private static String getJsonWithPassword(String password) {
        return String.format("{\"password\":\"%s\"}", password);
    }

    private static String getJsonWithEmailAndPassword(String email, String password) {
        return String.format("{\"email\":\"%s\",\"password\":\"%s\"}", email, password);
    }

    private void assertJsonResponseWithFile(Response response, String fileName) {
        assertJsonMatchesFileContent(response.getEntity().toString(), getPathFileResponse(PATH_RESOURCE, fileName));
    }

    private void setUpPrincipalUser(User user) {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn(user.getEmail());

        when(securityContext.getUserPrincipal()).thenReturn(principal);
        when(servicesCollaborator.findByEmail(user.getEmail())).thenReturn(user);
    }
}
