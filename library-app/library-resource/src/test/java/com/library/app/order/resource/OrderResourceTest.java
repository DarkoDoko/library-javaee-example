package com.library.app.order.resource;

import com.library.app.DateUtils;
import com.library.app.FieldNotValidException;
import com.library.app.book.BookNotFoundException;
import com.library.app.common.model.HttpCode;
import static com.library.app.commontests.utils.FileTestNameUtils.getPathFileRequest;
import static com.library.app.commontests.utils.FileTestNameUtils.getPathFileResponse;
import static com.library.app.commontests.utils.JsonTestUtils.assertJsonMatchesExpectedJson;
import static com.library.app.commontests.utils.JsonTestUtils.assertJsonMatchesFileContent;
import static com.library.app.commontests.utils.JsonTestUtils.readJsonFile;
import static com.library.app.order.OrderArgumentMatcher.orderEq;
import static com.library.app.order.OrderForTestsRepository.orderDelivered;
import static com.library.app.order.OrderForTestsRepository.orderReserved;
import static com.library.app.order.OrderForTestsRepository.orderWithId;
import com.library.app.order.OrderNotFoundException;
import com.library.app.order.OrderStatusCannotBeChangedException;
import com.library.app.order.model.Order;
import com.library.app.order.model.Order.OrderStatus;
import com.library.app.order.model.OrderFilter;
import com.library.app.order.model.OrderHistoryEntry;
import com.library.app.order.model.OrderItem;
import com.library.app.order.services.OrderServices;
import com.library.app.pagination.PaginatedData;
import com.library.app.user.UserNotFoundException;
import com.library.app.user.model.User.Roles;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Matchers.anyObject;
import org.mockito.Mock;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

public class OrderResourceTest {

    private OrderResource orderResource;
    
    @Mock
	private OrderServices orderServices;

	@Mock
	private UriInfo uriInfo;

	@Mock
	private SecurityContext securityContext;

	private static final String PATH_RESOURCE = "orders";

	@Before
	public void initTestCase() {
		MockitoAnnotations.initMocks(this);

		orderResource = new OrderResource();

		orderResource.orderServices = orderServices;
		orderResource.uriInfo = uriInfo;
		orderResource.orderJsonConverter = new OrderJsonConverter();
		orderResource.securityContext = securityContext;
	}

	@Test
	public void addValidOrder() {
		final Order expectedOrder = new Order();
		expectedOrder.setItems(orderReserved().getItems());
		when(orderServices.add(orderEq(expectedOrder))).thenReturn(orderWithId(orderReserved(), 1L));

		final Response response = orderResource.add(readJsonFile(getPathFileRequest(PATH_RESOURCE,
				"orderForCleanCodeAndRefactoring.json")));
		assertThat(response.getStatus(), is(equalTo(HttpCode.CREATED.getCode())));
		assertJsonMatchesExpectedJson(response.getEntity().toString(), "{\"id\": 1}");
	}

	@Test
	public void addOrderWithNullItems() {
		addOrderWithValidationError(new FieldNotValidException("items", "may not be null"),
				"orderForCleanCodeAndRefactoring.json", "orderErrorNullItems.json");
	}

	@Test
	public void addOrderWithInexistentCustomer() {
		addOrderWithValidationError(new UserNotFoundException(), "orderForCleanCodeAndRefactoring.json",
				"orderErrorInexistentCustomer.json");
	}

	@Test
	public void addOrderWithInexistentBook() {
		addOrderWithValidationError(new BookNotFoundException(), "orderForCleanCodeAndRefactoring.json",
				"orderErrorInexistentBook.json");
	}

	@Test
	public void addStatusReservationExpiredAsEmployee() {
		when(securityContext.isUserInRole(Roles.EMPLOYEE.name())).thenReturn(true);

		final Response response = orderResource.addStatus(1L, getStatusAsJson(OrderStatus.RESERVATION_EXPIRED));
		assertThat(response.getStatus(), is(equalTo(HttpCode.FORBIDDEN.getCode())));
	}

	@Test
	public void addStatusOrderNotFound() throws Exception {
		doThrow(new OrderNotFoundException()).when(orderServices).updateStatus(1L, OrderStatus.CANCELLED);

		final Response response = orderResource.addStatus(1L, getStatusAsJson(OrderStatus.CANCELLED));
		assertThat(response.getStatus(), is(equalTo(HttpCode.NOT_FOUND.getCode())));
	}

	@Test
	public void addStatusThatCannotBeChanged() throws Exception {
		doThrow(new OrderStatusCannotBeChangedException("The new state must be different from the current one"))
				.when(orderServices).updateStatus(1L, OrderStatus.RESERVED);

		final Response response = orderResource.addStatus(1L, getStatusAsJson(OrderStatus.RESERVED));
		assertThat(response.getStatus(), is(equalTo(HttpCode.VALIDATION_ERROR.getCode())));
	}

	@Test
	public void findByIdNotFound() throws OrderNotFoundException {
		when(orderServices.findById(1L)).thenThrow(new OrderNotFoundException());

		final Response response = orderResource.findById(1L);
		assertThat(response.getStatus(), is(equalTo(HttpCode.NOT_FOUND.getCode())));
	}

	@Test
	public void findById() throws OrderNotFoundException {
		final Order order = orderWithId(orderDelivered(), 1L);
		order.setCreatedAt(DateUtils.getAsDateTime("2015-01-04T10:10:34Z"));
		order.getCustomer().setId(1L);
		prepareOrderForJsonComparison(order);

		when(orderServices.findById(1L)).thenReturn(order);

		final Response response = orderResource.findById(1L);
		assertThat(response.getStatus(), is(equalTo(HttpCode.OK.getCode())));
		assertJsonResponseWithFile(response, "orderDeliveredFound.json");
	}

	@Test
	public void findByFilter() throws OrderNotFoundException {
		final Order order1 = orderWithId(orderDelivered(), 1L);
		order1.getCustomer().setId(1L);
		order1.setCreatedAt(DateUtils.getAsDateTime("2015-01-04T10:10:34Z"));
		prepareOrderForJsonComparison(order1);

		final Order order2 = orderWithId(orderReserved(), 2L);
		order2.getCustomer().setId(2L);
		order2.setCreatedAt(DateUtils.getAsDateTime("2015-01-05T10:10:34Z"));
		prepareOrderForJsonComparison(order1);

		final MultivaluedMap<String, String> multiMap = mock(MultivaluedMap.class);
		when(uriInfo.getQueryParameters()).thenReturn(multiMap);

		when(orderServices.findByFilter((OrderFilter) anyObject())).thenReturn(
				new PaginatedData<>(2, Arrays.asList(order1, order2)));

		final Response response = orderResource.findByFilter();
		assertThat(response.getStatus(), is(equalTo(HttpCode.OK.getCode())));
		assertJsonResponseWithFile(response, "ordersAllInOnePage.json");
	}

	private void addOrderWithValidationError(final Exception exceptionToBeThrown, final String requestFileName,
			final String responseFileName) {
		when(orderServices.add((Order) anyObject())).thenThrow(exceptionToBeThrown);

		final Response response = orderResource.add(readJsonFile(getPathFileRequest(PATH_RESOURCE, requestFileName)));
		assertThat(response.getStatus(), is(equalTo(HttpCode.VALIDATION_ERROR.getCode())));
		assertJsonResponseWithFile(response, responseFileName);
	}

	private void assertJsonResponseWithFile(final Response response, final String fileName) {
		assertJsonMatchesFileContent(response.getEntity().toString(), getPathFileResponse(PATH_RESOURCE, fileName));
	}

	private void prepareOrderForJsonComparison(final Order order) {
		final Comparator<OrderItem> sortOrderItemByBookTitle = (final OrderItem o1, final OrderItem o2) -> (o1
				.getBook().getTitle().compareTo(o2.getBook().getTitle()));

		final SortedSet<OrderItem> items = new TreeSet<>(sortOrderItemByBookTitle);
		items.addAll(order.getItems());
		order.setItems(items);

		final Comparator<OrderHistoryEntry> sortOrderHistoryEntry = (final OrderHistoryEntry o1,
				final OrderHistoryEntry o2) -> (o1.getCreatedAt().compareTo(o2.getCreatedAt()));

		final SortedSet<OrderHistoryEntry> historyEntries = new TreeSet<>(sortOrderHistoryEntry);
		historyEntries.addAll(order.getHistoryEntries());
		order.setHistoryEntries(historyEntries);

		final Iterator<OrderHistoryEntry> itHistoryEntries = order.getHistoryEntries().iterator();
		itHistoryEntries.next().setCreatedAt(DateUtils.getAsDateTime("2015-01-04T10:10:34Z"));
		if (itHistoryEntries.hasNext()) {
			itHistoryEntries.next().setCreatedAt(DateUtils.getAsDateTime("2015-01-05T10:10:34Z"));
		}
	}

    private String getStatusAsJson(OrderStatus orderStatus) {
        return String.format("{\"status\":\"%s\"}", orderStatus);
    }
}
