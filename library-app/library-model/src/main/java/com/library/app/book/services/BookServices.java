package com.library.app.book.services;

import com.library.app.FieldNotValidException;
import com.library.app.author.AuthorNotFoundException;
import com.library.app.book.model.Book;
import com.library.app.book.model.BookFilter;
import com.library.app.book.BookNotFoundException;
import com.library.app.category.CategoryNotFoundException;
import com.library.app.pagination.PaginatedData;
import javax.ejb.Local;

@Local
public interface BookServices {
    Book add(Book book) throws FieldNotValidException, CategoryNotFoundException, AuthorNotFoundException;

	void update(Book book) throws FieldNotValidException, CategoryNotFoundException, AuthorNotFoundException, BookNotFoundException;

	Book findById(Long id) throws BookNotFoundException;

	PaginatedData<Book> findByFilter(BookFilter bookFilter);
}
