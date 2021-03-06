package com.library.app.author.services.impl;

import com.library.app.ValidationUtils;
import com.library.app.author.AuthorNotFoundException;
import com.library.app.author.model.Author;
import com.library.app.author.model.AuthorFilter;
import com.library.app.author.repository.AuthorRepository;
import com.library.app.author.services.AuthorServices;
import com.library.app.logaudit.interceptor.Auditable;
import com.library.app.logaudit.interceptor.LogAuditInterceptor;
import com.library.app.logaudit.model.LogAudit.Action;
import com.library.app.pagination.PaginatedData;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.validation.Validator;

@Stateless
@Interceptors(LogAuditInterceptor.class)
public class AuthorServicesImpl implements AuthorServices{

    @Inject
    AuthorRepository repository;

    @Inject
    Validator validator;

    @Override
    @Auditable(action = Action.ADD)
    public Author add(Author author) {
        ValidationUtils.validateEntityFields(validator, author);

        return repository.add(author);
    }

    @Override
    @Auditable(action = Action.UPDATE)    
    public void update(Author author) {
        ValidationUtils.validateEntityFields(validator, author);

        if(!repository.existsById(author.getId())){
            throw new AuthorNotFoundException();
        }

        repository.update(author);
    }

    @Override
    public Author findById(Long id) throws AuthorNotFoundException {
        Author author = repository.findById(id);

        if(author == null){
            throw new AuthorNotFoundException();
        }

        return author;
    }

    @Override
    public PaginatedData<Author> findByFilter(AuthorFilter authorFilter) {
        return repository.findByFilter(authorFilter);
    }
    
}
