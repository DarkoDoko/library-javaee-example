package com.library.app.category.resource;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.library.app.category.CategoryExistentException;
import com.library.app.category.CategoryNotFoundException;
import com.library.app.category.model.Category;
import com.library.app.category.services.CategoryServices;
import com.library.app.FieldNotValidException;
import com.library.app.json.JsonUtils;
import com.library.app.json.JsonWriter;
import com.library.app.json.OperationResultJsonWriter;
import com.library.app.common.model.HttpCode;
import com.library.app.common.model.OperationResult;
import com.library.app.common.model.ResourceMessage;
import static com.library.app.common.model.StandardsOperationResults.getOperationResultExistent;
import static com.library.app.common.model.StandardsOperationResults.getOperationResultInvalidField;
import static com.library.app.common.model.StandardsOperationResults.getOperationResultNotFound;
import com.library.app.pagination.PaginatedData;
import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/categories")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CategoryResource {
    
    private Logger logger = LoggerFactory.getLogger(getClass());
    
    private static final ResourceMessage RESOURCE_MESSAGE = new ResourceMessage("category");
    
    @Inject
    CategoryServices services;
    
    @Inject
    CategoryJsonConverter jsonConverter;

    @POST
    public Response add(String body) {
        logger.debug("Adding a new category with body {}", body);
        
        Category category = jsonConverter.convertFrom(body);
        
        HttpCode httpCode = HttpCode.CREATED;
        OperationResult result;
        
        try{
            category = services.add(category);
            result = OperationResult.success(JsonUtils.getJsonElementWithId(category.getId()));
        } catch(FieldNotValidException e){
            logger.error("Field not valid");
            httpCode = HttpCode.VALIDATION_ERROR;
            result = getOperationResultInvalidField(RESOURCE_MESSAGE, e);
        }catch(CategoryExistentException e){
            logger.error("There's already a category for the given name.");
            httpCode = HttpCode.VALIDATION_ERROR;
            result = getOperationResultExistent(RESOURCE_MESSAGE, "name");
        }
                
        logger.debug("Returning the opertaion result after adding category: {}", result);
        return Response
                .status(httpCode.getCode())
                .entity(OperationResultJsonWriter.toJson(result))
                .build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, String body) {
        logger.debug("Updating the category {} with body {}", id, body);
        Category category = jsonConverter.convertFrom(body);
        category.setId(id);
        
        HttpCode httpCode = HttpCode.OK;
        OperationResult result;
        
        try{
            services.update(category);
            result = OperationResult.success();
        } catch(CategoryExistentException e){
            logger.error("There is already a category for the given name", e);
            httpCode = HttpCode.VALIDATION_ERROR;
            result = getOperationResultExistent(RESOURCE_MESSAGE, "name");
        } catch(FieldNotValidException e){
            logger.error("One of the fields of the category is not valid", e);
            httpCode = HttpCode.VALIDATION_ERROR;
            result = getOperationResultInvalidField(RESOURCE_MESSAGE, e);
        } catch(CategoryNotFoundException e){
            logger.error("No category found for given id", e);
            httpCode = HttpCode.NOT_FOUND;
            result = getOperationResultNotFound(RESOURCE_MESSAGE);
        }
        
        logger.debug("Returning the operation result after updating category: {}", result);
        
        return Response
                .status(httpCode.getCode())
                .entity(OperationResultJsonWriter.toJson(result))
                .build();
    }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") Long id) {
        logger.debug("Find category: {}", id);
        
        ResponseBuilder responseBuilder;
        
        try{
            Category category = services.findById(id);
            OperationResult result = OperationResult.success(jsonConverter.convertToJsonElement(category));
            responseBuilder = Response
                                .status(HttpCode.OK.getCode())
                                .entity(OperationResultJsonWriter.toJson(result));
            logger.debug("Category found: {}", category);
        }  catch(CategoryNotFoundException e){
            logger.error("No category found for id", id);
            responseBuilder = Response.status(HttpCode.NOT_FOUND.getCode());
        }
        return responseBuilder.build();
    }

    @GET
    public Response findAll() {
        logger.debug("Find all categories");
        
        List<Category> categories = services.findAll();
        
        logger.debug("Found {} categories", categories.size());
        
        JsonElement jsonWithPagingAndEntries = JsonUtils.getJsonElementWithPagingAndEntries(
                new PaginatedData<>(categories.size(), categories),
                jsonConverter);
        
        return Response
                .status(HttpCode.OK.getCode())
                .entity(JsonWriter.writeToString(jsonWithPagingAndEntries))
                .build();
    }
}
