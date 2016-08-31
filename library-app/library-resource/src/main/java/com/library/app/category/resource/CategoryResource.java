package com.library.app.category.resource;

import com.library.app.category.model.Category;
import com.library.app.category.services.CategoryServices;
import com.library.app.common.json.JsonUtils;
import com.library.app.common.json.OperationResultJsonWriter;
import com.library.app.common.model.HttpCode;
import com.library.app.common.model.OperationResult;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author ddoko
 */
public class CategoryResource {
    
    private Logger logger = LoggerFactory.getLogger(getClass());
    
    CategoryServices services;
    CategoryJsonConverter jsonConverter;

    public Response add(String body) {
        logger.debug("Adding a new category with body {}", body);
        
        Category category = jsonConverter.convertFrom(body);
        
        category = services.add(category);
        OperationResult result = OperationResult.success(JsonUtils.getjsonElementWithId(category.getId()));
        
        logger.debug("Returning the opertaion result after adding category: {}", result);
        return Response.status(HttpCode.CREATED.getCode()).entity(OperationResultJsonWriter.toJson(result)).build();
    }
}
