package com.library.app.user.resource;

import com.library.app.common.model.HttpCode;
import static com.library.app.commontests.utils.FileTestNameUtils.getPathFileRequest;
import com.library.app.commontests.utils.JsonTestUtils;
import static com.library.app.commontests.utils.JsonTestUtils.assertJsonMatchesExpectedJson;
import static com.library.app.commontests.utils.JsonTestUtils.readJsonFile;
import static com.library.app.user.UserArgumentMatcher.userEq;
import static com.library.app.user.UserForTestsRepository.johnDoe;
import static com.library.app.user.UserForTestsRepository.userWithIdAndCreatedAt;
import com.library.app.user.services.UserServices;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
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
        
        Response response = resourceUnderTest.add(
                                            readJsonFile(
                                            getPathFileRequest(PATH_RESOURCE, "customerJohnDoe.json")));
        
        assertThat(response.getStatus(), is(equalTo(HttpCode.CREATED.getCode())));
        assertJsonMatchesExpectedJson(response.getEntity().toString(), "{\"id\": 1}");
    }

    private static String getJsonWithPassword(final String password) {
		return String.format("{\"password\":\"%s\"}", password);
	}

	private static String getJsonWithEmailAndPassword(final String email, final String password) {
		return String.format("{\"email\":\"%s\",\"password\":\"%s\"}", email, password);
	}    
}
