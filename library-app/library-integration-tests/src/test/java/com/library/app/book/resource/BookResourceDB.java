package com.library.app.book.resource;

import static com.library.app.book.BookForTestsRepository.allBooks;
import static com.library.app.book.BookForTestsRepository.normalizeDependencies;
import com.library.app.book.services.BookServices;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/DB/books")
@Produces(MediaType.APPLICATION_JSON)
public class BookResourceDB {
    
    @PersistenceContext
    private EntityManager em;
    
    @Inject
    private BookServices bookServices;
    
    @POST
    public void addAll(){
        allBooks().forEach((book) -> bookServices.add(normalizeDependencies(book, em)));
    }
    
}
