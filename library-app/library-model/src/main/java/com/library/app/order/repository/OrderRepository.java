package com.library.app.order.repository;

import com.library.app.GenericRepository;
import com.library.app.order.model.Order;
import com.library.app.order.model.OrderFilter;
import com.library.app.pagination.PaginatedData;
import java.util.HashMap;
import java.util.Map;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class OrderRepository extends GenericRepository<Order>{
    
    @PersistenceContext
    EntityManager em;

    @Override
    protected Class<Order> getPersistentClass() {
        return Order.class;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
    public PaginatedData<Order> findByFilter(OrderFilter orderFilter) {
        StringBuilder clause = new StringBuilder("WHERE e.id IS NOT NULL");
        Map<String, Object> queryParameters = new HashMap<>();
        
        if(orderFilter.getStatus() != null){
            clause.append(" AND e.currentStatus = :status");
            queryParameters.put("status", orderFilter.getStatus());
        }
        
        if(orderFilter.getCustomerId() != null){
            clause.append(" AND e.customer.id = :customerId");
            queryParameters.put("customerId", orderFilter.getCustomerId());
        }

        if(orderFilter.getStartDate()!= null){
            clause.append(" AND e.createdAt >= :startDate");
            queryParameters.put("startDate", orderFilter.getStartDate());
        }

        if(orderFilter.getEndDate() != null){
            clause.append(" AND e.createdAt <= :endDate");
            queryParameters.put("endDate", orderFilter.getEndDate());
        }

        return findByParameters(clause.toString(), orderFilter.getPaginationData(), queryParameters, "createdAt DESC");
    }
}
