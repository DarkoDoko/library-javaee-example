package com.library.app.category.services.impl;

import com.library.app.category.CategoryExistentException;
import com.library.app.category.CategoryNotFoundException;
import com.library.app.category.model.Category;
import com.library.app.category.repository.CategoryRepository;
import com.library.app.category.services.CategoryServices;
import com.library.app.ValidationUtils;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.Validator;

@Stateless
public class CategoryServicesImpl implements CategoryServices{

    @Inject
    Validator validator;
    
    @Inject
    CategoryRepository repository;
    
    @Override
    public Category add(Category category) {
        
        validateCategory(category);
        
        return repository.add(category);
    }

    @Override
    public void update(Category category) {
        validateCategory(category);
        
        if(!repository.existsById(category.getId())){
            throw new CategoryNotFoundException();
        }
        
        repository.update(category);
    }

    @Override
    public Category findById(long id) {
        Category category = repository.findById(id);
        
        if(category == null){
            throw new CategoryNotFoundException();
        }
        
        return category;
    }

    @Override
    public List<Category> findAll() {
        return repository.findAll("name");
    }
    
    private void validateCategory(Category category) {
        ValidationUtils.validateEntityFields(validator, category);
        
        if(repository.alreadyExists(category)){
            throw new CategoryExistentException();
        }
    }    
}
