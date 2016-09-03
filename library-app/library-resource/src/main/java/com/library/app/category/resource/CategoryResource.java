package com.library.app.category.resource;

import com.library.app.category.exception.CategoryExistentException;
import com.library.app.category.model.Category;
import com.library.app.category.services.CategoryServices;
import com.library.app.common.json.JsonUtils;
import com.library.app.common.json.OperationResultJsonWriter;
import com.library.app.common.model.HttpCode;
import com.library.app.common.model.OperationResult;
import com.library.app.common.model.ResourceMessage;
import static com.library.app.common.model.StandardsOperationResults.getOperationResultExistent;
import javax.ws.rs.core.Response;
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
        } catch(CategoryExistentException e){
            logger.error("There's already a category for the given name.");
            httpCode = HttpCode.VALIDATION_ERROR;
            result = getOperationResultExistent(RESOURCE_MESSAGE, "name");
        }
                
        logger.debug("Returning the opertaion result after adding category: {}", result);
        return Response.status(httpCode.getCode()).entity(OperationResultJsonWriter.toJson(result)).build();
    }
}
