package com.library.app.order.services.impl;

import com.library.app.appproperties.ApplicationProperties;
import com.library.app.appproperties.PropertyValue;
import com.library.app.order.services.OrderServices;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class OrderExpiratorJob {
    
    @Inject
    OrderServices orderServices;
    
    //@Inject
    //private ApplicationProperties applicationProperties;
    
    @PropertyValue(name = "days-before-order-expiration")
    private Integer daysBeforeOrderExpiration;
    
    private Logger logger = LoggerFactory.getLogger(getClass());
    
    @Schedule(hour = "*/1", minute = "0", second = "0", persistent = false)
    public void run(){
        logger.debug("Executing order expirator job");
        orderServices.changeStatusOfExpiredOrders(daysBeforeOrderExpiration);
    }
    
}
