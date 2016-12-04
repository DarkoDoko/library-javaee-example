package com.library.app.book;

import static com.library.app.author.AuthorForTestsRepository.*;
import com.library.app.author.model.Author;
import com.library.app.book.model.Book;
import com.library.app.category.CategoryForTestsRepository;
import static com.library.app.category.CategoryForTestsRepository.*;
import com.library.app.category.model.Category;
import java.util.Arrays;
import java.util.List;
import javax.persistence.EntityManager;

public class BookForTestsRepository {

    public static Book cleanCode() {
		Book book = new Book();
		book.setTitle("Clean Code: A Handbook of Agile Software Craftsmanship");
		book.setDescription("Even bad code can function. But if code isn't clean, ...");
		book.setCategory(categoryWithId(CategoryForTestsRepository.cleanCode(), 1L));
		book.addAuthor(robertMartin());
		book.setPrice(35.06);

		return book;
	}

	public static Book designPatterns() {
		Book book = new Book();
		book.setTitle("Design Patterns: Elements of Reusable Object-Oriented Software");
		book.setDescription("Design Patterns is a modern classic in the literature of object-oriented development");
		book.setCategory(categoryWithId(architecture(), 1L));
		book.addAuthor(erichGamma());
		book.addAuthor(johnVlissides());
		book.addAuthor(ralphJohnson());
		book.addAuthor(richardHelm());
		book.setPrice(48.94D);

		return book;
	}

	public static Book peaa() {
		Book book = new Book();
		book.setTitle("Patterns of Enterprise Application Architecture");
		book.setDescription("Developers of enterprise applications (e.g reservation systems, supply...");
		book.setCategory(categoryWithId(architecture(), 1L));
		book.addAuthor(martinFowler());
		book.setPrice(52D);

		return book;
	}

	public static Book refactoring() {
		Book book = new Book();
		book.setTitle("Refactoring: Improving the Design of Existing Code");
		book.setDescription("Your class library works, but could it be better?...");
		book.setCategory(categoryWithId(CategoryForTestsRepository.cleanCode(), 1L));
		book.addAuthor(martinFowler());
		book.addAuthor(kentBeck());
		book.addAuthor(johnBrant());
		book.addAuthor(williamOpdyke());
		book.addAuthor(donRoberts());
		book.setPrice(31.16D);

		return book;
	}

	public static Book effectiveJava() {
		Book book = new Book();
		book.setTitle("Effective Java (2nd Edition)");
		book.setDescription("Are you looking for a deeper understanding of the Java programming language so ..");
		book.setCategory(categoryWithId(java(), 1L));
		book.addAuthor(joshuaBloch());
		book.setPrice(38.80D);

		return book;
	}

	public static Book bookWithId(final Book book, final Long id) {
		book.setId(id);
		return book;
	}

	public static List<Book> allBooks() {
		return Arrays.asList(cleanCode(), designPatterns(), peaa(), refactoring(), effectiveJava());
	}
    
    public static Book normalizeDependencies(Book book, EntityManager em) {
		Category managedCategory = findByPropertyNameAndValue(em, Category.class, "name", book.getCategory().getName());
		book.setCategory(managedCategory);

        book.getAuthors().forEach((author) -> {
            Author managedAuthor = findByPropertyNameAndValue(em, Author.class, "name", author.getName());
            author.setId(managedAuthor.getId());
        });

		return book;
	}

	private static <T> T findByPropertyNameAndValue(EntityManager em, Class<T> clazz, String propertyName, 
        String propertyValue) {
		
        return (T) em
				.createQuery("SELECT o FROM " + clazz.getSimpleName() +
						" o WHERE o." + propertyName + " = :propertyValue")
				.setParameter("propertyValue", propertyValue)
				.getSingleResult();
	}
    
}
