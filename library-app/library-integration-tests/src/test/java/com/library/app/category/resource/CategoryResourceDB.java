package com.library.app.category.resource;

import com.library.app.category.CategoryForTestsRepository;
import com.library.app.category.model.Category;
import com.library.app.category.services.CategoryServices;
import com.library.app.common.model.HttpCode;
import com.library.app.json.JsonWriter;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/DB/categories")
@Produces(MediaType.APPLICATION_JSON)
public class CategoryResourceDB {
    
    @Inject
    private CategoryServices services;
    
    @Inject
    private CategoryJsonConverter jsonConverter;
    
    @POST
    public void addAll(){
        CategoryForTestsRepository.allCategories().forEach(services::add);
    }
    
    @GET
	@Path("/{name}")
	public Response findByName(@PathParam("name") String name) {
		List<Category> categories = services.findAll();

        Optional<Category> category = categories.stream().
                                                filter(c -> c.getName().equals(name)).
                                                findFirst();
		
        if (category.isPresent()) {
			String categoryAsJson = JsonWriter.writeToString(jsonConverter.convertToJsonElement(category.get()));
			return Response.status(HttpCode.OK.getCode()).entity(categoryAsJson).build();
		} else {
			return Response.status(HttpCode.NOT_FOUND.getCode()).build();
		}
	}

}
