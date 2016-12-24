package com.library.app;

import javax.ejb.ApplicationException;

@ApplicationException
public class UserNotAuthorizedException extends RuntimeException{
    private static final long serialVersionUID = 7372656222495716286L;
    
}
