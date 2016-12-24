package com.library.app.order.services;

import com.library.app.FieldNotValidException;
import com.library.app.UserNotAuthorizedException;
import com.library.app.book.BookNotFoundException;
import com.library.app.order.OrderNotFoundException;
import com.library.app.order.OrderStatusCannotBeChangedException;
import com.library.app.order.model.Order;
import com.library.app.order.model.Order.OrderStatus;
import com.library.app.order.model.OrderFilter;
import com.library.app.pagination.PaginatedData;
import javax.ejb.Local;

@Local
public interface OrderServices {
    
    Order add(Order order) throws UserNotAuthorizedException, BookNotFoundException, FieldNotValidException;
    
    Order findById(Long id) throws OrderNotFoundException;
    
    void updateStatus(Long id, OrderStatus newStatus) throws OrderNotFoundException, OrderStatusCannotBeChangedException, UserNotAuthorizedException;
    
    PaginatedData<Order> findByFilter(OrderFilter filter);
    
}
