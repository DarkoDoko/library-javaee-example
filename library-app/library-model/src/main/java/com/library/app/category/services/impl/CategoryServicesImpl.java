package com.library.app.category.services.impl;

import com.library.app.category.exception.CategoryExistentException;
import com.library.app.category.exception.CategoryNotFoundException;
import com.library.app.category.model.Category;
import com.library.app.category.repository.CategoryRepository;
import com.library.app.category.services.CategoryServices;
import com.library.app.common.exception.FieldNotValidException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

/**
 *
 * @author ddoko
 */
public class CategoryServicesImpl implements CategoryServices{

    Validator validator;
    
    CategoryRepository repository;
    
    @Override
    public Category add(Category category) {
        
        Set<ConstraintViolation<Category>> errors = validator.validate(category);
        Iterator<ConstraintViolation<Category>> iterErrors = errors.iterator();
        
        if(iterErrors.hasNext()) {
            ConstraintViolation<Category> violation = iterErrors.next();
            throw new FieldNotValidException(violation.getPropertyPath().toString(), violation.getMessage());
        }
        
        if(repository.alreadyExists(category)){
            throw new CategoryExistentException();
        }
        
        return repository.add(category);
    }

    @Override
    public void update(Category category) {
        Set<ConstraintViolation<Category>> errors = validator.validate(category);
        Iterator<ConstraintViolation<Category>> iterErrors = errors.iterator();
        
        if(iterErrors.hasNext()){
            ConstraintViolation<Category> violation = iterErrors.next();
            throw new FieldNotValidException(violation.getPropertyPath().toString(), violation.getMessage());
        }
        
        if(repository.alreadyExists(category)){
           throw new CategoryExistentException();
        }
        
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
        List<Category> categories = repository.findAll("name");
        
        return categories;
    }
    
}
