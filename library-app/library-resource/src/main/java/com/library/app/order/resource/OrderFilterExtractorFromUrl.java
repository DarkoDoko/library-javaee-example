package com.library.app.order.resource;

import com.library.app.DateUtils;
import com.library.app.common.resource.AbstractFilterExtractorFromUrl;
import com.library.app.order.model.Order.OrderStatus;
import com.library.app.order.model.OrderFilter;
import javax.ws.rs.core.UriInfo;

public class OrderFilterExtractorFromUrl extends AbstractFilterExtractorFromUrl{

    public OrderFilterExtractorFromUrl(UriInfo uriInfo) {
        super(uriInfo);
    }

    @Override
    protected String getDefaultSortField() {
        return "-createdAt";
    }
    
    public OrderFilter getFilter(){
        OrderFilter orderFilter = new OrderFilter();

		orderFilter.setPaginationData(extractPaginationData());

		String startDateStr = getUriInfo().getQueryParameters().getFirst("startDate");
		if (startDateStr != null) {
			orderFilter.setStartDate(DateUtils.getAsDateTime(startDateStr));
		}

		String endDateStr = getUriInfo().getQueryParameters().getFirst("endDate");
		if (endDateStr != null) {
			orderFilter.setEndDate(DateUtils.getAsDateTime(endDateStr));
		}

		String customerIdStr = getUriInfo().getQueryParameters().getFirst("customerId");
		if (customerIdStr != null) {
			orderFilter.setCustomerId(Long.valueOf(customerIdStr));
		}

		String status = getUriInfo().getQueryParameters().getFirst("status");
		if (status != null) {
			orderFilter.setStatus(OrderStatus.valueOf(status));
		}

		return orderFilter;
    }
    
}
