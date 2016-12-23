package com.library.app.order;

import com.library.app.DateUtils;
import com.library.app.book.BookForTestsRepository;
import static com.library.app.book.BookForTestsRepository.bookWithId;
import static com.library.app.book.BookForTestsRepository.designPatterns;
import static com.library.app.book.BookForTestsRepository.refactoring;
import com.library.app.book.model.Book;
import com.library.app.order.model.Order;
import com.library.app.order.model.Order.OrderStatus;
import com.library.app.user.UserForTestsRepository;
import static com.library.app.user.UserForTestsRepository.johnDoe;
import static com.library.app.user.UserForTestsRepository.mary;
import com.library.app.user.model.Customer;
import javax.persistence.EntityManager;
import org.junit.Ignore;

@Ignore
public class OrderForTestsRepository {
    
    public static Order orderDelivered() {
        Order order = new Order();
        
        order.setCustomer((Customer) mary());
        
        order.addItem(bookWithId(designPatterns(), 1L), 2);
        order.addItem(bookWithId(refactoring(), 2L), 1);
        
        order.setInitialStatus();
        order.calculateTotal();
        
        order.addHistoryEntry(OrderStatus.DELIVERED);
        return order;
    }
    
    public static Order orderReserved() {
        Order order = new Order();
        
        order.setCustomer((Customer)johnDoe());
        
        order.addItem(bookWithId(designPatterns(), 1L), 2);
        order.addItem(bookWithId(refactoring(), 2L), 1);
        
        order.setInitialStatus();
        order.calculateTotal();
        
        return order;
    }
    
    public static Order orderWithId(Order order, Long id){
        order.setId(id);
        return order;
    }
    
    public static Order orderCreatedAt(Order order, String dateTime) {
        order.setCreatedAt(DateUtils.getAsDateTime(dateTime));
        return order;
    }
    
    public static Order normalizeDependencies(Order order, EntityManager em) {
        order.setCustomer(findByPropertyNameAndValue(em, Customer.class, "name", order.getCustomer().getName()));
        
        order.getItems().forEach(
            (item) -> item.setBook(findByPropertyNameAndValue(em, Book.class, "title", item.getBook().getTitle()))
        );
        
        return order;
    }
    
    private static <T> T findByPropertyNameAndValue(EntityManager em, Class<T> clazz, String propertyName, String propertyValue) {
        return (T) em.createQuery("SELECT o FROM " + clazz.getSimpleName() + " o "
                                + "WHERE o." + propertyName + " =:propertyValue")
                    .setParameter("propertyValue", propertyValue)
                    .getSingleResult();
    }
}
