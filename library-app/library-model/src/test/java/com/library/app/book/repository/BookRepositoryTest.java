package com.library.app.book.repository;

import static com.library.app.author.AuthorForTestsRepository.allAuthors;
import static com.library.app.author.AuthorForTestsRepository.erichGamma;
import static com.library.app.author.AuthorForTestsRepository.johnVlissides;
import static com.library.app.author.AuthorForTestsRepository.ralphJohnson;
import static com.library.app.author.AuthorForTestsRepository.richardHelm;
import com.library.app.author.model.Author;
import com.library.app.book.BookForTestsRepository;
import static com.library.app.book.BookForTestsRepository.allBooks;
import static com.library.app.book.BookForTestsRepository.designPatterns;
import static com.library.app.book.BookForTestsRepository.effectiveJava;
import static com.library.app.book.BookForTestsRepository.normalizeDependencies;
import static com.library.app.book.BookForTestsRepository.peaa;
import static com.library.app.book.BookForTestsRepository.refactoring;
import com.library.app.book.model.Book;
import com.library.app.book.model.BookFilter;
import static com.library.app.category.CategoryForTestsRepository.allCategories;
import com.library.app.commontests.utils.TestBaseRepository;
import com.library.app.pagination.PaginatedData;
import com.library.app.pagination.filter.PaginationData;
import com.library.app.pagination.filter.PaginationData.OrderMode;
import java.util.List;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import org.junit.After;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;

public class BookRepositoryTest extends TestBaseRepository {

    private BookRepository bookRUT;

    @Before
    public void initTestCase() {
        initializeTestDB();

        bookRUT = new BookRepository();
        bookRUT.em = em;

        loadCategoriesAndAuthors();
    }

    @After
    public void setDownTestCase() {
        closeEntityManager();
    }

    @Test
    public void addBookAndFindIt() {
        Book designPatterns = normalizeDependencies(designPatterns(), em);

        Long bookAddedId = dbExecutor.executeCommand(() -> {
            return bookRUT.add(designPatterns).getId();
        });

        assertThat(bookAddedId, is(notNullValue()));

        Book book = bookRUT.findById(bookAddedId);
        assertThat(book.getTitle(), is(equalTo(designPatterns().getTitle())));
        assertThat(book.getDescription(), is(equalTo(designPatterns().getDescription())));
        assertThat(book.getCategory().getName(), is(equalTo(designPatterns().getCategory().getName())));
        assertAuthors(book, erichGamma(), johnVlissides(), ralphJohnson(), richardHelm());
        assertThat(book.getPrice(), is(equalTo(48.94D)));

    }

    @Test
    public void findBookByIdNotFound() {
        Book book = bookRUT.findById(999L);
        assertThat(book, is(nullValue()));
    }

    @Test
    public void updateBook() {
        Book designPatterns = normalizeDependencies(designPatterns(), em);
        Long bookAddedId = dbExecutor.executeCommand(() -> {
            return bookRUT.add(designPatterns).getId();
        });

        assertThat(bookAddedId, is(notNullValue()));

        Book book = bookRUT.findById(bookAddedId);

        assertThat(book.getTitle(), is(equalTo(designPatterns().getTitle())));

        book.setTitle("Design Patterns");
        dbExecutor.executeCommand(() -> {
            bookRUT.update(book);
            return null;
        });

        Book bookAfterUpdate = bookRUT.findById(bookAddedId);
        assertThat(bookAfterUpdate.getTitle(), is(equalTo("Design Patterns")));
    }

    @Test
    public void existsById() {
        Book designPatterns = normalizeDependencies(designPatterns(), em);
        Long bookAddedId = dbExecutor.executeCommand(() -> {
            return bookRUT.add(designPatterns).getId();
        });

        assertThat(bookRUT.existsById(bookAddedId), is(equalTo(true)));
        assertThat(bookRUT.existsById(999l), is(equalTo(false)));
    }

    @Test
    public void findByFilterNoFilter() {
        loadBooksForFindByFilter();

        PaginatedData<Book> result = bookRUT.findByFilter(new BookFilter());
        assertThat(result.getNumberOfRows(), is(equalTo(5)));
        assertThat(result.getRows().size(), is(equalTo(5)));
        assertThat(result.getRow(0).getTitle(), is(equalTo(BookForTestsRepository.cleanCode().getTitle())));
        assertThat(result.getRow(1).getTitle(), is(equalTo(designPatterns().getTitle())));
        assertThat(result.getRow(2).getTitle(), is(equalTo(effectiveJava().getTitle())));
        assertThat(result.getRow(3).getTitle(), is(equalTo(peaa().getTitle())));
        assertThat(result.getRow(4).getTitle(), is(equalTo(refactoring().getTitle())));
    }
    
    @Test
	public void findByFilterWithPaging() {
		loadBooksForFindByFilter();

		final BookFilter bookFilter = new BookFilter();
		bookFilter.setPaginationData(new PaginationData(0, 3, "title", OrderMode.DESCENDING));
		PaginatedData<Book> result = bookRUT.findByFilter(bookFilter);

		assertThat(result.getNumberOfRows(), is(equalTo(5)));
		assertThat(result.getRows().size(), is(equalTo(3)));
		assertThat(result.getRow(0).getTitle(), is(equalTo(refactoring().getTitle())));
		assertThat(result.getRow(1).getTitle(), is(equalTo(peaa().getTitle())));
		assertThat(result.getRow(2).getTitle(), is(equalTo(effectiveJava().getTitle())));

		bookFilter.setPaginationData(new PaginationData(3, 3, "title", OrderMode.DESCENDING));
		result = bookRUT.findByFilter(bookFilter);

		assertThat(result.getNumberOfRows(), is(equalTo(5)));
		assertThat(result.getRows().size(), is(equalTo(2)));
		assertThat(result.getRow(0).getTitle(), is(equalTo(designPatterns().getTitle())));
		assertThat(result.getRow(1).getTitle(), is(equalTo(BookForTestsRepository.cleanCode().getTitle())));
	}

    private void loadBooksForFindByFilter() {
        dbExecutor.executeCommand(() -> {
            allBooks().forEach((book) -> bookRUT.add(normalizeDependencies(book, em)));
            return null;
        });
    }

    private void assertAuthors(Book book, Author... expectedAuthors) {
        List<Author> authors = book.getAuthors();
        assertThat(authors.size(), is(equalTo(expectedAuthors.length)));

        for (int i = 0; i < expectedAuthors.length; i++) {
            final Author actualAuthor = authors.get(i);
            final Author expectedAuthor = expectedAuthors[i];
            assertThat(actualAuthor.getName(), is(equalTo(expectedAuthor.getName())));
        }
    }

    private void loadCategoriesAndAuthors() {
        dbExecutor.executeCommand(() -> {
            allCategories().forEach(em::persist);
            allAuthors().forEach(em::persist);
            return null;
        });
    }
}
