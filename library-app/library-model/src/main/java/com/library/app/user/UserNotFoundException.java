package com.library.app.user;

import javax.ejb.ApplicationException;

@ApplicationException
public class UserNotFoundException extends RuntimeException{    
    private static final long serialVersionUID = 5211604718471572549L;
    
}
