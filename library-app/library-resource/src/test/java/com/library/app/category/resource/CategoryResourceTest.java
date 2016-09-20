package com.library.app.category.resource;

import com.library.app.category.CategoryExistentException;
import com.library.app.category.CategoryNotFoundException;
import com.library.app.category.model.Category;
import com.library.app.category.services.CategoryServices;
import com.library.app.FieldNotValidException;
import com.library.app.common.model.HttpCode;
import static com.library.app.category.CategoryForTestsRepository.categoryWithId;
import static com.library.app.category.CategoryForTestsRepository.java;
import static com.library.app.category.CategoryForTestsRepository.networks;
import com.library.app.commontests.utils.FileTestNameUtils;
import static com.library.app.commontests.utils.FileTestNameUtils.getPathFileRequest;
import static com.library.app.commontests.utils.JsonTestUtils.assertJsonMatchesExpectedJson;
import static com.library.app.commontests.utils.JsonTestUtils.assertJsonMatchesFileContent;
import static com.library.app.commontests.utils.JsonTestUtils.readJsonFile;
import java.util.ArrayList;
import java.util.Arrays;
import javax.ws.rs.core.Response;
import static org.hamcrest.CoreMatchers.equalTo;
import org.junit.Test;
import org.junit.Before;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

/**
 *
 * @author ddoko
 */
public class CategoryResourceTest {
    
    private CategoryResource resourceUnderTest;
    
    private static final String PATH_RESOURCE = "categories";
    
    @Mock
    private CategoryServices services;
    
    @Before
    public void initTestCase() {
        MockitoAnnotations.initMocks(this);
        
        resourceUnderTest = new CategoryResource();
        
        resourceUnderTest.services = services;
        resourceUnderTest.jsonConverter = new CategoryJsonConverter();
    }

    @Test
    public void addValidCategory() {
        when(services.add(java())).thenReturn(categoryWithId(java(), 1L));
        
        Response response = resourceUnderTest.add(readJsonFile(getPathFileRequest(PATH_RESOURCE, "newCategory.json")));
        
        assertThat(response.getStatus(), is(equalTo(HttpCode.CREATED.getCode())));
        assertJsonMatchesExpectedJson(response.getEntity().toString(), "{\"id\": 1}");
    }
    
    @Test
    public void addExistentCategory(){
        when(services.add(java())).thenThrow(new CategoryExistentException());
        
        Response response = resourceUnderTest.add(readJsonFile(getPathFileRequest(PATH_RESOURCE, "newCategory.json")));
        
        assertThat(response.getStatus(), is(equalTo(HttpCode.VALIDATION_ERROR.getCode())));
        assertJsonResponseWithFile(response, "categoryAlreadyExists.json");
    }
    
    @Test
    public void addCategoryWithNullName() {
        when(services.add(new Category())).thenThrow(new FieldNotValidException("name", "may not be null"));
        
        Response response = resourceUnderTest.add(
                readJsonFile(getPathFileRequest(PATH_RESOURCE, "categoryWithNullName.json")));
        
        assertThat(response.getStatus(), is(equalTo(HttpCode.VALIDATION_ERROR.getCode())));
        assertJsonResponseWithFile(response, "categoryErrorNullName.json");
    }
    
    @Test
    public void updateValidCategory(){
        Response response = resourceUnderTest.update(1L, 
                readJsonFile(getPathFileRequest(PATH_RESOURCE, "category.json")));
        
        assertThat(response.getStatus(), is(equalTo(HttpCode.OK.getCode())));
        assertThat(response.getEntity().toString(), is(equalTo("")));
        
        verify(services).update(categoryWithId(java(), 1L));
    }
    
    @Test
    public void updateCategoryWithNameBelongingToOtherCategory(){
        doThrow(new CategoryExistentException()).when(services).update(categoryWithId(java(), 1L));
        
        Response response = resourceUnderTest.update(1L, 
                readJsonFile(getPathFileRequest(PATH_RESOURCE, "category.json")));
        
        assertThat(response.getStatus(), is(equalTo(HttpCode.VALIDATION_ERROR.getCode())));
        assertJsonResponseWithFile(response, "categoryAlreadyExists.json");
    }
    
    @Test
    public void updateCategoryWithNullName(){
        doThrow(new FieldNotValidException("name", "may not be null")).
                when(services).update(categoryWithId(new Category(), 1L));
        
        Response response = resourceUnderTest.update(1L, 
                readJsonFile(getPathFileRequest(PATH_RESOURCE, "categoryWithNullName.json")));
        
        assertThat(response.getStatus(), is(equalTo(HttpCode.VALIDATION_ERROR.getCode())));
        assertJsonResponseWithFile(response, "categoryErrorNullName.json");
    }
    
    @Test
    public void updateCategoryNotFound(){
        doThrow(new CategoryNotFoundException()).when(services).update(categoryWithId(java(), 2L));
        
        Response response = resourceUnderTest.update(2L, 
                readJsonFile(getPathFileRequest(PATH_RESOURCE, "category.json")));
        
        assertThat(response.getStatus(), is(equalTo(HttpCode.NOT_FOUND.getCode())));
        assertJsonResponseWithFile(response, "categoryNotFound.json");
    }
    
    @Test
    public void findCategory(){
        when(services.findById(1L)).thenReturn(categoryWithId(java(), 1L));
        
        Response response = resourceUnderTest.findById(1L);
        
        assertThat(response.getStatus(), is(equalTo(HttpCode.OK.getCode())));
        assertJsonResponseWithFile(response, "categoryFound.json");
    }
    
    @Test
    public void findCategoryNotFound(){
        when(services.findById(1L)).thenThrow(new CategoryNotFoundException());
        
        Response response = resourceUnderTest.findById(1L);
        
        assertThat(response.getStatus(), is(equalTo(HttpCode.NOT_FOUND.getCode())));
    }
    
    @Test
    public void findAllNoCategories(){
        when(services.findAll()).thenReturn(new ArrayList<>());
        
        Response response = resourceUnderTest.findAll();
        
        assertThat(response.getStatus(), is(equalTo(HttpCode.OK.getCode())));
        assertJsonResponseWithFile(response, "emptyListOfCategories.json");
    }
    
    @Test
    public void findAllTwoCategories(){
        when(services.findAll()).thenReturn(
                Arrays.asList(categoryWithId(java(), 1L), categoryWithId(networks(), 2L)));
        
        Response response = resourceUnderTest.findAll();
        
        assertThat(response.getStatus(), is(equalTo(HttpCode.OK.getCode())));
        assertJsonResponseWithFile(response, "twoCategories.json");
        
    }
    
    private void assertJsonResponseWithFile(Response response, String fileName){
        assertJsonMatchesFileContent(
                response.getEntity().toString(), 
                FileTestNameUtils.getPathFileResponse(PATH_RESOURCE, fileName));
    }
    
}
