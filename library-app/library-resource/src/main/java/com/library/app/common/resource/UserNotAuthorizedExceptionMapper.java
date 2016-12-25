package com.library.app.common.resource;

import com.library.app.UserNotAuthorizedException;
import com.library.app.common.model.HttpCode;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class UserNotAuthorizedExceptionMapper implements ExceptionMapper<UserNotAuthorizedException>{

    @Override
    public Response toResponse(UserNotAuthorizedException exception) {
        return Response.status(HttpCode.FORBIDDEN.getCode()).build();
    }
    
}
