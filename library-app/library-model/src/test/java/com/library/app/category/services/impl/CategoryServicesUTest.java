package com.library.app.category.services.impl;

import com.library.app.category.model.Category;
import com.library.app.category.repository.CategoryRepository;
import com.library.app.category.services.CategoryServices;
import com.library.app.common.exception.FieldNotValidException;
import javax.validation.Validation;
import javax.validation.Validator;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.mock;

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

    private void addCategoryWithInvalidName(String name) {
        try{
            services.add(new Category(name));
            fail("An error should have been thrown");
        } catch (FieldNotValidException e){
            assertThat(e.getFieldName(), is(equalTo("name")));
        }
        
    }
    
}
