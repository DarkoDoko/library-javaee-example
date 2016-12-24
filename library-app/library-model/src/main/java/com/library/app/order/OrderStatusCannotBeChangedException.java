package com.library.app.order;

import javax.ejb.ApplicationException;

@ApplicationException
public class OrderStatusCannotBeChangedException extends RuntimeException{
    private static final long serialVersionUID = -4117362449707573906L;

    public OrderStatusCannotBeChangedException(String message) {
        super(message);
    }
}
