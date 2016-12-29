package com.library.app.order.services;

import com.library.app.DateUtils;
import static com.library.app.author.AuthorForTestsRepository.allAuthors;
import static com.library.app.book.BookForTestsRepository.allBooks;
import static com.library.app.book.BookForTestsRepository.normalizeDependencies;
import static com.library.app.category.CategoryForTestsRepository.allCategories;
import com.library.app.commontests.utils.ArquillianTestUtils;
import com.library.app.commontests.utils.TestRepositoryEJB;
import com.library.app.order.OrderForTestsRepository;
import static com.library.app.order.OrderForTestsRepository.orderReserved;
import com.library.app.order.model.Order;
import com.library.app.order.model.Order.OrderStatus;
import com.library.app.order.model.OrderFilter;
import com.library.app.order.services.impl.OrderExpiratorJob;
import com.library.app.pagination.PaginatedData;
import static com.library.app.user.UserForTestsRepository.allUsers;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class OrderExpirationJobIntTest {
    
    @Inject
    private OrderExpiratorJob orderExpiratorJob;
    
    @Inject
    private OrderServices orderServices;
    
    @Inject
    private TestRepositoryEJB testRepositoryEJB;
    
    @PersistenceContext
    private EntityManager em;
    
    @Deployment
    public static WebArchive createDeployment(){
        return ArquillianTestUtils.createDeploymentArchive();
    }
    
    @Test
    @InSequence(1)
    public void prepareOrderForTest() {
        testRepositoryEJB.deleteAll();

		allCategories().forEach(testRepositoryEJB::add);
		allAuthors().forEach(testRepositoryEJB::add);
		allBooks().forEach((book) -> testRepositoryEJB.add(normalizeDependencies(book, em)));
		allUsers().forEach(testRepositoryEJB::add);

		final Order orderReservedToBeExpired = orderReserved();
		orderReservedToBeExpired.setCreatedAt(DateUtils.currentDatePlusDays(-8));

		final Order orderReserved = orderReserved();

		testRepositoryEJB.add(OrderForTestsRepository.normalizeDependencies(orderReservedToBeExpired, em));
		testRepositoryEJB.add(OrderForTestsRepository.normalizeDependencies(orderReserved, em));
    }
    
    @Test
    @InSequence(2)
    public void runJob(){
        assertNumberOfOrdersWithStatus(2, OrderStatus.RESERVED);
		assertNumberOfOrdersWithStatus(0, OrderStatus.RESERVATION_EXPIRED);

		orderExpiratorJob.run();

		assertNumberOfOrdersWithStatus(1, OrderStatus.RESERVED);
		assertNumberOfOrdersWithStatus(1, OrderStatus.RESERVATION_EXPIRED);
        
    }
    
    private void assertNumberOfOrdersWithStatus(final int expectedTotalRecords, final OrderStatus status) {
		final OrderFilter orderFilter = new OrderFilter();
		orderFilter.setStatus(status);

		final PaginatedData<Order> orders = orderServices.findByFilter(orderFilter);

		assertThat(orders.getNumberOfRows(), is(equalTo(expectedTotalRecords)));
	}
}
