package com.library.app.category.services;

import com.library.app.category.CategoryExistentException;
import com.library.app.category.CategoryNotFoundException;
import com.library.app.category.model.Category;
import com.library.app.FieldNotValidException;
import java.util.List;
import javax.ejb.Local;

@Local
public interface CategoryServices {
    
    Category add(Category category) throws FieldNotValidException, CategoryExistentException; 

    void update(Category category) throws FieldNotValidException, CategoryNotFoundException;

    Category findById(long id) throws CategoryNotFoundException;

    List<Category> findAll();
    
}
