package com.library.app.commontests.author;

import com.library.app.author.AuthorForTestsRepository;
import com.library.app.author.services.AuthorServices;
import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/DB/authors")
@Produces(MediaType.APPLICATION_JSON)
public class AuthorResourceDB {
    
    @Inject
    private AuthorServices services;
    
    @POST
    public void addAll() {
        AuthorForTestsRepository.allAuthors().forEach(services::add);
    }
    
}
