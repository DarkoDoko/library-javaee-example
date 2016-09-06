package com.library.app.category.resource;

import com.google.gson.JsonObject;
import com.library.app.common.json.JsonReader;
import com.library.app.common.model.HttpCode;
import static com.library.app.commontests.category.CategoryForTestsRepository.java;
import static com.library.app.commontests.utils.FileTestNameUtils.getPathFileRequest;
import com.library.app.commontests.utils.JsonTestUtils;
import com.library.app.commontests.utils.ResourceClient;
import com.library.app.commontests.utils.ResourceDefinitions;
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
                .addAsResource("persistance-integration.xml", "META-INF/persistance.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .setWebXML(new File("src/test/resources.web.xml"))
                .addAsLibraries(Maven.resolver().resolve("com.google.code.gson:gson:2.3.1", "org.mockito:mockito-core:1.9.5").withTransitivity().asFile());
    }
    
    @Before
    public void innitTestCase(){
        this.resourceClient = new ResourceClient(url);
    }
    
    @Test
    @RunAsClient
    public void addValidCategoryAndFindIt(){
        Response response = resourceClient.resourcePath(PATH_RESOURCE)
                                .postWithFile(getPathFileRequest(PATH_RESOURCE, "category.json"));
        
        assertThat(response.getStatus(), is(equalTo(HttpCode.CREATED.getCode())));
        
        Long id = JsonTestUtils.getIdFromJson(response.readEntity(String.class));
        
        Response responseGet = resourceClient.resourcePath(PATH_RESOURCE + "/" + id).get();
        assertThat(responseGet.getStatus(), is(equalTo(HttpCode.OK.getCode())));
        
        JsonObject category = JsonReader.readAsJsonObject(responseGet.readEntity(String.class));
        assertThat(JsonReader.getStringOrNull(category, "name"), is(equalTo(java().getName())));
        
    }
}
