package com.library.app.category.resource;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.library.app.category.model.Category;
import com.library.app.json.JsonReader;
import com.library.app.common.model.HttpCode;
import static com.library.app.category.CategoryForTestsRepository.architecture;
import static com.library.app.category.CategoryForTestsRepository.cleanCode;
import static com.library.app.category.CategoryForTestsRepository.java;
import static com.library.app.category.CategoryForTestsRepository.networks;
import static com.library.app.commontests.utils.FileTestNameUtils.getPathFileRequest;
import static com.library.app.commontests.utils.FileTestNameUtils.getPathFileResponse;
import com.library.app.commontests.utils.IntTestUtils;
import static com.library.app.commontests.utils.IntTestUtils.addElementWithFileAndGetId;
import static com.library.app.commontests.utils.JsonTestUtils.assertJsonMatchesFileContent;
import com.library.app.commontests.utils.ResourceClient;
import java.io.File;
import java.net.URL;
import javax.ws.rs.core.Response;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class CategoryResourceIntTest {
    
    @ArquillianResource
    private URL url;
    
    private ResourceClient resourceClient;
    
    private static final String PATH_RESOURCE = ResourceDefinitions.CATEGORY.getResourceName();
    
    @Deployment
    public static WebArchive createDeployment(){
        return ShrinkWrap.create(WebArchive.class)
                .addPackages(true, "com.library.app")
                .addAsResource("persistence-integration.xml", "META-INF/persistence.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .setWebXML(new File("src/test/resources/web.xml"))
                .addAsLibraries(Maven.resolver()
                                    .resolve("com.google.code.gson:gson:2.3.1", "org.mockito:mockito-core:1.9.5")
                                    .withTransitivity()
                                    .asFile());
    }
    
    @Before
    public void initTestCase(){
        resourceClient = new ResourceClient(url);
        resourceClient.resourcePath("/DB").delete();
    }
    
    @Test
    @RunAsClient
    public void addValidCategoryAndFindIt(){        
        Long id = addCategoryAndGetId("category.json");
        findCategoryAndAssertResponseWithCategory(id, java());
    }
    
    @Test
    @RunAsClient
    public void addCategoryWithNullName(){
        
        Response response = resourceClient
                                .resourcePath(PATH_RESOURCE)
                                .postWithFile(getPathFileRequest(PATH_RESOURCE, "categoryWithNullName.json"));
        
        assertThat(response.getStatus(), is(equalTo(HttpCode.VALIDATION_ERROR.getCode())));
        
        assertJsonResponseWithFile(response, "categoryErrorNullName.json");
    }
    
    @Test
    @RunAsClient
    public void addExistentCategory(){

        //add category first time
        resourceClient.resourcePath(PATH_RESOURCE).postWithFile(getPathFileRequest(PATH_RESOURCE, "category.json"));
        
        //add caetgory second time
        Response response = resourceClient
                                .resourcePath(PATH_RESOURCE)
                                .postWithFile(getPathFileRequest(PATH_RESOURCE, "category.json"));
        
        assertThat(response.getStatus(), is(equalTo(HttpCode.VALIDATION_ERROR.getCode())));
        
        assertJsonResponseWithFile(response, "categoryAlreadyExists.json");
    }
    
    @Test
    @RunAsClient
    public void updateCategoryWithNameBelongingToOtherCategory() {
        Long javaCategoryId = addCategoryAndGetId("category.json");
        addCategoryAndGetId("categoryCleanCode.json");

        Response response = resourceClient.resourcePath(PATH_RESOURCE + "/" + javaCategoryId).putWithFile(
                        getPathFileRequest(PATH_RESOURCE, "categoryCleanCode.json"));
        assertThat(response.getStatus(), is(equalTo(HttpCode.VALIDATION_ERROR.getCode())));
        assertJsonResponseWithFile(response, "categoryAlreadyExists.json");
    }
    
    @Test
    @RunAsClient
    public void updateValidCategory() {
        Long id = addCategoryAndGetId("category.json");
        findCategoryAndAssertResponseWithCategory(id, java());

        Response response = resourceClient.resourcePath(PATH_RESOURCE + "/" + id).putWithFile(
                        getPathFileRequest(PATH_RESOURCE, "categoryCleanCode.json"));
        assertThat(response.getStatus(), is(equalTo(HttpCode.OK.getCode())));

        findCategoryAndAssertResponseWithCategory(id, cleanCode());
    }

    @Test
    @RunAsClient
    public void updateCategoryNotFound() {
        Response response = resourceClient.resourcePath(PATH_RESOURCE + "/999").putWithFile(
				getPathFileRequest(PATH_RESOURCE, "category.json"));
        assertThat(response.getStatus(), is(equalTo(HttpCode.NOT_FOUND.getCode())));
    }    
    
    @Test
    @RunAsClient
    public void findCategoryNotFound() {
        final Response response = resourceClient.resourcePath(PATH_RESOURCE + "/999").get();
        assertThat(response.getStatus(), is(equalTo(HttpCode.NOT_FOUND.getCode())));
    }
    
    @Test
    @RunAsClient
    public void findAllCategories() {
        resourceClient.resourcePath("DB/" + PATH_RESOURCE).postWithContent("");

        Response response = resourceClient.resourcePath(PATH_RESOURCE).get();
        assertThat(response.getStatus(), is(equalTo(HttpCode.OK.getCode())));
        assertResponseContainsTheCategories(response, 4, architecture(), cleanCode(), java(), networks());
    }
    
    private void assertResponseContainsTheCategories(Response response, int expectedTotalRecords, Category... expectedCategories){
        JsonObject result = JsonReader.readAsJsonObject(response.readEntity(String.class));
        
        int totalRecords = result.getAsJsonObject("paging").get("totalRecords").getAsInt();
        assertThat(totalRecords, is(equalTo(expectedTotalRecords)));
        
        JsonArray categoriesList = result.getAsJsonArray("entries");
        assertThat(categoriesList.size(), is(equalTo(expectedCategories.length)));
        
        for(int i = 0; i < expectedCategories.length; i++){
            Category expectedCategory = expectedCategories[i];
            assertThat(
                    categoriesList.get(i).getAsJsonObject().get("name").getAsString(),
                    is(equalTo(expectedCategory.getName())));
        }
    }
    
    private Long addCategoryAndGetId(final String fileName) {
        return addElementWithFileAndGetId(resourceClient, PATH_RESOURCE, PATH_RESOURCE, fileName);
    }

    private void assertJsonResponseWithFile(final Response response, final String fileName) {
        assertJsonMatchesFileContent(response.readEntity(String.class), getPathFileResponse(PATH_RESOURCE, fileName));
    }

    private void findCategoryAndAssertResponseWithCategory(Long categoryIdToBeFound, Category expectedCategory) {
        String json = IntTestUtils.findById(resourceClient, PATH_RESOURCE, categoryIdToBeFound);
	JsonObject categoryAsJson = JsonReader.readAsJsonObject(json);
        assertThat(JsonReader.getStringOrNull(categoryAsJson, "name"), is(equalTo(expectedCategory.getName())));
    }

}
