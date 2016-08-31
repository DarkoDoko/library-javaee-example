package com.library.app.common.exception;

/**
 *
 * @author ddoko
 */
public class InvalidJsonException extends RuntimeException{
    private static final long serialVersionUID = 6087454351913028554L;
    
    public InvalidJsonException(String message){
        super(message);
    }
    
    public InvalidJsonException(Throwable t){
        super(t);
    }
    
}
