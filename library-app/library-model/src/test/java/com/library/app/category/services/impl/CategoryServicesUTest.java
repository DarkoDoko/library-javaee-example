package com.library.app.category.services.impl;

import com.library.app.category.exception.CategoryExistentException;
import com.library.app.category.exception.CategoryNotFoundException;
import com.library.app.category.model.Category;
import com.library.app.category.repository.CategoryRepository;
import com.library.app.category.services.CategoryServices;
import com.library.app.common.exception.FieldNotValidException;
import static com.library.app.commontests.category.CategoryForTestsRepository.categoryWithId;
import static com.library.app.commontests.category.CategoryForTestsRepository.java;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Validation;
import javax.validation.Validator;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import org.hamcrest.core.IsNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 *
 * @author ddoko
 */
public class CategoryServicesUTest {
    
    private CategoryServices services;
    private CategoryRepository repository;
    private Validator validator;
    
    @Before
    public void init(){
        validator = Validation.buildDefaultValidatorFactory().getValidator();
        repository = mock(CategoryRepository.class);
        services = new CategoryServicesImpl();
        
        ((CategoryServicesImpl) services).validator = validator;
        ((CategoryServicesImpl) services).repository = repository;
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
        
        Category added = services.add(java());
        assertThat(added.getId(), is(equalTo(1L)));
    }

    @Test(expected = CategoryExistentException.class)
    public void addCategoryWithExistantName(){
        when(repository.alreadyExists(java())).thenReturn(true);
        
        services.add(java());
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
        
        services.update(categoryWithId(java(), 1L));
    }
    
    @Test(expected = CategoryNotFoundException.class)
    public void updateCategoryNotFound(){
        when(repository.alreadyExists(categoryWithId(java(), 1L))).thenReturn(false);
        when(repository.existsById(1L)).thenReturn(false);
        
        services.update(categoryWithId(java(), 1L));
    }
    
    @Test
    public void updateValidCategory(){
        when(repository.alreadyExists(categoryWithId(java(), 1L))).thenReturn(false);
        when(repository.existsById(1L)).thenReturn(true);
        
        services.update(categoryWithId(java(), 1L));
        
        verify(repository).update(categoryWithId(java(), 1L));
    }
    
    @Test
    public void findCategoryById(){
        when(repository.findById(1L)).thenReturn(categoryWithId(java(), 1L));
        
        Category category = services.findById(1L);
        
        assertThat(category, is(notNullValue()));
        assertThat(category.getId(), is(equalTo(1L)));
        assertThat(category.getName(), is(equalTo(java().getName())));
    }
    
    @Test(expected = CategoryNotFoundException.class)
    public void findCategotyByIdNotFound(){
        when(repository.findById(1L)).thenReturn(null);
        
        services.findById(1L);
    }
    
    @Test
    public void findAllNoCategories(){
        when(repository.findAll("name")).thenReturn(new ArrayList<>());
        
        List<Category> categories = services.findAll();
        
        assertThat(categories.isEmpty(), is(equalTo(true)));
    }

    private void addCategoryWithInvalidName(String name) {
        try{
            services.add(new Category(name));
            fail("An error should have been thrown");
        } catch (FieldNotValidException e){
            assertThat(e.getFieldName(), is(equalTo("name")));
        }   
    }
    
    private void updateCategoryWithInvalidName(String name) {
        try{
            services.update(new Category(name));
            fail("An error should have been thrown");
        } catch (FieldNotValidException e){
            assertThat(e.getFieldName(), is(equalTo("name")));
        }   
    }
    
}
