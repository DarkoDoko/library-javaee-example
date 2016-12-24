package com.library.app.order.services.impl;

import com.library.app.FieldNotValidException;
import com.library.app.UserNotAuthorizedException;
import static com.library.app.book.BookForTestsRepository.bookWithId;
import static com.library.app.book.BookForTestsRepository.designPatterns;
import static com.library.app.book.BookForTestsRepository.refactoring;
import com.library.app.book.BookNotFoundException;
import com.library.app.book.services.BookServices;
import static com.library.app.order.OrderArgumentMatcher.orderEq;
import com.library.app.order.OrderForTestsRepository;
import static com.library.app.order.OrderForTestsRepository.orderDelivered;
import static com.library.app.order.OrderForTestsRepository.orderReserved;
import static com.library.app.order.OrderForTestsRepository.orderWithId;
import com.library.app.order.OrderNotFoundException;
import com.library.app.order.OrderStatusCannotBeChangedException;
import com.library.app.order.model.Order;
import com.library.app.order.model.Order.OrderStatus;
import com.library.app.order.model.OrderFilter;
import com.library.app.order.repository.OrderRepository;
import com.library.app.order.services.OrderServices;
import com.library.app.pagination.PaginatedData;
import static com.library.app.user.UserForTestsRepository.johnDoe;
import com.library.app.user.UserNotFoundException;
import com.library.app.user.model.User;
import com.library.app.user.model.User.Roles;
import com.library.app.user.services.UserServices;
import java.security.Principal;
import java.util.Arrays;
import javax.ejb.SessionContext;
import javax.validation.Validation;
import javax.validation.Validator;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyObject;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

public class OrderServicesTest {
    private Validator validator;
    private OrderServices servicesUnderTest;
    
    @Mock
    private OrderRepository repository;
    
    @Mock
    private UserServices userServices;
    
    @Mock
    private BookServices bookServices;
    
    @Mock
    private SessionContext sessionContext;
    
    private static final String LOGGED_EMAIL = "anymail@domain.com";
    
    @Before
    public void initTestCase(){
        validator = Validation.buildDefaultValidatorFactory().getValidator();
        
        MockitoAnnotations.initMocks(this);
        
        servicesUnderTest = new OrderServicesImpl();
        
        ((OrderServicesImpl) servicesUnderTest).validator = validator;
        ((OrderServicesImpl) servicesUnderTest).repository = repository;
        ((OrderServicesImpl) servicesUnderTest).userServices = userServices;
        ((OrderServicesImpl) servicesUnderTest).bookServices = bookServices;
        ((OrderServicesImpl) servicesUnderTest).sessionContext = sessionContext;
        
        setupLoggedEmail(LOGGED_EMAIL, Roles.ADMINISTRATOR);
    }
    
    @Test(expected = UserNotFoundException.class)
    public void addOrderWithInexistentCustomer() throws Exception{
        when(userServices.findByEmail(LOGGED_EMAIL)).thenThrow(new UserNotFoundException());
        
        servicesUnderTest.add(orderReserved());
    }
    
    @Test(expected = BookNotFoundException.class)
	public void addOrderWithInexistentBook() {
		when(userServices.findByEmail(LOGGED_EMAIL)).thenReturn(johnDoe());
		when(bookServices.findById(anyLong())).thenThrow(new BookNotFoundException());

		servicesUnderTest.add(orderReserved());
	}

	@Test
	public void addOrderWithNullQuantityInOneItem() {
		when(userServices.findByEmail(LOGGED_EMAIL)).thenReturn(johnDoe());
		when(bookServices.findById(1L)).thenReturn(bookWithId(designPatterns(), 1L));
		when(bookServices.findById(2L)).thenReturn(bookWithId(refactoring(), 2L));

		final Order order = orderReserved();
		order.getItems().iterator().next().setQuantity(null);

		addOrderWithInvalidField(order, "items[].quantity");
	}

	@Test
	public void addOrderWithoutItems() throws Exception {
		when(userServices.findByEmail(LOGGED_EMAIL)).thenReturn(johnDoe());

		final Order order = orderReserved();
		order.setItems(null);
		addOrderWithInvalidField(order, "items");
	}

	@Test
	public void addOrderWithNullBookInOneItem() throws Exception {
		when(userServices.findByEmail(LOGGED_EMAIL)).thenReturn(johnDoe());

		final Order order = orderReserved();
		order.getItems().iterator().next().setBook(null);

		addOrderWithInvalidField(order, "items[].book");
	}

	@Test
	public void addValidOrder() {
		when(userServices.findByEmail(LOGGED_EMAIL)).thenReturn(johnDoe());
		when(bookServices.findById(1L)).thenReturn(bookWithId(designPatterns(), 1L));
		when(bookServices.findById(2L)).thenReturn(bookWithId(refactoring(), 2L));
		when(repository.add(orderEq(orderReserved()))).thenReturn(orderWithId(orderReserved(), 1L));

		final Order order = new Order();
		order.setItems(orderReserved().getItems());

		final Long id = servicesUnderTest.add(order).getId();
		assertThat(id, is(notNullValue()));
	}

	@Test(expected = OrderNotFoundException.class)
	public void findOrderByIdNotFound() {
		when(repository.findById(1L)).thenReturn(null);

		servicesUnderTest.findById(1L);
	}

	@Test
	public void findOrderById() {
		when(repository.findById(1L)).thenReturn(orderWithId(orderReserved(), 1L));

		final Order order = servicesUnderTest.findById(1L);
		assertThat(order, is(notNullValue()));
	}

	@Test(expected = OrderNotFoundException.class)
	public void updateStatusOrderNotFound() {
		when(repository.findById(1L)).thenReturn(null);

		servicesUnderTest.updateStatus(1L, OrderStatus.DELIVERED);
	}

	@Test(expected = OrderStatusCannotBeChangedException.class)
	public void updateStatusForSameStatus() {
		when(repository.findById(1L)).thenReturn(orderWithId(orderReserved(), 1L));

		servicesUnderTest.updateStatus(1L, OrderStatus.RESERVED);
	}

	@Test(expected = OrderStatusCannotBeChangedException.class)
	public void updateStatusForInvalidStatus() throws Exception {
		when(repository.findById(1L)).thenReturn(orderWithId(orderDelivered(), 1L));

		servicesUnderTest.updateStatus(1L, OrderStatus.RESERVED);
	}

	@Test(expected = UserNotAuthorizedException.class)
	public void updateStatusDeliveredAsNotEmployee() throws Exception {
		setupLoggedEmail(LOGGED_EMAIL, Roles.CUSTOMER);
		when(repository.findById(1L)).thenReturn(orderWithId(orderReserved(), 1L));

		servicesUnderTest.updateStatus(1L, OrderStatus.DELIVERED);
	}

	@Test
	public void updateStatusDeliveredAsEmployee() throws Exception {
		setupLoggedEmail(LOGGED_EMAIL, Roles.EMPLOYEE);
		when(repository.findById(1L)).thenReturn(orderWithId(orderReserved(), 1L));

		servicesUnderTest.updateStatus(1L, OrderStatus.DELIVERED);

		final Order expectedOrder = orderWithId(orderReserved(), 1L);
		expectedOrder.addHistoryEntry(OrderStatus.DELIVERED);
		verify(repository).update(orderEq(expectedOrder));
	}

	@Test(expected = UserNotAuthorizedException.class)
	public void updateStatusCancelledAsCustomerNotTheOrderCustomer() throws Exception {
		setupLoggedEmail(LOGGED_EMAIL, Roles.CUSTOMER);
		when(repository.findById(1L)).thenReturn(orderWithId(orderReserved(), 1L));

		servicesUnderTest.updateStatus(1L, OrderStatus.CANCELLED);
	}

	@Test
	public void updateStatusCancelledAsCustomerTheOrderCustomer() throws Exception {
		setupLoggedEmail(orderReserved().getCustomer().getEmail(), Roles.CUSTOMER);
		when(repository.findById(1L)).thenReturn(orderWithId(orderReserved(), 1L));

		servicesUnderTest.updateStatus(1L, OrderStatus.CANCELLED);

		final Order expectedOrder = orderWithId(orderReserved(), 1L);
		expectedOrder.addHistoryEntry(OrderStatus.CANCELLED);
		verify(repository).update(orderEq(expectedOrder));
	}

	@Test
	public void findByFilter() {
		final PaginatedData<Order> orders = new PaginatedData<>(2,
				Arrays.asList(orderReserved(), orderDelivered()));
		when(repository.findByFilter((OrderFilter) anyObject())).thenReturn(orders);

		final PaginatedData<Order> ordersReturned = servicesUnderTest.findByFilter(new OrderFilter());
		assertThat(ordersReturned.getNumberOfRows(), is(equalTo(2)));
		assertThat(ordersReturned.getRows().size(), is(equalTo(2)));
	}

    private void setupLoggedEmail(String email, Roles userRole) {
        reset(sessionContext);
        
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn(email);
        
        when(sessionContext.getCallerPrincipal()).thenReturn(principal);
        when(sessionContext.isCallerInRole(Roles.EMPLOYEE.name())).thenReturn(userRole == Roles.EMPLOYEE);
        when(sessionContext.isCallerInRole(Roles.CUSTOMER.name())).thenReturn(userRole == Roles.CUSTOMER);
    }

    private void addOrderWithInvalidField(Order order, String invalidField) {
        try{
            servicesUnderTest.add(order);
            fail("An error should have been thrown");
        } catch(FieldNotValidException e) {
            assertThat(e.getFieldName(), is(equalTo(invalidField)));
        }
    }
    
}
