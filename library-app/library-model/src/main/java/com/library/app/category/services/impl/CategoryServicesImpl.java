package com.library.app.category.services.impl;

import com.library.app.category.CategoryExistentException;
import com.library.app.category.CategoryNotFoundException;
import com.library.app.category.model.Category;
import com.library.app.category.repository.CategoryRepository;
import com.library.app.category.services.CategoryServices;
import com.library.app.ValidationUtils;
import com.library.app.logaudit.interceptor.Auditable;
import com.library.app.logaudit.interceptor.LogAuditInterceptor;
import com.library.app.logaudit.model.LogAudit;
import com.library.app.logaudit.model.LogAudit.Action;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.validation.Validator;

@Stateless
@Interceptors(LogAuditInterceptor.class)
public class CategoryServicesImpl implements CategoryServices{

    @Inject
    Validator validator;
    
    @Inject
    CategoryRepository repository;
    
    @Override
    @Auditable(action = Action.ADD)
    public Category add(Category category) {
        
        validateCategory(category);
        
        return repository.add(category);
    }

    @Override
    @Auditable(action = Action.UPDATE)
    public void update(Category category) {
        validateCategory(category);
        
        if(!repository.existsById(category.getId())){
            throw new CategoryNotFoundException();
        }
        
        repository.update(category);
    }

    @Override
    public Category findById(long id) throws CategoryNotFoundException{
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
