package com.library.app.book;

import javax.ejb.ApplicationException;

@ApplicationException
public class BookNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 6743083947302477041L;
    
}
