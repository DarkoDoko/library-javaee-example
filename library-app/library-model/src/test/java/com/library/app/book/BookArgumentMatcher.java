package com.library.app.book;

import com.library.app.book.model.Book;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.mockito.ArgumentMatcher;
import org.mockito.Matchers;

public class BookArgumentMatcher extends ArgumentMatcher<Book>{
    
    private Book expected;
    
    public static Book bookEq(Book expected){
        return Matchers.argThat(new BookArgumentMatcher(expected));
    }

    public BookArgumentMatcher(Book expected) {
        this.expected = expected;
    }

    @Override
    public boolean matches(Object actualObj) {
        Book actual = (Book) actualObj;
        
        assertThat(actual.getId(), is(equalTo(expected.getId())));
		assertThat(actual.getTitle(), is(equalTo(expected.getTitle())));
		assertThat(actual.getDescription(), is(equalTo(expected.getDescription())));
		assertThat(actual.getCategory(), is(equalTo(expected.getCategory())));
		assertThat(actual.getAuthors(), is(equalTo(expected.getAuthors())));
		assertThat(actual.getPrice(), is(equalTo(expected.getPrice())));

        return true;
    }
    
}
