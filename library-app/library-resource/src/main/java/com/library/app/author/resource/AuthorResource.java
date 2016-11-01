package com.library.app.author.resource;

import com.google.gson.JsonElement;
import com.library.app.FieldNotValidException;
import com.library.app.author.AuthorNotFoundException;
import com.library.app.author.model.Author;
import com.library.app.author.model.AuthorFilter;
import com.library.app.author.services.AuthorServices;
import com.library.app.common.model.HttpCode;
import com.library.app.common.model.OperationResult;
import com.library.app.common.model.ResourceMessage;
import static com.library.app.common.model.StandardsOperationResults.getOperationResultInvalidField;
import static com.library.app.common.model.StandardsOperationResults.getOperationResultNotFound;
import com.library.app.json.JsonUtils;
import com.library.app.json.JsonWriter;
import com.library.app.json.OperationResultJsonWriter;
import com.library.app.pagination.PaginatedData;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/authors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({ "EMPLOYEE" })
public class AuthorResource {
    
    private Logger logger = LoggerFactory.getLogger(getClass());

    private static final ResourceMessage RESOURCE_MESSAGE = new ResourceMessage("author");

    @Inject
    AuthorServices services;
    
    @Inject
    AuthorJsonConverter jsonConverter;
    
    @Context
    UriInfo uriInfo;

    @POST
    public Response add(String body) {
        logger.debug("Adding a new author with body {}", body);
        Author author = jsonConverter.convertFrom(body);

        HttpCode httpCode = HttpCode.CREATED;
        OperationResult result;
        try {
            author = services.add(author);
            result = OperationResult.success(JsonUtils.getJsonElementWithId(author.getId()));
        } catch (final FieldNotValidException e) {
            httpCode = HttpCode.VALIDATION_ERROR;
            logger.error("One of the fields of the author is not valid", e);
            result = getOperationResultInvalidField(RESOURCE_MESSAGE, e);
        }

        logger.debug("Returning the operation result after adding author: {}", result);
        return Response
                    .status(httpCode.getCode())
                    .entity(OperationResultJsonWriter.toJson(result))
                    .build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, String body) {
        logger.debug("Updating the author {} with body {}", id, body);
        final Author author = jsonConverter.convertFrom(body);
        author.setId(id);

        HttpCode httpCode = HttpCode.OK;
        OperationResult result;
        try {
            services.update(author);
            result = OperationResult.success();
        } catch (final FieldNotValidException e) {
            httpCode = HttpCode.VALIDATION_ERROR;
            logger.error("One of the fields of the author is not valid", e);
            result = getOperationResultInvalidField(RESOURCE_MESSAGE, e);
        } catch (final AuthorNotFoundException e) {
            httpCode = HttpCode.NOT_FOUND;
            logger.error("No author found for the given id", e);
            result = getOperationResultNotFound(RESOURCE_MESSAGE);
        }

        logger.debug("Returning the operation result after updating author: {}", result);
        return Response
                    .status(httpCode.getCode())
                    .entity(OperationResultJsonWriter.toJson(result))
                    .build();
    }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") Long id) {
        logger.debug("Find author: {}", id);
        ResponseBuilder responseBuilder;
        try {
            final Author author = services.findById(id);
            final OperationResult result = OperationResult.success(jsonConverter.convertToJsonElement(author));
            responseBuilder = Response
                                .status(HttpCode.OK.getCode())
                                .entity(OperationResultJsonWriter.toJson(result));
            logger.debug("Author found: {}", author);
        } catch (final AuthorNotFoundException e) {
            logger.error("No author found for id", id);
            responseBuilder = Response.status(HttpCode.NOT_FOUND.getCode());
        }

        return responseBuilder.build();    
    }

    @GET
    @PermitAll
    public Response findByFilter() {
        AuthorFilter filter = new AuthorFilterExtractorFromUrl(uriInfo).getFilter();
        PaginatedData<Author> authors = services.findByFilter(filter);

        JsonElement jsonWithPagingAndEntries = JsonUtils.getJsonElementWithPagingAndEntries(authors, jsonConverter);
    
        return Response
                    .status(HttpCode.OK.getCode())
                    .entity(JsonWriter.writeToString(jsonWithPagingAndEntries))
                    .build();
    }
    
}
