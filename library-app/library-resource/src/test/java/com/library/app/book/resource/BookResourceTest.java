package com.library.app.book.resource;

import com.library.app.author.resource.AuthorJsonConverter;
import com.library.app.book.services.BookServices;
import com.library.app.category.resource.CategoryJsonConverter;
import javax.ws.rs.core.UriInfo;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class BookResourceTest {
    
    private BookResource bookResourceUnderTest;
    
    @Mock
    private BookServices bookServices;
    
    @Mock
    private UriInfo uriInfo;
    
    private static final String PATH_RESOURCE = "books";
    
    @Before
    public void initTestCase(){
        MockitoAnnotations.initMocks(this);

		bookResourceUnderTest = new BookResource();

		final BookJsonConverter bookJsonConverter = new BookJsonConverter();
		bookJsonConverter.categoryJsonConverter = new CategoryJsonConverter();
		bookJsonConverter.authorJsonConverter = new AuthorJsonConverter();

		bookResourceUnderTest.bookServices = bookServices;
		bookResourceUnderTest.uriInfo = uriInfo;
		bookResourceUnderTest.bookJsonConverter = bookJsonConverter;
        
    }
    
}
