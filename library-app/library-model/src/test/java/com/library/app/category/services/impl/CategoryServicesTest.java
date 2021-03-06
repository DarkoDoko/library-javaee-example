package com.library.app.category.services.impl;

import com.library.app.category.CategoryExistentException;
import com.library.app.category.CategoryNotFoundException;
import com.library.app.category.model.Category;
import com.library.app.category.repository.CategoryRepository;
import com.library.app.category.services.CategoryServices;
import com.library.app.FieldNotValidException;
import static com.library.app.category.CategoryForTestsRepository.categoryWithId;
import static com.library.app.category.CategoryForTestsRepository.cleanCode;
import static com.library.app.category.CategoryForTestsRepository.java;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.validation.Validation;
import javax.validation.Validator;
import static org.hamcrest.CoreMatchers.equalTo;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.when;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class CategoryServicesTest {
    
    private CategoryServices servicesUnderTest;
    private CategoryRepository repositoryCollaborator;
    private Validator validatorCollaborator;
    
    @Before
    public void init(){
        validatorCollaborator = Validation.buildDefaultValidatorFactory().getValidator();
        repositoryCollaborator = mock(CategoryRepository.class);
        
        servicesUnderTest = new CategoryServicesImpl();
        
        ((CategoryServicesImpl) servicesUnderTest).validator = validatorCollaborator;
        ((CategoryServicesImpl) servicesUnderTest).repository = repositoryCollaborator;
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
        when(repositoryCollaborator.alreadyExists(java())).thenReturn(false);
        when(repositoryCollaborator.add(java())).thenReturn(categoryWithId(java(), 1L));
        
        Category added = servicesUnderTest.add(java());
        assertThat(added.getId(), is(equalTo(1L)));
    }

    @Test(expected = CategoryExistentException.class)
    public void addCategoryWithExistantName(){
        when(repositoryCollaborator.alreadyExists(java())).thenReturn(true);
        
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
        when(repositoryCollaborator.alreadyExists(categoryWithId(java(), 1L))).thenReturn(true);
        
        servicesUnderTest.update(categoryWithId(java(), 1L));
    }
    
    @Test(expected = CategoryNotFoundException.class)
    public void updateCategoryNotFound(){
        when(repositoryCollaborator.alreadyExists(categoryWithId(java(), 1L))).thenReturn(false);
        when(repositoryCollaborator.existsById(1L)).thenReturn(false);
        
        servicesUnderTest.update(categoryWithId(java(), 1L));
    }
    
    @Test
    public void updateValidCategory(){
        when(repositoryCollaborator.alreadyExists(categoryWithId(java(), 1L))).thenReturn(false);
        when(repositoryCollaborator.existsById(1L)).thenReturn(true);
        
        servicesUnderTest.update(categoryWithId(java(), 1L));
        
        verify(repositoryCollaborator).update(categoryWithId(java(), 1L));
    }
    
    @Test
    public void findCategoryById(){
        when(repositoryCollaborator.findById(1L)).thenReturn(categoryWithId(java(), 1L));
        
        Category category = servicesUnderTest.findById(1L);
        
        assertThat(category, is(notNullValue()));
        assertThat(category.getId(), is(equalTo(1L)));
        assertThat(category.getName(), is(equalTo(java().getName())));
    }
    
    @Test(expected = CategoryNotFoundException.class)
    public void findCategoryByIdNotFound(){
        when(repositoryCollaborator.findById(1L)).thenReturn(null);
        
        servicesUnderTest.findById(1L);
    }
    
    @Test
    public void findAllNoCategories(){
        when(repositoryCollaborator.findAll("name")).thenReturn(new ArrayList<>());
        
        List<Category> categories = servicesUnderTest.findAll();
        
        assertThat(categories.isEmpty(), is(equalTo(true)));
    }
    
    @Test
    public void findAllCategories(){
        when(repositoryCollaborator.findAll("name")).thenReturn(
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
