package com.library.app.book.repository;

import com.library.app.GenericRepository;
import com.library.app.book.model.Book;
import com.library.app.book.model.BookFilter;
import com.library.app.pagination.PaginatedData;
import java.util.HashMap;
import java.util.Map;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class BookRepository extends GenericRepository<Book>{
    
    @PersistenceContext
    EntityManager em;

    @Override
    protected Class<Book> getPersistentClass() {
        return Book.class;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
    public PaginatedData<Book> findByFilter(BookFilter bookFilter) {
		final StringBuilder clause = new StringBuilder("WHERE e.id IS NOT NULL");
		final Map<String, Object> queryParameters = new HashMap<>();
		if (bookFilter.getTitle() != null) {
			clause.append(" AND UPPER(e.title) LIKE UPPER(:title)");
			queryParameters.put("title", "%" + bookFilter.getTitle() + "%");
		}
		if (bookFilter.getCategoryId() != null) {
			clause.append(" AND e.category.id = :categoryId");
			queryParameters.put("categoryId", bookFilter.getCategoryId());
		}

		return findByParameters(clause.toString(), bookFilter.getPaginationData(), queryParameters, "title ASC");
	}
    
}
