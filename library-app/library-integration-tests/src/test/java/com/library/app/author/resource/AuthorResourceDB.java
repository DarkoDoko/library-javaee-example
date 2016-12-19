package com.library.app.author.resource;

import com.library.app.author.AuthorForTestsRepository;
import com.library.app.author.model.Author;
import com.library.app.author.model.AuthorFilter;
import com.library.app.author.services.AuthorServices;
import com.library.app.common.model.HttpCode;
import com.library.app.json.JsonWriter;
import com.library.app.pagination.PaginatedData;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/DB/authors")
@Produces(MediaType.APPLICATION_JSON)
public class AuthorResourceDB {
    
    @Inject
    private AuthorServices services;
    
    @Inject
    private  AuthorJsonConverter jsonConverter;
    
    @POST
    public void addAll() {
        AuthorForTestsRepository.allAuthors().forEach(services::add);
    }
    
    @GET
    @Path("/{name}")
    public Response findByName(@PathParam("name") String name){
        AuthorFilter filter = new AuthorFilter();
        filter.setName(name);
        
        PaginatedData<Author> authors = services.findByFilter(filter);
        if(authors.getNumberOfRows() > 0) {
            Author author = authors.getRow(0);
            String authorAsJson = JsonWriter.writeToString(jsonConverter.convertToJsonElement(author));
            return Response.status(HttpCode.OK.getCode()).entity(authorAsJson).build();
        }
        
        return Response.status(HttpCode.NOT_FOUND.getCode()).build();
    }
}
