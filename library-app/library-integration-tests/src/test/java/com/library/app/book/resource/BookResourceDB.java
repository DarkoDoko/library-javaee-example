package com.library.app.book.resource;

import static com.library.app.book.BookForTestsRepository.allBooks;
import static com.library.app.book.BookForTestsRepository.normalizeDependencies;
import com.library.app.book.model.Book;
import com.library.app.book.model.BookFilter;
import com.library.app.book.services.BookServices;
import com.library.app.common.model.HttpCode;
import com.library.app.json.JsonWriter;
import com.library.app.pagination.PaginatedData;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/DB/books")
@Produces(MediaType.APPLICATION_JSON)
public class BookResourceDB {
    
    @PersistenceContext
    private EntityManager em;
    
    @Inject
    private BookServices bookServices;
    
    @Inject
    private BookJsonConverter jsonConverter;
    
    @POST
    public void addAll(){
        allBooks().forEach((book) -> bookServices.add(normalizeDependencies(book, em)));
    }
    
    @GET
    @Path("/{title}")
    public Response findByTitle(@PathParam("title") String title) {
        BookFilter bookFilter = new BookFilter();
        bookFilter.setTitle(title);
        
        PaginatedData<Book> books = bookServices.findByFilter(bookFilter);
        if(books.getNumberOfRows() > 0){
            Book book = books.getRow(0);
            String bookAsJson = JsonWriter.writeToString(jsonConverter.convertToJsonElement(book));
            return Response.status(HttpCode.OK.getCode()).entity(bookAsJson).build();
        }
        
        return Response.status(HttpCode.NOT_FOUND.getCode()).build();
    }
    
}
