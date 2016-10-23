package com.library.app.user;

import javax.ejb.ApplicationException;

@ApplicationException
public class UserExistentException extends RuntimeException{    
    private static final long serialVersionUID = 6388106165652297398L;
    
}
