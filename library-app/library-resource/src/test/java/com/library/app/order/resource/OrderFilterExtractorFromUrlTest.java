package com.library.app.order.resource;

import com.library.app.DateUtils;
import static com.library.app.commontests.utils.FilterExtractorTestUtils.assertActualPaginationDataWithExpected;
import static com.library.app.commontests.utils.FilterExtractorTestUtils.setupUriInfoWithMap;
import com.library.app.order.model.Order.OrderStatus;
import com.library.app.order.model.OrderFilter;
import com.library.app.pagination.filter.PaginationData;
import com.library.app.pagination.filter.PaginationData.OrderMode;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.ws.rs.core.UriInfo;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.mock;

public class OrderFilterExtractorFromUrlTest {
    private UriInfo uriInfo;
    
    @Before
    public void initTestCase() {
        uriInfo = mock(UriInfo.class);
    }
    
    @Test
	public void onlyDefaultValues() {
		setUpUriInfo(null, null, null, null, null, null, null);

		OrderFilterExtractorFromUrl extractor = new OrderFilterExtractorFromUrl(uriInfo);
		OrderFilter orderFilter = extractor.getFilter();

		assertActualPaginationDataWithExpected(orderFilter.getPaginationData(), new PaginationData(0, 10, "createdAt",
				OrderMode.DESCENDING));
		assertFieldsOnFilter(orderFilter, null, null, null, null);
	}

	@Test
	public void withPaginationAndStartDateAndEndDateAndCustomerIdAndStatusAndSortAscending() {
		setUpUriInfo("2", "5", "2015-01-04T10:10:34Z", "2015-01-05T10:10:34Z", "10", OrderStatus.CANCELLED.name(),
				"createdAt");

		final OrderFilterExtractorFromUrl extractor = new OrderFilterExtractorFromUrl(uriInfo);
		final OrderFilter orderFilter = extractor.getFilter();

		assertActualPaginationDataWithExpected(orderFilter.getPaginationData(), new PaginationData(10, 5, "createdAt",
				OrderMode.ASCENDING));
		assertFieldsOnFilter(orderFilter, DateUtils.getAsDateTime("2015-01-04T10:10:34Z"),
				DateUtils.getAsDateTime("2015-01-05T10:10:34Z"), 10L, OrderStatus.CANCELLED);
	}

	private void assertFieldsOnFilter(OrderFilter orderFilter, Date startDate, Date endDate, Long customerId,
                                      OrderStatus status) {
		assertThat(orderFilter.getStartDate(), is(equalTo(startDate)));
		assertThat(orderFilter.getEndDate(), is(equalTo(endDate)));
		assertThat(orderFilter.getCustomerId(), is(equalTo(customerId)));
		assertThat(orderFilter.getStatus(), is(equalTo(status)));
	}
    
        private void setUpUriInfo(String page, String perPage, String startDate, String endDate, String customerId,
                              String status, String sort) {
		Map<String, String> parameters = new LinkedHashMap<>();
		parameters.put("page", page);
		parameters.put("per_page", perPage);
		parameters.put("startDate", startDate);
		parameters.put("endDate", endDate);
		parameters.put("customerId", customerId);
		parameters.put("status", status);
		parameters.put("sort", sort);

		setupUriInfoWithMap(uriInfo, parameters);
	}
    
}
