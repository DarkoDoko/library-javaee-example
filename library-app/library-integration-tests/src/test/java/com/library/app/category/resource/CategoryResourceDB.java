package com.library.app.category.resource;

import com.library.app.category.CategoryForTestsRepository;
import com.library.app.category.services.CategoryServices;
import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/DB/categories")
@Produces(MediaType.APPLICATION_JSON)
public class CategoryResourceDB {
    
    @Inject
    private CategoryServices services;
    
    @POST
    public void addAll(){
        CategoryForTestsRepository.allCategories().forEach(services::add);
    }
}
