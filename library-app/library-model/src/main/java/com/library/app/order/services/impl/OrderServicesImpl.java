package com.library.app.order.services.impl;

import com.library.app.FieldNotValidException;
import com.library.app.UserNotAuthorizedException;
import com.library.app.ValidationUtils;
import com.library.app.book.BookNotFoundException;
import com.library.app.book.model.Book;
import com.library.app.book.services.BookServices;
import com.library.app.order.OrderNotFoundException;
import com.library.app.order.OrderStatusCannotBeChangedException;
import com.library.app.order.model.Order;
import com.library.app.order.model.Order.OrderStatus;
import com.library.app.order.model.OrderFilter;
import com.library.app.order.repository.OrderRepository;
import com.library.app.order.services.OrderServices;
import com.library.app.pagination.PaginatedData;
import com.library.app.user.model.Customer;
import com.library.app.user.model.User;
import com.library.app.user.model.User.Roles;
import com.library.app.user.services.UserServices;
import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.Validator;

@Stateless
public class OrderServicesImpl implements OrderServices{

    @Inject
    OrderRepository repository;
    
    @Inject
    UserServices userServices;
    
    @Inject
    BookServices bookServices;
    
    @Inject
    Validator validator;
    
    @Resource
    SessionContext sessionContext;
    
    @Override
    public Order add(Order order) {
        checkCustomerAndSetItOnOrder(order);
        checkBooksForItemsAndSetThem(order);
        
        order.setInitialStatus();
        order.calculateTotal();
        
        ValidationUtils.validateEntityFields(validator, order);
        
        return repository.add(order);
    }

    @Override
    public Order findById(Long id) throws OrderNotFoundException {
        Order order = repository.findById(id);
        if(order == null){
            throw new OrderNotFoundException();
        }
        
        return order;
    }

    @Override
    public void updateStatus(Long id, OrderStatus newStatus) {
        Order order = this.findById(id);
        
        if(newStatus == OrderStatus.DELIVERED && 
           !sessionContext.isCallerInRole(Roles.EMPLOYEE.name())) {
            
            throw new UserNotAuthorizedException();
        }
        
        if(newStatus == OrderStatus.CANCELLED &&
           sessionContext.isCallerInRole(Roles.CUSTOMER.name()) &&
           !order.getCustomer().getEmail().equals(sessionContext.getCallerPrincipal().getName())) {
            
            throw new UserNotAuthorizedException();
        }
        
        try{
            order.addHistoryEntry(newStatus);
        } catch(IllegalArgumentException e) {
            throw new OrderStatusCannotBeChangedException(e.getMessage());
        }
        
        repository.update(order);        
    }

    @Override
    public PaginatedData<Order> findByFilter(OrderFilter filter) {
        return repository.findByFilter(filter);
    }

    private void checkCustomerAndSetItOnOrder(Order order) {
        User user = userServices.findByEmail(sessionContext.getCallerPrincipal().getName());
        order.setCustomer((Customer) user);
    }

    private void checkBooksForItemsAndSetThem(Order order) {
        order.getItems().forEach((item) -> {
            if(item.getBook() != null) {
                Book book = bookServices.findById(item.getBook().getId());
                item.setBook(book);
            }
        });
    }
    
}
