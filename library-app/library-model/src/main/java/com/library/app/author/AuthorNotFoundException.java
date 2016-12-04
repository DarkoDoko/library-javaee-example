package com.library.app.author;

import javax.ejb.ApplicationException;

@ApplicationException
public class AuthorNotFoundException extends RuntimeException {
    private static final long serialVersionUID = -8526377028109307421L;
}
