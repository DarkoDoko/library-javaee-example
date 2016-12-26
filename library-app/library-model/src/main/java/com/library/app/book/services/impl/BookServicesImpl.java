package com.library.app.book.services.impl;

import com.library.app.ValidationUtils;
import com.library.app.author.model.Author;
import com.library.app.author.services.AuthorServices;
import com.library.app.book.BookNotFoundException;
import com.library.app.book.model.Book;
import com.library.app.book.model.BookFilter;
import com.library.app.book.repository.BookRepository;
import com.library.app.book.services.BookServices;
import com.library.app.category.model.Category;
import com.library.app.category.services.CategoryServices;
import com.library.app.logaudit.interceptor.Auditable;
import com.library.app.logaudit.interceptor.LogAuditInterceptor;
import com.library.app.logaudit.model.LogAudit;
import com.library.app.logaudit.model.LogAudit.Action;
import com.library.app.pagination.PaginatedData;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.validation.Validator;

@Stateless
@Interceptors(LogAuditInterceptor.class)
public class BookServicesImpl implements BookServices{
    
    @Inject
    BookRepository bookRepository;

    @Inject
    Validator validator;
    
    @Inject
    AuthorServices authorServices;
    
    @Inject
    CategoryServices categoryServices;
    
    @Override
    @Auditable(action = Action.ADD)
    public Book add(Book book) {
        ValidationUtils.validateEntityFields(validator, book);
        
        checkCategoryAndSetItOnBook(book);
        checkAuthorsAndSetThemOnBook(book);
        
        return bookRepository.add(book);
    }

    @Override
    @Auditable(action = Action.UPDATE)
    public void update(Book book) {
        ValidationUtils.validateEntityFields(validator, book);
        
        if(!bookRepository.existsById(book.getId())) {
            throw new BookNotFoundException();
        }
        
        checkCategoryAndSetItOnBook(book);
        checkAuthorsAndSetThemOnBook(book);
        
        bookRepository.update(book);
    }

    @Override
    public Book findById(Long id) {
        Book book = bookRepository.findById(id);
        if(book == null) {
            throw new BookNotFoundException();
        }
        return book;
    }

    @Override
    public PaginatedData<Book> findByFilter(BookFilter bookFilter) {
        return bookRepository.findByFilter(bookFilter);
    }

    private void checkCategoryAndSetItOnBook(Book book) {
        Category category = categoryServices.findById(book.getCategory().getId());
        book.setCategory(category);
    }

    private void checkAuthorsAndSetThemOnBook(Book book) {
        List<Author> newAuthorList = new ArrayList<>();
        
        book.getAuthors().forEach((a) -> {
            Author author = authorServices.findById(a.getId());
            newAuthorList.add(author);
        });
        book.setAuthors(newAuthorList);
    }
}
