package com.library.app.user.resource;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.library.app.FieldNotValidException;
import com.library.app.common.model.HttpCode;
import com.library.app.common.model.OperationResult;
import com.library.app.common.model.ResourceMessage;
import com.library.app.common.model.StandardsOperationResults;
import static com.library.app.common.model.StandardsOperationResults.getOperationResultExistent;
import static com.library.app.common.model.StandardsOperationResults.getOperationResultInvalidField;
import static com.library.app.common.model.StandardsOperationResults.getOperationResultNotFound;
import com.library.app.json.JsonReader;
import com.library.app.json.JsonUtils;
import com.library.app.json.JsonWriter;
import com.library.app.json.OperationResultJsonWriter;
import com.library.app.pagination.PaginatedData;
import com.library.app.user.UserExistentException;
import com.library.app.user.UserNotFoundException;
import com.library.app.user.model.Customer;
import com.library.app.user.model.User;
import com.library.app.user.model.User.Roles;
import com.library.app.user.model.User.UserType;
import com.library.app.user.model.filter.UserFilter;
import com.library.app.user.services.UserServices;
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

        if (user.getUserType().equals(UserType.EMPLOYEE)) {
            return Response.status(HttpCode.FORBIDDEN.getCode()).build();
        }

        HttpCode httpCode = HttpCode.CREATED;
        OperationResult result;
        try {
            user = services.add(user);
            result = OperationResult.success(JsonUtils.getJsonElementWithId(user.getId()));
        } catch (UserExistentException e) {

            httpCode = HttpCode.VALIDATION_ERROR;
            logger.error("There is already an user for the given email", e);
            result = getOperationResultExistent(RESOURCE_MESSAGE, "email");
        } catch (FieldNotValidException e) {

            httpCode = HttpCode.VALIDATION_ERROR;
            logger.error("One of the fields of the user is not valid", e);
            result = getOperationResultInvalidField(RESOURCE_MESSAGE, e);
        }
        logger.debug("Returning the operation result after adding user: {}", result);
        return Response.
            status(httpCode.getCode()).
            entity(OperationResultJsonWriter.toJson(result)).
            build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, String body) {
        logger.debug("Updating the user {} with body {}", id, body);

        if (!securityContext.isUserInRole(Roles.ADMINISTRATOR.name())) {
            if (!isLoggedUser(id)) {
                return Response.status(HttpCode.FORBIDDEN.getCode()).build();
            }
        }

        User user = jsonConverter.convertFrom(body);
        user.setId(id);

        HttpCode httpCode = HttpCode.OK;
        OperationResult result;
        try {
            services.update(user);
            result = OperationResult.success();

        } catch (final FieldNotValidException e) {

            httpCode = HttpCode.VALIDATION_ERROR;
            logger.error("One of the fields of the user is not valid", e);
            result = getOperationResultInvalidField(RESOURCE_MESSAGE, e);

        } catch (final UserExistentException e) {

            httpCode = HttpCode.VALIDATION_ERROR;
            logger.error("There is already an user for the given email", e);
            result = getOperationResultExistent(RESOURCE_MESSAGE, "email");

        } catch (final UserNotFoundException e) {

            httpCode = HttpCode.NOT_FOUND;
            logger.error("No user found for the given id", e);
            result = getOperationResultNotFound(RESOURCE_MESSAGE);

        }

        logger.debug("Returning the operation result after updating user: {}", result);
        return Response.status(httpCode.getCode()).entity(OperationResultJsonWriter.toJson(result)).build();
    }

    @PUT
    @Path("/{id}/password")
    public Response updatePassword(@PathParam("id") Long id, String body) {
        logger.debug("Updating the password for user {}", id);

        if (!securityContext.isUserInRole(Roles.ADMINISTRATOR.name()) && !isLoggedUser(id)) {
            return Response.status(HttpCode.FORBIDDEN.getCode()).build();
        }

        HttpCode httpCode = HttpCode.OK;
        OperationResult result;
        try {
            services.updatePassword(id, getPasswordFromJson(body));
            result = OperationResult.success();
        } catch (final UserNotFoundException e) {
            httpCode = HttpCode.NOT_FOUND;
            logger.error("No user found for the given id", e);
            result = getOperationResultNotFound(RESOURCE_MESSAGE);
        }

        logger.debug("Returning the operation result after updating user password: {}", result);
        return Response.status(httpCode.getCode()).entity(OperationResultJsonWriter.toJson(result)).build();
    }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") Long id) {
        logger.debug("Find user by id: {}", id);
        ResponseBuilder responseBuilder;
        try {
            User user = services.findById(id);
            OperationResult result = OperationResult.success(jsonConverter.convertToJsonElement(user));
            responseBuilder = Response.
                                status(HttpCode.OK.getCode()).
                                entity(OperationResultJsonWriter.toJson(result));
            logger.debug("User found by id: {}", user);
        } catch (final UserNotFoundException e) {
            logger.error("No user found for id", id);
            responseBuilder = Response.status(HttpCode.NOT_FOUND.getCode());
        }

        return responseBuilder.build();
    }

    @POST
    @Path("/authenticate")
    public Response findByEmailAndPassword(String body) {
        logger.debug("Find user by email and password");
        ResponseBuilder responseBuilder;
        try {
            User userWithEmailAndPassword = getUserWithEmailAndPasswordFromJson(body);
            User user = services.findByEmailAndPassword(userWithEmailAndPassword.getEmail(),
                userWithEmailAndPassword.getPassword());

            OperationResult result = OperationResult.success(jsonConverter.convertToJsonElement(user));
            responseBuilder = Response.
                                status(HttpCode.OK.getCode()).
                                entity(OperationResultJsonWriter.toJson(result));
            logger.debug("User found by email/password: {}", user);
        } catch (UserNotFoundException e) {
            logger.error("No user found for email/password");
            responseBuilder = Response.status(HttpCode.NOT_FOUND.getCode());
        }

        return responseBuilder.build();
    }

    @GET
    public Response findByFilter() {
        UserFilter userFilter = new UserFilterExtractorFromUrl(uriInfo).getFilter();
        logger.debug("Finding users using filter: {}", userFilter);

        final PaginatedData<User> users = services.findByFilter(userFilter);

        logger.debug("Found {} users", users.getNumberOfRows());

        JsonElement jsonWithPagingAndEntries = JsonUtils.getJsonElementWithPagingAndEntries(users, jsonConverter);
        return Response.
            status(HttpCode.OK.getCode()).
            entity(JsonWriter.writeToString(jsonWithPagingAndEntries)).
            build();
    }

    private User getUserWithEmailAndPasswordFromJson(final String body) {
        User user = new Customer(); // The implementation does not matter

        JsonObject jsonObject = JsonReader.readAsJsonObject(body);
        user.setEmail(JsonReader.getStringOrNull(jsonObject, "email"));
        user.setPassword(JsonReader.getStringOrNull(jsonObject, "password"));

        return user;
    }

    private boolean isLoggedUser(final Long id) {
        try {
            User loggerUser = services.findByEmail(securityContext.getUserPrincipal().getName());
            if (loggerUser.getId().equals(id)) {
                return true;
            }
        } catch (final UserNotFoundException e) {
        }
        return false;
    }

    private String getPasswordFromJson(String body) {
        JsonObject jsonObject = JsonReader.readAsJsonObject(body);
        return JsonReader.getStringOrNull(jsonObject, "password");
    }

}
