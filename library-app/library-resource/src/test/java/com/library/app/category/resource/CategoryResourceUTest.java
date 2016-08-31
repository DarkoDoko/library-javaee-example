package com.library.app.category.resource;

import com.library.app.category.services.CategoryServices;
import static com.library.app.commontests.category.CategoryForTestsRepository.categoryWithId;
import static com.library.app.commontests.category.CategoryForTestsRepository.java;
import static com.library.app.commontests.utils.FileTestNameUtils.getPathFileRequest;
import static com.library.app.commontests.utils.JsonTestUtils.assertJsonMatchesExpectedJson;
import static com.library.app.commontests.utils.JsonTestUtils.readJsonFile;
import javax.ws.rs.core.Response;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import org.junit.Before;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

/**
 *
 * @author ddoko
 */
public class CategoryResourceUTest {
    
    private CategoryResource resource;
    
    private static final String PATH_RESOURCE = "categories";
    
    @Mock
    private CategoryServices services;
    
    @Before
    public void initTestCase() {
        MockitoAnnotations.initMocks(this);
        
        resource = new CategoryResource();
        
        resource.services = services;
    }

    @Test
    public void addValidCategory() {
        when(services.add(java())).thenReturn(categoryWithId(java(), 1L));
        
        Response response = resource.add(readJsonFile(getPathFileRequest(PATH_RESOURCE, "newCategory.json")));
        
        assertThat(response.getStatus(), is(equalTo(201)));
        assertJsonMatchesExpectedJson(response.getEntity().toString(), "{\"id\": 1}");
    }
    
}
