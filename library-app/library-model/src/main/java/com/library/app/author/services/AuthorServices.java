package com.library.app.author.services;

import com.library.app.author.AuthorNotFoundException;
import com.library.app.FieldNotValidException;
import com.library.app.author.model.Author;
import com.library.app.author.model.AuthorFilter;
import com.library.app.pagination.PaginatedData;
import javax.ejb.Local;

@Local
public interface AuthorServices {
    Author add(Author author) throws FieldNotValidException;

    void update(Author author) throws FieldNotValidException, AuthorNotFoundException;

    Author findById(Long id) throws AuthorNotFoundException;

    PaginatedData<Author> findByFilter(AuthorFilter authorFilter);
}
