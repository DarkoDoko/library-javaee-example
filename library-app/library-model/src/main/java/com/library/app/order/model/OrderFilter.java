package com.library.app.order.model;

import com.library.app.order.model.Order.OrderStatus;
import com.library.app.pagination.filter.GenericFilter;
import java.util.Date;

public class OrderFilter extends GenericFilter{
    
    private Date startDate;
    private Date endDate;
    private Long customerId;
    private OrderStatus status;

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "OrderFilter{" + "startDate=" + startDate + ", endDate=" + endDate + ", customerId=" + customerId + ", status=" + status + '}';
    }
    
}
