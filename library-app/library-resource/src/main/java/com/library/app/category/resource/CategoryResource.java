package com.library.app.category.resource;

import com.library.app.category.exception.CategoryExistentException;
import com.library.app.category.exception.CategoryNotFoundException;
import com.library.app.category.model.Category;
import com.library.app.category.services.CategoryServices;
import com.library.app.common.exception.FieldNotValidException;
import com.library.app.common.json.JsonUtils;
import com.library.app.common.json.OperationResultJsonWriter;
import com.library.app.common.model.HttpCode;
import com.library.app.common.model.OperationResult;
import com.library.app.common.model.ResourceMessage;
import static com.library.app.common.model.StandardsOperationResults.getOperationResultExistent;
import static com.library.app.common.model.StandardsOperationResults.getOperationResultInvalidField;
import static com.library.app.common.model.StandardsOperationResults.getOperationResultNotFound;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author ddoko
 */
public class CategoryResource {
    
    private Logger logger = LoggerFactory.getLogger(getClass());
    
    private static final ResourceMessage RESOURCE_MESSAGE = new ResourceMessage("category");
    
    CategoryServices services;
    CategoryJsonConverter jsonConverter;

    public Response add(String body) {
        logger.debug("Adding a new category with body {}", body);
        
        Category category = jsonConverter.convertFrom(body);
        
        HttpCode httpCode = HttpCode.CREATED;
        OperationResult result = null;
        
        try{
            category = services.add(category);
            result = OperationResult.success(JsonUtils.getjsonElementWithId(category.getId()));
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
        return Response.status(httpCode.getCode()).entity(OperationResultJsonWriter.toJson(result)).build();
    }

    Response update(Long id, String body) {
        logger.debug("Updating the category {} with body {}", id, body);
        Category category = jsonConverter.convertFrom(body);
        category.setId(id);
        
        HttpCode httpCode = HttpCode.OK;
        OperationResult result = null;
        
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
        
        return Response.status(httpCode.getCode()).entity(OperationResultJsonWriter.toJson(result)).build();
    }

    Response findById(Long id) {
        logger.debug("Find category: {}", id);
        
        ResponseBuilder responseBuilder;
        
        Category category = services.findById(id);
        OperationResult result = OperationResult.success(jsonConverter.convertToJsonElement(category));
        
        responseBuilder = Response.status(HttpCode.OK.getCode()).entity(OperationResultJsonWriter.toJson(result));
        
        logger.debug("Category found: {}", category);
        
        return responseBuilder.build();
    }
}
