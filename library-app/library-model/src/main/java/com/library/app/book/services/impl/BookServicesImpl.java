package com.library.app.book.services.impl;

import com.library.app.FieldNotValidException;
import com.library.app.ValidationUtils;
import com.library.app.author.AuthorNotFoundException;
import com.library.app.author.model.Author;
import com.library.app.author.services.AuthorServices;
import com.library.app.book.BookNotFoundException;
import com.library.app.book.model.Book;
import com.library.app.book.model.BookFilter;
import com.library.app.book.repository.BookRepository;
import com.library.app.book.services.BookServices;
import com.library.app.category.CategoryNotFoundException;
import com.library.app.category.model.Category;
import com.library.app.category.services.CategoryServices;
import com.library.app.pagination.PaginatedData;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.validation.Validator;

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
    public Book add(Book book) throws FieldNotValidException, CategoryNotFoundException, AuthorNotFoundException {
        ValidationUtils.validateEntityFields(validator, book);
        
        checkCategoryAndSetItOnBook(book);
        checkAuthorsAndSetThemOnBook(book);
        
        return bookRepository.add(book);
    }

    @Override
    public void update(Book book) throws FieldNotValidException, CategoryNotFoundException, AuthorNotFoundException, BookNotFoundException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Book findById(Long id) throws BookNotFoundException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PaginatedData<Book> findByFilter(BookFilter bookFilter) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void checkCategoryAndSetItOnBook(Book book) {
        Category category = categoryServices.findById(book.getCategory().getId());
        book.setCategory(category);
    }

    private void checkAuthorsAndSetThemOnBook(Book book) {
        List<Author> newAuthorList = new ArrayList<>();
        
        book.getAuthors().forEach((a) -> {
            try {
                Author author = authorServices.findById(a.getId());
                newAuthorList.add(author);
            } catch (AuthorNotFoundException ex) {
                Logger.getLogger(BookServicesImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        book.setAuthors(newAuthorList);
    }
}
