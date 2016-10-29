package com.library.app.user.resource;

import com.library.app.common.model.HttpCode;
import com.library.app.common.model.OperationResult;
import com.library.app.common.model.ResourceMessage;
import com.library.app.json.JsonUtils;
import com.library.app.json.OperationResultJsonWriter;
import com.library.app.user.model.User;
import com.library.app.user.model.User.UserType;
import com.library.app.user.services.UserServices;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    private Logger logger = LoggerFactory.getLogger(getClass());
    
    private static final ResourceMessage RESOURCE_MESSAGE = new ResourceMessage("user");

    @Inject
    UserJsonConverter jsonConverter;
    
    @Inject
    UserServices services;
    
    @Context
    SecurityContext securityContext;
    
    @Context
    UriInfo uriInfo;
    
    @POST
    public Response add(String body) {
        logger.debug("Adding a new user with body {}", body);
        
        User user = jsonConverter.convertFrom(body);
        
        if(user.getUserType().equals(UserType.EMPLOYEE)) {
            return Response.status(HttpCode.FORBIDDEN.getCode()).build();
        }
        
        user = services.add(user);
        OperationResult result = OperationResult.success(JsonUtils.getJsonElementWithId(user.getId()));
        
        logger.debug("Returning the operation result after adding user: {}", result);
        return Response.
                    status(HttpCode.CREATED.getCode()).
                    entity(OperationResultJsonWriter.toJson(result)).
                    build();
    }
    
}
