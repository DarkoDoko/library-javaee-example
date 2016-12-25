package com.library.app.order.resource;

import com.library.app.DateUtils;
import static com.library.app.order.OrderForTestsRepository.normalizeDependencies;
import static com.library.app.order.OrderForTestsRepository.orderReserved;
import com.library.app.order.model.Order;
import com.library.app.order.model.Order.OrderStatus;
import com.library.app.order.services.OrderServices;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/DB/orders")
@Produces(MediaType.APPLICATION_JSON)
public class OrderResourceDB {

    @Inject
    private OrderServices services;
    
    @PersistenceContext
    private EntityManager em;
    
    @POST
    public void addAll() throws Exception {
        Order order = normalizeDependencies(orderReserved(), em);
        order.setCreatedAt(DateUtils.getAsDateTime("2015-01-04T10:10:34Z"));
        services.add(order);
        
        Order order2 = normalizeDependencies(orderReserved(), em);
        order2.setCreatedAt(DateUtils.getAsDateTime("2015-01-05T10:10:34Z"));
        services.add(order2);
        services.updateStatus(order2.getId(), OrderStatus.CANCELLED);
    }
}
