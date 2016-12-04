package com.library.app.book.resource;

import com.library.app.FieldNotValidException;
import com.library.app.author.AuthorNotFoundException;
import com.library.app.book.BookNotFoundException;
import com.library.app.book.model.Book;
import com.library.app.book.services.BookServices;
import com.library.app.category.CategoryNotFoundException;
import com.library.app.common.model.HttpCode;
import com.library.app.common.model.OperationResult;
import com.library.app.common.model.ResourceMessage;
import static com.library.app.common.model.StandardsOperationResults.getOperationResultDependencyNotFound;
import static com.library.app.common.model.StandardsOperationResults.getOperationResultInvalidField;
import static com.library.app.common.model.StandardsOperationResults.getOperationResultNotFound;
import com.library.app.json.JsonUtils;
import com.library.app.json.OperationResultJsonWriter;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/books")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({"EMPLOYEE"})
public class BookResource {
    private Logger logger = LoggerFactory.getLogger(getClass());

	private static final ResourceMessage RESOURCE_MESSAGE = new ResourceMessage("book");

	@Inject
	BookServices bookServices;

	@Inject
	BookJsonConverter bookJsonConverter;

	@Context
	UriInfo uriInfo;
    
    @POST
	public Response add(final String body) {
		logger.debug("Adding a new book with body {}", body);
		Book book = bookJsonConverter.convertFrom(body);

		HttpCode httpCode = HttpCode.CREATED;
		OperationResult result;
		try {
			book = bookServices.add(book);
			result = OperationResult.success(JsonUtils.getJsonElementWithId(book.getId()));
		} catch (final FieldNotValidException e) {
			httpCode = HttpCode.VALIDATION_ERROR;
			logger.error("One of the fields of the book is not valid", e);
			result = getOperationResultInvalidField(RESOURCE_MESSAGE, e);
		} catch (final CategoryNotFoundException e) {
			httpCode = HttpCode.VALIDATION_ERROR;
			logger.error("Category not found for book", e);
			result = getOperationResultDependencyNotFound(RESOURCE_MESSAGE, "category");
		} catch (final AuthorNotFoundException e) {
			httpCode = HttpCode.VALIDATION_ERROR;
			logger.error("Author not found for book", e);
			result = getOperationResultDependencyNotFound(RESOURCE_MESSAGE, "author");
		}

		logger.debug("Returning the operation result after adding book: {}", result);
		return Response.status(httpCode.getCode()).entity(OperationResultJsonWriter.toJson(result)).build();
	}
    
    @PUT
	@Path("/{id}")
	public Response update(@PathParam("id") final Long id, final String body) {
		logger.debug("Updating the book {} with body {}", id, body);
		final Book book = bookJsonConverter.convertFrom(body);
		book.setId(id);

		HttpCode httpCode = HttpCode.OK;
		OperationResult result;
		try {
			bookServices.update(book);
			result = OperationResult.success();
		} catch (final FieldNotValidException e) {
			httpCode = HttpCode.VALIDATION_ERROR;
			logger.error("One of the fields of the book is not valid", e);
			result = getOperationResultInvalidField(RESOURCE_MESSAGE, e);
		} catch (final CategoryNotFoundException e) {
			httpCode = HttpCode.VALIDATION_ERROR;
			logger.error("Category not found for book", e);
			result = getOperationResultDependencyNotFound(RESOURCE_MESSAGE, "category");
		} catch (final AuthorNotFoundException e) {
			httpCode = HttpCode.VALIDATION_ERROR;
			logger.error("Author not found for book", e);
			result = getOperationResultDependencyNotFound(RESOURCE_MESSAGE, "author");
		} catch (final BookNotFoundException e) {
			httpCode = HttpCode.NOT_FOUND;
			logger.error("No book found for the given id", e);
			result = getOperationResultNotFound(RESOURCE_MESSAGE);
		}

		logger.debug("Returning the operation result after updating book: {}", result);
		return Response.status(httpCode.getCode()).entity(OperationResultJsonWriter.toJson(result)).build();
	}
    
}
