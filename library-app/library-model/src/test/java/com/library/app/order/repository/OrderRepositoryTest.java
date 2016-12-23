package com.library.app.order.repository;

import com.library.app.DateUtils;
import static com.library.app.author.AuthorForTestsRepository.allAuthors;
import com.library.app.book.BookForTestsRepository;
import static com.library.app.book.BookForTestsRepository.allBooks;
import com.library.app.book.model.Book;
import static com.library.app.category.CategoryForTestsRepository.allCategories;
import com.library.app.commontests.utils.TestBaseRepository;
import static com.library.app.order.OrderForTestsRepository.normalizeDependencies;
import static com.library.app.order.OrderForTestsRepository.orderCreatedAt;
import static com.library.app.order.OrderForTestsRepository.orderDelivered;
import static com.library.app.order.OrderForTestsRepository.orderReserved;
import com.library.app.order.model.Order;
import com.library.app.order.model.Order.OrderStatus;
import com.library.app.order.model.OrderFilter;
import com.library.app.order.model.OrderItem;
import com.library.app.pagination.PaginatedData;
import com.library.app.pagination.filter.PaginationData;
import com.library.app.pagination.filter.PaginationData.OrderMode;
import static com.library.app.user.UserForTestsRepository.allUsers;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import org.junit.After;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;

public class OrderRepositoryTest extends TestBaseRepository{
    private OrderRepository orderRepository;

	@Before
	public void initTestCase() {
		initializeTestDB();

		orderRepository = new OrderRepository();
		orderRepository.em = em;

		loadBooks();
	}

	@After
	public void setDownTestCase() {
		closeEntityManager();
	}

	@Test
	public void addAnOrderAndFindIt() {
		final Long orderAddedId = dbExecutor.executeCommand(() -> {
			return orderRepository.add(normalizeDependencies(orderDelivered(), em)).getId();
		});
		assertThat(orderAddedId, is(notNullValue()));

		final Order orderAdded = orderRepository.findById(orderAddedId);
		assertActualOrderWithExpectedOrder(orderAdded, normalizeDependencies(orderDelivered(), em));
	}

	@Test
	public void findOrderByIdNotFound() {
		final Order order = orderRepository.findById(999L);
		assertThat(order, is(nullValue()));
	}

	@Test
	public void updateOrder() {
		final Long orderAddedId = dbExecutor.executeCommand(() -> {
			return orderRepository.add(normalizeDependencies(orderReserved(), em)).getId();
		});
		assertThat(orderAddedId, is(notNullValue()));

		final Order orderAdded = orderRepository.findById(orderAddedId);
		assertThat(orderAdded.getHistoryEntries().size(), is(equalTo(1)));
		assertThat(orderAdded.getCurrentStatus(), is(equalTo(OrderStatus.RESERVED)));

		orderAdded.addHistoryEntry(OrderStatus.DELIVERED);
		dbExecutor.executeCommand(() -> {
			orderRepository.update(orderAdded);
			return null;
		});

		final Order orderAfterUpdate = orderRepository.findById(orderAddedId);
		assertThat(orderAfterUpdate.getHistoryEntries().size(), is(equalTo(2)));
		assertThat(orderAfterUpdate.getCurrentStatus(), is(equalTo(OrderStatus.DELIVERED)));
	}

	@Test
	public void existsById() {
		final Long orderAddedId = dbExecutor.executeCommand(() -> {
			return orderRepository.add(normalizeDependencies(orderReserved(), em)).getId();
		});
		assertThat(orderAddedId, is(notNullValue()));

		assertThat(orderRepository.existsById(orderAddedId), is(equalTo(true)));
		assertThat(orderRepository.existsById(999l), is(equalTo(false)));
	}

	@Test
	public void findByFilterNoFilter() {
		loadForFindByFilter();

		final PaginatedData<Order> orders = orderRepository.findByFilter(new OrderFilter());
		assertThat(orders.getNumberOfRows(), is(equalTo(3)));
		assertThat(DateUtils.formatDateTime(orders.getRow(0).getCreatedAt()), is(equalTo("2015-01-08T10:10:21Z")));
		assertThat(DateUtils.formatDateTime(orders.getRow(1).getCreatedAt()), is(equalTo("2015-01-07T10:10:21Z")));
		assertThat(DateUtils.formatDateTime(orders.getRow(2).getCreatedAt()), is(equalTo("2015-01-06T10:10:21Z")));
	}

	@Test
	public void findByFilterFilteringByStatus() {
		loadForFindByFilter();

		final OrderFilter filter = new OrderFilter();
		filter.setStatus(OrderStatus.RESERVED);

		final PaginatedData<Order> orders = orderRepository.findByFilter(filter);
		assertThat(orders.getNumberOfRows(), is(equalTo(1)));
		assertThat(DateUtils.formatDateTime(orders.getRow(0).getCreatedAt()), is(equalTo("2015-01-06T10:10:21Z")));
	}

	@Test
	public void findByFilterFilteringByCustomerOrderingByCreationAscending() {
		loadForFindByFilter();

		final OrderFilter filter = new OrderFilter();
		filter.setCustomerId(normalizeDependencies(orderDelivered(), em).getCustomer().getId());
		filter.setPaginationData(new PaginationData(0, 10, "createdAt", OrderMode.ASCENDING));

		final PaginatedData<Order> orders = orderRepository.findByFilter(filter);
		assertThat(orders.getNumberOfRows(), is(equalTo(2)));
		assertThat(DateUtils.formatDateTime(orders.getRow(0).getCreatedAt()), is(equalTo("2015-01-07T10:10:21Z")));
		assertThat(DateUtils.formatDateTime(orders.getRow(1).getCreatedAt()), is(equalTo("2015-01-08T10:10:21Z")));
	}

	@Test
	public void findByFilterFilteringByDate() {
		loadForFindByFilter();

		final OrderFilter filter = new OrderFilter();
		filter.setStartDate(DateUtils.getAsDateTime("2015-01-07T10:10:21Z"));
		filter.setEndDate(DateUtils.getAsDateTime("2015-01-08T10:10:21Z"));

		final PaginatedData<Order> orders = orderRepository.findByFilter(filter);
		assertThat(orders.getNumberOfRows(), is(equalTo(2)));
		assertThat(DateUtils.formatDateTime(orders.getRow(0).getCreatedAt()), is(equalTo("2015-01-08T10:10:21Z")));
		assertThat(DateUtils.formatDateTime(orders.getRow(1).getCreatedAt()), is(equalTo("2015-01-07T10:10:21Z")));
	}

	private void loadForFindByFilter() {
		final Order order1 = normalizeDependencies(orderReserved(), em);
		orderCreatedAt(order1, "2015-01-06T10:10:21Z");

		final Order order2 = normalizeDependencies(orderDelivered(), em);
		orderCreatedAt(order2, "2015-01-07T10:10:21Z");

		final Order order3 = normalizeDependencies(orderDelivered(), em);
		orderCreatedAt(order3, "2015-01-08T10:10:21Z");

		dbExecutor.executeCommand(() -> {
			orderRepository.add(order1);
			orderRepository.add(order2);
			orderRepository.add(order3);
			return null;
		});
	}

	private void assertActualOrderWithExpectedOrder(final Order actualOrder, final Order expectedOrder) {
		assertThat(expectedOrder.getCreatedAt(), is(notNullValue()));
		assertThat(actualOrder.getCustomer(), is(equalTo(expectedOrder.getCustomer())));
		assertThat(actualOrder.getItems().size(), is(equalTo(expectedOrder.getItems().size())));
		for (final OrderItem actualItem : actualOrder.getItems()) {
			final OrderItem expectedItem = findItemByBook(expectedOrder, actualItem.getBook());
			assertThat(actualItem.getBook().getTitle(), is(equalTo(expectedItem.getBook().getTitle())));
			assertThat(actualItem.getPrice(), is(equalTo(expectedItem.getPrice())));
			assertThat(actualItem.getQuantity(), is(equalTo(expectedItem.getQuantity())));
		}
		assertThat(actualOrder.getTotal(), is(equalTo(expectedOrder.getTotal())));
		assertThat(actualOrder.getCurrentStatus(), is(equalTo(expectedOrder.getCurrentStatus())));
		assertThat(actualOrder.getHistoryEntries().size(), is(equalTo(expectedOrder.getHistoryEntries().size())));
		for (int i = 0; i < actualOrder.getHistoryEntries().size(); i++) {
			assertThat(actualOrder.getHistoryEntries(), is(equalTo(expectedOrder.getHistoryEntries())));
		}
	}

	private OrderItem findItemByBook(final Order order, final Book book) {
		for (final OrderItem item : order.getItems()) {
			if (item.getBook().getTitle().equals(book.getTitle())) {
				return item;
			}
		}
		return null;
	}

	private void loadBooks() {
		dbExecutor.executeCommand(() -> {
			allUsers().forEach(em::persist);
			allCategories().forEach(em::persist);
			allAuthors().forEach(em::persist);
			allBooks().forEach(book -> em.persist(BookForTestsRepository.normalizeDependencies(book, em)));
			return null;
		});
	}
}
