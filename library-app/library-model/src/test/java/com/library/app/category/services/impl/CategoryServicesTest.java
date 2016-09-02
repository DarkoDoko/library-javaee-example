package com.library.app.category.services.impl;

import com.library.app.category.exception.CategoryExistentException;
import com.library.app.category.exception.CategoryNotFoundException;
import com.library.app.category.model.Category;
import com.library.app.category.repository.CategoryRepository;
import com.library.app.category.services.CategoryServices;
import com.library.app.common.exception.FieldNotValidException;
import static com.library.app.commontests.category.CategoryForTestsRepository.categoryWithId;
import static com.library.app.commontests.category.CategoryForTestsRepository.cleanCode;
import static com.library.app.commontests.category.CategoryForTestsRepository.java;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.validation.Validation;
import javax.validation.Validator;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 *
 * @author ddoko
 */
public class CategoryServicesTest {
    
    private CategoryServices servicesUnderTest;
    private CategoryRepository repository;
    private Validator validator;
    
    @Before
    public void init(){
        validator = Validation.buildDefaultValidatorFactory().getValidator();
        repository = mock(CategoryRepository.class);
        servicesUnderTest = new CategoryServicesImpl();
        
        ((CategoryServicesImpl) servicesUnderTest).validator = validator;
        ((CategoryServicesImpl) servicesUnderTest).repository = repository;
    }
    
    @Test
    public void addCategoryWithNullName(){
        addCategoryWithInvalidName(null);
    }
    
    @Test
    public void addCategoryWithShortName(){
        addCategoryWithInvalidName("A");
    }
    
    @Test
    public void addCategoryWithLongName(){
        addCategoryWithInvalidName("This is a long name that will cause an exception to be thrown");
    }
    
    @Test
    public void addValidCategory(){
        when(repository.alreadyExists(java())).thenReturn(false);
        when(repository.add(java())).thenReturn(categoryWithId(java(), 1L));
        
        Category added = servicesUnderTest.add(java());
        assertThat(added.getId(), is(equalTo(1L)));
    }

    @Test(expected = CategoryExistentException.class)
    public void addCategoryWithExistantName(){
        when(repository.alreadyExists(java())).thenReturn(true);
        
        servicesUnderTest.add(java());
    }
    
    @Test
    public void updateCategoryWithNullName(){
        updateCategoryWithInvalidName(null);
    }
    
    @Test
    public void updateCategoryWithShortName(){
        updateCategoryWithInvalidName("A");
    }
    
    @Test
    public void updateCategoryWithLongName(){
        updateCategoryWithInvalidName("This is a long name that will cause an exception to be thrown");
    }
    
    @Test(expected = CategoryExistentException.class)
    public void updateCategoryWithExistantName(){
        when(repository.alreadyExists(categoryWithId(java(), 1L))).thenReturn(true);
        
        servicesUnderTest.update(categoryWithId(java(), 1L));
    }
    
    @Test(expected = CategoryNotFoundException.class)
    public void updateCategoryNotFound(){
        when(repository.alreadyExists(categoryWithId(java(), 1L))).thenReturn(false);
        when(repository.existsById(1L)).thenReturn(false);
        
        servicesUnderTest.update(categoryWithId(java(), 1L));
    }
    
    @Test
    public void updateValidCategory(){
        when(repository.alreadyExists(categoryWithId(java(), 1L))).thenReturn(false);
        when(repository.existsById(1L)).thenReturn(true);
        
        servicesUnderTest.update(categoryWithId(java(), 1L));
        
        verify(repository).update(categoryWithId(java(), 1L));
    }
    
    @Test
    public void findCategoryById(){
        when(repository.findById(1L)).thenReturn(categoryWithId(java(), 1L));
        
        Category category = servicesUnderTest.findById(1L);
        
        assertThat(category, is(notNullValue()));
        assertThat(category.getId(), is(equalTo(1L)));
        assertThat(category.getName(), is(equalTo(java().getName())));
    }
    
    @Test(expected = CategoryNotFoundException.class)
    public void findCategotyByIdNotFound(){
        when(repository.findById(1L)).thenReturn(null);
        
        servicesUnderTest.findById(1L);
    }
    
    @Test
    public void findAllNoCategories(){
        when(repository.findAll("name")).thenReturn(new ArrayList<>());
        
        List<Category> categories = servicesUnderTest.findAll();
        
        assertThat(categories.isEmpty(), is(equalTo(true)));
    }
    
    @Test
    public void findAllCategories(){
        when(repository.findAll("name")).thenReturn(
                Arrays.asList(categoryWithId(java(), 1L), categoryWithId(cleanCode(), 2L)));
        
        List<Category> categories = servicesUnderTest.findAll();
        
        assertThat(categories, is(notNullValue()));
        assertThat(categories.size(), is(equalTo(2)));
        assertThat(categories.get(0).getName(), is(equalTo(java().getName())));
        assertThat(categories.get(1).getName(), is(equalTo(cleanCode().getName())));
    }

    private void addCategoryWithInvalidName(String name) {
        try{
            servicesUnderTest.add(new Category(name));
            fail("An error should have been thrown");
        } catch (FieldNotValidException e){
            assertThat(e.getFieldName(), is(equalTo("name")));
        }   
    }
    
    private void updateCategoryWithInvalidName(String name) {
        try{
            servicesUnderTest.update(new Category(name));
            fail("An error should have been thrown");
        } catch (FieldNotValidException e){
            assertThat(e.getFieldName(), is(equalTo("name")));
        }   
    }
    
}
